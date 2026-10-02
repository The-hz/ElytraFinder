/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.pathing;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public interface IPathManager {
    public boolean isPathing();

    public void stop();

    public void moveTo(BlockPos var1, boolean var2);

    public void mine(Block ... var1);
}

