/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.elytra;

import com.door.hunt.AddonTemplate;
import com.door.hunt.pathing.PathManagers;
import com.door.hunt.utils.EntityUtils;
import com.door.hunt.utils.PlayerInputUtils;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ElytraJump
extends Module {
    public static ElytraJump INSTANCE;
    private final SettingGroup sgGeneral;
    private final Setting<Boolean> conditionalSprint;
    private final Setting<Double> pitch;
    private final Setting<Boolean> sneak;
    private final Setting<Double> groundHeight;
    private final Setting<Boolean> antiStuck;
    private final Setting<Integer> antiStuckInterval;
    private final Setting<Double> antiStuckOffset;
    private final Setting<Integer> antiStuckResumeTicks;
    private boolean workThisTick;
    private boolean lastFallFly;
    private float savedPitch;
    private boolean pitchModified;
    private final List<Vec3d> checkHistory;
    private int lastDirX;
    private int lastDirZ;
    private boolean baritonePaused;
    private int stuckTimeoutTick;
    private BlockPos lastTarget;
    private int arrivedWaitTick;
    private int intervalCounter;

    public ElytraJump() {
        super(AddonTemplate.CATEGORY, "\u9798\u7fc5\u8df3\u8dc3", "\u9798\u7fc5\u8df3\u8dc3\uff1a\u6a21\u5757\u5f00\u542f\u5373\u81ea\u52a8\u8d77\u8df3\u5e76\u5c55\u5f00\u9798\u7fc5 (\u9644\u9632\u5361\u4f4f\u5bfb\u8def)\u3002");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.conditionalSprint = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u6761\u4ef6\u75be\u8dd1")).description("\u6761\u4ef6\u75be\u8dd1 (\u9884\u7559)\u3002")).defaultValue(false)).build());
        this.pitch = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u4fef\u4ef0\u89d2")).description("\u8d77\u98de\u62ac\u5934\u4fef\u4ef0\u89d2\u3002")).defaultValue(75.0).min(0.0).max(90.0).build());
        this.sneak = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u6f5c\u884c")).description("\u8d77\u98de\u65f6\u6f5c\u884c\u3002")).defaultValue(false)).build());
        this.groundHeight = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u79bb\u5730\u9ad8\u5ea6")).description("\u811a\u4e0b\u591a\u5c11\u683c\u5185\u6709\u5730\u9762\u65f6\u624d\u5de5\u4f5c (\u907f\u514d\u9ad8\u7a7a\u8bef\u89e6\u53d1)\u3002")).defaultValue(3.0).min(1.0).sliderMax(20.0).build());
        this.antiStuck = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u9632\u5361\u4f4f")).description("\u9632\u6b62\u5361\u4f4f\uff1a\u68c0\u6d4b\u5230\u8fde\u7eed\u4e24\u6b21\u68c0\u6d4b\u5728\u540c\u4e00\u4f4d\u7f6e\u65f6\u7528 Baritone \u5bfb\u8def\u7ed5\u5f00\u3002")).defaultValue(false)).build());
        this.antiStuckInterval = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u9632\u5361\u68c0\u6d4b\u95f4\u9694")).description("\u9632\u5361\u4f4f\u68c0\u6d4b\u95f4\u9694 (tick)\u3002")).defaultValue(20)).range(2, 200).build());
        this.antiStuckOffset = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u9632\u5361\u4f4d\u79fb")).description("\u9632\u5361\u4f4f\u76ee\u6807\u70b9\u504f\u79fb (\u6cbf\u8fd0\u52a8\u65b9\u5411\uff0c\u65b9\u5757)\u3002")).defaultValue(4.0).min(1.0).sliderMax(30.0).build());
        this.antiStuckResumeTicks = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u9632\u5361\u6062\u590d\u5ef6\u8fdf")).description("\u89e6\u53d1\u540e\u7b49\u5f85\u591a\u5c11 tick \u6062\u590d\u9798\u7fc5\u8df3\u8dc3 (baritone \u5bfb\u8def\u671f\u95f4\u4fdd\u6301\u6682\u505c\uff0c\u5bfb\u8def\u901a\u5e38\u5728\u6b64\u65f6\u95f4\u5185\u5b8c\u6210)\u3002")).defaultValue(80)).range(10, 600).build());
        this.workThisTick = false;
        this.lastFallFly = false;
        this.savedPitch = 0.0f;
        this.pitchModified = false;
        this.checkHistory = new ArrayList<Vec3d>();
        this.lastDirX = 0;
        this.lastDirZ = 0;
        this.baritonePaused = false;
        this.stuckTimeoutTick = 0;
        this.lastTarget = null;
        this.arrivedWaitTick = -1;
        this.intervalCounter = 0;
        INSTANCE = this;
    }

    public void onActivate() {
        this.workThisTick = false;
        this.lastFallFly = false;
        this.pitchModified = false;
        this.baritonePaused = false;
        this.checkHistory.clear();
        this.intervalCounter = 0;
        this.info("\u9798\u7fc5\u8df3\u8dc3\u5df2\u542f\u7528\u3002", new Object[0]);
    }

    public void onDeactivate() {
        if (this.baritonePaused) {
            this.baritonePaused = false;
            PathManagers.get().stop();
        }
        if (this.pitchModified && this.mc.player != null) {
            this.mc.player.setPitch(this.savedPitch);
            this.pitchModified = false;
        }
        this.checkHistory.clear();
    }

    private boolean hasGroundWithin(double height) {
        Box box = this.mc.player.getBoundingBox().stretch(0.0, -height, 0.0);
        int minX = MathHelper.floor((double)box.minX);
        int maxX = MathHelper.ceil((double)box.maxX) - 1;
        int minY = MathHelper.floor((double)box.minY);
        int maxY = MathHelper.ceil((double)box.maxY) - 1;
        int minZ = MathHelper.floor((double)box.minZ);
        int maxZ = MathHelper.ceil((double)box.maxZ) - 1;
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    if (this.mc.world.getBlockState(new BlockPos(x, y, z)).isAir()) continue;
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    private void onTickPre(TickEvent.Pre event) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        this.antiStuckTick();
        boolean bl = this.workThisTick = !this.baritonePaused && this.hasGroundWithin((Double)this.groundHeight.get());
        if (this.workThisTick) {
            if (this.mc.player.isOnGround()) {
                this.mc.player.jump();
                this.mc.player.setSprinting(true);
            } else if (!this.mc.player.isGliding() && this.mc.player.checkGliding()) {
                this.mc.getNetworkHandler().sendPacket((Packet)new ClientCommandC2SPacket((Entity)this.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
        }
        if (this.workThisTick && (this.mc.player.isGliding() || this.lastFallFly)) {
            this.savedPitch = this.mc.player.getPitch();
            this.mc.player.setPitch((float)((Double)this.pitch.get()).doubleValue());
            this.pitchModified = true;
        } else {
            this.pitchModified = false;
        }
        this.lastFallFly = this.mc.player.isGliding();
    }

    @EventHandler
    private void onTickPost(TickEvent.Post event) {
        if (this.pitchModified && this.mc.player != null) {
            this.mc.player.setPitch(this.savedPitch);
            this.pitchModified = false;
        }
    }

    public static void onPostInputTickHook() {
        if (INSTANCE != null) {
            INSTANCE.applyInputInjection();
        }
    }

    private void applyInputInjection() {
        if (this.mc.player == null || !this.isActive()) {
            return;
        }
        if (this.baritonePaused) {
            PlayerInputUtils.Input re = PlayerInputUtils.of(this.mc.player);
            re.jump(false).applyInput(this.mc.player);
            this.lastFallFly = this.mc.player.isGliding();
            return;
        }
        if (!this.workThisTick) {
            this.lastFallFly = this.mc.player.isGliding();
            return;
        }
        PlayerInputUtils.Input re = PlayerInputUtils.of(this.mc.player);
        re.sprint(true).forward(true).sneak((Boolean)this.sneak.get()).applyInput(this.mc.player);
        if (this.mc.player.isOnGround()) {
            this.mc.player.setSprinting(true);
        }
        this.lastFallFly = this.mc.player.isGliding();
    }

    private void antiStuckTick() {
        int sz = 0;
        int sx = 0;
        if (!((Boolean)this.antiStuck.get()).booleanValue()) {
            return;
        }
        if (this.baritonePaused) {
            double dz;
            double dx;
            --this.stuckTimeoutTick;
            if (this.arrivedWaitTick >= 0) {
                ++this.arrivedWaitTick;
                if (this.arrivedWaitTick >= 10) {
                    this.baritonePaused = false;
                    this.lastTarget = null;
                    this.arrivedWaitTick = -1;
                    this.checkHistory.clear();
                    this.intervalCounter = 0;
                    this.info("\u9632\u5361\u4f4f: \u5df2\u5230\u8fbe\u5bfb\u8def\u5750\u6807\uff0c\u6062\u590d\u9798\u7fc5\u8df3\u8dc3\u3002", new Object[0]);
                }
            } else if (this.lastTarget != null && Math.hypot(dx = this.mc.player.getX() - ((double)this.lastTarget.getX() + 0.5), dz = this.mc.player.getZ() - ((double)this.lastTarget.getZ() + 0.5)) < 2.0) {
                this.arrivedWaitTick = 0;
                this.info("\u9632\u5361\u4f4f: \u5df2\u5230\u8fbe\u5bfb\u8def\u5750\u6807\uff0c10 tick \u540e\u6062\u590d\u9798\u7fc5\u8df3\u8dc3\u3002", new Object[0]);
            }
            if (this.stuckTimeoutTick <= 0) {
                this.baritonePaused = false;
                this.lastTarget = null;
                this.arrivedWaitTick = -1;
                this.checkHistory.clear();
                this.intervalCounter = 0;
                this.info("\u9632\u5361\u4f4f: \u5bfb\u8def\u8d85\u65f6\uff0c\u6062\u590d\u9798\u7fc5\u8df3\u8dc3\u3002", new Object[0]);
            }
            return;
        }
        ++this.intervalCounter;
        if (this.intervalCounter < (Integer)this.antiStuckInterval.get()) {
            return;
        }
        this.intervalCounter = 0;
        Vec3d pos = this.mc.player.getEntityPos();
        this.checkHistory.add(pos);
        while (this.checkHistory.size() > 3) {
            this.checkHistory.remove(0);
        }
        if (this.checkHistory.size() < 3) {
            return;
        }
        Vec3d c0 = this.checkHistory.get(0);
        Vec3d c1 = this.checkHistory.get(1);
        Vec3d c2 = this.checkHistory.get(2);
        double hDist = Math.hypot(c2.x - c1.x, c2.z - c1.z);
        if (hDist >= 1.0) {
            return;
        }
        double dx = c1.x - c0.x;
        double dz = c1.z - c0.z;
        int n = Math.abs(dx) >= 4.0 ? (dx > 0.0 ? 1 : -1) : (sx = 0);
        int n2 = Math.abs(dz) >= 4.0 ? (dz > 0.0 ? 1 : -1) : (sz = 0);
        if (sx == 0 && sz == 0) {
            sx = this.lastDirX;
            sz = this.lastDirZ;
            if (sx == 0 && sz == 0) {
                Direction d = EntityUtils.yawToHorizontalDirection(this.mc.player.getYaw());
                sx = d.getOffsetX();
                sz = d.getOffsetZ();
            }
        } else {
            this.lastDirX = sx;
            this.lastDirZ = sz;
        }
        double ox = (double)sx * (Double)this.antiStuckOffset.get();
        double oz = (double)sz * (Double)this.antiStuckOffset.get();
        BlockPos p = new BlockPos(MathHelper.floor((double)(c2.x + ox)), MathHelper.floor((double)c0.y), MathHelper.floor((double)(c2.z + oz)));
        this.baritonePaused = true;
        this.stuckTimeoutTick = (Integer)this.antiStuckResumeTicks.get();
        this.lastTarget = p;
        this.arrivedWaitTick = -1;
        if (this.mc.player.isGliding()) {
            this.mc.player.stopGliding();
        }
        this.info("\u9632\u5361\u4f4f: \u68c0\u6d4b\u5230\u5361\u4f4f\uff0c\u6682\u505c\u9798\u7fc5\u8df3\u8dc3\u5e76\u843d\u5730\uff0cBaritone \u5bfb\u8def\u5230 " + String.valueOf(p), new Object[0]);
        PathManagers.get().moveTo(p, false);
    }
}

