/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.utils;

import com.door.hunt.utils.EntityUtils;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class RaycastUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean raycastAnySolidBlock(Entity e, Vec3d from, Vec3d to) {
        BlockHitResult bResult = RaycastUtils.raycastSolidBlockResult(e, from, to);
        return bResult != null && bResult.getType() != HitResult.Type.MISS;
    }

    public static BlockHitResult raycastSolidBlockResult(Entity e, Vec3d from, Vec3d to) {
        return RaycastUtils.mc.world.raycast(new RaycastContext(from, to, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, e));
    }

    public static HitResult createEntityOnlyCrossHairResult(Entity camera, double entityInteractionRange, float tickDelta, Predicate<Entity> filter) {
        Box box;
        double d = entityInteractionRange;
        double e = MathHelper.square((double)d);
        Vec3d vec3d = camera.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = camera.getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        EntityHitResult entityHitResult = ProjectileUtil.raycast((Entity)camera, (Vec3d)vec3d, (Vec3d)vec3d3, (Box)(box = camera.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0)), entity -> !entity.isSpectator() && entity.canHit() && (filter == null || filter.test((Entity)entity)), (double)e);
        return entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(vec3d) < e ? RaycastUtils.ensureTargetInRange((HitResult)entityHitResult, vec3d, entityInteractionRange) : null;
    }

    private static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        Vec3d vec3d = hitResult.getPos();
        if (!vec3d.isInRange((Position)cameraPos, interactionRange)) {
            Vec3d vec3d2 = hitResult.getPos();
            Direction direction = Direction.getFacing((double)(vec3d2.x - cameraPos.x), (double)(vec3d2.y - cameraPos.y), (double)(vec3d2.z - cameraPos.z));
            return BlockHitResult.createMissed((Vec3d)vec3d2, (Direction)direction, (BlockPos)BlockPos.ofFloored((Position)vec3d2));
        }
        return hitResult;
    }

    public static boolean canRaycastHit(PlayerEntity player, float pitch, float yaw, Entity target) {
        return RaycastUtils.canRaycastHit(player, pitch, yaw, target, player.getEntityInteractionRange());
    }

    public static boolean canRaycastHit(PlayerEntity player, float pitch, float yaw, Entity target, double distance) {
        Vec3d vec3d = player.getEyePos();
        Vec3d look = EntityUtils.pitchYawToRotation(pitch, yaw);
        Vec3d raycast = look.normalize().multiply(distance);
        Box targetBox = target.getBoundingBox();
        return targetBox.raycast(vec3d, vec3d.add(raycast)).isPresent();
    }
}

