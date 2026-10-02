/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import com.door.hunt.modules.ElytraFinderStatus22;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;

public final class ElytraVisitedUi22 {
    private ElytraVisitedUi22() {
    }

    public static void enhance(Object object, GuiTheme guiTheme, WSection wSection) {
        if (object == null || guiTheme == null || wSection == null) {
            return;
        }
        try {
            wSection.add((WWidget)guiTheme.horizontalSeparator("\u9ed1\u540d\u5355 / \u5df2\u8bbf\u95ee\u8bb0\u5f55")).expandX();
            WLabel wLabel = (WLabel)wSection.add((WWidget)guiTheme.label("\u5df2\u8bbf\u95ee\u8239\u53ea\uff1a" + ElytraFinderStatus22.visitedCount(object))).expandX().widget();
            WLabel wLabel2 = (WLabel)wSection.add((WWidget)guiTheme.label("\u672c\u6b21\u5931\u8d25\u9ed1\u540d\u5355\uff1a" + ElytraFinderStatus22.blacklistCount(object))).expandX().widget();
            WButton wButton = (WButton)wSection.add((WWidget)guiTheme.button("\u6e05\u7a7a\u5df2\u8bbf\u95ee\u8bb0\u5f55")).expandX().widget();
            wButton.action = () -> {
                ElytraVisitedUi22.clearVisited(object);
                wLabel.set("\u5df2\u8bbf\u95ee\u8239\u53ea\uff1a" + ElytraFinderStatus22.visitedCount(object));
            };
            WButton wButton2 = (WButton)wSection.add((WWidget)guiTheme.button("\u6e05\u7a7a\u672c\u6b21\u5931\u8d25\u9ed1\u540d\u5355")).expandX().widget();
            wButton2.action = () -> {
                ElytraVisitedUi22.clearSession(object);
                wLabel2.set("\u672c\u6b21\u5931\u8d25\u9ed1\u540d\u5355\uff1a" + ElytraFinderStatus22.blacklistCount(object));
            };
        }
        catch (Throwable throwable) {
            System.out.println("[Elytra Finder] \u8bb0\u5f55\u7ba1\u7406\u754c\u9762\u52a0\u8f7d\u5931\u8d25: " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
        }
    }

    private static void clearVisited(Object object) {
        try {
            Object object2 = ElytraVisitedUi22.getField(object, "o");
            Method method = ElytraVisitedUi22.findCompatibleMethod(object2.getClass(), "set", new ArrayList());
            method.invoke(object2, new ArrayList());
            System.out.println("[Elytra Finder] \u5df2\u6e05\u7a7a\u6301\u4e45\u5316\u5df2\u8bbf\u95ee\u8239\u53ea\u8bb0\u5f55\u3002");
        }
        catch (Throwable throwable) {
            System.out.println("[Elytra Finder] \u6e05\u7a7a\u5df2\u8bbf\u95ee\u8bb0\u5f55\u5931\u8d25: " + throwable.getMessage());
        }
    }

    private static void clearSession(Object object) {
        try {
            Object object2 = ElytraVisitedUi22.getField(object, "ap");
            if (object2 instanceof Set) {
                Set set = (Set)object2;
                set.clear();
            }
            System.out.println("[Elytra Finder] \u5df2\u6e05\u7a7a\u672c\u6b21\u4f1a\u8bdd\u5931\u8d25\u9ed1\u540d\u5355\u3002");
        }
        catch (Throwable throwable) {
            System.out.println("[Elytra Finder] \u6e05\u7a7a\u672c\u6b21\u5931\u8d25\u9ed1\u540d\u5355\u5931\u8d25: " + throwable.getMessage());
        }
    }

    private static Object getField(Object object, String string) throws Exception {
        return ElytraVisitedUi22.findField(object.getClass(), string).get(object);
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

    private static Method findCompatibleMethod(Class<?> clazz, String string, Object object) throws Exception {
        Class<?> clazz2 = object == null ? null : object.getClass();
        for (Class<?> clazz3 = clazz; clazz3 != null; clazz3 = clazz3.getSuperclass()) {
            for (Method method : clazz3.getDeclaredMethods()) {
                if (!method.getName().equals(string) || method.getParameterCount() != 1) continue;
                Class<?> clazz4 = method.getParameterTypes()[0];
                if (!(clazz2 == null ? !clazz4.isPrimitive() : clazz4.isAssignableFrom(clazz2) || clazz4 == Object.class)) continue;
                method.setAccessible(true);
                return method;
            }
        }
        throw new NoSuchMethodException(clazz.getName() + "." + string);
    }
}

