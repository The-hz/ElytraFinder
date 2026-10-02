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
    private final SettingGroup a;
    private final Setting<Mode> b;
    private final Setting<Integer> c;
    private final Setting<Integer> d;
    private final Setting<Double> e;
    private final Setting<Double> f;
    private final Setting<Double> g;
    private final Setting<Boolean> h;
    private final Setting<Boolean> i;
    private final Setting<Double> j;

    public SearchControl() {
        super(AddonTemplate.CATEGORY, "搜索控制", (String)"搜索视角控制：以中心点为中心按矩形/圆形/螺旋轨迹自动转头巡逻.");
        this.a = this.settings.getDefaultGroup();
        this.b = this.a.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("模式")).description((String)"巡逻模式：矩形 / 圆形 / 螺旋.")).defaultValue(Mode.SPIRAL)).build());
        this.c = this.a.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("中心 X")).description((String)"巡逻中心 X 坐标 (方块).")).defaultValue(0)).build());
        this.d = this.a.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("中心 Z")).description((String)"巡逻中心 Z 坐标 (方块).")).defaultValue(0)).build());
        this.e = this.a.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("矩形范围")).description((String)"矩形巡逻边长 (方块).")).defaultValue(192.0).min(0.0).build());
        this.f = this.a.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("圆形范围")).description((String)"圆形巡逻半径 (方块).")).defaultValue(192.0).min(0.0).build());
        this.g = this.a.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("螺旋范围")).description((String)"螺旋巡逻螺距 (方块).")).defaultValue(32.0).min(0.0).build());
        this.h = this.a.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("按 WASD 中止")).description((String)"玩家有 WASD 输入时暂停巡逻.")).defaultValue(false)).build());
        this.i = this.a.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("仅飞行时启用")).description((String)"仅鞘翅滑翔时生效，非滑翔状态不转头.")).defaultValue(false)).build());
        this.j = this.a.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("最大距离")).description((String)"离中心超过此距离时停止巡逻 (方块).")).defaultValue(300000.0).min(0.0).build());
    }

    public void onActivate() {
    }

    public void onDeactivate() {
    }

    @EventHandler
    private void a(TickEvent.Pre event) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (((Boolean)this.h.get()).booleanValue() && PlayerInputUtils.of(this.mc.options).hasMovementControl()) {
            return;
        }
        if (((Boolean)this.i.get()).booleanValue() && !this.mc.player.isGliding()) {
            return;
        }
        Vec3d center = new Vec3d((double)((Integer)this.c.get()).intValue() + 0.5, this.mc.player.getY(), (double)((Integer)this.d.get()).intValue() + 0.5);
        if (this.mc.player.getEntityPos().squaredDistanceTo(center) > (Double)this.j.get() * (Double)this.j.get()) {
            return;
        }
        switch (((Mode)((Object)this.b.get())).ordinal()) {
            case 0: {
                this.b(center);
                break;
            }
            case 1: {
                this.c(center);
                break;
            }
            case 2: {
                this.d(center);
            }
        }
    }

    private void b(Vec3d center) {
        Vec3d current = this.mc.player.getEntityPos();
        Vec3d relativeCoord = current.subtract(center);
        double xzdp = relativeCoord.z - relativeCoord.x;
        double xzdn = relativeCoord.x + relativeCoord.z;
        float targetYaw = xzdn <= 0.0 ? (xzdp <= 0.0 ? -90.0f : 180.0f) : (xzdp <= (Double)this.e.get() ? 0.0f : 90.0f);
        this.mc.player.setYaw(targetYaw);
    }

    private void c(Vec3d center) {
        Vec3d current = this.mc.player.getEntityPos();
        Vec3d relativeCoord = current.subtract(center);
        double range = (Double)this.f.get();
        Vec3d center1 = new Vec3d(0.0, 0.0, range / 2.0);
        Vec3d center2 = new Vec3d(0.0, 0.0, -range / 2.0);
        Vec3d selectedCenter = relativeCoord.x > 0.0 ? center1 : center2;
        Vec3d deltaR = relativeCoord.subtract(selectedCenter);
        float yaw = EntityUtils.rotationToYaw(deltaR.normalize());
        float cos = (float)(this.mc.player.getVelocity().horizontalLength() / (2.0 * deltaR.horizontalLength()));
        float yawControl = yaw + 90.0f - cos;
        EntityUtils.setEntityYawSafe((Entity)this.mc.player, yawControl);
    }

    private void d(Vec3d center) {
        Vec3d current = this.mc.player.getEntityPos();
        Vec3d relativeCoord = current.subtract(center);
        double P = (Double)this.g.get();
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

