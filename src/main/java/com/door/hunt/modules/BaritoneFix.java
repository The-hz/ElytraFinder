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
    private final Setting<Boolean> e = this.d.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("启用 Baritone 命令保护")).description((String)"Baritone 命令 `#` 前缀保护：检测到消息以 # 开头但 Baritone 不可用时拦截发送。")).defaultValue(false)).build());
    private final Setting<Boolean> f = this.d.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("启用自动跳跃修复")).description((String)"Baritone 鞘翅寻路停滞且玩家未滑翔时自动起跳 (jump + START_FALL_FLYING 包)。")).defaultValue(false)).build());
    private final Setting<Boolean> g = this.d.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("启用紧急降落修复")).description((String)"Baritone mine/寻路结束后未滑翔且低于指定高度时自动重新起跳滑翔 (防卡住)。")).defaultValue(false)).build());
    private final Setting<Double> h = this.d.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("紧急降落高度")).description((String)"低于该 Y 且未滑翔时视为紧急降落。")).defaultValue(-20.0).min(-320.0).max(320.0).sliderMin(-100.0).sliderMax(100.0).build());
    private final Setting<Boolean> i = this.d.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("Baritone 条件暂停")).description((String)"Baritone 条件暂停：玩家正在手动控制 (WASD/跳跃有输入) 或按暂停热键时，每 tick 取消寻路。")).defaultValue(false)).build());
    private final Setting<Keybind> j = this.d.add((Setting)((KeybindSetting.Builder)((KeybindSetting.Builder)new KeybindSetting.Builder().name("Baritone 暂停热键")).description((String)"条件暂停热键：按下后手动接管 (切换接管状态)，配合条件暂停取消 Baritone 寻路。")).action(() -> {
        this.b = !this.b;
    }).build());
    private final Setting<Boolean> k = this.d.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("启用维度修复")).description((String)"维度修复提示：真实维度替换需依赖 Baritone 内部控制，移植版仅记录提示，不可用。")).defaultValue(false)).build());

    public BaritoneFix() {
        super(AddonTemplate.CATEGORY, "Baritone 修复", (String)"Baritone 防卡住修复：Baritone 寻路/鞘翅飞行异常时自动接管处理。");
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
            this.info((String)"Baritone Fix: dimension-fix 需依赖 Baritone 内部控制，反射移植版不可用，已忽略该功能。", new Object[0]);
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
            this.info((String)"Baritone Fix: 检测到 Baritone 命令 (#...) 但 Baritone 不可用，已取消该消息发送。", new Object[0]);
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

