/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.utils.algorithms;

import com.door.hunt.utils.IndexEntry;
import java.util.ArrayList;
import java.util.List;

public class StateMachine {
    int state;
    final int initState;
    boolean currentEnd = false;
    final StateAction[] actions;
    final StateUpdater updater;
    final List<IndexEntry<StateUpdateListener>> listeners = new ArrayList<IndexEntry<StateUpdateListener>>();
    boolean stepping = false;

    public StateMachine(int initializeState, StateUpdater stateUpdater, StateAction ... actions) {
        if (initializeState < 0 || initializeState >= actions.length) {
            throw new IllegalArgumentException("initializeState out of range");
        }
        this.initState = initializeState;
        this.state = initializeState;
        this.updater = stateUpdater;
        this.actions = actions;
    }

    public int getState() {
        return this.state;
    }

    public void registerListener(int state, StateUpdateListener listener) {
        this.listeners.add(new IndexEntry<StateUpdateListener>(state, listener));
    }

    private void callUpdate(int from, int to) {
        if (from != to) {
            for (IndexEntry<StateUpdateListener> entry : this.listeners) {
                if (entry.index() == from) {
                    entry.val().onUpdate(false);
                }
                if (entry.index() != to) continue;
                entry.val().onUpdate(true);
            }
        }
    }

    public void setState(int state) {
        if (this.stepping) {
            throw new IllegalStateException("Set during state running");
        }
        this.setStateInternal(state);
    }

    private void setStateInternal(int state) {
        int oldState = this.state;
        this.state = state;
        this.callUpdate(oldState, state);
    }

    public void markForEndState() {
        this.currentEnd = true;
    }

    public void step() {
        this.stepping = true;
        try {
            this.currentEnd = false;
            this.setStateInternal(this.updater.update(this, this.state));
            for (int i = 0; i < this.actions.length && !this.currentEnd; ++i) {
                StateAction action = this.actions[this.state];
                this.setStateInternal(action.step(this));
            }
        }
        finally {
            this.stepping = false;
        }
    }

    public static interface StateUpdater {
        public int update(StateMachine var1, int var2);
    }

    public static interface StateAction {
        public int step(StateMachine var1);
    }

    public static interface StateUpdateListener {
        public void onUpdate(boolean var1);
    }
}

