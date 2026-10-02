/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.utils.entity;

import net.minecraft.util.math.Vec3d;

public interface Predictor {
    public Vec3d getKnownDeltaMovement();

    public Vec3d predict(int var1, int var2, int var3);
}

