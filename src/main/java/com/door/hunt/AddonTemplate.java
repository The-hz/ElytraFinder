/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt;

import com.door.hunt.commands.CommandExample;
import com.door.hunt.elytra.ElytraJump;
import com.door.hunt.elytra.ElytraPipeline;
import com.door.hunt.elytra.ElytraSlowFall;
import com.door.hunt.hud.ElytraFinderStatusHud;
import com.door.hunt.modules.BaritoneFix;
import com.door.hunt.modules.ElytraCollectorModule;
import com.door.hunt.modules.PullUp;
import com.door.hunt.modules.RocketSafetyLogout;
import com.door.hunt.modules.SearchControl;
import com.door.hunt.modules.UnbreakableElytra;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class AddonTemplate
extends MeteorAddon {
    public static final Category CATEGORY = new Category("Elytra Finder");
    public static final HudGroup HUD_GROUP = new HudGroup("Elytra Finder");

    public void onInitialize() {
        ElytraPipeline.ensureSubscribed();
        Modules.get().add((Module)new ElytraCollectorModule());
        Modules.get().add((Module)new PullUp());
        Modules.get().add((Module)new ElytraJump());
        Modules.get().add((Module)new ElytraSlowFall());
        Modules.get().add((Module)new UnbreakableElytra());
        Modules.get().add((Module)new SearchControl());
        Modules.get().add((Module)new BaritoneFix());
        Modules.get().add((Module)new RocketSafetyLogout());
        Commands.add((Command)new CommandExample());
        Hud.get().register(ElytraFinderStatusHud.INFO);
        ElytraFinderStatusHud.ensureAdded();
    }

    public void onRegisterCategories() {
        Modules.registerCategory((Category)CATEGORY);
    }

    public String getPackage() {
        return "com.door.hunt";
    }

    public GithubRepo getRepo() {
        return null;
    }
}

