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
    private final SettingGroup a;
    private final Setting<Integer> b;
    private final Setting<Integer> c;
    private final Setting<Double> d;
    private final Setting<Double> e;
    private boolean f;
    private int g;
    private int h;
    static Object j;
    static Object k;
    static Object i;
    static Object l;
    static Object m;
    static Object n;
    static Object o;
    static Object p;

    public PullUp() {
        super(AddonTemplate.CATEGORY, "\u7d27\u6025\u62c9\u5347", (String)"\u4f4e\u4e8e\u8bbe\u5b9a Y \u503c\u65f6\u65e0\u6761\u4ef6\u5f3a\u5236\u62c9\u5347 (\u62ac\u5934 + \u70df\u82b1)\uff0c\u5347\u5230\u5b89\u5168\u9ad8\u5ea6\u81ea\u52a8\u505c\u6b62.");
        this.a = this.settings.getDefaultGroup();
        this.b = this.a.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u5371\u9669 Y \u9ad8\u5ea6")).description((String)"\u5371\u9669\u9ad8\u5ea6\uff1a\u4f4e\u4e8e\u6b64 Y \u65f6\u65e0\u6761\u4ef6\u5f3a\u5236\u62c9\u5347 (\u91c7\u96c6\u5668\u964d\u843d/\u964d\u843d\u6062\u590d\u671f\u95f4\u9664\u5916).")).defaultValue(40)).range(30, 300).sliderRange(30, 260).build());
        this.c = this.a.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u76ee\u6807 Y \u9ad8\u5ea6")).description((String)"\u62c9\u5347\u76ee\u6807\u9ad8\u5ea6\uff1a\u8fbe\u5230\u6b64 Y \u540e\u505c\u6b62\u62c9\u5347.")).defaultValue(70)).range(30, 500).sliderRange(50, 500).build());
        this.d = this.a.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u62c9\u5347\u4fef\u4ef0\u89d2")).description((String)"\u62c9\u5347\u65f6\u62ac\u5934\u7684\u4fef\u4ef0\u89d2 (\u8d1f\u503c=\u62ac\u5934\uff1b\u9ed8\u8ba4\u5f57\u661f pitch40 \u89d2\u5ea6 37.72\u00b0).")).defaultValue(-37.72).min(-90.0).max(-10.0).sliderMin(-80.0).sliderMax(-20.0).build());
        this.e = this.a.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u70df\u82b1\u95f4\u9694")).description((String)"\u62c9\u5347\u671f\u95f4\u6bcf\u9694\u8fd9\u4e48\u591a\u79d2\u4f7f\u7528\u4e00\u4e2a\u70df\u82b1\u52a0\u901f (\u79d2).")).defaultValue(2.0).min(0.5).sliderRange(0.5, 10.0).build());
        this.f = false;
        this.g = 0;
        this.h = 0;
    }

    public void onActivate() {
        this.f = false;
        this.g = 0;
        this.h = 0;
    }

    public void onDeactivate() {
        this.f = false;
        this.mc.options.forwardKey.setPressed(false);
        ElytraCollectorModule collector = (ElytraCollectorModule)Modules.get().get(ElytraCollectorModule.class);
        if (collector != null) {
            collector.setPullUpSuppressed(false);
        }
    }

    @EventHandler
    private void a(TickEvent.Pre event) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        ++this.g;
        ElytraCollectorModule collector = (ElytraCollectorModule)Modules.get().get(ElytraCollectorModule.class);
        if (collector != null && collector.isActive() && collector.isLandingOrRecovering()) {
            if (this.f) {
                this.f = false;
                collector.setPullUpSuppressed(false);
            }
            return;
        }
        if (this.f) {
            if (this.mc.player.getY() >= (double)((Integer)this.c.get()).intValue()) {
                this.f = false;
                if (collector != null) {
                    collector.setPullUpSuppressed(false);
                }
                this.info((String)"\u5df2\u5347\u5230\u76ee\u6807\u9ad8\u5ea6\uff0c\u505c\u6b62\u62c9\u5347.", new Object[0]);
            }
        } else if (this.mc.player.getY() < (double)((Integer)this.b.get()).intValue()) {
            this.f = true;
            this.h = -((int)Math.max(1L, Math.round((Double)this.e.get() * 20.0)));
            this.info("\u5371\u9669\u9ad8\u5ea6: \u4f4e\u4e8e " + String.valueOf(this.b.get()) + "\uff0c\u5f3a\u5236\u62c9\u5347.", new Object[0]);
            if (collector != null) {
                collector.abortStorageForPullUp();
                collector.setPullUpSuppressed(true);
            }
            PathManagers.get().stop();
        }
        if (!this.f) {
            return;
        }
        this.b();
    }

    private void b() {
        this.mc.player.setPitch(((Double)this.d.get()).floatValue());
        this.mc.options.forwardKey.setPressed(true);
        if (!this.mc.player.isGliding()) {
            if (!this.mc.player.isOnGround()) {
                this.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
            return;
        }
        if ((long)(this.g - this.h) >= Math.max(1L, Math.round((Double)this.e.get() * 20.0))) {
            this.c();
            this.h = this.g;
        }
    }

    private void c() {
        FindItemResult fw = InvUtils.findInHotbar((Item[])new Item[]{Items.FIREWORK_ROCKET});
        if (!fw.found()) {
            FindItemResult inv = InvUtils.find((Item[])new Item[]{Items.FIREWORK_ROCKET});
            if (inv.found()) {
                InvUtils.move().from(inv.slot()).toHotbar(1);
                this.info((String)"\u7269\u54c1\u680f\u6ca1\u6709\u70df\u82b1\uff0c\u4ece\u80cc\u5305\u8c03\u53d6\u653e\u5230\u7b2c 2 \u4e2a\u69fd\u4f4d.", new Object[0]);
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

