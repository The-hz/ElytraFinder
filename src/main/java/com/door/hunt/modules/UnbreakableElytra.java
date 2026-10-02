/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import com.door.hunt.AddonTemplate;
import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class UnbreakableElytra
extends Module {
    private final SettingGroup a;
    private final Setting<Integer> b;
    private final Setting<Integer> c;
    private int d;
    static Object f;
    static Object g;
    static Object e;
    static Object h;
    static Object i;
    static Object j;

    public UnbreakableElytra() {
        super(AddonTemplate.CATEGORY, "\u9798\u7fc5\u8010\u4e45\u4fdd\u62a4", (String)"\u65e0\u9650\u8010\u4e45\u9798\u7fc5\uff1a\u6ed1\u7fd4\u671f\u95f4\u5468\u671f\u68c0\u67e5\u80f8\u7532\u9798\u7fc5\u8010\u4e45\uff0c\u4f4e\u4e8e\u9608\u503c\u81ea\u52a8\u6362\u80cc\u5305\u91cc\u7684\u65b0\u9798\u7fc5\u3002");
        this.a = this.settings.getDefaultGroup();
        this.b = this.a.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u4fdd\u62a4\u5468\u671f")).description((String)"\u65e0\u9650\u8010\u4e45\u68c0\u67e5\u5468\u671f (tick)\u3002")).defaultValue(16)).range(1, 200).build());
        this.c = this.a.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u8010\u4e45\u9608\u503c")).description((String)"\u9798\u7fc5\u8010\u4e45\u4f4e\u4e8e\u6b64\u503c (\u5269\u4f59\u8010\u4e45) \u65f6\u5207\u6362\u3002\u9798\u7fc5\u603b\u8010\u4e45 432\u3002")).defaultValue(100)).range(1, 431).build());
        this.d = 0;
    }

    public void onActivate() {
        this.d = 0;
    }

    @EventHandler
    private void a(TickEvent.Pre event) {
        if (this.mc.player == null) {
            return;
        }
        if (!this.mc.player.isGliding()) {
            this.d = 0;
            return;
        }
        ++this.d;
        if (this.d % (Integer)this.b.get() != 0) {
            return;
        }
        ItemStack chest = this.mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (!chest.isOf(Items.ELYTRA)) {
            return;
        }
        int damage = chest.getDamage();
        int maxDamage = chest.getMaxDamage();
        if (maxDamage - damage >= (Integer)this.c.get()) {
            return;
        }
        int elytraSlot = this.b();
        if (elytraSlot != -1) {
            InvUtils.move().from(elytraSlot).toArmor(2);
            this.info("\u65e0\u9650\u8010\u4e45: \u5df2\u6362\u4e0a\u80cc\u5305\u91cc\u7684\u65b0\u9798\u7fc5 (\u69fd " + elytraSlot + ")\u3002", new Object[0]);
        }
    }

    private int b() {
        FindItemResult result = InvUtils.find((Item[])new Item[]{Items.ELYTRA});
        if (result.found() && !result.isArmor()) {
            return result.slot();
        }
        return -1;
    }

    

    
}

