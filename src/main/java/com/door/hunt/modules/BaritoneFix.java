/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import com.door.hunt.AddonTemplate;
import com.door.hunt.pathing.BaritonePathManager;
import com.door.hunt.pathing.PathManagers;
import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class BaritoneFix
extends Module {
    public static BaritoneFix a;
    private boolean b = false;
    private boolean c = false;
    private final SettingGroup d = this.settings.getDefaultGroup();
    private final Setting<Boolean> e = this.d.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u542f\u7528 Baritone \u547d\u4ee4\u4fdd\u62a4")).description((String)"Baritone \u547d\u4ee4 `#` \u524d\u7f00\u4fdd\u62a4\uff1a\u68c0\u6d4b\u5230\u6d88\u606f\u4ee5 # \u5f00\u5934\u4f46 Baritone \u4e0d\u53ef\u7528\u65f6\u62e6\u622a\u53d1\u9001\u3002")).defaultValue(false)).build());
    private final Setting<Boolean> f = this.d.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u542f\u7528\u81ea\u52a8\u8df3\u8dc3\u4fee\u590d")).description((String)"Baritone \u9798\u7fc5\u5bfb\u8def\u505c\u6ede\u4e14\u73a9\u5bb6\u672a\u6ed1\u7fd4\u65f6\u81ea\u52a8\u8d77\u8df3 (jump + START_FALL_FLYING \u5305)\u3002")).defaultValue(false)).build());
    private final Setting<Boolean> g = this.d.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u542f\u7528\u7d27\u6025\u964d\u843d\u4fee\u590d")).description((String)"Baritone mine/\u5bfb\u8def\u7ed3\u675f\u540e\u672a\u6ed1\u7fd4\u4e14\u4f4e\u4e8e\u6307\u5b9a\u9ad8\u5ea6\u65f6\u81ea\u52a8\u91cd\u65b0\u8d77\u8df3\u6ed1\u7fd4 (\u9632\u5361\u4f4f)\u3002")).defaultValue(false)).build());
    private final Setting<Double> h = this.d.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u7d27\u6025\u964d\u843d\u9ad8\u5ea6")).description((String)"\u4f4e\u4e8e\u8be5 Y \u4e14\u672a\u6ed1\u7fd4\u65f6\u89c6\u4e3a\u7d27\u6025\u964d\u843d\u3002")).defaultValue(-20.0).min(-320.0).max(320.0).sliderMin(-100.0).sliderMax(100.0).build());
    private final Setting<Boolean> i = this.d.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("Baritone \u6761\u4ef6\u6682\u505c")).description((String)"Baritone \u6761\u4ef6\u6682\u505c\uff1a\u73a9\u5bb6\u6b63\u5728\u624b\u52a8\u63a7\u5236 (WASD/\u8df3\u8dc3\u6709\u8f93\u5165) \u6216\u6309\u6682\u505c\u70ed\u952e\u65f6\uff0c\u6bcf tick \u53d6\u6d88\u5bfb\u8def\u3002")).defaultValue(false)).build());
    private final Setting<Keybind> j = this.d.add((Setting)((KeybindSetting.Builder)((KeybindSetting.Builder)new KeybindSetting.Builder().name("Baritone \u6682\u505c\u70ed\u952e")).description((String)"\u6761\u4ef6\u6682\u505c\u70ed\u952e\uff1a\u6309\u4e0b\u540e\u624b\u52a8\u63a5\u7ba1 (\u5207\u6362\u63a5\u7ba1\u72b6\u6001)\uff0c\u914d\u5408\u6761\u4ef6\u6682\u505c\u53d6\u6d88 Baritone \u5bfb\u8def\u3002")).action(() -> {
        this.b = !this.b;
    }).build());
    private final Setting<Boolean> k = this.d.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u542f\u7528\u7ef4\u5ea6\u4fee\u590d")).description((String)"\u7ef4\u5ea6\u4fee\u590d\u63d0\u793a\uff1a\u771f\u5b9e\u7ef4\u5ea6\u66ff\u6362\u9700\u4f9d\u8d56 Baritone \u5185\u90e8\u63a7\u5236\uff0c\u79fb\u690d\u7248\u4ec5\u8bb0\u5f55\u63d0\u793a\uff0c\u4e0d\u53ef\u7528\u3002")).defaultValue(false)).build());
    static Object m;
    static Object l;
    static Object n;
    static Object o;
    static Object p;
    static Object q;
    static Object r;
    static Object s;

    public BaritoneFix() {
        super(AddonTemplate.CATEGORY, "Baritone \u4fee\u590d", (String)"Baritone \u9632\u5361\u4f4f\u4fee\u590d\uff1aBaritone \u5bfb\u8def/\u9798\u7fc5\u98de\u884c\u5f02\u5e38\u65f6\u81ea\u52a8\u63a5\u7ba1\u5904\u7406\u3002");
        a = this;
    }

    public void onActivate() {
        this.b = false;
        this.c = false;
    }

    public void onDeactivate() {
        this.b = false;
        this.c = false;
    }

    @EventHandler
    private void a(TickEvent.Pre event) {
        ClientPlayerEntity p = this.mc.player;
        if (p == null) {
            return;
        }
        boolean pathing = PathManagers.get().isPathing();
        if (((Boolean)this.f.get()).booleanValue() && pathing && !p.isGliding() && !p.isOnGround()) {
            this.c(p);
        }
        if (((Boolean)this.g.get()).booleanValue() && !p.isGliding() && !p.isOnGround() && !p.isTouchingWater() && p.getY() < (Double)this.h.get()) {
            this.c(p);
        }
        if (((Boolean)this.i.get()).booleanValue() && (this.b || this.d()) && pathing) {
            PathManagers.get().stop();
        }
        if (((Boolean)this.k.get()).booleanValue() && !this.c) {
            this.c = true;
            this.info((String)"Baritone Fix: dimension-fix \u9700\u4f9d\u8d56 Baritone \u5185\u90e8\u63a7\u5236\uff0c\u53cd\u5c04\u79fb\u690d\u7248\u4e0d\u53ef\u7528\uff0c\u5df2\u5ffd\u7565\u8be5\u529f\u80fd\u3002", new Object[0]);
        }
    }

    @EventHandler
    private void b(SendMessageEvent event) {
        if (!((Boolean)this.e.get()).booleanValue()) {
            return;
        }
        String msg = event.message;
        if (msg != null && msg.startsWith((String)"#") && !BaritonePathManager.isAvailable()) {
            event.setCancelled(true);
            this.info((String)"Baritone Fix: \u68c0\u6d4b\u5230 Baritone \u547d\u4ee4 (#...) \u4f46 Baritone \u4e0d\u53ef\u7528\uff0c\u5df2\u53d6\u6d88\u8be5\u6d88\u606f\u53d1\u9001\u3002", new Object[0]);
        }
    }

    private void c(ClientPlayerEntity p) {
        p.jump();
        if (!p.isOnGround() && p.checkGliding() && p.networkHandler != null) {
            p.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)p, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
    }

    private boolean d() {
        GameOptions o = this.mc.options;
        return o.forwardKey.isPressed() || o.backKey.isPressed() || o.leftKey.isPressed() || o.rightKey.isPressed() || o.jumpKey.isPressed();
    }

    

    
}

