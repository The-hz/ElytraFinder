/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import com.door.hunt.AddonTemplate;
import com.door.hunt.utils.EntityUtils;
import com.door.hunt.utils.PlayerInputUtils;
import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class SearchControl
extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Mode> mode;
    private final Setting<Integer> centerX;
    private final Setting<Integer> centerZ;
    private final Setting<Double> range;
    private final Setting<Double> range2;
    private final Setting<Double> range3;
    private final Setting<Boolean> stopWhenInput;
    private final Setting<Boolean> onlyFlying;
    private final Setting<Double> maxRange;

    public SearchControl() {
        super(AddonTemplate.CATEGORY, "搜索控制", (String)"搜索视角控制：以中心点为中心按矩形/圆形/螺旋轨迹自动转头巡逻.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.mode = this.sgGeneral.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("模式")).description((String)"巡逻模式：矩形 / 圆形 / 螺旋.")).defaultValue(Mode.SPIRAL)).build());
        this.centerX = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("中心 X")).description((String)"巡逻中心 X 坐标 (方块).")).defaultValue(0)).build());
        this.centerZ = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("中心 Z")).description((String)"巡逻中心 Z 坐标 (方块).")).defaultValue(0)).build());
        this.range = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("矩形范围")).description((String)"矩形巡逻边长 (方块).")).defaultValue(192.0).min(0.0).build());
        this.range2 = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("圆形范围")).description((String)"圆形巡逻半径 (方块).")).defaultValue(192.0).min(0.0).build());
        this.range3 = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("螺旋范围")).description((String)"螺旋巡逻螺距 (方块).")).defaultValue(32.0).min(0.0).build());
        this.stopWhenInput = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("按 WASD 中止")).description((String)"玩家有 WASD 输入时暂停巡逻.")).defaultValue(false)).build());
        this.onlyFlying = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("仅飞行时启用")).description((String)"仅鞘翅滑翔时生效，非滑翔状态不转头.")).defaultValue(false)).build());
        this.maxRange = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("最大距离")).description((String)"离中心超过此距离时停止巡逻 (方块).")).defaultValue(300000.0).min(0.0).build());
    }

    public void onActivate() {
    }

    public void onDeactivate() {
    }

    @EventHandler
    private void onTickPre(TickEvent.Pre event) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (((Boolean)this.stopWhenInput.get()).booleanValue() && PlayerInputUtils.of(this.mc.options).hasMovementControl()) {
            return;
        }
        if (((Boolean)this.onlyFlying.get()).booleanValue() && !this.mc.player.isGliding()) {
            return;
        }
        Vec3d center = new Vec3d((double)((Integer)this.centerX.get()).intValue() + 0.5, this.mc.player.getY(), (double)((Integer)this.centerZ.get()).intValue() + 0.5);
        if (this.mc.player.getEntityPos().squaredDistanceTo(center) > (Double)this.maxRange.get() * (Double)this.maxRange.get()) {
            return;
        }
        switch (((Mode)((Object)this.mode.get())).ordinal()) {
            case 0: {
                this.setYawSquare(center);
                break;
            }
            case 1: {
                this.setYawCircle(center);
                break;
            }
            case 2: {
                this.setYawSpiral(center);
            }
        }
    }

    private void setYawSquare(Vec3d center) {
        Vec3d current = this.mc.player.getEntityPos();
        Vec3d relativeCoord = current.subtract(center);
        double xzdp = relativeCoord.z - relativeCoord.x;
        double xzdn = relativeCoord.x + relativeCoord.z;
        float targetYaw = xzdn <= 0.0 ? (xzdp <= 0.0 ? -90.0f : 180.0f) : (xzdp <= (Double)this.range.get() ? 0.0f : 90.0f);
        this.mc.player.setYaw(targetYaw);
    }

    private void setYawCircle(Vec3d center) {
        Vec3d current = this.mc.player.getEntityPos();
        Vec3d relativeCoord = current.subtract(center);
        double range = (Double)this.range2.get();
        Vec3d center1 = new Vec3d(0.0, 0.0, range / 2.0);
        Vec3d center2 = new Vec3d(0.0, 0.0, -range / 2.0);
        Vec3d selectedCenter = relativeCoord.x > 0.0 ? center1 : center2;
        Vec3d deltaR = relativeCoord.subtract(selectedCenter);
        float yaw = EntityUtils.rotationToYaw(deltaR.normalize());
        float cos = (float)(this.mc.player.getVelocity().horizontalLength() / (2.0 * deltaR.horizontalLength()));
        float yawControl = yaw + 90.0f - cos;
        EntityUtils.setEntityYawSafe((Entity)this.mc.player, yawControl);
    }

    private void setYawSpiral(Vec3d center) {
        Vec3d current = this.mc.player.getEntityPos();
        Vec3d relativeCoord = current.subtract(center);
        double P = (Double)this.range3.get();
        double b = P / (Math.PI * 2);
        double r = relativeCoord.horizontalLength();
        float yawRadial = EntityUtils.rotationToYaw(relativeCoord.normalize());
        double phiRad = Math.atan2(r, b);
        float phiDeg = (float)Math.toDegrees(phiRad);
        float cos = (float)(this.mc.player.getVelocity().horizontalLength() / (2.0 * relativeCoord.horizontalLength()));
        float yawControl = yawRadial + phiDeg - cos;
        EntityUtils.setEntityYawSafe((Entity)this.mc.player, yawControl);
    }

    

    

    public static enum Mode {
        RECT,
        CIRCLE,
        SPIRAL;
    }
}

