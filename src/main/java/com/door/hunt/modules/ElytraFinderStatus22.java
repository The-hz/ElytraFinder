/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import com.door.hunt.modules.ElytraApproachSafety22;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public final class ElytraFinderStatus22 {
    private ElytraFinderStatus22() {
    }

    public static String[] lines(Object object) {
        try {
            double d;
            String string;
            if (object == null) {
                return new String[]{"\u72b6\u6001\uff1a\u6a21\u5757\u672a\u52a0\u8f7d"};
            }
            boolean bl = ElytraFinderStatus22.invokeBooleanPublic(object, "isActive", false);
            if (!bl) {
                return new String[]{"\u72b6\u6001\uff1a\u672a\u542f\u7528", "\u5df2\u8bbf\u95ee\uff1a" + ElytraFinderStatus22.visitedCount(object) + "   \u672c\u6b21\u9ed1\u540d\u5355\uff1a" + ElytraFinderStatus22.blacklistCount(object)};
            }
            String string2 = String.valueOf(ElytraFinderStatus22.getField(object, "x"));
            String string3 = ElytraApproachSafety22.status(object);
            boolean bl2 = ElytraFinderStatus22.getBoolean(object, "ba", false);
            int n = ElytraFinderStatus22.getInt(object, "cr", 0);
            boolean bl3 = ElytraFinderStatus22.getBoolean(object, "aj", false);
            if (bl2) {
                string = switch (n) {
                    case 0 -> "\u8239\u5e95\u5b89\u5168\u9003\u9038";
                    case 1 -> "\u8239\u5916\u5b89\u5168\u722c\u5347";
                    case 2 -> "\u5b89\u5168\u91cd\u65b0\u8fdb\u8fd1";
                    default -> "\u5b89\u5168\u590d\u98de";
                };
            } else if (string3 != null) {
                string = string3;
            } else {
                string = switch (string2) {
                    case "IDLE" -> "\u5f85\u673a";
                    case "SEARCHING" -> "\u641c\u7d22\u672b\u5730\u8239";
                    case "RISING" -> "\u722c\u5347";
                    case "FLYING" -> {
                        if (bl3) {
                            yield "\u722c\u5347";
                        }
                        yield "\u6ed1\u7fd4";
                    }
                    case "LANDING" -> "\u964d\u843d";
                    case "COLLECTING" -> "\u6536\u96c6\u9798\u7fc5";
                    case "DONE" -> "\u5b8c\u6210";
                    default -> string2;
                };
            }
            Object object2 = ElytraFinderStatus22.getField(object, "mc");
            Object object3 = object2 == null ? null : ElytraFinderStatus22.getField(object2, "player");
            double d2 = object3 == null ? Double.NaN : ElytraFinderStatus22.invokeDouble(object3, "getY", Double.NaN);
            int n2 = ElytraFinderStatus22.getSettingInt(object, "h", 0);
            int n3 = ElytraFinderStatus22.getSettingInt(object, "i", 0);
            Object object4 = ElytraFinderStatus22.getTarget(object);
            Object object5 = "\u65e0";
            String string4 = "--";
            if (object3 != null && object4 != null) {
                int n4 = ElytraFinderStatus22.invokeInt(object4, "getX", 0);
                int n5 = ElytraFinderStatus22.invokeInt(object4, "getY", 0);
                int n6 = ElytraFinderStatus22.invokeInt(object4, "getZ", 0);
                object5 = n4 + ", " + n5 + ", " + n6;
                d = ElytraFinderStatus22.invokeDouble(object3, "getX", 0.0);
                double d3 = ElytraFinderStatus22.invokeDouble(object3, "getZ", 0.0);
                double d4 = Math.hypot(d - ((double)n4 + 0.5), d3 - ((double)n6 + 0.5));
                string4 = String.format(Locale.ROOT, "%.1f \u683c", d4);
            }
            ArrayList<Object> arrayList = new ArrayList<Object>();
            arrayList.add("\u72b6\u6001\uff1a" + string + "\t\u8ddd\u79bb\uff1a" + string4);
            arrayList.add("\u76ee\u6807\uff1a" + (String)object5 + "\t");
            if (!Double.isNaN(d2)) {
                arrayList.add(String.format(Locale.ROOT, "\u5de1\u822a\u8303\u56f4\uff1a%d\uff5e%d \u683c\t\u9ad8\u5ea6\uff1a%.1f \u683c", n2, n3, d2));
            } else {
                arrayList.add(String.format(Locale.ROOT, "\u5de1\u822a\u8303\u56f4\uff1a%d\uff5e%d \u683c\t\u9ad8\u5ea6\uff1a--", n2, n3));
            }
            double d5 = ElytraApproachSafety22.safeHeight(object);
            d = ElytraApproachSafety22.predictedHeight(object);
            if (!Double.isNaN(d5) && string3 != null) {
                arrayList.add(String.format(Locale.ROOT, "\u8239\u4f53\u5b89\u5168\u9ad8\u5ea6\uff1a%.1f \u683c", d5));
                arrayList.add(String.format(Locale.ROOT, "\u9884\u6d4b\u5230\u8fbe\u9ad8\u5ea6\uff1a%.1f \u683c", d));
            }
            arrayList.add("\u5df2\u8bbf\u95ee\uff1a" + ElytraFinderStatus22.visitedCount(object) + "   \u672c\u6b21\u9ed1\u540d\u5355\uff1a" + ElytraFinderStatus22.blacklistCount(object));
            return (String[])arrayList.toArray(String[]::new);
        }
        catch (Throwable throwable) {
            return new String[]{"\u72b6\u6001\uff1a\u8bfb\u53d6\u5931\u8d25", "\u539f\u56e0\uff1a" + throwable.getClass().getSimpleName()};
        }
    }

    public static int visitedCount(Object object) {
        try {
            int n;
            Object object2 = ElytraFinderStatus22.getField(object, "o");
            Object object3 = ElytraFinderStatus22.findMethod(object2.getClass(), "get", 0).invoke(object2, new Object[0]);
            if (object3 instanceof Collection) {
                Collection collection = (Collection)object3;
                n = collection.size();
            } else {
                n = 0;
            }
            return n;
        }
        catch (Throwable throwable) {
            return 0;
        }
    }

    public static int blacklistCount(Object object) {
        try {
            int n;
            Object object2 = ElytraFinderStatus22.getField(object, "ap");
            if (object2 instanceof Collection) {
                Collection collection = (Collection)object2;
                n = collection.size();
            } else {
                n = 0;
            }
            return n;
        }
        catch (Throwable throwable) {
            return 0;
        }
    }

    private static Object getTarget(Object object) {
        Object object2;
        try {
            object2 = ElytraFinderStatus22.getField(object, "af");
            if (object2 != null) {
                return ElytraFinderStatus22.getField(object2, "j");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            object2 = ElytraFinderStatus22.getField(object, "ae");
            if (object2 != null) {
                return ElytraFinderStatus22.getField(object2, "d");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private static int getSettingInt(Object object, String string, int n) {
        try {
            Object object2 = ElytraFinderStatus22.getField(object, string);
            Object object3 = ElytraFinderStatus22.findMethod(object2.getClass(), "get", 0).invoke(object2, new Object[0]);
            return object3 instanceof Number ? ((Number)object3).intValue() : n;
        }
        catch (Throwable throwable) {
            return n;
        }
    }

    private static Object getField(Object object, String string) throws Exception {
        if (object == null) {
            return null;
        }
        return ElytraFinderStatus22.findField(object.getClass(), string).get(object);
    }

    private static boolean getBoolean(Object object, String string, boolean bl) {
        try {
            return ElytraFinderStatus22.findField(object.getClass(), string).getBoolean(object);
        }
        catch (Throwable throwable) {
            return bl;
        }
    }

    private static int getInt(Object object, String string, int n) {
        try {
            return ElytraFinderStatus22.findField(object.getClass(), string).getInt(object);
        }
        catch (Throwable throwable) {
            return n;
        }
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

    private static boolean invokeBooleanPublic(Object object, String string, boolean bl) {
        try {
            Method method = object.getClass().getMethod(string, new Class[0]);
            return (Boolean)method.invoke(object, new Object[0]);
        }
        catch (Throwable throwable) {
            return bl;
        }
    }

    private static double invokeDouble(Object object, String string, double d) {
        try {
            return ((Number)ElytraFinderStatus22.findMethod(object.getClass(), string, 0).invoke(object, new Object[0])).doubleValue();
        }
        catch (Throwable throwable) {
            return d;
        }
    }

    private static int invokeInt(Object object, String string, int n) {
        try {
            return ((Number)ElytraFinderStatus22.findMethod(object.getClass(), string, 0).invoke(object, new Object[0])).intValue();
        }
        catch (Throwable throwable) {
            return n;
        }
    }
}

