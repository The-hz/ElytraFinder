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
    private final SettingGroup sgGeneral;
    private final Setting<Integer> protectTime;
    private final Setting<Integer> duraLimit;
    private int gildingTick;

    public UnbreakableElytra() {
        super(AddonTemplate.CATEGORY, "鞘翅耐久保护", (String)"无限耐久鞘翅：滑翔期间周期检查胸甲鞘翅耐久，低于阈值自动换背包里的新鞘翅。");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.protectTime = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("保护周期")).description((String)"无限耐久检查周期 (tick)。")).defaultValue(16)).range(1, 200).build());
        this.duraLimit = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("耐久阈值")).description((String)"鞘翅耐久低于此值 (剩余耐久) 时切换。鞘翅总耐久 432。")).defaultValue(100)).range(1, 431).build());
        this.gildingTick = 0;
    }

    public void onActivate() {
        this.gildingTick = 0;
    }

    @EventHandler
    private void onTickPre(TickEvent.Pre event) {
        if (this.mc.player == null) {
            return;
        }
        if (!this.mc.player.isGliding()) {
            this.gildingTick = 0;
            return;
        }
        ++this.gildingTick;
        if (this.gildingTick % (Integer)this.protectTime.get() != 0) {
            return;
        }
        ItemStack chest = this.mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (!chest.isOf(Items.ELYTRA)) {
            return;
        }
        int damage = chest.getDamage();
        int maxDamage = chest.getMaxDamage();
        if (maxDamage - damage >= (Integer)this.duraLimit.get()) {
            return;
        }
        int elytraSlot = this.findElytra();
        if (elytraSlot != -1) {
            InvUtils.move().from(elytraSlot).toArmor(2);
            this.info("无限耐久: 已换上背包里的新鞘翅 (槽 " + elytraSlot + ")。", new Object[0]);
        }
    }

    private int findElytra() {
        FindItemResult result = InvUtils.find((Item[])new Item[]{Items.ELYTRA});
        if (result.found() && !result.isArmor()) {
            return result.slot();
        }
        return -1;
    }

    

    
}

