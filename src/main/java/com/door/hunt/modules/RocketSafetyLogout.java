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
        super(AddonTemplate.CATEGORY, "\u70df\u82b1\u5b89\u5168\u64a4\u79bb", "\u627e\u9798\u7fc5\u65f6\u76d1\u63a7\u80cc\u5305+\u5feb\u6377\u680f\u70df\u82b1\u3002\u4f4e\u4e8e\u9608\u503c\u540e\u722c\u5347\u5230\u8bbe\u5b9a\u9ad8\u5ea6\uff0c\u786e\u8ba4\u6ed1\u7fd4\u5e76\u81ea\u52a8\u79bb\u7ebf\u3002");
        this.sg = this.settings.getDefaultGroup();
        this.threshold = this.sg.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u70df\u82b1\u64a4\u79bb\u9608\u503c")).description("\u4e3b\u80cc\u5305\u548c\u5feb\u6377\u680f\u70df\u82b1\u603b\u6570\u4f4e\u4e8e\u8be5\u503c\u65f6\uff0c\u505c\u6b62\u627e\u8239\u5e76\u6267\u884c\u5b89\u5168\u64a4\u79bb\u3002")).defaultValue(32)).range(10, 64).sliderRange(10, 64).build());
        this.targetY = this.sg.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u64a4\u79bb\u9ad8\u5ea6-Y")).description("\u70df\u82b1\u4e0d\u8db3\u540e\u4f7f\u7528\u5269\u4f59\u70df\u82b1\u722c\u5347\u5230\u8be5Y\u9ad8\u5ea6\u3002")).defaultValue(300)).range(100, 500).sliderRange(100, 500).build());
        this.glideTicks = this.sg.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u6ed1\u7fd4\u786e\u8ba4\u65f6\u95f4")).description("\u5230\u8fbe\u76ee\u6807\u9ad8\u5ea6\u540e\uff0c\u8fde\u7eed\u786e\u8ba4\u5904\u4e8e\u9798\u7fc5\u6ed1\u7fd4\u591a\u5c11tick\u518d\u81ea\u52a8\u79bb\u7ebf\u300220tick\u7ea61\u79d2\u3002")).defaultValue(20)).range(5, 100).sliderRange(5, 60).build());
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
            this.info("\u70df\u82b1\u4ec5\u5269 %d\uff0c\u4f4e\u4e8e\u9608\u503c %d\uff0c\u5f00\u59cb\u5b89\u5168\u64a4\u79bb\u3002", new Object[]{n, this.threshold.get()});
            elytraCollectorModule.toggle();
            try {
                PathManagers.get().stop();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            double d = this.mc.player.getY();
            if (d >= (double)((Integer)this.targetY.get()).intValue()) {
                this.beginGlide("\u5df2\u5904\u4e8e\u64a4\u79bb\u9ad8\u5ea6");
            } else if (n <= 0) {
                this.beginGlide("\u70df\u82b1\u5df2\u8017\u5c3d");
            } else {
                this.startPullUp(d);
            }
            return;
        }
        ++this.phaseTicks;
        if (this.phase == Phase.CLIMBING) {
            double d = this.mc.player.getY();
            if (d >= (double)((Integer)this.targetY.get()).intValue()) {
                this.beginGlide("\u5df2\u5230\u8fbe Y=" + String.valueOf(this.targetY.get()));
                return;
            }
            if (n <= 0) {
                this.beginGlide("\u70df\u82b1\u5728\u722c\u5347\u9014\u4e2d\u8017\u5c3d");
                return;
            }
            if (this.phaseTicks > 900) {
                this.beginGlide("\u722c\u5347\u8d85\u65f6");
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
                this.warning("\u6ed1\u7fd4\u786e\u8ba4\u8d85\u65f6\uff0c\u6267\u884c\u79bb\u7ebf\u515c\u5e95\u3002\u5f53\u524dY=%.1f", new Object[]{this.mc.player.getY()});
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
            this.beginGlide("\u672a\u627e\u5230PullUp\u6a21\u5757");
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
            this.info("\u5df2\u505c\u6b62\u627e\u8239\uff0c\u4f7f\u7528\u5269\u4f59\u70df\u82b1\u722c\u5347\uff1a\u5f53\u524dY %.1f -> \u76ee\u6807Y %d\u3002", new Object[]{d, this.targetY.get()});
        }
        catch (Throwable throwable) {
            this.warning("\u65e0\u6cd5\u542f\u52a8\u64a4\u79bb\u722c\u5347\uff0c\u8f6c\u5165\u6ed1\u7fd4\u79bb\u7ebf\u4fdd\u62a4\uff1a%s", new Object[]{throwable.getClass().getSimpleName()});
            this.beginGlide("PullUp\u914d\u7f6e\u5931\u8d25");
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
        this.info("%s\uff0c\u505c\u6b62\u4f7f\u7528\u70df\u82b1\u5e76\u8fdb\u5165\u6ed1\u7fd4\uff1b\u8fde\u7eed\u786e\u8ba4 %d tick \u540e\u81ea\u52a8\u79bb\u7ebf\u3002", new Object[]{string, this.glideTicks.get()});
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
        this.info("\u5b89\u5168\u64a4\u79bb\u5b8c\u6210\uff0c\u6b63\u5728\u81ea\u52a8\u79bb\u7ebf\u3002\u70df\u82b1\u5269\u4f59\uff1a%d", new Object[]{this.rocketCount()});
        this.mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket((Text)Text.literal((String)"Elytra Finder\uff1a\u70df\u82b1\u4e0d\u8db3\uff0c\u5df2\u5b8c\u6210\u5b89\u5168\u64a4\u79bb\u3002")));
    }

    private static enum Phase {
        ARMED,
        CLIMBING,
        GLIDING,
        DONE;

    }
}

