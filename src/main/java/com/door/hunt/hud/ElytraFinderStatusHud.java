/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.hud;

import com.door.hunt.AddonTemplate;
import com.door.hunt.hud.ElytraStatusOverlay24;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;

public final class ElytraFinderStatusHud
extends HudElement {
    public static final HudElementInfo<ElytraFinderStatusHud> INFO = new HudElementInfo(AddonTemplate.HUD_GROUP, "elytra-finder-status", "找鞘翅状态（旧）", "兼容旧配置；实际状态 HUD 现在由插件自身设置直接渲染。", ElytraFinderStatusHud::new);

    public ElytraFinderStatusHud() {
        super(INFO);
    }

    public static void attach(Object object) {
        ElytraStatusOverlay24.install(object);
    }

    public static void ensureAdded() {
    }

    public void render(HudRenderer hudRenderer) {
        this.setSize(0.0, 0.0);
    }
}

