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
    static Object l;
    static Object m;
    static Object n;
    static Object o;
    static Object p;
    static Object k;
    static Object q;
    static Object r;

    public SearchControl() {
        super(AddonTemplate.CATEGORY, "\u641c\u7d22\u63a7\u5236", (String)"\u641c\u7d22\u89c6\u89d2\u63a7\u5236\uff1a\u4ee5\u4e2d\u5fc3\u70b9\u4e3a\u4e2d\u5fc3\u6309\u77e9\u5f62/\u5706\u5f62/\u87ba\u65cb\u8f68\u8ff9\u81ea\u52a8\u8f6c\u5934\u5de1\u903b.");
        this.a = this.settings.getDefaultGroup();
        this.b = this.a.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("\u6a21\u5f0f")).description((String)"\u5de1\u903b\u6a21\u5f0f\uff1a\u77e9\u5f62 / \u5706\u5f62 / \u87ba\u65cb.")).defaultValue(Mode.SPIRAL)).build());
        this.c = this.a.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u4e2d\u5fc3 X")).description((String)"\u5de1\u903b\u4e2d\u5fc3 X \u5750\u6807 (\u65b9\u5757).")).defaultValue(0)).build());
        this.d = this.a.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u4e2d\u5fc3 Z")).description((String)"\u5de1\u903b\u4e2d\u5fc3 Z \u5750\u6807 (\u65b9\u5757).")).defaultValue(0)).build());
        this.e = this.a.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u77e9\u5f62\u8303\u56f4")).description((String)"\u77e9\u5f62\u5de1\u903b\u8fb9\u957f (\u65b9\u5757).")).defaultValue(192.0).min(0.0).build());
        this.f = this.a.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u5706\u5f62\u8303\u56f4")).description((String)"\u5706\u5f62\u5de1\u903b\u534a\u5f84 (\u65b9\u5757).")).defaultValue(192.0).min(0.0).build());
        this.g = this.a.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u87ba\u65cb\u8303\u56f4")).description((String)"\u87ba\u65cb\u5de1\u903b\u87ba\u8ddd (\u65b9\u5757).")).defaultValue(32.0).min(0.0).build());
        this.h = this.a.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u6309 WASD \u4e2d\u6b62")).description((String)"\u73a9\u5bb6\u6709 WASD \u8f93\u5165\u65f6\u6682\u505c\u5de1\u903b.")).defaultValue(false)).build());
        this.i = this.a.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u4ec5\u98de\u884c\u65f6\u542f\u7528")).description((String)"\u4ec5\u9798\u7fc5\u6ed1\u7fd4\u65f6\u751f\u6548\uff0c\u975e\u6ed1\u7fd4\u72b6\u6001\u4e0d\u8f6c\u5934.")).defaultValue(false)).build());
        this.j = this.a.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u6700\u5927\u8ddd\u79bb")).description((String)"\u79bb\u4e2d\u5fc3\u8d85\u8fc7\u6b64\u8ddd\u79bb\u65f6\u505c\u6b62\u5de1\u903b (\u65b9\u5757).")).defaultValue(300000.0).min(0.0).build());
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

