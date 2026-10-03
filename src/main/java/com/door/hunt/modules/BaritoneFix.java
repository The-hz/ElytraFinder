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
    public static BaritoneFix INSTANCE;
    private boolean isPauseBaritoneTiggered = false;
    private boolean bool1 = false;
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<Boolean> baritoneProtect = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("启用 Baritone 命令保护")).description((String)"Baritone 命令 `#` 前缀保护：检测到消息以 # 开头但 Baritone 不可用时拦截发送。")).defaultValue(false)).build());
    private final Setting<Boolean> jumpFix = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("启用自动跳跃修复")).description((String)"Baritone 鞘翅寻路停滞且玩家未滑翔时自动起跳 (jump + START_FALL_FLYING 包)。")).defaultValue(false)).build());
    private final Setting<Boolean> emergencyLandingFix = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("启用紧急降落修复")).description((String)"Baritone mine/寻路结束后未滑翔且低于指定高度时自动重新起跳滑翔 (防卡住)。")).defaultValue(false)).build());
    private final Setting<Double> emergencyLandingHigh = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("紧急降落高度")).description((String)"低于该 Y 且未滑翔时视为紧急降落。")).defaultValue(-20.0).min(-320.0).max(320.0).sliderMin(-100.0).sliderMax(100.0).build());
    private final Setting<Boolean> pauseBaritone = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("Baritone 条件暂停")).description((String)"Baritone 条件暂停：玩家正在手动控制 (WASD/跳跃有输入) 或按暂停热键时，每 tick 取消寻路。")).defaultValue(false)).build());
    private final Setting<Keybind> pauseHotKey = this.sgGeneral.add((Setting)((KeybindSetting.Builder)((KeybindSetting.Builder)new KeybindSetting.Builder().name("Baritone 暂停热键")).description((String)"条件暂停热键：按下后手动接管 (切换接管状态)，配合条件暂停取消 Baritone 寻路。")).action(() -> {
        this.isPauseBaritoneTiggered = !this.isPauseBaritoneTiggered;
    }).build());
    private final Setting<Boolean> dimFix = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("启用维度修复")).description((String)"维度修复提示：真实维度替换需依赖 Baritone 内部控制，移植版仅记录提示，不可用。")).defaultValue(false)).build());

    public BaritoneFix() {
        super(AddonTemplate.CATEGORY, "Baritone 修复", (String)"Baritone 防卡住修复：Baritone 寻路/鞘翅飞行异常时自动接管处理。");
        INSTANCE = this;
    }

    public void onActivate() {
        this.isPauseBaritoneTiggered = false;
        this.bool1 = false;
    }

    public void onDeactivate() {
        this.isPauseBaritoneTiggered = false;
        this.bool1 = false;
    }

    @EventHandler
    private void onTickPre(TickEvent.Pre event) {
        ClientPlayerEntity p = this.mc.player;
        if (p == null) {
            return;
        }
        boolean pathing = PathManagers.get().isPathing();
        if (((Boolean)this.jumpFix.get()).booleanValue() && pathing && !p.isGliding() && !p.isOnGround()) {
            this.jump(p);
        }
        if (((Boolean)this.emergencyLandingFix.get()).booleanValue() && !p.isGliding() && !p.isOnGround() && !p.isTouchingWater() && p.getY() < (Double)this.emergencyLandingHigh.get()) {
            this.jump(p);
        }
        if (((Boolean)this.pauseBaritone.get()).booleanValue() && (this.isPauseBaritoneTiggered || this.hasInput()) && pathing) {
            PathManagers.get().stop();
        }
        if (((Boolean)this.dimFix.get()).booleanValue() && !this.bool1) {
            this.bool1 = true;
            this.info((String)"Baritone Fix: dimension-fix 需依赖 Baritone 内部控制，反射移植版不可用，已忽略该功能。", new Object[0]);
        }
    }

    @EventHandler
    private void onMessageEvent(SendMessageEvent event) {
        if (!((Boolean)this.baritoneProtect.get()).booleanValue()) {
            return;
        }
        String msg = event.message;
        if (msg != null && msg.startsWith((String)"#") && !BaritonePathManager.isAvailable()) {
            event.setCancelled(true);
            this.info((String)"Baritone Fix: 检测到 Baritone 命令 (#...) 但 Baritone 不可用，已取消该消息发送。", new Object[0]);
        }
    }

    private void jump(ClientPlayerEntity p) {
        p.jump();
        if (!p.isOnGround() && p.checkGliding() && p.networkHandler != null) {
            p.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)p, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
    }

    private boolean hasInput() {
        GameOptions o = this.mc.options;
        return o.forwardKey.isPressed() || o.backKey.isPressed() || o.leftKey.isPressed() || o.rightKey.isPressed() || o.jumpKey.isPressed();
    }

    

    
}

