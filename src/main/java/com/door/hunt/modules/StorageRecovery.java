/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class StorageRecovery {
    private static final int WAIT_TICKS = 15;
    private static final int MAX_RETRIES = 3;
    private static final long RESET_GAP_NS = 30000000000L;
    private static final Map<Object, RetryState> STATES = Collections.synchronizedMap(new WeakHashMap());

    private StorageRecovery() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void onInvalidScreen(Object object) {
        if (object == null) {
            return;
        }
        try {
            RetryState retryState;
            int n = StorageRecovery.getInt(object, "bg");
            if (n < 15) {
                return;
            }
            long l = System.nanoTime();
            Object object2 = STATES;
            synchronized (object2) {
                retryState = STATES.get(object);
                if (retryState == null || l - retryState.lastRecoveryNs > 30000000000L) {
                    retryState = new RetryState();
                    STATES.put(object, retryState);
                }
                retryState.lastRecoveryNs = l;
                ++retryState.attempts;
            }
            if (retryState.attempts <= 3) {
                StorageRecovery.chat(object, "warning", "\u6f5c\u5f71\u76d2\u754c\u9762\u5c1a\u672a\u540c\u6b65\uff0c\u7b49\u5f85\u540e\u91cd\u65b0\u6253\u5f00\uff08" + retryState.attempts + "/3\uff09...");
                object2 = StorageRecovery.storagePhase(object, "OPEN_BOX");
                Object object3 = StorageRecovery.storagePhase(object, "CLOSE_SCREEN");
                StorageRecovery.set(object, "bq", object2);
                StorageRecovery.set(object, "bf", object3);
                StorageRecovery.setInt(object, "bg", 0);
                StorageRecovery.setInt(object, "cn", 0);
                StorageRecovery.setInt(object, "co", -1);
                StorageRecovery.setBoolean(object, "cw", false);
                return;
            }
            object2 = STATES;
            synchronized (object2) {
                STATES.remove(object);
            }
            StorageRecovery.invokePrivate(object, "o", new Class[]{String.class}, new Object[]{"\u6f5c\u5f71\u76d2\u754c\u9762\u8fde\u7eed\u5f02\u5e38\uff0c\u5df2\u81ea\u52a8\u91cd\u8bd5 3 \u6b21\uff0c\u505c\u6b62\u4efb\u52a1."});
        }
        catch (Throwable throwable) {
            System.err.println("[Elytra Finder/Test33] \u6f5c\u5f71\u76d2\u754c\u9762\u6062\u590d\u5931\u8d25: " + String.valueOf(throwable));
            throwable.printStackTrace();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void onValidScreen(Object object) {
        if (object == null) {
            return;
        }
        Map<Object, RetryState> map = STATES;
        synchronized (map) {
            STATES.remove(object);
        }
    }

    private static Object storagePhase(Object object, String string) throws Exception {
        ClassLoader classLoader = object.getClass().getClassLoader();
        Class<?> clazz = Class.forName("com.door.hunt.modules.ElytraCollectorModule$StoragePhase", true, classLoader);
        @SuppressWarnings({"unchecked", "rawtypes"})
        Class<? extends Enum> enumClass = (Class<? extends Enum>) clazz;
        return Enum.valueOf(enumClass, string);
    }

    private static void chat(Object object, String string, String string2) {
        try {
            Method method = StorageRecovery.findMethod(object.getClass(), string, String.class, Object[].class);
            if (method == null) {
                return;
            }
            method.setAccessible(true);
            method.invoke(object, string2, new Object[0]);
        }
        catch (Throwable throwable) {
            System.out.println("[Elytra Finder/Test33] " + string2);
        }
    }

    private static void invokePrivate(Object object, String string, Class<?>[] classArray, Object[] objectArray) throws Exception {
        Method method = StorageRecovery.findMethod(object.getClass(), string, classArray);
        if (method == null) {
            throw new NoSuchMethodException(string);
        }
        method.setAccessible(true);
        method.invoke(object, objectArray);
    }

    private static Object get(Object object, String string) throws Exception {
        Field field = StorageRecovery.findField(object.getClass(), string);
        if (field == null) {
            throw new NoSuchFieldException(string);
        }
        field.setAccessible(true);
        return field.get(object);
    }

    private static void set(Object object, String string, Object object2) throws Exception {
        Field field = StorageRecovery.findField(object.getClass(), string);
        if (field == null) {
            throw new NoSuchFieldException(string);
        }
        field.setAccessible(true);
        field.set(object, object2);
    }

    private static int getInt(Object object, String string) throws Exception {
        Field field = StorageRecovery.findField(object.getClass(), string);
        if (field == null) {
            throw new NoSuchFieldException(string);
        }
        field.setAccessible(true);
        return field.getInt(object);
    }

    private static void setInt(Object object, String string, int n) throws Exception {
        Field field = StorageRecovery.findField(object.getClass(), string);
        if (field == null) {
            throw new NoSuchFieldException(string);
        }
        field.setAccessible(true);
        field.setInt(object, n);
    }

    private static void setBoolean(Object object, String string, boolean bl) throws Exception {
        Field field = StorageRecovery.findField(object.getClass(), string);
        if (field == null) {
            throw new NoSuchFieldException(string);
        }
        field.setAccessible(true);
        field.setBoolean(object, bl);
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

    private static final class RetryState {
        int attempts;
        long lastRecoveryNs;

        private RetryState() {
        }
    }
}

