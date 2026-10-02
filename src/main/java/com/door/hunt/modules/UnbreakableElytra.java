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

    public UnbreakableElytra() {
        super(AddonTemplate.CATEGORY, "鞘翅耐久保护", (String)"无限耐久鞘翅：滑翔期间周期检查胸甲鞘翅耐久，低于阈值自动换背包里的新鞘翅。");
        this.a = this.settings.getDefaultGroup();
        this.b = this.a.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("保护周期")).description((String)"无限耐久检查周期 (tick)。")).defaultValue(16)).range(1, 200).build());
        this.c = this.a.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("耐久阈值")).description((String)"鞘翅耐久低于此值 (剩余耐久) 时切换。鞘翅总耐久 432。")).defaultValue(100)).range(1, 431).build());
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
            this.info("无限耐久: 已换上背包里的新鞘翅 (槽 " + elytraSlot + ")。", new Object[0]);
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

