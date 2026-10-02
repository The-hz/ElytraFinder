/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class ElytraApproachSafety22 {
    private static final double TRIGGER_DISTANCE = 200.0;
    private static final double LANDING_HANDOFF_DISTANCE = 80.0;
    private static final double SAFE_MARGIN_ABOVE_TARGET = 30.0;
    private static final double SAFE_EXTRA_ON_RETURN = 8.0;
    private static final Map<Object, Data> STATES = Collections.synchronizedMap(new WeakHashMap());

    private ElytraApproachSafety22() {
    }

    public static boolean tick(Object object2) {
        if (object2 == null) {
            return false;
        }
        try {
            double d;
            Object object3 = ElytraApproachSafety22.getField(object2, "mc");
            if (object3 == null) {
                return false;
            }
            Object object4 = ElytraApproachSafety22.getField(object3, "player");
            if (object4 == null) {
                return false;
            }
            if (!ElytraApproachSafety22.invokeBoolean(object4, "isGliding")) {
                return false;
            }
            Object object5 = ElytraApproachSafety22.getField(object2, "af");
            if (object5 == null) {
                ElytraApproachSafety22.clear(object2);
                return false;
            }
            Object object6 = ElytraApproachSafety22.getField(object5, "j");
            if (object6 == null) {
                ElytraApproachSafety22.clear(object2);
                return false;
            }
            double d2 = ElytraApproachSafety22.invokeDouble(object4, "getX");
            double d3 = ElytraApproachSafety22.invokeDouble(object4, "getY");
            double d4 = ElytraApproachSafety22.invokeDouble(object4, "getZ");
            double d5 = (double)ElytraApproachSafety22.invokeInt(object6, "getX") + 0.5;
            double d6 = ElytraApproachSafety22.invokeInt(object6, "getY");
            double d7 = (double)ElytraApproachSafety22.invokeInt(object6, "getZ") + 0.5;
            double d8 = Math.hypot(d2 - d5, d4 - d7);
            String string = (int)d5 + "," + (int)d6 + "," + (int)d7;
            Data data = STATES.computeIfAbsent(object2, object -> new Data());
            if (!string.equals(data.targetKey)) {
                data.targetKey = string;
                data.mode = 0;
                data.ticks = 0;
                data.announced = false;
            }
            if (d8 > 200.0 && data.mode == 0 && !data.announced) {
                return false;
            }
            if (d8 > 200.0 && data.mode == 0 && data.announced) {
                return false;
            }
            double d9 = Math.abs(ElytraApproachSafety22.getSettingDouble(object2, "cruiseGlideAngle", 15.0));
            double d10 = Math.max(3.0, Math.min(12.0, d9));
            double d11 = d6 + 30.0;
            double d12 = d3 - d8 * Math.tan(Math.toRadians(d10));
            data.safeY = d11;
            data.predictedY = d12;
            boolean bl = ElytraApproachSafety22.getBooleanField(object2, "au");
            boolean bl2 = ElytraApproachSafety22.getBooleanField(object2, "av");
            if (d8 <= 80.0 && bl && !bl2) {
                ElytraApproachSafety22.pressForward(object3, false);
                ElytraApproachSafety22.setBooleanField(object2, "aj", false);
                ElytraApproachSafety22.setIntField(object2, "ag", 0);
                ElytraApproachSafety22.setEnumField(object2, "x", "LANDING");
                ElytraApproachSafety22.clear(object2);
                ElytraApproachSafety22.log(String.format("\u52a8\u6001\u8fdb\u8fd1: \u5df2\u786e\u8ba4\u9798\u7fc5\uff0c\u8ddd\u79bb %.1f \u683c\uff0c\u9ad8\u5ea6 %.1f\uff0c\u5207\u6362\u5230\u964d\u843d\u3002", d8, d3));
                return true;
            }
            if (!data.announced) {
                data.announced = true;
                if (d12 >= d11 + 8.0) {
                    data.mode = 0;
                    ElytraApproachSafety22.log(String.format("\u52a8\u6001\u8fdb\u8fd1: \u8ddd\u79bb %.1f \u683c\uff0c\u9884\u6d4b\u5230\u8239\u4f4d\u7f6e Y\u2248%.1f\uff0c\u5b89\u5168\u9ad8\u5ea6 Y=%.1f\uff0c\u53ef\u76f4\u63a5\u4ece\u8239\u4e0a\u65b9\u8fdb\u8fd1\u3002", d8, d12, d11));
                } else {
                    data.mode = 1;
                    data.awayYaw = ElytraApproachSafety22.yawTo(object2, object6);
                    data.ticks = 0;
                    ElytraApproachSafety22.log(String.format("\u52a8\u6001\u8fdb\u8fd1: \u8ddd\u79bb %.1f \u683c\uff0c\u9884\u6d4b\u5230\u8239\u4f4d\u7f6e Y\u2248%.1f < \u5b89\u5168\u9ad8\u5ea6 Y=%.1f\uff0c\u76f4\u63a5\u671d\u5411\u672b\u5730\u8239\u62c9\u5347\u3002", d8, d12, d11));
                }
            }
            ++data.ticks;
            if (data.mode == 1) {
                double d13;
                double d14;
                ElytraApproachSafety22.smoothYaw(object2, object4, data.awayYaw);
                float f = (float)Math.abs(ElytraApproachSafety22.getSettingDouble(object2, "startClimbAngle", 40.0));
                ElytraApproachSafety22.setPitch(object4, -f);
                ElytraApproachSafety22.pressForward(object3, true);
                int n = ElytraApproachSafety22.fireworkIntervalTicks(object2);
                if (data.ticks == 1 || data.ticks % n == 0) {
                    ElytraApproachSafety22.invokeNoArg(object2, "cv");
                }
                if (d3 >= (d14 = d11 + Math.max(35.0, Math.min(90.0, (d13 = d8 * Math.tan(Math.toRadians(d10))) + 12.0)))) {
                    data.mode = 2;
                    data.ticks = 0;
                    ElytraApproachSafety22.setBooleanField(object2, "ay", false);
                    ElytraApproachSafety22.log(String.format("\u52a8\u6001\u8fdb\u8fd1: \u76f4\u63a5\u62c9\u5347\u5230 Y=%.1f\uff08\u5f53\u524d\u9700\u8981\u7ea6 Y=%.1f\uff09\uff0c\u6062\u590d\u5b89\u5168\u8fdb\u8fd1\u3002", d3, d14));
                }
                return true;
            }
            float f = ElytraApproachSafety22.yawTo(object2, object6);
            ElytraApproachSafety22.smoothYaw(object2, object4, f);
            ElytraApproachSafety22.setPitch(object4, (float)d10);
            ElytraApproachSafety22.pressForward(object3, true);
            ElytraApproachSafety22.setBooleanField(object2, "aj", false);
            data.predictedY = d = d3 - d8 * Math.tan(Math.toRadians(d10));
            if (d < d11 + 3.0 && d8 > 95.0) {
                data.mode = 1;
                data.awayYaw = f;
                data.ticks = 0;
                ElytraApproachSafety22.setBooleanField(object2, "ay", false);
                ElytraApproachSafety22.log(String.format("\u52a8\u6001\u8fdb\u8fd1: \u8fd4\u56de\u8fc7\u7a0b\u4e2d\u9884\u6d4b\u9ad8\u5ea6\u4e0d\u8db3\uff08Y\u2248%.1f < %.1f\uff09\uff0c\u76f4\u63a5\u671d\u5411\u672b\u5730\u8239\u7ee7\u7eed\u62c9\u5347\u3002", d, d11 + 3.0));
                return true;
            }
            return d8 <= 200.0 || data.mode == 2;
        }
        catch (Throwable throwable) {
            System.out.println("[Elytra Finder] \u52a8\u6001\u8fdb\u8fd1\u5f02\u5e38\uff0c\u5df2\u56de\u9000\u539f\u98de\u884c\u903b\u8f91: " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
            ElytraApproachSafety22.clear(object2);
            return false;
        }
    }

    public static String status(Object object) {
        Data data = STATES.get(object);
        if (data == null || !data.announced) {
            return null;
        }
        return switch (data.mode) {
            case 1 -> "\u671d\u8239\u76f4\u63a5\u62c9\u5347";
            case 2 -> "\u5b89\u5168\u91cd\u65b0\u8fdb\u8fd1";
            default -> "\u52a8\u6001\u5b89\u5168\u8fdb\u8fd1";
        };
    }

    public static double safeHeight(Object object) {
        Data data = STATES.get(object);
        return data == null ? Double.NaN : data.safeY;
    }

    public static double predictedHeight(Object object) {
        Data data = STATES.get(object);
        return data == null ? Double.NaN : data.predictedY;
    }

    public static void clear(Object object) {
        if (object != null) {
            STATES.remove(object);
        }
    }

    private static int fireworkIntervalTicks(Object object) {
        double d = ElytraApproachSafety22.getSettingDouble(object, "n", 2.0);
        return Math.max(1, (int)Math.round(d * 20.0));
    }

    private static double getSettingDouble(Object object, String string, double d) {
        try {
            Object object2 = ElytraApproachSafety22.getField(object, string);
            Method method = ElytraApproachSafety22.findMethod(object2.getClass(), "get", 0);
            Object object3 = method.invoke(object2, new Object[0]);
            return object3 instanceof Number ? ((Number)object3).doubleValue() : d;
        }
        catch (Throwable throwable) {
            return d;
        }
    }

    private static float yawTo(Object object, Object object2) throws Exception {
        Method method = ElytraApproachSafety22.findMethod(object.getClass(), "cy", 1);
        return ((Number)method.invoke(object, object2)).floatValue();
    }

    private static void smoothYaw(Object object, Object object2, float f) throws Exception {
        try {
            Method method = ElytraApproachSafety22.findMethod(object.getClass(), "db", 2);
            float f2 = ((Number)ElytraApproachSafety22.findMethod(object2.getClass(), "getPitch", 0).invoke(object2, new Object[0])).floatValue();
            method.invoke(object, Float.valueOf(f), Float.valueOf(f2));
        }
        catch (Throwable throwable) {
            Method method = ElytraApproachSafety22.findCompatibleMethod(object2.getClass(), "setYaw", Float.valueOf(f));
            method.invoke(object2, Float.valueOf(f));
        }
    }

    private static void setPitch(Object object, float f) throws Exception {
        ElytraApproachSafety22.findCompatibleMethod(object.getClass(), "setPitch", Float.valueOf(f)).invoke(object, Float.valueOf(f));
    }

    private static void pressForward(Object object, boolean bl) throws Exception {
        Object object2 = ElytraApproachSafety22.getField(object, "options");
        Object object3 = ElytraApproachSafety22.getField(object2, "forwardKey");
        ElytraApproachSafety22.findCompatibleMethod(object3.getClass(), "setPressed", bl).invoke(object3, bl);
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
        Field field = ElytraApproachSafety22.findField(object.getClass(), string);
        Class<?> clazz = field.getType();
        Enum enum_ = Enum.valueOf(clazz.asSubclass(Enum.class), string2);
        field.set(object, enum_);
    }

    private static Object getField(Object object, String string) throws Exception {
        if (object == null) {
            return null;
        }
        return ElytraApproachSafety22.findField(object.getClass(), string).get(object);
    }

    private static boolean getBooleanField(Object object, String string) throws Exception {
        return ElytraApproachSafety22.findField(object.getClass(), string).getBoolean(object);
    }

    private static void setBooleanField(Object object, String string, boolean bl) throws Exception {
        ElytraApproachSafety22.findField(object.getClass(), string).setBoolean(object, bl);
    }

    private static void setIntField(Object object, String string, int n) throws Exception {
        ElytraApproachSafety22.findField(object.getClass(), string).setInt(object, n);
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
                if (!method.getName().equals(string) || method.getParameterCount() != 1 || !ElytraApproachSafety22.compatible(clazz4 = method.getParameterTypes()[0], clazz2)) continue;
                method.setAccessible(true);
                return method;
            }
        }
        throw new NoSuchMethodException(clazz.getName() + "." + string);
    }

    private static boolean compatible(Class<?> clazz, Class<?> clazz2) {
        if (clazz2 == null) {
            return !clazz.isPrimitive();
        }
        if (clazz.isAssignableFrom(clazz2)) {
            return true;
        }
        return clazz == Boolean.TYPE && clazz2 == Boolean.class || clazz == Float.TYPE && clazz2 == Float.class || clazz == Double.TYPE && clazz2 == Double.class || clazz == Integer.TYPE && clazz2 == Integer.class || clazz == Long.TYPE && clazz2 == Long.class;
    }

    private static boolean invokeBoolean(Object object, String string) throws Exception {
        return (Boolean)ElytraApproachSafety22.findMethod(object.getClass(), string, 0).invoke(object, new Object[0]);
    }

    private static double invokeDouble(Object object, String string) throws Exception {
        return ((Number)ElytraApproachSafety22.findMethod(object.getClass(), string, 0).invoke(object, new Object[0])).doubleValue();
    }

    private static int invokeInt(Object object, String string) throws Exception {
        return ((Number)ElytraApproachSafety22.findMethod(object.getClass(), string, 0).invoke(object, new Object[0])).intValue();
    }

    private static Object invokeNoArg(Object object, String string) throws Exception {
        return ElytraApproachSafety22.findMethod(object.getClass(), string, 0).invoke(object, new Object[0]);
    }

    private static void log(String string) {
        System.out.println("[Elytra Finder] " + string);
    }

    private static final class Data {
        String targetKey = "";
        int mode = 0;
        int ticks = 0;
        float awayYaw = 0.0f;
        double safeY = 0.0;
        double predictedY = 0.0;
        boolean announced = false;

        private Data() {
        }
    }
}

