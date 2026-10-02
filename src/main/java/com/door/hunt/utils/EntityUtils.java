/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.utils;

import com.door.hunt.utils.PlayerInputUtils;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class EntityUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final double SQRT_SPEED = Math.sqrt(0.0825);

    public static void parseEntityWhiteList(String value, Set<EntityType<?>> collection) {
        collection.clear();
        try {
            for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
                if (!Pattern.matches(value, Registries.ENTITY_TYPE.getId(entityType).getPath())) continue;
                collection.add(entityType);
            }
            if (Pattern.matches(value, "animal")) {
                for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
                    if (entityType.getSpawnGroup() != SpawnGroup.CREATURE) continue;
                    collection.add(entityType);
                }
            }
            if (Pattern.matches(value, "monster")) {
                for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
                    if (entityType.getSpawnGroup() != SpawnGroup.MONSTER || entityType == EntityType.ZOMBIFIED_PIGLIN || entityType == EntityType.ENDERMAN) continue;
                    collection.add(entityType);
                }
            }
            for (SpawnGroup group : SpawnGroup.values()) {
                if (group == SpawnGroup.MONSTER || !Pattern.matches(value, group.getName())) continue;
                for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
                    if (entityType.getSpawnGroup() != group) continue;
                    collection.add(entityType);
                }
            }
            if (Pattern.matches(value, "living_entity")) {
                for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
                    if (entityType.getSpawnGroup() == SpawnGroup.MISC) continue;
                    collection.add(entityType);
                }
            }
            Iterator<EntityType<?>> iter = collection.iterator();
            while (iter.hasNext()) {
                EntityType<?> entityType = iter.next();
                if (Pattern.matches(value, Registries.ENTITY_TYPE.getId(entityType).getPath()) || !Pattern.matches(value, "!" + Registries.ENTITY_TYPE.getId(entityType).getPath())) continue;
                iter.remove();
            }
        }
        catch (Throwable valuePatternError) {
            collection.clear();
        }
    }

    public static Vec3d getEntityLookXZ(Entity entity) {
        float yaw = entity.getYaw();
        float pitch = entity.getPitch();
        float f = MathHelper.cos((double)(-yaw * ((float)Math.PI / 180) - (float)Math.PI));
        float g = MathHelper.sin((double)(-yaw * ((float)Math.PI / 180) - (float)Math.PI));
        float h = -MathHelper.cos((double)(-pitch * ((float)Math.PI / 180)));
        return new Vec3d((double)(g * h), 0.0, (double)(f * h));
    }

    public static void setEntityRotation(Entity entity, Vec3d vec) {
        vec = vec.normalize();
        entity.setPitch((float)Math.toDegrees(Math.asin(-vec.y)));
        entity.setYaw((float)Math.toDegrees(Math.atan2(-vec.x, vec.z)));
    }

    public static void setEntityYawSafe(Entity entity, Vec2f vec2f) {
        EntityUtils.setEntityYawSafe(entity, (float)Math.toDegrees(Math.atan2(-vec2f.x, vec2f.y)));
    }

    public static void setEntityRotationSafe(Entity entity, Vec3d vec) {
        vec = vec.normalize();
        EntityUtils.setEntityPitchSafe(entity, (float)Math.toDegrees(Math.asin(-vec.y)));
        float newYaw = (float)Math.toDegrees(Math.atan2(-vec.x, vec.z));
        EntityUtils.setEntityYawSafe(entity, newYaw);
    }

    public static float getSafeYaw(Entity entity, float newYaw) {
        return EntityUtils.getSafeYaw(entity.getYaw(), newYaw);
    }

    public static float getSafeYaw(float oldYaw, float newYaw) {
        return oldYaw + EntityUtils.getSafeYawDiff(oldYaw, newYaw);
    }

    public static float getSafeYawDiff(float oldYaw, float newYaw) {
        float diff = newYaw - oldYaw;
        return (diff % 360.0f + 720.0f + 180.0f) % 360.0f - 180.0f;
    }

    public static float getSafePitch(float newPitch) {
        return EntityUtils.normalizePitch(newPitch);
    }

    public static float normalizeYaw(float yaw) {
        if ((double)yaw > -1.0E-5 && (double)yaw < 360.00001) {
            return yaw;
        }
        return (yaw % 360.0f + 720.0f + 180.0f) % 360.0f - 180.0f;
    }

    public static float normalizePitch(float newPitch) {
        if ((double)newPitch < 90.00001 && (double)newPitch > -90.00001) {
            return newPitch;
        }
        return (newPitch % 180.0f + 720.0f + 90.0f) % 180.0f - 90.0f;
    }

    public static void setEntityYawSafe(Entity entity, float newYaw) {
        entity.setYaw(EntityUtils.getSafeYaw(entity, newYaw));
    }

    public static void setEntityPitchSafe(Entity entity, float newPitch) {
        entity.setPitch(EntityUtils.getSafePitch(newPitch));
    }

    public static Vec3d pitchYawToRotation(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180);
        float g = -yaw * ((float)Math.PI / 180);
        float h = MathHelper.cos((double)g);
        float i = MathHelper.sin((double)g);
        float j = MathHelper.cos((double)f);
        float k = MathHelper.sin((double)f);
        return new Vec3d((double)(i * j), (double)(-k), (double)(h * j));
    }

    public static Vec2f rotationToPitchYaw(Vec3d vec) {
        return new Vec2f(EntityUtils.rotationToPitch(vec), EntityUtils.rotationToYaw(vec));
    }

    public static Vec2f directionToPitchYaw(Direction direction) {
        return switch (direction) {
            case Direction.DOWN -> new Vec2f(89.9f, 0.0f);
            case Direction.UP -> new Vec2f(-89.9f, 0.0f);
            case Direction.NORTH -> new Vec2f(0.0f, 180.0f);
            case Direction.SOUTH -> new Vec2f(0.0f, 0.0f);
            case Direction.WEST -> new Vec2f(0.0f, 90.0f);
            case Direction.EAST -> new Vec2f(0.0f, -90.0f);
            default -> throw new IllegalArgumentException("Unknown direction: " + String.valueOf(direction));
        };
    }

    public static float rotationToYaw(Vec3d vec) {
        return (float)Math.toDegrees(Math.atan2(-vec.x, vec.z));
    }

    public static float rotationToYaw(Direction direction) {
        return switch (direction) {
            case Direction.SOUTH -> 0.0f;
            case Direction.WEST -> 90.0f;
            case Direction.NORTH -> 180.0f;
            case Direction.EAST -> -90.0f;
            default -> 0.0f;
        };
    }

    public static float rotationToPitch(Vec3d vec) {
        return (float)Math.toDegrees(Math.asin(-vec.y));
    }

    public static Direction pitchYawToDirection(Vec2f pitchYaw) {
        float pitch = pitchYaw.x;
        float yaw = pitchYaw.y;
        double radPitch = Math.toRadians(pitch);
        double radYaw = Math.toRadians(yaw);
        double cosPitch = Math.cos(radPitch);
        double sinPitch = Math.sin(radPitch);
        double cosYaw = Math.cos(radYaw);
        double sinYaw = Math.sin(radYaw);
        double x = -cosPitch * sinYaw;
        double y = -sinPitch;
        double z = cosPitch * cosYaw;
        double x2 = x * x;
        double y2 = y * y;
        double z2 = z * z;
        if (x2 > y2 && x2 > z2) {
            return x > 0.0 ? Direction.EAST : Direction.WEST;
        }
        if (y2 > x2 && y2 > z2) {
            return y > 0.0 ? Direction.UP : Direction.DOWN;
        }
        return z > 0.0 ? Direction.SOUTH : Direction.NORTH;
    }

    public static Direction yawToHorizontalDirection(float yaw) {
        float shifted = yaw + 45.0f;
        float norm = shifted % 360.0f;
        if (norm < 0.0f) {
            norm += 360.0f;
        }
        int quarter = (int)(norm / 90.0f);
        return switch (quarter) {
            case 0 -> Direction.SOUTH;
            case 1 -> Direction.WEST;
            case 2 -> Direction.NORTH;
            default -> Direction.EAST;
        };
    }

    public static int yawToXSgn(float yaw) {
        float norm = yaw % 360.0f;
        if (norm < 0.0f) {
            norm += 360.0f;
        }
        float THRESH = 0.001f;
        if (norm < 0.001f || Math.abs(norm - 180.0f) < 0.001f || Math.abs(norm - 360.0f) < 0.001f) {
            return 0;
        }
        return norm > 0.0f && norm < 180.0f ? -1 : 1;
    }

    public static int yawToZSgn(float yaw) {
        float norm = yaw % 360.0f;
        if (norm < 0.0f) {
            norm += 360.0f;
        }
        float THRESH = 0.001f;
        if (Math.abs(norm - 90.0f) < 0.001f || Math.abs(norm - 270.0f) < 0.001f) {
            return 0;
        }
        return norm > 90.0f && norm < 270.0f ? -1 : 1;
    }

    public static boolean isRotationDifferent(float lastPitch, float pitch, float lastYaw, float yaw) {
        return (double)Math.abs(pitch - lastPitch) > 0.01 || (double)Math.abs(EntityUtils.getSafeYawDiff(lastYaw, yaw)) > 0.01;
    }

    public static double getProjectileGravity(Item item) {
        if (item instanceof RangedWeaponItem) {
            return 0.05;
        }
        if (item instanceof ThrowablePotionItem) {
            return 0.4;
        }
        if (item instanceof FishingRodItem) {
            return 0.15;
        }
        if (item instanceof TridentItem) {
            return 0.015;
        }
        return 0.03;
    }

    public static RaycastContext.FluidHandling getFluidHandling(Item item) {
        if (item instanceof FishingRodItem) {
            return RaycastContext.FluidHandling.ANY;
        }
        return RaycastContext.FluidHandling.NONE;
    }

    public static Vec3d rotateVec(Vec3d vec, float pitch, float yaw) {
        Vec3d facing = vec.normalize();
        double len = vec.length();
        Vec2f py = EntityUtils.rotationToPitchYaw(facing);
        Vec3d rotated = EntityUtils.pitchYawToRotation(py.x + pitch, py.y + yaw);
        return rotated.normalize().multiply(len);
    }

    public static Vec3d lookCoordToAbsolutePos(Entity source, double x, double y, double z) {
        Vec2f vec2f = source.getRotationClient();
        Vec3d vec3d = source.getEntityPos();
        float f = MathHelper.cos((double)((vec2f.y + 90.0f) * ((float)Math.PI / 180)));
        float g = MathHelper.sin((double)((vec2f.y + 90.0f) * ((float)Math.PI / 180)));
        float h = MathHelper.cos((double)(-vec2f.x * ((float)Math.PI / 180)));
        float i = MathHelper.sin((double)(-vec2f.x * ((float)Math.PI / 180)));
        float j = MathHelper.cos((double)((-vec2f.x + 90.0f) * ((float)Math.PI / 180)));
        float k = MathHelper.sin((double)((-vec2f.x + 90.0f) * ((float)Math.PI / 180)));
        Vec3d vec3d2 = new Vec3d((double)(f * h), (double)i, (double)(g * h));
        Vec3d vec3d3 = new Vec3d((double)(f * j), (double)k, (double)(g * j));
        Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1.0);
        double d = vec3d2.x * z + vec3d3.x * y + vec3d4.x * x;
        double e = vec3d2.y * z + vec3d3.y * y + vec3d4.y * x;
        double l = vec3d2.z * z + vec3d3.z * y + vec3d4.z * x;
        return new Vec3d(vec3d.x + d, vec3d.y + e, vec3d.z + l);
    }

    public static Vec3d lookCoordToPos(float pitch, float yaw, double x, double y, double z) {
        Vec2f vec2f = new Vec2f(pitch, yaw);
        float f = MathHelper.cos((double)((vec2f.y + 90.0f) * ((float)Math.PI / 180)));
        float g = MathHelper.sin((double)((vec2f.y + 90.0f) * ((float)Math.PI / 180)));
        float h = MathHelper.cos((double)(-vec2f.x * ((float)Math.PI / 180)));
        float i = MathHelper.sin((double)(-vec2f.x * ((float)Math.PI / 180)));
        float j = MathHelper.cos((double)((-vec2f.x + 90.0f) * ((float)Math.PI / 180)));
        float k = MathHelper.sin((double)((-vec2f.x + 90.0f) * ((float)Math.PI / 180)));
        Vec3d vec3d2 = new Vec3d((double)(f * h), (double)i, (double)(g * h));
        Vec3d vec3d3 = new Vec3d((double)(f * j), (double)k, (double)(g * j));
        Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1.0);
        double d = vec3d2.x * z + vec3d3.x * y + vec3d4.x * x;
        double e = vec3d2.y * z + vec3d3.y * y + vec3d4.y * x;
        double l = vec3d2.z * z + vec3d3.z * y + vec3d4.z * x;
        return new Vec3d(d, e, l);
    }

    public static PlayerEntity getPlayerByName(String name) {
        return MinecraftClient.getInstance().world.getPlayers().stream().filter(m -> m.getNameForScoreboard().equals(name)).findFirst().orElse(null);
    }

    public static Stream<String> getWorldPlayerNames(boolean containSelf) {
        return EntityUtils.mc.world.getPlayers().stream().filter(i -> containSelf || i != EntityUtils.mc.player).map(PlayerEntity::getNameForScoreboard);
    }

    public static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        }
        Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply((double)speed);
        float f = MathHelper.sin((double)(yaw * ((float)Math.PI / 180)));
        float g = MathHelper.cos((double)(yaw * ((float)Math.PI / 180)));
        return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
    }

    public static Text getEntityDisplayable(Entity target) {
        Text text;
        if (target instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)target;
            text = Text.literal((String)player.getNameForScoreboard());
        } else {
            text = target.getDisplayName();
        }
        return text;
    }

    public static double sqrtSpeed(Vec3d vec) {
        return Math.sqrt(vec.x * vec.x + vec.z * vec.z);
    }

    public static Vec3d withStrafe(Vec3d self, double speed) {
        return EntityUtils.withStrafe(self, speed, 1.0);
    }

    public static Vec3d withStrafe(Vec3d self, double speed, double strength) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        PlayerInputUtils.Input input = PlayerInputUtils.of(player);
        float yaw = EntityUtils.getMovementDirectionOfInput(player.getYaw(), input);
        return EntityUtils.withStrafe(self, speed, strength, input, yaw);
    }

    public static Vec3d withStrafe(Vec3d self, double speed, double strength, PlayerInputUtils.Input input, float yaw) {
        if (input != null && !input.hasWASDMovement()) {
            return new Vec3d(0.0, self.y, 0.0);
        }
        double prevX = self.x * (1.0 - strength);
        double prevZ = self.z * (1.0 - strength);
        double useSpeed = speed * strength;
        double angle = Math.toRadians(yaw);
        double x = -Math.sin(angle) * useSpeed + prevX;
        double z = Math.cos(angle) * useSpeed + prevZ;
        return new Vec3d(x, self.y, z);
    }

    public static float getMovementDirectionOfInput(float facingYaw, PlayerInputUtils.Input input) {
        boolean forwards = input.forward() && !input.backward();
        boolean backwards = input.backward() && !input.forward();
        boolean left = input.left() && !input.right();
        boolean right = input.right() && !input.left();
        float actualYaw = facingYaw;
        float forward = 1.0f;
        if (backwards) {
            actualYaw += 180.0f;
            forward = -0.5f;
        } else if (forwards) {
            forward = 0.5f;
        }
        if (left) {
            actualYaw -= 90.0f * forward;
        }
        if (right) {
            actualYaw += 90.0f * forward;
        }
        return MathHelper.wrapDegrees((float)actualYaw);
    }

    public static double getEffectiveGravity(ClientPlayerEntity player) {
        boolean bl = player.getVelocity().y <= 0.0;
        return bl && player.hasStatusEffect(StatusEffects.SLOW_FALLING) ? Math.min(player.getFinalGravity(), 0.01) : player.getFinalGravity();
    }

    public static Vec3d calculateGlidingVelocity(ClientPlayerEntity player, Vec3d oldVelocity, Vec3d rotationVector, boolean hasGravity) {
        Vec3d look = rotationVector;
        float pitch = EntityUtils.rotationToPitch(rotationVector);
        float pitchRad = pitch * ((float)Math.PI / 180);
        double lookHorizLen = Math.sqrt(look.x * look.x + look.z * look.z);
        double initialHorizSpeed = oldVelocity.horizontalLength();
        double gravity = hasGravity ? EntityUtils.getEffectiveGravity(player) : 0.0;
        double cosPitchSq = MathHelper.square((double)Math.cos(pitchRad));
        double newY = oldVelocity.y + gravity * (cosPitchSq * 0.75 - 1.0);
        Vec3d vel = new Vec3d(oldVelocity.x, newY, oldVelocity.z);
        if (newY < 0.0 && lookHorizLen > 0.0) {
            double lift = newY * -0.1 * cosPitchSq;
            vel = vel.add(look.x * lift / lookHorizLen, lift, look.z * lift / lookHorizLen);
        }
        if (pitchRad < 0.0f && lookHorizLen > 0.0) {
            double dive = initialHorizSpeed * (double)(-MathHelper.sin((double)pitchRad)) * 0.04;
            vel = vel.add(-look.x * dive / lookHorizLen, dive * 3.2, -look.z * dive / lookHorizLen);
        }
        if (lookHorizLen > 0.0) {
            double targetScale = initialHorizSpeed / lookHorizLen;
            vel = vel.add((look.x * targetScale - vel.x) * 0.1, 0.0, (look.z * targetScale - vel.z) * 0.1);
        }
        return vel.multiply(0.99, 0.98, 0.99);
    }

    public static Vec3d simulateTravelInFluidVelocity(Vec3d velocity, PlayerInputUtils.Input input, boolean lastInWater, boolean lastInLava, boolean hasGravity) {
        if (lastInWater) {
            return EntityUtils.simulateTravelInWaterVelocity(velocity, input, hasGravity);
        }
        if (lastInLava) {
            return EntityUtils.simulateTravelInLavaVelocity(velocity, input, hasGravity);
        }
        return velocity;
    }

    private static Vec3d simulateTravelInWaterVelocity(Vec3d velocity, PlayerInputUtils.Input input, boolean hasGravity) {
        Vec3d movementInput = new Vec3d((double)input.sidewaysSpeed(), (double)input.upwardSpeed(), (double)input.forwardSpeed());
        boolean falling = velocity.y <= 0.0;
        double y = EntityUtils.mc.player.getY();
        double gravity = EntityUtils.getEffectiveGravity(EntityUtils.mc.player);
        float drag = EntityUtils.mc.player.isSprinting() ? 0.9f : 0.8f;
        float acceleration = 0.02f;
        float efficiency = (float)EntityUtils.mc.player.getAttributeValue(EntityAttributes.WATER_MOVEMENT_EFFICIENCY);
        if (!EntityUtils.mc.player.isOnGround()) {
            efficiency *= 0.5f;
        }
        if (efficiency > 0.0f) {
            drag += (0.54600006f - drag) * efficiency;
            acceleration += (EntityUtils.mc.player.getMovementSpeed() - acceleration) * efficiency;
        }
        if (EntityUtils.mc.player.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
            drag = 0.96f;
        }
        Vec3d nextVelocity = velocity.add(EntityUtils.movementInputToVelocity(movementInput, acceleration, EntityUtils.mc.player.getYaw()));
        if (EntityUtils.mc.player.horizontalCollision && EntityUtils.mc.player.isClimbing()) {
            nextVelocity = new Vec3d(nextVelocity.x, 0.2, nextVelocity.z);
        }
        nextVelocity = nextVelocity.multiply((double)drag, (double)0.8f, (double)drag);
        nextVelocity = EntityUtils.simulateApplyFluidMovingSpeed(gravity, falling, nextVelocity, hasGravity, EntityUtils.mc.player.isSprinting());
        return EntityUtils.simulateResetVerticalVelocityInFluid(nextVelocity, y);
    }

    private static Vec3d simulateTravelInLavaVelocity(Vec3d velocity, PlayerInputUtils.Input input, boolean hasGravity) {
        Vec3d movementInput = new Vec3d((double)input.sidewaysSpeed(), (double)input.upwardSpeed(), (double)input.forwardSpeed());
        boolean falling = velocity.y <= 0.0;
        double y = EntityUtils.mc.player.getY();
        double gravity = EntityUtils.getEffectiveGravity(EntityUtils.mc.player);
        Vec3d nextVelocity = velocity.add(EntityUtils.movementInputToVelocity(movementInput, 0.02f, EntityUtils.mc.player.getYaw()));
        if (EntityUtils.mc.player.getFluidHeight(FluidTags.LAVA) <= EntityUtils.mc.player.getSwimHeight()) {
            nextVelocity = nextVelocity.multiply(0.5, (double)0.8f, 0.5);
            nextVelocity = EntityUtils.simulateApplyFluidMovingSpeed(gravity, falling, nextVelocity, hasGravity, EntityUtils.mc.player.isSprinting());
        } else {
            nextVelocity = nextVelocity.multiply(0.5);
        }
        if (gravity != 0.0) {
            nextVelocity = nextVelocity.add(0.0, -gravity / 4.0, 0.0);
        }
        return EntityUtils.simulateResetVerticalVelocityInFluid(nextVelocity, y);
    }

    private static Vec3d simulateApplyFluidMovingSpeed(double gravity, boolean falling, Vec3d velocity, boolean hasGravity, boolean isSprinting) {
        if (gravity != 0.0 && hasGravity && !isSprinting) {
            double nextY = falling && Math.abs(velocity.y - 0.005) >= 0.003 && Math.abs(velocity.y - gravity / 16.0) < 0.003 ? -0.003 : velocity.y - gravity / 16.0;
            return new Vec3d(velocity.x, nextY, velocity.z);
        }
        return velocity;
    }

    private static Vec3d simulateResetVerticalVelocityInFluid(Vec3d velocity, double y) {
        if (EntityUtils.mc.player.horizontalCollision && EntityUtils.mc.player.doesNotCollide(velocity.x, velocity.y + (double)0.6f - EntityUtils.mc.player.getY() + y, velocity.z)) {
            return new Vec3d(velocity.x, (double)0.3f, velocity.z);
        }
        return velocity;
    }
}

