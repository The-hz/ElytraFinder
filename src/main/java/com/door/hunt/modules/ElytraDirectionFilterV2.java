/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public final class ElytraDirectionFilterV2 {
    private ElytraDirectionFilterV2() {
    }

    public static void filter(List<?> list, Object object, Object object2, Object object3, Object object4, int n, int n2) {
        if (list == null) {
            return;
        }
        int n3 = list.size();
        if (n3 == 0) {
            System.out.println("[Elytra Finder] 方向过滤: 原始候选=0");
            return;
        }
        try {
            boolean bl = ElytraDirectionFilterV2.bool(object);
            boolean bl2 = ElytraDirectionFilterV2.bool(object2);
            boolean bl3 = ElytraDirectionFilterV2.bool(object3);
            boolean bl4 = ElytraDirectionFilterV2.bool(object4);
            Iterator<?> iterator = list.iterator();
            while (iterator.hasNext()) {
                boolean bl5;
                Object obj = iterator.next();
                Object object5 = ElytraDirectionFilterV2.findField(obj.getClass(), "d").get(obj);
                int n4 = ((Number)ElytraDirectionFilterV2.invokeNoArg(object5, "getX")).intValue();
                int n5 = ((Number)ElytraDirectionFilterV2.invokeNoArg(object5, "getZ")).intValue();
                double d = n4 - n;
                double d2 = n5 - n2;
                if (Math.abs(d) >= Math.abs(d2)) {
                    bl5 = d >= 0.0 ? bl3 : bl4;
                } else {
                    boolean bl6 = bl5 = d2 >= 0.0 ? bl2 : bl;
                }
                if (bl5) continue;
                iterator.remove();
            }
            System.out.println("[Ying] 方向过滤: 原始候选=" + n3 + ", 保留=" + list.size() + ", 基准X=" + n + ", Z=" + n2 + ", 北=" + bl + ", 南=" + bl2 + ", 东=" + bl3 + ", 西=" + bl4);
        }
        catch (Throwable throwable) {
            System.out.println("[Ying] 方向过滤异常，保留当前候选: " + String.valueOf(throwable));
        }
    }

    private static boolean bool(Object object) throws Exception {
        if (object == null) {
            return true;
        }
        Object object2 = ElytraDirectionFilterV2.invokeNoArg(object, "get");
        return !(object2 instanceof Boolean) || (Boolean)object2 != false;
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

