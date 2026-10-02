/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.mixin;

import com.door.hunt.elytra.ElytraJump;
import com.door.hunt.utils.entity.LegalMovementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets={"net.minecraft.class_743"})
public abstract class InputMixin {
    @Inject(method={"tick"}, at={@At(value="RETURN")})
    private void onPostInputTick(CallbackInfo callbackInfo) {
        ElytraJump.onPostInputTickHook();
        LegalMovementManager.get().onPostInputTick();
    }
}

