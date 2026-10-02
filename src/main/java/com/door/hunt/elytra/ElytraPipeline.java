/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.elytra;

import com.door.hunt.utils.TickCounter;
import com.door.hunt.utils.entity.LegalMovementManager;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;

public class ElytraPipeline {
    private static final ElytraPipeline INSTANCE = new ElytraPipeline();
    private static boolean subscribed = false;

    public static void ensureSubscribed() {
        if (!subscribed) {
            MeteorClient.EVENT_BUS.subscribe((Object)INSTANCE);
            subscribed = true;
        }
    }

    @EventHandler
    private void onTickPre(TickEvent.Pre event) {
        LegalMovementManager.get().onTickPre();
    }

    @EventHandler
    private void onSendPre(SendMovementPacketsEvent.Pre event) {
        LegalMovementManager.get().onSendMovementPre();
    }

    @EventHandler
    private void onTickPost(TickEvent.Post event) {
        ++TickCounter.tick;
        LegalMovementManager.get().onTickPost();
    }
}

