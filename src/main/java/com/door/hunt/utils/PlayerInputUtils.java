/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.utils;

import com.door.hunt.mixin.InputAccessor;
import com.door.hunt.utils.EntityUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;

public class PlayerInputUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final Input EMPTY = new Input(false, false, false, false, false, false, false);

    public static Input of(ClientPlayerEntity player) {
        return PlayerInputUtils.of(player.input);
    }

    public static Input of(net.minecraft.client.input.Input input) {
        return new Input(input.playerInput.forward(), input.playerInput.backward(), input.playerInput.left(), input.playerInput.right(), input.playerInput.jump(), input.playerInput.sneak(), input.playerInput.sprint());
    }

    public static Input of(GameOptions options) {
        return new Input(options.forwardKey.isPressed(), options.backKey.isPressed(), options.leftKey.isPressed(), options.rightKey.isPressed(), options.jumpKey.isPressed(), options.sneakKey.isPressed(), options.sprintKey.isPressed());
    }

    public static Input tryCorrectMovementInput(Input input, float originalYaw, float currentYaw) {
        int sidewaySpeed;
        int forwardSpeed;
        float diff = EntityUtils.getSafeYawDiff(originalYaw, currentYaw);
        int movementForward = input.forwardSpeed();
        int movementSideways = input.sidewaysSpeed();
        if ((double)diff < 22.5 && (double)diff >= -22.5) {
            return input;
        }
        if ((double)diff < 67.5 && (double)diff >= 22.5) {
            forwardSpeed = movementForward - movementSideways;
            sidewaySpeed = movementForward + movementSideways;
        } else if ((double)diff >= 67.5 && diff < 112.5f) {
            forwardSpeed = -movementSideways;
            sidewaySpeed = movementForward;
        } else if (diff >= 112.5f && diff < 157.5f) {
            forwardSpeed = -movementForward - movementSideways;
            sidewaySpeed = movementForward - movementSideways;
        } else if (diff >= 157.5f || diff < -157.5f) {
            forwardSpeed = -movementForward;
            sidewaySpeed = -movementSideways;
        } else if (diff >= -157.5f && diff < -112.5f) {
            forwardSpeed = -movementForward + movementSideways;
            sidewaySpeed = -movementForward - movementSideways;
        } else if (diff >= -112.5f && diff < -67.5f) {
            forwardSpeed = movementSideways;
            sidewaySpeed = -movementForward;
        } else if (diff >= -67.5f && diff < -22.5f) {
            forwardSpeed = movementForward + movementSideways;
            sidewaySpeed = -movementForward + movementSideways;
        } else {
            return input;
        }
        return new Input(forwardSpeed > 0, forwardSpeed < 0, sidewaySpeed > 0, sidewaySpeed < 0, input.jump(), input.sneak(), input.sprint());
    }

    public static class Input {
        boolean forward;
        boolean backward;
        boolean left;
        boolean right;
        boolean jump;
        boolean sneak;
        boolean sprint;

        public Input(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
            this.forward = forward;
            this.backward = backward;
            this.left = left;
            this.right = right;
            this.jump = jump;
            this.sneak = sneak;
            this.sprint = sprint;
        }

        public Input(boolean forward, boolean backward, boolean left, boolean right) {
            this(forward, backward, left, right, false, false, false);
        }

        public Input forward(boolean v) {
            return new Input(v, this.backward, this.left, this.right, this.jump, this.sneak, this.sprint);
        }

        public Input backward(boolean v) {
            return new Input(this.forward, v, this.left, this.right, this.jump, this.sneak, this.sprint);
        }

        public Input left(boolean v) {
            return new Input(this.forward, this.backward, v, this.right, this.jump, this.sneak, this.sprint);
        }

        public Input right(boolean v) {
            return new Input(this.forward, this.backward, this.left, v, this.jump, this.sneak, this.sprint);
        }

        public Input jump(boolean v) {
            return new Input(this.forward, this.backward, this.left, this.right, v, this.sneak, this.sprint);
        }

        public Input sneak(boolean v) {
            return new Input(this.forward, this.backward, this.left, this.right, this.jump, v, this.sprint);
        }

        public Input sprint(boolean v) {
            return new Input(this.forward, this.backward, this.left, this.right, this.jump, this.sneak, v);
        }

        public boolean forward() {
            return this.forward;
        }

        public boolean backward() {
            return this.backward;
        }

        public boolean left() {
            return this.left;
        }

        public boolean right() {
            return this.right;
        }

        public boolean jump() {
            return this.jump;
        }

        public boolean sneak() {
            return this.sneak;
        }

        public boolean sprint() {
            return this.sprint;
        }

        public Input sendPlayerInputAsRiding() {
            mc.getNetworkHandler().sendPacket((Packet)new PlayerInputC2SPacket(new PlayerInput(this.forward, this.backward, this.left, this.right, this.jump, this.sneak, this.sprint)));
            return this;
        }

        public Input sendPlayerSneakUpdatePacket() {
            // MC 1.21+: sneak state is now part of PlayerInput inside PlayerMoveC2SPacket.
            // The dedicated ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY / RELEASE_SHIFT_KEY
            // constants no longer exist. Send a PlayerInputC2SPacket with the sneak bit set
            // (the closest equivalent to the old sneak-toggle packet).
            PlayerInput input = new PlayerInput(false, false, false, false, false, this.sneak, false);
            mc.getNetworkHandler().sendPacket((Packet)new PlayerInputC2SPacket(input));
            return this;
        }

        public int forwardSpeed() {
            return this.forward == this.backward ? 0 : (this.forward ? 1 : -1);
        }

        public int sidewaysSpeed() {
            return this.left == this.right ? 0 : (this.left ? 1 : -1);
        }

        public int upwardSpeed() {
            return this.jump == this.sneak ? 0 : (this.jump ? 1 : -1);
        }

        public Input applyInput(ClientPlayerEntity clientPlayerEntity) {
            net.minecraft.client.input.Input input = clientPlayerEntity.input;
            input.playerInput = new PlayerInput(this.forward, this.backward, this.left, this.right, this.jump, this.sneak, this.sprint);
            ((InputAccessor)input).ying$setMovementVector(new Vec2f((float)this.sidewaysSpeed(), (float)this.forwardSpeed()));
            clientPlayerEntity.setSprinting(this.sprint);
            return this;
        }

        public Input applyInput(GameOptions options) {
            options.forwardKey.setPressed(this.forward);
            options.backKey.setPressed(this.backward);
            options.leftKey.setPressed(this.left);
            options.rightKey.setPressed(this.right);
            options.jumpKey.setPressed(this.jump);
            options.sneakKey.setPressed(this.sneak);
            options.sprintKey.setPressed(this.sprint);
            return this;
        }

        public boolean hasMovement() {
            return this.forward || this.backward || this.left || this.right || this.jump;
        }

        public boolean hasWASDMovement() {
            return this.forward != this.backward || this.left != this.right;
        }

        public boolean hasMovementControl() {
            return this.forward || this.backward || this.left || this.right || this.jump || this.sneak;
        }

        public Input clone() {
            return new Input(this.forward, this.backward, this.left, this.right, this.jump, this.sneak, this.sprint);
        }

        public String toString() {
            return "Input{forward=" + this.forward + ", backward=" + this.backward + ", left=" + this.left + ", right=" + this.right + ", jump=" + this.jump + ", sneak=" + this.sneak + ", sprint=" + this.sprint + "}";
        }
    }
}

