/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.hud;

import com.door.hunt.modules.ElytraFinderStatus22;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;

public final class ElytraStatusOverlay24 {
    private static final ElytraStatusOverlay24 INSTANCE = new ElytraStatusOverlay24();
    private static volatile Object module;
    private static volatile boolean subscribed;
    private static Setting<Boolean> visible;
    private static Setting<Double> horizontal;
    private static Setting<Double> vertical;
    private static Setting<Double> scale;
    private static Setting<Integer> margin;
    private static Setting<Integer> columnGap;

    private ElytraStatusOverlay24() {
    }

    public static void install(Object object) {
        module = object;
        if (!(object instanceof Module)) {
            return;
        }
        Module module = (Module)object;
        SettingGroup settingGroup = module.settings.createGroup("\u72b6\u6001 HUD");
        visible = settingGroup.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u663e\u793a\u72b6\u6001 HUD")).description("\u5728\u6e38\u620f\u753b\u9762\u4e2d\u663e\u793a\u4e2d\u6587\u98de\u884c\u72b6\u6001\u3002")).defaultValue(true)).build());
        horizontal = settingGroup.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("HUD \u6a2a\u5411\u4f4d\u7f6e (%)")).description("0 \u4e3a\u5c4f\u5e55\u6700\u5de6\u4fa7\uff0c100 \u4e3a\u6700\u53f3\u4fa7\u3002100 \u4f1a\u6309\u771f\u5b9e\u7a97\u53e3\u5bbd\u5ea6\u8d34\u5230\u53f3\u8fb9\u3002")).defaultValue(100.0).range(0.0, 100.0).sliderRange(0.0, 100.0).decimalPlaces(1).build());
        vertical = settingGroup.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("HUD \u7eb5\u5411\u4f4d\u7f6e (%)")).description("0 \u4e3a\u5c4f\u5e55\u6700\u4e0a\u65b9\uff0c100 \u4e3a\u6700\u4e0b\u65b9\u3002")).defaultValue(0.0).range(0.0, 100.0).sliderRange(0.0, 100.0).decimalPlaces(1).build());
        scale = settingGroup.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("HUD \u5927\u5c0f")).description("HUD \u5b57\u4f53\u500d\u7387\u3002\u5b9e\u9645\u6309\u6700\u63a5\u8fd1\u7684\u6574\u6570\u500d\u7387\u6e32\u67d3\uff0c\u907f\u514d\u975e\u6574\u6570\u653e\u5927\u9020\u6210\u6587\u5b57\u53d1\u865a\u3002")).defaultValue(2.0).range(1.0, 8.0).sliderRange(1.0, 6.0).decimalPlaces(0).build());
        margin = settingGroup.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("HUD \u8fb9\u8ddd")).description("HUD \u4e0e\u5c4f\u5e55\u8fb9\u7f18\u4fdd\u7559\u7684\u771f\u5b9e\u50cf\u7d20\u8ddd\u79bb\uff1b\u8bbe\u4e3a 0 \u53ef\u8d34\u7d27\u5c4f\u5e55\u53f3\u8fb9\u7f18\u3002")).defaultValue(4)).range(0, 100).sliderRange(0, 40).build());
        columnGap = settingGroup.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("HUD \u5b57\u6bb5\u95f4\u8ddd")).description("\u72b6\u6001\u4e0e\u8ddd\u79bb\u3001\u5de1\u822a\u8303\u56f4\u4e0e\u9ad8\u5ea6\u4e4b\u95f4\u7684\u771f\u5b9e\u50cf\u7d20\u95f4\u8ddd\u3002")).defaultValue(2)).range(0, 30).sliderRange(0, 20).build());
        if (!subscribed) {
            MeteorClient.EVENT_BUS.subscribe((Object)INSTANCE);
            subscribed = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventHandler
    private void onRender(Render2DEvent render2DEvent) {
        String[] stringArray;
        Object object = module;
        if (object == null || visible == null || !((Boolean)visible.get()).booleanValue()) {
            return;
        }
        try {
            stringArray = ElytraFinderStatus22.lines(object);
        }
        catch (Throwable throwable) {
            stringArray = new String[]{"\u72b6\u6001\uff1aHUD \u6570\u636e\u8bfb\u53d6\u5931\u8d25"};
        }
        if (stringArray == null || stringArray.length == 0) {
            return;
        }
        double d = scale == null ? 2.0 : (Double)scale.get();
        double d2 = Math.max(1.0, Math.min(8.0, Math.rint(d)));
        int[] nArray = ElytraStatusOverlay24.framebufferSize(render2DEvent);
        int n = nArray[0];
        int n2 = nArray[1];
        TextRenderer textRenderer = TextRenderer.get();
        textRenderer.begin(d2 * 0.5, false, false);
        try {
            int n3 = margin == null ? 4 : (Integer)margin.get();
            n3 = Math.max(0, Math.min(100, n3));
            double d3 = horizontal == null ? 100.0 : (Double)horizontal.get();
            double d4 = vertical == null ? 0.0 : (Double)vertical.get();
            double d5 = Math.max(0.0, Math.min(1.0, d3 / 100.0));
            double d6 = Math.max(0.0, Math.min(1.0, d4 / 100.0));
            int n4 = columnGap == null ? 2 : (Integer)columnGap.get();
            n4 = Math.max(0, Math.min(30, n4));
            double d7 = n4;
            double d8 = textRenderer.getHeight(false) + Math.max(2.0, d2);
            double d9 = d8 * (double)stringArray.length;
            double d10 = Math.max(0.0, (double)n - 2.0 * (double)n3);
            double d11 = Math.max(0.0, (double)n2 - 2.0 * (double)n3);
            double d12 = (double)n3 + d10 * d5;
            double d13 = (double)n3 + d11 * d6 - d9 * d6;
            d13 = Math.max((double)n3, Math.min((double)(n2 - n3) - d9, d13));
            d13 = Math.rint(d13);
            double var44_33 = 0.0;
            for (String string : stringArray) {
                String string2 = string == null ? "" : string;
                int n5 = string2.indexOf(9);
                if (n5 >= 0) {
                    String string3 = string2.substring(0, n5);
                    String string4 = string2.substring(n5 + 1);
                    var44_33 = textRenderer.getWidth(string3, false);
                    double d14 = string4.isEmpty() ? 0.0 : textRenderer.getWidth(string4, false);
                    double d15 = string4.isEmpty() ? 0.0 : d7;
                    double d16 = var44_33 + d15 + d14;
                    double d17 = ElytraStatusOverlay24.anchoredX(n, n3, d12, d5, d16);
                    ElytraStatusOverlay24.renderCrisp(textRenderer, string3, d17, d13);
                    if (!string4.isEmpty()) {
                        ElytraStatusOverlay24.renderCrisp(textRenderer, string4, Math.rint(d17 + var44_33 + d15), d13);
                    }
                } else {
                    double d18 = textRenderer.getWidth(string2, false);
                    var44_33 = ElytraStatusOverlay24.anchoredX(n, n3, d12, d5, d18);
                    ElytraStatusOverlay24.renderCrisp(textRenderer, string2, var44_33, d13);
                }
                d13 = Math.rint(d13 + d8);
            }
        }
        finally {
            textRenderer.end();
        }
    }

    private static double anchoredX(int n, int n2, double d, double d2, double d3) {
        double d4 = d - d3 * d2;
        if (d2 <= 0.0) {
            d4 = n2;
        } else if (d2 >= 1.0) {
            d4 = (double)(n - n2) - d3;
        }
        return Math.rint(Math.max(0.0, Math.min((double)n - d3, d4)));
    }

    private static void renderCrisp(TextRenderer textRenderer, String string, double d, double d2) {
        if (string == null || string.isEmpty()) {
            return;
        }
        textRenderer.render(string, d - 1.0, d2, Color.BLACK, false);
        textRenderer.render(string, d + 1.0, d2, Color.BLACK, false);
        textRenderer.render(string, d, d2 - 1.0, Color.BLACK, false);
        textRenderer.render(string, d, d2 + 1.0, Color.BLACK, false);
        textRenderer.render(string, d, d2, Color.WHITE, false);
    }

    private static int[] framebufferSize(Render2DEvent render2DEvent) {
        int n = Math.max(1, render2DEvent.screenWidth);
        int n2 = Math.max(1, render2DEvent.screenHeight);
        try {
            Field field = MeteorClient.class.getDeclaredField("mc");
            field.setAccessible(true);
            Object object = field.get(null);
            if (object == null) {
                return new int[]{n, n2};
            }
            Method method = object.getClass().getMethod("getWindow", new Class[0]);
            Object object2 = method.invoke(object, new Object[0]);
            if (object2 == null) {
                return new int[]{n, n2};
            }
            Method method2 = object2.getClass().getMethod("getFramebufferWidth", new Class[0]);
            Method method3 = object2.getClass().getMethod("getFramebufferHeight", new Class[0]);
            Object object3 = method2.invoke(object2, new Object[0]);
            Object object4 = method3.invoke(object2, new Object[0]);
            if (object3 instanceof Number) {
                Number number = (Number)object3;
                if (object4 instanceof Number) {
                    Number number2 = (Number)object4;
                    int n3 = number.intValue();
                    int n4 = number2.intValue();
                    if (n3 > 0 && n4 > 0) {
                        return new int[]{n3, n4};
                    }
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return new int[]{n, n2};
    }
}

