/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.mixin;

import net.minecraft.client.input.Input;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={Input.class}, remap=false)
public interface InputAccessor {
    @Accessor(value="movementVector", remap=false)
    public void ying$setMovementVector(Vec2f var1);
}

