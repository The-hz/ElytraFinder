/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import com.door.hunt.AddonTemplate;
import com.door.hunt.modules.ElytraCollectorModule;
import com.door.hunt.pathing.PathManagers;
import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.Hand;

public class PullUp
extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Integer> dangerY;
    private final Setting<Integer> targetY;
    private final Setting<Double> pullupPitch;
    private final Setting<Double> fireworkRocketDelay;
    private boolean isInDanger;
    private int tickPassed;
    private int tickWait;

    public PullUp() {
        super(AddonTemplate.CATEGORY, "紧急拉升", (String)"低于设定 Y 值时无条件强制拉升 (抬头 + 烟花)，升到安全高度自动停止.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.dangerY = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("危险 Y 高度")).description((String)"危险高度：低于此 Y 时无条件强制拉升 (采集器降落/降落恢复期间除外).")).defaultValue(40)).range(30, 300).sliderRange(30, 260).build());
        this.targetY = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("目标 Y 高度")).description((String)"拉升目标高度：达到此 Y 后停止拉升.")).defaultValue(70)).range(30, 500).sliderRange(50, 500).build());
        this.pullupPitch = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("拉升俯仰角")).description((String)"拉升时抬头的俯仰角 (负值=抬头；默认彗星 pitch40 角度 37.72°).")).defaultValue(-37.72).min(-90.0).max(-10.0).sliderMin(-80.0).sliderMax(-20.0).build());
        this.fireworkRocketDelay = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("烟花间隔")).description((String)"拉升期间每隔这么多秒使用一个烟花加速 (秒).")).defaultValue(2.0).min(0.5).sliderRange(0.5, 10.0).build());
        this.isInDanger = false;
        this.tickPassed = 0;
        this.tickWait = 0;
    }

    public void onActivate() {
        this.isInDanger = false;
        this.tickPassed = 0;
        this.tickWait = 0;
    }

    public void onDeactivate() {
        this.isInDanger = false;
        this.mc.options.forwardKey.setPressed(false);
        ElytraCollectorModule collector = (ElytraCollectorModule)Modules.get().get(ElytraCollectorModule.class);
        if (collector != null) {
            collector.setPullUpSuppressed(false);
        }
    }

    @EventHandler
    private void onTickPre(TickEvent.Pre event) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        ++this.tickPassed;
        ElytraCollectorModule collector = (ElytraCollectorModule)Modules.get().get(ElytraCollectorModule.class);
        if (collector != null && collector.isActive() && collector.isLandingOrRecovering()) {
            if (this.isInDanger) {
                this.isInDanger = false;
                collector.setPullUpSuppressed(false);
            }
            return;
        }
        if (this.isInDanger) {
            if (this.mc.player.getY() >= (double)((Integer)this.targetY.get()).intValue()) {
                this.isInDanger = false;
                if (collector != null) {
                    collector.setPullUpSuppressed(false);
                }
                this.info((String)"已升到目标高度，停止拉升.", new Object[0]);
            }
        } else if (this.mc.player.getY() < (double)((Integer)this.dangerY.get()).intValue()) {
            this.isInDanger = true;
            this.tickWait = -((int)Math.max(1L, Math.round((Double)this.fireworkRocketDelay.get() * 20.0)));
            this.info("危险高度: 低于 " + String.valueOf(this.dangerY.get()) + "，强制拉升.", new Object[0]);
            if (collector != null) {
                collector.abortStorageForPullUp();
                collector.setPullUpSuppressed(true);
            }
            PathManagers.get().stop();
        }
        if (!this.isInDanger) {
            return;
        }
        this.pullup();
    }

    private void pullup() {
        this.mc.player.setPitch(((Double)this.pullupPitch.get()).floatValue());
        this.mc.options.forwardKey.setPressed(true);
        if (!this.mc.player.isGliding()) {
            if (!this.mc.player.isOnGround()) {
                this.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
            return;
        }
        if ((long)(this.tickPassed - this.tickWait) >= Math.max(1L, Math.round((Double)this.fireworkRocketDelay.get() * 20.0))) {
            this.swapFireworkRocket();
            this.tickWait = this.tickPassed;
        }
    }

    private void swapFireworkRocket() {
        FindItemResult fw = InvUtils.findInHotbar((Item[])new Item[]{Items.FIREWORK_ROCKET});
        if (!fw.found()) {
            FindItemResult inv = InvUtils.find((Item[])new Item[]{Items.FIREWORK_ROCKET});
            if (inv.found()) {
                InvUtils.move().from(inv.slot()).toHotbar(1);
                this.info((String)"物品栏没有烟花，从背包调取放到第 2 个槽位.", new Object[0]);
            }
            return;
        }
        if (fw.isOffhand()) {
            this.mc.interactionManager.interactItem((PlayerEntity)this.mc.player, Hand.OFF_HAND);
        } else {
            InvUtils.swap((int)fw.slot(), (boolean)true);
            this.mc.interactionManager.interactItem((PlayerEntity)this.mc.player, Hand.MAIN_HAND);
            InvUtils.swapBack();
        }
    }

    

    
}

