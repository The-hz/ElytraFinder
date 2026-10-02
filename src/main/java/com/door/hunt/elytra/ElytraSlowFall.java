/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.elytra;

import com.door.hunt.AddonTemplate;
import com.door.hunt.utils.EntityUtils;
import com.door.hunt.utils.TickCounter;
import com.door.hunt.utils.entity.LegalMovementManager;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.Entity;

public class ElytraSlowFall
extends Module
implements LegalMovementManager.MovementModifier {
    public static ElytraSlowFall INSTANCE;
    private final SettingGroup sgGeneral;
    private final Setting<Boolean> enable;

    public ElytraSlowFall() {
        super(AddonTemplate.CATEGORY, "鞘翅缓降", "鞘翅缓降：平视 + 交替翻转抵消水平速度。");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.enable = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("enable")).description("启用鞘翅缓降。")).defaultValue(false)).build());
        INSTANCE = this;
    }

    public void onActivate() {
        LegalMovementManager.get().addMovementModifier(this);
    }

    public void onDeactivate() {
        LegalMovementManager.get().removeMovementModifier(this);
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void applyPreTickModify() {
        if (this.mc.player == null) {
            return;
        }
        if (!((Boolean)this.enable.get()).booleanValue() || !this.mc.player.isGliding() || this.mc.player.isOnGround()) {
            return;
        }
        boolean rotateYaw = TickCounter.tick % 2 == 0;
        LegalMovementManager.get().pushImportantRotation(true, rotateYaw);
        EntityUtils.setEntityPitchSafe((Entity)this.mc.player, 0.0f);
        if (rotateYaw) {
            EntityUtils.setEntityYawSafe((Entity)this.mc.player, this.mc.player.getYaw() + 180.0f);
        }
        LegalMovementManager.get().markForResetRot();
    }
}

