/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.utils.entity;

import com.door.hunt.utils.EntityUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class EntityMovementStatus<T extends Entity> {
    public T entity;
    public boolean onGround;
    public boolean horizontalCollision;
    public boolean verticalCollision;
    public boolean groundCollision;
    public boolean collidedSoftly;
    public Vec3d pos;
    public float pitch;
    public float yaw;
    public Vec3d vec;
    public float speed;
    public float distanceTraveled;
    public boolean sprinting;
    public boolean touchingWater;
    public boolean submergedInWater;
    public boolean inPowderSnow;

    public EntityMovementStatus(T entity) {
        this.entity = entity;
        this.onGround = entity.isOnGround();
        this.horizontalCollision = ((Entity)entity).horizontalCollision;
        this.verticalCollision = ((Entity)entity).verticalCollision;
        this.groundCollision = ((Entity)entity).groundCollision;
        this.pos = entity.getEntityPos();
        this.pitch = entity.getPitch();
        this.yaw = entity.getYaw();
        this.vec = entity.getVelocity();
        this.speed = ((Entity)entity).speed;
        this.distanceTraveled = ((Entity)entity).distanceTraveled;
        this.sprinting = entity.isSprinting();
        this.touchingWater = entity.isTouchingWater();
        this.collidedSoftly = ((Entity)entity).collidedSoftly;
        this.submergedInWater = entity.isSubmergedInWater();
        this.inPowderSnow = ((Entity)entity).inPowderSnow;
    }

    public void restore() {
        ((Entity)this.entity).horizontalCollision = this.horizontalCollision;
        ((Entity)this.entity).verticalCollision = this.verticalCollision;
        ((Entity)this.entity).groundCollision = this.groundCollision;
        ((Entity)this.entity).collidedSoftly = this.collidedSoftly;
        this.restorePosRot();
        this.restoreOnGround();
        this.entity.setVelocity(this.vec);
        ((Entity)this.entity).speed = this.speed;
        ((Entity)this.entity).distanceTraveled = this.distanceTraveled;
        this.entity.setSprinting(this.sprinting);
    }

    public void restoreOnGround() {
        this.entity.setOnGround(this.onGround);
    }

    public void restorePosRot() {
        this.restoreRotation();
        this.restorePos();
    }

    public void restoreRotation() {
        EntityUtils.setEntityPitchSafe(this.entity, this.pitch);
        EntityUtils.setEntityYawSafe(this.entity, this.yaw);
    }

    public void restorePos() {
        this.entity.setPosition(this.pos);
    }

    public Vec3d calculateLastMoveVelocity(int forward, int sideward) {
        T t = this.entity;
        if (t instanceof LivingEntity) {
            double d;
            LivingEntity livingEntity = (LivingEntity)t;
            Vec2f vec2f = new Vec2f((float)sideward, (float)forward).normalize();
            vec2f = this.applyMovementFactors(vec2f);
            double d2 = vec2f.x;
            T t2 = this.entity;
            if (t2 instanceof LivingEntity) {
                LivingEntity lv = (LivingEntity)t2;
                d = lv.upwardSpeed;
            } else {
                d = 0.0;
            }
            Vec3d vec3d2 = new Vec3d(d2, d, (double)vec2f.y);
            float f = this.entity.isOnGround() ? this.entity.getEntityWorld().getBlockState(this.entity.getVelocityAffectingPos()).getBlock().getSlipperiness() : 1.0f;
            float speed = livingEntity.getMovementSpeed();
            Vec3d more = EntityUtils.movementInputToVelocity(vec3d2, speed, this.yaw);
            return this.vec.add(more);
        }
        return this.entity.getVelocity();
    }

    private Vec2f applyMovementFactors(Vec2f vec2f) {
        if (vec2f.lengthSquared() == 0.0f) {
            return vec2f;
        }
        T t = this.entity;
        if (t instanceof ClientPlayerEntity) {
            ClientPlayerEntity p = (ClientPlayerEntity)t;
            vec2f = vec2f.multiply(0.98f);
            if (p.isUsingItem() && !p.hasVehicle()) {
                vec2f = vec2f.multiply(0.2f);
            }
            if (p.shouldSlowDown()) {
                float f = (float)p.getAttributeValue(EntityAttributes.SNEAKING_SPEED);
                vec2f = vec2f.multiply(f);
            }
            float f = vec2f.length();
            vec2f = vec2f.multiply(1.0f / f);
            float g = EntityMovementStatus.getDirectionalMovementSpeedMultiplier(vec2f);
            float h = Math.min(f * g, 1.0f);
            return vec2f.multiply(h);
        }
        return vec2f;
    }

    private static float getDirectionalMovementSpeedMultiplier(Vec2f vec) {
        float f = Math.abs(vec.x);
        float g = Math.abs(vec.y);
        float h = g > f ? f / g : g / f;
        return MathHelper.sqrt((float)(1.0f + MathHelper.square((float)h)));
    }
}

