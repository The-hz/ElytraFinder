/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.utils;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.MaceItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.tag.ItemTags;

public class VItem {
    private static final VItem INSTANCE = new VItem();

    public static VItem getInstance() {
        return INSTANCE;
    }

    public boolean canGlide(ItemStack stack) {
        return stack.isOf(Items.ELYTRA) && stack.getDamage() < stack.getMaxDamage() - 1;
    }

    public boolean isSpear(ItemStack stack) {
        if (stack.isEmpty() || !stack.isIn(ItemTags.SWORDS)) {
            return false;
        }
        NbtComponent nbt = (NbtComponent)stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbt != null && nbt.copyNbt().contains("ying_spear")) {
            return true;
        }
        String name = stack.getName().getString();
        return name.contains("Spear") || name.contains("\u77db");
    }

    public boolean isWeapon(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() instanceof MaceItem) {
            return true;
        }
        return stack.contains(DataComponentTypes.TOOL) && (stack.getItem() instanceof AxeItem || !stack.isIn(ItemTags.SWORDS));
    }

    public boolean isTool(ItemStack stack) {
        return !stack.isEmpty() && stack.contains(DataComponentTypes.TOOL);
    }

    public boolean isNotAttackingTool(ItemStack stack) {
        return !this.isTool(stack) || !this.isWeapon(stack);
    }

    public boolean isShield(ItemStack stack) {
        return stack.getItem() instanceof ShieldItem;
    }

    public boolean isAxe(ItemStack stack) {
        return stack.getItem() instanceof AxeItem;
    }

    public boolean isEatable(ItemStack stack) {
        return !stack.isEmpty() && stack.contains(DataComponentTypes.FOOD);
    }

    public Integer getAttackDurabilityCost(ItemStack stack) {
        Item item = stack.getItem();
        if (stack.isIn(ItemTags.SWORDS)) {
            return 2;
        }
        if (item instanceof AxeItem
                || item instanceof HoeItem
                || item instanceof ShovelItem
                || stack.isIn(ItemTags.PICKAXES)
                || item instanceof MaceItem
                || item instanceof TridentItem) {
            return 1;
        }
        return null;
    }
}

