/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.utils.entity;

import com.door.hunt.utils.MathUtils;
import com.door.hunt.utils.TickCounter;
import com.door.hunt.utils.entity.Predictor;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class PredictorImpl
implements Predictor {
    private static final MinecraftClient mc;
    private final Entity owner;
    private final Deque<KnownPosition> positions = new ArrayDeque<KnownPosition>();
    private static final int MAX_HISTORY;

    public PredictorImpl(Entity owner) {
        this.owner = owner;
    }

    public void tick() {
        while (this.positions.size() > 30) {
            this.positions.removeFirst();
        }
        if (PredictorImpl.mc.player == this.owner) {
            this.addRecord(new KnownPosition(this.owner.getEntityPos(), TickCounter.tick));
        }
    }

    public void onEntityPositionUpdate(Entity entity) {
        if (entity == this.owner) {
            this.addRecord(new KnownPosition(this.owner.getEntityPos(), TickCounter.tick));
        }
    }

    @Override
    public Vec3d getKnownDeltaMovement() {
        if (this.positions.size() < 2) {
            return Vec3d.ZERO;
        }
        Iterator<KnownPosition> it = this.positions.descendingIterator();
        KnownPosition newest = it.next();
        KnownPosition second = it.next();
        int dt = newest.tick() - second.tick();
        if (dt == 0) {
            return newest.vec3d().subtract(second.vec3d());
        }
        Vec3d displacement = newest.vec3d().subtract(second.vec3d());
        return displacement.multiply(1.0 / (double)dt);
    }

    @Override
    public Vec3d predict(int ticksLater, int method, int useTicksBefore) {
        if (ticksLater == 0) {
            return this.owner.getEntityPos();
        }
        int currentTick = TickCounter.tick;
        Vec3d currentPos = this.owner.getEntityPos();
        ArrayList<KnownPosition> histRecords = new ArrayList<KnownPosition>();
        KnownPosition lastKnown = null;
        boolean add = false;
        for (KnownPosition pos : this.positions) {
            if (pos.tick() >= currentTick - useTicksBefore) {
                add = true;
                if (lastKnown != null) {
                    histRecords.add(lastKnown);
                }
            }
            lastKnown = pos;
        }
        if (lastKnown != null && add) {
            histRecords.add(lastKnown);
        }
        if (histRecords.isEmpty()) {
            return currentPos;
        }
        int lastTick0 = ((KnownPosition)histRecords.get(histRecords.size() - 1)).tick();
        int firstTick0 = ((KnownPosition)histRecords.get(0)).tick();
        int startTick0 = currentTick - useTicksBefore;
        if (currentTick + ticksLater < firstTick0) {
            return ((KnownPosition)histRecords.get(0)).vec3d();
        }
        if (startTick0 >= lastTick0) {
            return currentPos;
        }
        if (firstTick0 > startTick0) {
            KnownPosition firstPosition = (KnownPosition)histRecords.get(0);
            histRecords.add(0, new KnownPosition(firstPosition.vec3d(), startTick0));
            firstTick0 = startTick0;
        }
        int usableTicks = lastTick0 - startTick0 + 1;
        int blankTicks = currentTick - lastTick0;
        if (usableTicks < 2) {
            return currentPos;
        }
        Vec3d[] history = new Vec3d[usableTicks];
        int currentIndex = 0;
        KnownPosition pos = (KnownPosition)histRecords.get(currentIndex);
        KnownPosition lastPos = null;
        block6: for (int i = 0; i < history.length; ++i) {
            int realTick = startTick0 + i;
            while (true) {
                if (pos.tick() == realTick) {
                    history[i] = pos.vec3d();
                    continue block6;
                }
                if (lastPos != null && lastPos.tick() < realTick && pos.tick() > realTick) {
                    history[i] = pos.vec3d().multiply((double)(pos.tick() - realTick)).add(lastPos.vec3d().multiply((double)(realTick - lastPos.tick()))).multiply(1.0 / (double)(pos.tick() - lastPos.tick()));
                    continue block6;
                }
                lastPos = pos;
                if (++currentIndex >= histRecords.size()) {
                    throw new RuntimeException("PredictorImpl: history index out of range");
                }
                pos = (KnownPosition)histRecords.get(currentIndex);
            }
        }
        int futureSteps = blankTicks + ticksLater;
        if (futureSteps <= 0) {
            return history[history.length - 1 + futureSteps];
        }
        switch (method) {
            case 1: {
                return MathUtils.linearPrediction(history, futureSteps);
            }
            case 2: {
                return MathUtils.quadraticPrediction(history, futureSteps);
            }
            case 3: {
                Vec3d[] ring = Arrays.copyOf(history, history.length);
                int currentIdx = history.length - 1;
                return new MathUtils.NVPredictor(ring, () -> currentIdx).compute(futureSteps);
            }
        }
        return currentPos;
    }

    public List<KnownPosition> getLastKnownPositions(int lastNumber) {
        int missing;
        if (lastNumber <= 0) {
            return Collections.emptyList();
        }
        List<KnownPosition> result = new ArrayList<KnownPosition>(this.positions);
        if (result.size() > lastNumber) {
            result = result.subList(result.size() - lastNumber, result.size());
        }
        if ((missing = lastNumber - result.size()) > 0) {
            Vec3d currentPos = this.owner.getEntityPos();
            int currentTick = TickCounter.tick;
            for (int i = 0; i < missing; ++i) {
                result.add(new KnownPosition(currentPos, currentTick));
            }
        }
        return result;
    }

    private void addRecord(KnownPosition record) {
        KnownPosition pos = this.positions.peekLast();
        if (!Objects.equals(pos, record)) {
            this.positions.add(record);
        }
    }

    static {
        MAX_HISTORY = 30;
        mc = MinecraftClient.getInstance();
    }

    public record KnownPosition(Vec3d vec3d, int tick) {
    }
}

