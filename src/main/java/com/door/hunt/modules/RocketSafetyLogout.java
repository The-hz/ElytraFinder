/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import com.door.hunt.AddonTemplate;
import com.door.hunt.modules.ElytraCollectorModule;
import com.door.hunt.modules.PullUp;
import com.door.hunt.pathing.PathManagers;
import java.lang.reflect.Field;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.text.Text;

public class RocketSafetyLogout
extends Module {
    private final SettingGroup sg;
    private final Setting<Integer> threshold;
    private final Setting<Integer> targetY;
    private final Setting<Integer> glideTicks;
    private Phase phase;
    private int phaseTicks;
    private int glideStableTicks;
    private int glideAttemptTicks;
    private Integer savedPullTrigger;
    private Integer savedPullTarget;
    private boolean pullSettingsChanged;

    public RocketSafetyLogout() {
        super(AddonTemplate.CATEGORY, "烟花安全撤离", "找鞘翅时监控背包+快捷栏烟花。低于阈值后爬升到设定高度，确认滑翔并自动离线。");
        this.sg = this.settings.getDefaultGroup();
        this.threshold = this.sg.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("烟花撤离阈值")).description("主背包和快捷栏烟花总数低于该值时，停止找船并执行安全撤离。")).defaultValue(32)).range(10, 64).sliderRange(10, 64).build());
        this.targetY = this.sg.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("撤离高度-Y")).description("烟花不足后使用剩余烟花爬升到该Y高度。")).defaultValue(300)).range(100, 500).sliderRange(100, 500).build());
        this.glideTicks = this.sg.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("滑翔确认时间")).description("到达目标高度后，连续确认处于鞘翅滑翔多少tick再自动离线。20tick约1秒。")).defaultValue(20)).range(5, 100).sliderRange(5, 60).build());
        this.phase = Phase.ARMED;
    }

    public void onActivate() {
        this.resetState();
    }

    public void onDeactivate() {
        this.stopPullUpAndRestore();
        this.resetState();
    }

    private void resetState() {
        this.phase = Phase.ARMED;
        this.phaseTicks = 0;
        this.glideStableTicks = 0;
        this.glideAttemptTicks = 0;
        this.savedPullTrigger = null;
        this.savedPullTarget = null;
        this.pullSettingsChanged = false;
    }

    @EventHandler
    private void onTick(TickEvent.Pre pre) {
        if (this.mc.player == null) {
            return;
        }
        ElytraCollectorModule elytraCollectorModule = (ElytraCollectorModule)Modules.get().get(ElytraCollectorModule.class);
        int n = this.rocketCount();
        if (this.phase == Phase.DONE) {
            if (elytraCollectorModule == null || !elytraCollectorModule.isActive()) {
                return;
            }
            this.phase = Phase.ARMED;
            this.phaseTicks = 0;
        }
        if (this.phase == Phase.ARMED) {
            if (elytraCollectorModule == null || !elytraCollectorModule.isActive()) {
                return;
            }
            if (n >= (Integer)this.threshold.get()) {
                return;
            }
            this.info("烟花仅剩 %d，低于阈值 %d，开始安全撤离。", new Object[]{n, this.threshold.get()});
            elytraCollectorModule.toggle();
            try {
                PathManagers.get().stop();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            double d = this.mc.player.getY();
            if (d >= (double)((Integer)this.targetY.get()).intValue()) {
                this.beginGlide("已处于撤离高度");
            } else if (n <= 0) {
                this.beginGlide("烟花已耗尽");
            } else {
                this.startPullUp(d);
            }
            return;
        }
        ++this.phaseTicks;
        if (this.phase == Phase.CLIMBING) {
            double d = this.mc.player.getY();
            if (d >= (double)((Integer)this.targetY.get()).intValue()) {
                this.beginGlide("已到达 Y=" + String.valueOf(this.targetY.get()));
                return;
            }
            if (n <= 0) {
                this.beginGlide("烟花在爬升途中耗尽");
                return;
            }
            if (this.phaseTicks > 900) {
                this.beginGlide("爬升超时");
            }
            return;
        }
        if (this.phase == Phase.GLIDING) {
            ++this.glideAttemptTicks;
            this.mc.player.setPitch(5.0f);
            if (this.mc.player.isGliding()) {
                ++this.glideStableTicks;
            } else {
                this.glideStableTicks = 0;
                if (!this.mc.player.isOnGround() && this.glideAttemptTicks % 5 == 0 && this.mc.player.networkHandler != null) {
                    try {
                        this.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
            }
            if (this.glideStableTicks >= (Integer)this.glideTicks.get()) {
                this.disconnectNow();
                return;
            }
            if (this.glideAttemptTicks > 200) {
                this.warning("滑翔确认超时，执行离线兜底。当前Y=%.1f", new Object[]{this.mc.player.getY()});
                this.disconnectNow();
            }
        }
    }

    private int rocketCount() {
        return InvUtils.find(itemStack -> itemStack.getItem() == Items.FIREWORK_ROCKET, (int)0, (int)35).count();
    }

    private void startPullUp(double d) {
        PullUp pullUp = (PullUp)Modules.get().get(PullUp.class);
        if (pullUp == null) {
            this.beginGlide("未找到PullUp模块");
            return;
        }
        try {
            Field field = PullUp.class.getDeclaredField("b");
            Field field2 = PullUp.class.getDeclaredField("c");
            field.setAccessible(true);
            field2.setAccessible(true);
            Setting setting = (Setting)field.get((Object)pullUp);
            Setting setting2 = (Setting)field2.get((Object)pullUp);
            this.savedPullTrigger = (Integer)setting.get();
            this.savedPullTarget = (Integer)setting2.get();
            int n = (int)Math.ceil(d) + 2;
            n = Math.max(30, Math.min((Integer)this.targetY.get(), n));
            setting.set((Object)n);
            setting2.set((Object)((Integer)this.targetY.get()));
            this.pullSettingsChanged = true;
            if (!pullUp.isActive()) {
                pullUp.toggle();
            }
            this.phase = Phase.CLIMBING;
            this.phaseTicks = 0;
            this.info("已停止找船，使用剩余烟花爬升：当前Y %.1f -> 目标Y %d。", new Object[]{d, this.targetY.get()});
        }
        catch (Throwable throwable) {
            this.warning("无法启动撤离爬升，转入滑翔离线保护：%s", new Object[]{throwable.getClass().getSimpleName()});
            this.beginGlide("PullUp配置失败");
        }
    }

    private void beginGlide(String string) {
        this.stopPullUpAndRestore();
        try {
            PathManagers.get().stop();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.mc.player.setPitch(5.0f);
        this.phase = Phase.GLIDING;
        this.phaseTicks = 0;
        this.glideStableTicks = 0;
        this.glideAttemptTicks = 0;
        this.info("%s，停止使用烟花并进入滑翔；连续确认 %d tick 后自动离线。", new Object[]{string, this.glideTicks.get()});
    }

    private void stopPullUpAndRestore() {
        PullUp pullUp = (PullUp)Modules.get().get(PullUp.class);
        if (pullUp == null) {
            return;
        }
        if (pullUp.isActive()) {
            pullUp.toggle();
        }
        if (this.pullSettingsChanged) {
            try {
                Field field = PullUp.class.getDeclaredField("b");
                Field field2 = PullUp.class.getDeclaredField("c");
                field.setAccessible(true);
                field2.setAccessible(true);
                Setting setting = (Setting)field.get((Object)pullUp);
                Setting setting2 = (Setting)field2.get((Object)pullUp);
                if (this.savedPullTrigger != null) {
                    setting.set((Object)this.savedPullTrigger);
                }
                if (this.savedPullTarget != null) {
                    setting2.set((Object)this.savedPullTarget);
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.pullSettingsChanged = false;
        }
    }

    private void disconnectNow() {
        if (this.phase == Phase.DONE || this.mc.player == null || this.mc.player.networkHandler == null) {
            return;
        }
        this.phase = Phase.DONE;
        this.info("安全撤离完成，正在自动离线。烟花剩余：%d", new Object[]{this.rocketCount()});
        this.mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket((Text)Text.literal((String)"Elytra Finder：烟花不足，已完成安全撤离。")));
    }

    private static enum Phase {
        ARMED,
        CLIMBING,
        GLIDING,
        DONE;

    }
}

