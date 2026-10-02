/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public final class ElytraVisitedFix {
    private ElytraVisitedFix() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isVisited(Object object, Object object2, Object object3) {
        try {
            if (ElytraVisitedFix.bool(object3)) {
                return false;
            }
            if (object == null || object2 == null) {
                return false;
            }
            Object object4 = ElytraVisitedFix.findField(object.getClass(), "o").get(object);
            Object object5 = ElytraVisitedFix.invokeNoArg(object4, "get");
            if (!(object5 instanceof List)) {
                return false;
            }
            List list = (List)object5;
            long l = ((Number)ElytraVisitedFix.invokeNoArg(object2, "getX")).longValue();
            long l2 = ((Number)ElytraVisitedFix.invokeNoArg(object2, "getZ")).longValue();
            List list2 = list;
            synchronized (list2) {
                for (Object e : list) {
                    String string;
                    String[] stringArray;
                    if (!(e instanceof String) || (stringArray = (string = (String)e).split(",", -1)).length != 2) continue;
                    try {
                        long l3 = Long.parseLong(stringArray[0].trim());
                        long l4 = Long.parseLong(stringArray[1].trim());
                        if (l3 != l || l4 != l2) continue;
                        return true;
                    }
                    catch (NumberFormatException numberFormatException) {
                    }
                }
            }
            return false;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private static boolean bool(Object object) throws Exception {
        if (object == null) {
            return false;
        }
        Object object2 = ElytraVisitedFix.invokeNoArg(object, "get");
        return object2 instanceof Boolean && (Boolean)object2 != false;
    }

    private static Object invokeNoArg(Object object, String string) throws Exception {
        Method method = object.getClass().getMethod(string, new Class[0]);
        method.setAccessible(true);
        return method.invoke(object, new Object[0]);
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
        throw new NoSuchFieldException(string);
    }
}

