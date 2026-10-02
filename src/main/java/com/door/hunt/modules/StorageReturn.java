/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class StorageReturn {
    private static final double ARRIVAL_DISTANCE = 2.5;
    private static final int REPATH_INTERVAL = 20;
    private static final int TIMEOUT_TICKS = 600;
    private static final Map<Object, ReturnState> STATES = Collections.synchronizedMap(new WeakHashMap());

    private StorageReturn() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void markStart(Object object) {
        if (object == null) {
            return;
        }
        try {
            Object object2 = StorageReturn.resolveReturnTarget(object);
            if (object2 == null) {
                return;
            }
            ReturnState returnState = new ReturnState();
            returnState.target = object2;
            Map<Object, ReturnState> map = STATES;
            synchronized (map) {
                STATES.put(object, returnState);
            }
        }
        catch (Throwable throwable) {
            System.err.println("[Elytra Finder/Test34] 记录存储返回点失败: " + String.valueOf(throwable));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void begin(Object object) {
        if (object == null) {
            return;
        }
        try {
            ReturnState returnState;
            Map<Object, ReturnState> map = STATES;
            synchronized (map) {
                returnState = STATES.get(object);
            }
            if (returnState == null || returnState.target == null) {
                StorageReturn.invokeNoArg(object, "s");
                return;
            }
            if (StorageReturn.isNear(object, returnState.target)) {
                map = STATES;
                synchronized (map) {
                    STATES.remove(object);
                }
                StorageReturn.stopPath(object);
                StorageReturn.chat(object, "info", "存储结束，当前位置已在指定起飞点，开始起飞.");
                StorageReturn.invokeNoArg(object, "s");
                return;
            }
            returnState.returning = true;
            returnState.ticks = 0;
            returnState.lastPathTick = -999;
            StorageReturn.stopPath(object);
            StorageReturn.requestMove(object, returnState.target);
            returnState.lastPathTick = 0;
            StorageReturn.chat(object, "info", "存储结束，先返回指定起飞点再起飞.");
        }
        catch (Throwable throwable) {
            System.err.println("[Elytra Finder/Test34] 启动返回起飞点失败: " + String.valueOf(throwable));
            try {
                StorageReturn.invokeNoArg(object, "s");
            }
            catch (Throwable throwable2) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean tick(Object object) {
        ReturnState returnState;
        if (object == null) {
            return false;
        }
        Map<Object, ReturnState> map = STATES;
        synchronized (map) {
            returnState = STATES.get(object);
        }
        if (returnState == null || !returnState.returning || returnState.target == null) {
            return false;
        }
        try {
            ++returnState.ticks;
            if (StorageReturn.isNear(object, returnState.target)) {
                StorageReturn.stopPath(object);
                map = STATES;
                synchronized (map) {
                    STATES.remove(object);
                }
                StorageReturn.chat(object, "info", "已回到指定起飞点，开始起飞.");
                StorageReturn.invokeNoArg(object, "s");
                return true;
            }
            if (returnState.ticks >= 600) {
                StorageReturn.stopPath(object);
                map = STATES;
                synchronized (map) {
                    STATES.remove(object);
                }
                StorageReturn.invokePrivateString(object, "o", "存储后 30 秒内无法返回指定起飞点，已停止任务.");
                return true;
            }
            if (returnState.ticks - returnState.lastPathTick >= 20 || !StorageReturn.isPathing(object)) {
                StorageReturn.requestMove(object, returnState.target);
                returnState.lastPathTick = returnState.ticks;
            }
            return true;
        }
        catch (Throwable throwable) {
            System.err.println("[Elytra Finder/Test34] 返回起飞点检查失败: " + String.valueOf(throwable));
            throwable.printStackTrace();
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clear(Object object) {
        if (object == null) {
            return;
        }
        Map<Object, ReturnState> map = STATES;
        synchronized (map) {
            STATES.remove(object);
        }
    }

    private static Object resolveReturnTarget(Object object) throws Exception {
        Object object2;
        Object object3;
        Object object4;
        Object object5 = StorageReturn.get(object, "y");
        if (object5 instanceof Enum && "EXIT_LANDING".equals(((Enum)(object4 = (Enum)object5)).name()) && (object3 = StorageReturn.get(object, "af")) != null && (object2 = StorageReturn.get(object3, "j")) != null) {
            return object2;
        }
        object4 = StorageReturn.get(object, "mc");
        if (object4 == null) {
            return null;
        }
        object3 = StorageReturn.get(object4, "player");
        if (object3 == null) {
            return null;
        }
        object2 = StorageReturn.findNoArgMethod(object3.getClass(), "getBlockPos");
        if (object2 == null) {
            throw new NoSuchMethodException("player.getBlockPos");
        }
        ((Method)object2).setAccessible(true);
        return ((Method)object2).invoke(object3, new Object[0]);
    }

    private static boolean isNear(Object object, Object object2) throws Exception {
        Method method = StorageReturn.findMethodByNameAndArity(object.getClass(), "cb", 2);
        if (method == null) {
            throw new NoSuchMethodException("cb");
        }
        method.setAccessible(true);
        Object object3 = method.invoke(object, object2, 2.5);
        return Boolean.TRUE.equals(object3);
    }

    private static void requestMove(Object object, Object object2) throws Exception {
        Object object3 = StorageReturn.pathManager(object);
        Method method = StorageReturn.findMethodByNameAndArity(object3.getClass(), "moveTo", 2);
        if (method == null) {
            Class<?> clazz;
            Class<?>[] classArray = object3.getClass().getInterfaces();
            int n = classArray.length;
            for (int i = 0; i < n && (method = StorageReturn.findMethodByNameAndArity(clazz = classArray[i], "moveTo", 2)) == null; ++i) {
            }
        }
        if (method == null) {
            throw new NoSuchMethodException("PathManager.moveTo");
        }
        method.setAccessible(true);
        method.invoke(object3, object2, false);
    }

    private static boolean isPathing(Object object) {
        try {
            Object object2 = StorageReturn.pathManager(object);
            Method method = StorageReturn.findMethodByNameAndArity(object2.getClass(), "isPathing", 0);
            if (method == null) {
                Class<?> clazz;
                Class<?>[] classArray = object2.getClass().getInterfaces();
                int n = classArray.length;
                for (int i = 0; i < n && (method = StorageReturn.findMethodByNameAndArity(clazz = classArray[i], "isPathing", 0)) == null; ++i) {
                }
            }
            if (method == null) {
                return false;
            }
            method.setAccessible(true);
            return Boolean.TRUE.equals(method.invoke(object2, new Object[0]));
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private static void stopPath(Object object) {
        try {
            Object object2 = StorageReturn.pathManager(object);
            Method method = StorageReturn.findMethodByNameAndArity(object2.getClass(), "stop", 0);
            if (method == null) {
                Class<?> clazz;
                Class<?>[] classArray = object2.getClass().getInterfaces();
                int n = classArray.length;
                for (int i = 0; i < n && (method = StorageReturn.findMethodByNameAndArity(clazz = classArray[i], "stop", 0)) == null; ++i) {
                }
            }
            if (method != null) {
                method.setAccessible(true);
                method.invoke(object2, new Object[0]);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static Object pathManager(Object object) throws Exception {
        ClassLoader classLoader = object.getClass().getClassLoader();
        Class<?> clazz = Class.forName("com.door.hunt.pathing.PathManagers", true, classLoader);
        Method method = clazz.getDeclaredMethod("get", new Class[0]);
        method.setAccessible(true);
        return method.invoke(null, new Object[0]);
    }

    private static void chat(Object object, String string, String string2) {
        try {
            Method method = StorageReturn.findMethod(object.getClass(), string, String.class, Object[].class);
            if (method == null) {
                return;
            }
            method.setAccessible(true);
            method.invoke(object, string2, new Object[0]);
        }
        catch (Throwable throwable) {
            System.out.println("[Elytra Finder/Test34] " + string2);
        }
    }

    private static void invokeNoArg(Object object, String string) throws Exception {
        Method method = StorageReturn.findNoArgMethod(object.getClass(), string);
        if (method == null) {
            throw new NoSuchMethodException(string);
        }
        method.setAccessible(true);
        method.invoke(object, new Object[0]);
    }

    private static void invokePrivateString(Object object, String string, String string2) throws Exception {
        Method method = StorageReturn.findMethod(object.getClass(), string, String.class);
        if (method == null) {
            throw new NoSuchMethodException(string);
        }
        method.setAccessible(true);
        method.invoke(object, string2);
    }

    private static Object get(Object object, String string) throws Exception {
        Field field = StorageReturn.findField(object.getClass(), string);
        if (field == null) {
            throw new NoSuchFieldException(string);
        }
        field.setAccessible(true);
        return field.get(object);
    }

    private static Field findField(Class<?> clazz, String string) {
        for (Class<?> clazz2 = clazz; clazz2 != null; clazz2 = clazz2.getSuperclass()) {
            try {
                return clazz2.getDeclaredField(string);
            }
            catch (NoSuchFieldException noSuchFieldException) {
                continue;
            }
        }
        return null;
    }

    private static Method findMethod(Class<?> clazz, String string, Class<?> ... classArray) {
        for (Class<?> clazz2 = clazz; clazz2 != null; clazz2 = clazz2.getSuperclass()) {
            try {
                return clazz2.getDeclaredMethod(string, classArray);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                continue;
            }
        }
        return null;
    }

    private static Method findNoArgMethod(Class<?> clazz, String string) {
        return StorageReturn.findMethodByNameAndArity(clazz, string, 0);
    }

    private static Method findMethodByNameAndArity(Class<?> clazz, String string, int n) {
        for (Class<?> clazz2 = clazz; clazz2 != null; clazz2 = clazz2.getSuperclass()) {
            for (Method method : clazz2.getDeclaredMethods()) {
                if (!method.getName().equals(string) || method.getParameterCount() != n) continue;
                return method;
            }
        }
        return null;
    }

    private static final class ReturnState {
        Object target;
        boolean returning;
        int ticks;
        int lastPathTick;

        private ReturnState() {
        }
    }
}

