/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ElytraSafeEscape {
    private static final double ESCAPE_DISTANCE = 90.0;
    private static final double REAPPROACH_DISTANCE = 28.0;
    private static final int SAFE_HEIGHT_ABOVE_LANDING = 30;
    private static final int MIN_REAPPROACH_HEIGHT_ABOVE_LANDING = 10;

    private ElytraSafeEscape() {
    }

    public static boolean tick(Object object) {
        if (object == null) {
            return false;
        }
        try {
            float f;
            if (!ElytraSafeEscape.getBooleanField(object, "ba")) {
                return false;
            }
            Object object2 = ElytraSafeEscape.getField(object, "af");
            if (object2 == null) {
                ElytraSafeEscape.setBooleanField(object, "ba", false);
                return false;
            }
            Object object3 = ElytraSafeEscape.getField(object2, "j");
            if (object3 == null) {
                ElytraSafeEscape.setBooleanField(object, "ba", false);
                return false;
            }
            Object object4 = ElytraSafeEscape.getField(object, "mc");
            if (object4 == null) {
                return true;
            }
            Object object5 = ElytraSafeEscape.getField(object4, "player");
            if (object5 == null) {
                return true;
            }
            if (!ElytraSafeEscape.invokeBoolean(object5, "isGliding")) {
                ElytraSafeEscape.ensureFallFlying(object5);
                return true;
            }
            double d = ElytraSafeEscape.invokeDouble(object5, "getX");
            double d2 = ElytraSafeEscape.invokeDouble(object5, "getY");
            double d3 = ElytraSafeEscape.invokeDouble(object5, "getZ");
            double d4 = (double)ElytraSafeEscape.invokeInt(object3, "getX") + 0.5;
            double d5 = ElytraSafeEscape.invokeInt(object3, "getY");
            double d6 = (double)ElytraSafeEscape.invokeInt(object3, "getZ") + 0.5;
            double d7 = d - d4;
            double d8 = d3 - d6;
            double d9 = Math.hypot(d7, d8);
            int n = ElytraSafeEscape.getIntField(object, "cr");
            int n2 = ElytraSafeEscape.getIntField(object, "bd");
            if (n2 == 0) {
                if (n < 0 || n > 2) {
                    n = 0;
                }
                if (n == 0) {
                    f = ElytraSafeEscape.normalizeYaw(ElytraSafeEscape.yawTo(object, object3) + 180.0f);
                    ElytraSafeEscape.setFloatField(object, "bc", f);
                    ElytraSafeEscape.log("安全复飞: 进入船底逃逸，先水平背离末地船，达到约 90 格安全距离后再抬头。");
                }
            }
            ElytraSafeEscape.setIntField(object, "bd", ++n2);
            switch (n) {
                case 0: {
                    f = ElytraSafeEscape.getFloatField(object, "bc");
                    ElytraSafeEscape.smoothYaw(object, object5, f);
                    ElytraSafeEscape.setPitch(object5, 0.0f);
                    ElytraSafeEscape.pressForward(object4, true);
                    int n3 = ElytraSafeEscape.fireworkIntervalTicks(object);
                    if (n2 >= 60 && n2 % Math.max(n3, 40) == 0) {
                        ElytraSafeEscape.invokeNoArg(object, "cv");
                    }
                    if (d9 >= 90.0) {
                        ElytraSafeEscape.setIntField(object, "cr", 1);
                        ElytraSafeEscape.setIntField(object, "bd", 0);
                        ElytraSafeEscape.setBooleanField(object, "ay", false);
                        ElytraSafeEscape.log(String.format("安全复飞: 已离开船体水平范围（%.1f 格），开始在船外爬升到 Y=%d。", d9, (int)d5 + 30));
                    }
                    return true;
                }
                case 1: {
                    f = ElytraSafeEscape.getFloatField(object, "bc");
                    ElytraSafeEscape.smoothYaw(object, object5, f);
                    float f2 = (float)ElytraSafeEscape.getSettingDouble(object, "startClimbAngle", 40.0);
                    ElytraSafeEscape.setPitch(object5, -Math.abs(f2));
                    ElytraSafeEscape.pressForward(object4, true);
                    int n4 = ElytraSafeEscape.fireworkIntervalTicks(object);
                    if (n2 == 1 || n2 % n4 == 0) {
                        ElytraSafeEscape.invokeNoArg(object, "cv");
                    }
                    if (d2 >= d5 + 30.0) {
                        ElytraSafeEscape.setIntField(object, "cr", 2);
                        ElytraSafeEscape.setIntField(object, "bd", 0);
                        ElytraSafeEscape.setBooleanField(object, "ay", false);
                        ElytraSafeEscape.log("安全复飞: 已到达船体上方安全高度，开始从船外重新进近。");
                    }
                    return true;
                }
                case 2: {
                    f = ElytraSafeEscape.yawTo(object, object3);
                    ElytraSafeEscape.smoothYaw(object, object5, f);
                    double d10 = ElytraSafeEscape.getSettingDouble(object, "cruiseGlideAngle", 15.0);
                    float f3 = (float)Math.max(3.0, Math.min(12.0, d10));
                    ElytraSafeEscape.setPitch(object5, f3);
                    ElytraSafeEscape.pressForward(object4, true);
                    if (d2 < d5 + 10.0 && d9 > 35.0) {
                        float f4 = ElytraSafeEscape.normalizeYaw(f + 180.0f);
                        ElytraSafeEscape.setFloatField(object, "bc", f4);
                        ElytraSafeEscape.setIntField(object, "cr", 0);
                        ElytraSafeEscape.setIntField(object, "bd", 0);
                        ElytraSafeEscape.setBooleanField(object, "ay", false);
                        ElytraSafeEscape.log("安全复飞: 重新进近高度不足，取消本次进近并再次向船外撤离。");
                        return true;
                    }
                    if (d9 <= 28.0 && d2 >= d5 + 10.0) {
                        ElytraSafeEscape.pressForward(object4, false);
                        ElytraSafeEscape.setBooleanField(object, "ba", false);
                        ElytraSafeEscape.setIntField(object, "cr", 0);
                        ElytraSafeEscape.setIntField(object, "bd", 0);
                        ElytraSafeEscape.setBooleanField(object, "ay", false);
                        ElytraSafeEscape.setIntField(object, "ag", 0);
                        ElytraSafeEscape.setEnumField(object, "x", "LANDING");
                        ElytraSafeEscape.log("安全复飞: 已从安全高度返回船外，重新进入降落流程。");
                    }
                    return true;
                }
            }
            ElytraSafeEscape.setIntField(object, "cr", 0);
            ElytraSafeEscape.setIntField(object, "bd", 0);
            return true;
        }
        catch (Throwable throwable) {
            try {
                ElytraSafeEscape.setBooleanField(object, "ba", false);
            }
            catch (Throwable throwable2) {
                // empty catch block
            }
            System.out.println("[Ying] 安全复飞异常，已回退原逻辑: " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
            return false;
        }
    }

    private static int fireworkIntervalTicks(Object object) {
        double d = ElytraSafeEscape.getSettingDouble(object, "n", 2.0);
        return Math.max(1, (int)Math.round(d * 20.0));
    }

    private static void ensureFallFlying(Object object) throws Exception {
        if (ElytraSafeEscape.invokeBoolean(object, "isOnGround")) {
            try {
                ElytraSafeEscape.invokeNoArg(object, "jump");
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return;
        }
        Object object2 = ElytraSafeEscape.getField(object, "networkHandler");
        if (object2 == null) {
            return;
        }
        Class<?> clazz = Class.forName("net.minecraft.class_2848");
        Class<?> clazz2 = Class.forName("net.minecraft.class_2848$class_2849");
        Field field = ElytraSafeEscape.findField(clazz2, "START_FALL_FLYING");
        Object object3 = field.get(null);
        Constructor<?> constructor = null;
        for (Constructor<?> constructor2 : clazz.getDeclaredConstructors()) {
            if (constructor2.getParameterCount() != 2) continue;
            constructor = constructor2;
            break;
        }
        if (constructor == null) {
            return;
        }
        constructor.setAccessible(true);
        Object obj = constructor.newInstance(object, object3);
        Method method = ElytraSafeEscape.findCompatibleMethod(object2.getClass(), "sendPacket", obj);
        method.invoke(object2, obj);
    }

    private static void smoothYaw(Object object, Object object2, float f) throws Exception {
        try {
            Method method = ElytraSafeEscape.findMethod(object.getClass(), "db", 2);
            float f2 = ElytraSafeEscape.invokeFloat(object2, "getPitch");
            method.invoke(object, Float.valueOf(f), Float.valueOf(f2));
        }
        catch (Throwable throwable) {
            ElytraSafeEscape.setYaw(object2, f);
        }
    }

    private static float yawTo(Object object, Object object2) throws Exception {
        Method method = ElytraSafeEscape.findMethod(object.getClass(), "cy", 1);
        Object object3 = method.invoke(object, object2);
        return ((Number)object3).floatValue();
    }

    private static void setPitch(Object object, float f) throws Exception {
        Method method = ElytraSafeEscape.findCompatibleMethod(object.getClass(), "setPitch", Float.valueOf(f));
        method.invoke(object, Float.valueOf(f));
    }

    private static void setYaw(Object object, float f) throws Exception {
        Method method = ElytraSafeEscape.findCompatibleMethod(object.getClass(), "setYaw", Float.valueOf(f));
        method.invoke(object, Float.valueOf(f));
    }

    private static void pressForward(Object object, boolean bl) throws Exception {
        Object object2 = ElytraSafeEscape.getField(object, "options");
        Object object3 = ElytraSafeEscape.getField(object2, "forwardKey");
        Method method = ElytraSafeEscape.findCompatibleMethod(object3.getClass(), "setPressed", bl);
        method.invoke(object3, bl);
    }

    private static double getSettingDouble(Object object, String string, double d) {
        try {
            Object object2 = ElytraSafeEscape.getField(object, string);
            if (object2 == null) {
                return d;
            }
            Method method = ElytraSafeEscape.findMethod(object2.getClass(), "get", 0);
            Object object3 = method.invoke(object2, new Object[0]);
            return object3 instanceof Number ? ((Number)object3).doubleValue() : d;
        }
        catch (Throwable throwable) {
            return d;
        }
    }

    private static float normalizeYaw(float f) {
        if ((f %= 360.0f) >= 180.0f) {
            f -= 360.0f;
        }
        if (f < -180.0f) {
            f += 360.0f;
        }
        return f;
    }

    private static void setEnumField(Object object, String string, String string2) throws Exception {
        Field field = ElytraSafeEscape.findField(object.getClass(), string);
        Class<?> clazz = field.getType();
        Enum enum_ = Enum.valueOf(clazz.asSubclass(Enum.class), string2);
        field.set(object, enum_);
    }

    private static Object getField(Object object, String string) throws Exception {
        if (object == null) {
            return null;
        }
        Field field = ElytraSafeEscape.findField(object.getClass(), string);
        return field.get(object);
    }

    private static boolean getBooleanField(Object object, String string) throws Exception {
        return ElytraSafeEscape.findField(object.getClass(), string).getBoolean(object);
    }

    private static void setBooleanField(Object object, String string, boolean bl) throws Exception {
        ElytraSafeEscape.findField(object.getClass(), string).setBoolean(object, bl);
    }

    private static int getIntField(Object object, String string) throws Exception {
        return ElytraSafeEscape.findField(object.getClass(), string).getInt(object);
    }

    private static void setIntField(Object object, String string, int n) throws Exception {
        ElytraSafeEscape.findField(object.getClass(), string).setInt(object, n);
    }

    private static float getFloatField(Object object, String string) throws Exception {
        return ElytraSafeEscape.findField(object.getClass(), string).getFloat(object);
    }

    private static void setFloatField(Object object, String string, float f) throws Exception {
        ElytraSafeEscape.findField(object.getClass(), string).setFloat(object, f);
    }

    private static Field findField(Class<?> clazz, String string) throws Exception {
        for (Class<?> clazz2 = clazz; clazz2 != null; clazz2 = clazz2.getSuperclass()) {
            try {
                Field field = clazz2.getDeclaredField(string);
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                continue;
            }
        }
        throw new NoSuchFieldException(clazz.getName() + "." + string);
    }

    private static Method findMethod(Class<?> clazz, String string, int n) throws Exception {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            Method[] methodArray = c.getDeclaredMethods();
            int n2 = methodArray.length;
            for (int i = 0; i < n2; ++i) {
                Method method = methodArray[i];
                if (!method.getName().equals(string) || method.getParameterCount() != n) continue;
                method.setAccessible(true);
                return method;
            }
        }
        for (Method method : clazz.getMethods()) {
            if (!method.getName().equals(string) || method.getParameterCount() != n) continue;
            method.setAccessible(true);
            return method;
        }
        throw new NoSuchMethodException(clazz.getName() + "." + string + "/" + n);
    }

    private static Method findCompatibleMethod(Class<?> clazz, String string, Object object) throws Exception {
        Class<?> clazz2 = object == null ? null : object.getClass();
        for (Class<?> clazz3 = clazz; clazz3 != null; clazz3 = clazz3.getSuperclass()) {
            for (Method method : clazz3.getDeclaredMethods()) {
                Class<?> clazz4;
                if (!method.getName().equals(string) || method.getParameterCount() != 1 || !ElytraSafeEscape.isCompatible(clazz4 = method.getParameterTypes()[0], clazz2)) continue;
                method.setAccessible(true);
                return method;
            }
        }
        throw new NoSuchMethodException(clazz.getName() + "." + string);
    }

    private static boolean isCompatible(Class<?> clazz, Class<?> clazz2) {
        if (clazz2 == null) {
            return !clazz.isPrimitive();
        }
        if (clazz.isAssignableFrom(clazz2)) {
            return true;
        }
        if (clazz == Boolean.TYPE && clazz2 == Boolean.class) {
            return true;
        }
        if (clazz == Float.TYPE && clazz2 == Float.class) {
            return true;
        }
        if (clazz == Double.TYPE && clazz2 == Double.class) {
            return true;
        }
        if (clazz == Integer.TYPE && clazz2 == Integer.class) {
            return true;
        }
        return clazz == Long.TYPE && clazz2 == Long.class;
    }

    private static boolean invokeBoolean(Object object, String string) throws Exception {
        return (Boolean)ElytraSafeEscape.findMethod(object.getClass(), string, 0).invoke(object, new Object[0]);
    }

    private static double invokeDouble(Object object, String string) throws Exception {
        return ((Number)ElytraSafeEscape.findMethod(object.getClass(), string, 0).invoke(object, new Object[0])).doubleValue();
    }

    private static float invokeFloat(Object object, String string) throws Exception {
        return ((Number)ElytraSafeEscape.findMethod(object.getClass(), string, 0).invoke(object, new Object[0])).floatValue();
    }

    private static int invokeInt(Object object, String string) throws Exception {
        return ((Number)ElytraSafeEscape.findMethod(object.getClass(), string, 0).invoke(object, new Object[0])).intValue();
    }

    private static Object invokeNoArg(Object object, String string) throws Exception {
        return ElytraSafeEscape.findMethod(object.getClass(), string, 0).invoke(object, new Object[0]);
    }

    private static void log(String string) {
        System.out.println("[Ying] " + string);
    }
}

