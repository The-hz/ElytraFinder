/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import com.door.hunt.pathing.PathManagers;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;

public final class LowYSafetyLogout {
    private static volatile long lastTriggerMs;

    private LowYSafetyLogout() {
    }

    public static void trigger(Object object, String string) {
        Object field1724;
        Object object3;
        long l = System.currentTimeMillis();
        if (l - lastTriggerMs < 3000L) {
            return;
        }
        lastTriggerMs = l;
        try {
            if (object != null) {
                object3 = object.getClass().getDeclaredMethod("o", String.class);
                ((Method)object3).setAccessible(true);
                ((Method)object3).invoke(object, string);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            PathManagers.get().stop();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            for (Object module : Modules.get().getAll()) {
                if (!(module instanceof Module) || !((Module) module).isActive() || !module.getClass().getName().equals("meteordevelopment.meteorclient.systems.modules.combat.KillAura")) continue;
                ((Module) module).toggle();
                break;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            object3 = MeteorClient.class.getField("mc").get(null);
            if (object3 == null) {
                return;
            }
            field1724 = object3.getClass().getField("player");
            Object object4 = ((Field)field1724).get(object3);
            if (object4 == null) {
                return;
            }
            Field field = object4.getClass().getField("networkHandler");
            Object object5 = field.get(object4);
            if (object5 == null) {
                return;
            }
            Class<?> clazz = Class.forName("net.minecraft.class_2561");
            Method method = clazz.getMethod("literal", String.class);
            Object object6 = method.invoke(null, "Elytra Finder：高度低于安全阈值，已自动离线。");
            Class<?> clazz2 = Class.forName("net.minecraft.class_2661");
            Constructor<?> constructor = clazz2.getConstructor(clazz);
            Object obj = constructor.newInstance(object6);
            Method method2 = null;
            for (Method method3 : object5.getClass().getMethods()) {
                if (!method3.getName().equals("onDisconnect") || method3.getParameterCount() != 1) continue;
                method2 = method3;
                break;
            }
            if (method2 != null) {
                method2.invoke(object5, obj);
            }
        }
        catch (Throwable throwable) {
            try {
                if (object instanceof Module) {
                    Module moduleErr = (Module)object;
                    moduleErr.error("低高度自动离线失败: %s", new Object[]{throwable.getClass().getSimpleName()});
                }
            }
            catch (Throwable throwable2) {
                // empty catch block
            }
        }
    }
}

