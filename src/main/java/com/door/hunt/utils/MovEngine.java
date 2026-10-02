/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class MovEngine {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Vec3d simulateMovement(Entity entity, Vec3d from, Vec3d delta) {
        Vec3d actual;
        Vec3d original = entity.getEntityPos();
        boolean hc = entity.horizontalCollision;
        boolean vc = entity.verticalCollision;
        boolean gc = entity.groundCollision;
        entity.setPosition(from);
        try {
            entity.move(MovementType.SELF, delta);
            actual = entity.getEntityPos().subtract(from);
        }
        finally {
            entity.setPosition(original);
            entity.horizontalCollision = hc;
            entity.verticalCollision = vc;
            entity.groundCollision = gc;
        }
        return actual;
    }

    public static boolean checkEnvironmentCollision(Entity entity, Vec3d pos, boolean useBoundingBox) {
        Box box = entity.getBoundingBox().offset(pos.subtract(entity.getEntityPos()));
        return !MovEngine.mc.world.isSpaceEmpty(entity, box);
    }
}

