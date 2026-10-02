/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.pathing;

import com.door.hunt.pathing.IPathManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BaritonePathManager
implements IPathManager {
    private final Object baritone;
    private final Method pathingBehaviorM;
    private final Method isPathingM;
    private final Method cancelEverythingM;
    private final Method customGoalProcessM;
    private final Method setGoalAndPathM;
    private final Method mineProcessM;
    private final Method mineM;
    private final Constructor<?> goalXZCtor;
    private final Constructor<?> goalGetToBlockCtor;

    public static boolean isAvailable() {
        try {
            Class.forName("baritone.api.BaritoneAPI");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public BaritonePathManager() throws ReflectiveOperationException {
        Class<?> pathingBehavior;
        if (!BaritonePathManager.isAvailable()) {
            throw new IllegalStateException("Baritone API \u4e0d\u53ef\u7528");
        }
        Class<?> api = Class.forName("baritone.api.BaritoneAPI");
        Object provider = api.getMethod("getProvider", new Class[0]).invoke(null, new Object[0]);
        this.baritone = provider.getClass().getMethod("getPrimaryBaritone", new Class[0]).invoke(provider, new Object[0]);
        try {
            pathingBehavior = Class.forName("baritone.api.behavior.IPathingBehavior");
        }
        catch (ClassNotFoundException e) {
            pathingBehavior = Class.forName("baritone.api.pathing.behavior.IPathingBehavior");
        }
        this.pathingBehaviorM = this.baritone.getClass().getMethod("getPathingBehavior", new Class[0]);
        this.isPathingM = pathingBehavior.getMethod("isPathing", new Class[0]);
        this.cancelEverythingM = pathingBehavior.getMethod("cancelEverything", new Class[0]);
        Class<?> customGoalProcess = Class.forName("baritone.api.process.ICustomGoalProcess");
        this.customGoalProcessM = this.baritone.getClass().getMethod("getCustomGoalProcess", new Class[0]);
        this.setGoalAndPathM = customGoalProcess.getMethod("setGoalAndPath", Class.forName("baritone.api.pathing.goals.Goal"));
        Class<?> mineProcess = Class.forName("baritone.api.process.IMineProcess");
        this.mineProcessM = this.baritone.getClass().getMethod("getMineProcess", new Class[0]);
        this.mineM = mineProcess.getMethod("mine", Block[].class);
        this.goalXZCtor = Class.forName("baritone.api.pathing.goals.GoalXZ").getConstructor(Integer.TYPE, Integer.TYPE);
        this.goalGetToBlockCtor = Class.forName("baritone.api.pathing.goals.GoalGetToBlock").getConstructor(BlockPos.class);
    }

    @Override
    public boolean isPathing() {
        try {
            return (Boolean)this.isPathingM.invoke(this.pathingBehaviorM.invoke(this.baritone, new Object[0]), new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            return false;
        }
    }

    @Override
    public void stop() {
        try {
            this.cancelEverythingM.invoke(this.pathingBehaviorM.invoke(this.baritone, new Object[0]), new Object[0]);
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            // empty catch block
        }
    }

    @Override
    public void moveTo(BlockPos pos, boolean ignoreY) {
        try {
            Object goal = ignoreY ? this.goalXZCtor.newInstance(pos.getX(), pos.getZ()) : this.goalGetToBlockCtor.newInstance(pos);
            this.setGoalAndPathM.invoke(this.customGoalProcessM.invoke(this.baritone, new Object[0]), goal);
        }
        catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mine(Block ... blocks) {
        try {
            this.mineM.invoke(this.mineProcessM.invoke(this.baritone, new Object[0]), new Object[]{blocks});
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            // empty catch block
        }
    }
}

