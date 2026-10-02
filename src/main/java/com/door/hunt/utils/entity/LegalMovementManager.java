/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.utils.entity;

import com.door.hunt.utils.PlayerInputUtils;
import com.door.hunt.utils.entity.EntityMovementStatus;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class LegalMovementManager {
    public static final int PRIORITY_LOW;
    public static final int PRIORITY_COMMON;
    public static final int PRIORITY_HIGH;
    public static final int PRIORITY_HIGHEST;
    public static final int PRIORITY_MONITOR;
    private static final LegalMovementManager INSTANCE;
    private static final MinecraftClient mc;
    private final List<MovementModifier> modifiers = new ArrayList<MovementModifier>();
    private final List<MovementModifier> currentTick = new ArrayList<MovementModifier>();
    private final Deque<Pair<Float, Float>> importantRotation = new ArrayDeque<Pair<Float, Float>>();
    public EntityMovementStatus<ClientPlayerEntity> playerStatus;
    private boolean moveFix = false;
    private boolean resetPos = false;
    private boolean resetRot = false;

    public static LegalMovementManager get() {
        return INSTANCE;
    }

    public void addMovementModifier(MovementModifier m) {
        int index;
        int p = m.priority();
        for (index = 0; index < this.modifiers.size() && this.modifiers.get(index).priority() <= p; ++index) {
        }
        this.modifiers.add(index, m);
    }

    public void removeMovementModifier(MovementModifier m) {
        this.modifiers.remove(m);
    }

    public void onTickPre() {
        ClientPlayerEntity player = LegalMovementManager.mc.player;
        if (player == null) {
            return;
        }
        this.playerStatus = new EntityMovementStatus<ClientPlayerEntity>(player);
        this.currentTick.clear();
        this.importantRotation.clear();
        this.moveFix = false;
        this.resetPos = false;
        this.resetRot = false;
        for (MovementModifier m : this.modifiers) {
            this.currentTick.add(m);
            m.preTick();
            m.applyPreTickModify();
        }
    }

    public void onPostInputTick() {
        ClientPlayerEntity player = LegalMovementManager.mc.player;
        if (player == null) {
            return;
        }
        if (this.moveFix) {
            PlayerInputUtils.Input input = PlayerInputUtils.of(player);
            input = PlayerInputUtils.tryCorrectMovementInput(input, this.playerStatus.yaw, player.getYaw());
            input.applyInput(player);
        }
        for (MovementModifier m : this.currentTick) {
            m.applyAfterInputTick();
        }
    }

    public void onSendMovementPre() {
        for (MovementModifier m : this.currentTick) {
            m.applyBeforeMovementPacketModify();
        }
    }

    public void onTickPost() {
        for (MovementModifier m : this.currentTick) {
            if (m.postModify(true)) continue;
            this.modifiers.remove(m);
        }
        if (this.resetPos && this.playerStatus != null) {
            this.playerStatus.restorePos();
        }
        if (this.resetRot && this.playerStatus != null) {
            this.playerStatus.restoreRotation();
        }
        this.currentTick.clear();
    }

    public void pushImportantRotation(boolean hasPitch, boolean hasYaw) {
        if (!hasPitch && !hasYaw) {
            return;
        }
        this.importantRotation.addLast((Pair<Float, Float>)Pair.of((hasPitch ? Float.valueOf(LegalMovementManager.mc.player.getPitch()) : null), hasYaw ? Float.valueOf(LegalMovementManager.mc.player.getYaw()) : null));
    }

    public boolean hasImportantRotation() {
        return !this.importantRotation.isEmpty();
    }

    public boolean hasImportantPitch() {
        for (Pair<Float, Float> pair : this.importantRotation) {
            if (pair.getFirst() == null) continue;
            return true;
        }
        return false;
    }

    public boolean hasImportantYaw() {
        for (Pair<Float, Float> pair : this.importantRotation) {
            if (pair.getSecond() == null) continue;
            return true;
        }
        return false;
    }

    public void markForMoveFix() {
        this.moveFix = true;
    }

    public void markForResetPos() {
        this.resetPos = true;
    }

    public void markForResetRot() {
        this.resetRot = true;
    }

    static {
        PRIORITY_MONITOR = 0x7FFFFFFE;
        PRIORITY_HIGHEST = 10000000;
        PRIORITY_HIGH = 100000;
        PRIORITY_COMMON = 0;
        PRIORITY_LOW = -100000;
        INSTANCE = new LegalMovementManager();
        mc = MinecraftClient.getInstance();
    }

    public static interface MovementModifier {
        default public int priority() {
            return 0;
        }

        default public void preTick() {
        }

        default public void applyPreTickModify() {
        }

        default public void applyAfterInputTick() {
        }

        default public void applyBeforeTravelTick() {
        }

        default public void applyAfterTravelTick() {
        }

        default public void applyBeforeInputPacketModify() {
        }

        default public void applyBeforeMovementPacketModify() {
        }

        default public boolean postModify(boolean enabledThisTick) {
            return true;
        }
    }
}

