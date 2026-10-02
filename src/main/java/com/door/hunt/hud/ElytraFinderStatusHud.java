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
    public static final HudElementInfo<ElytraFinderStatusHud> INFO = new HudElementInfo(AddonTemplate.HUD_GROUP, "elytra-finder-status", "\u627e\u9798\u7fc5\u72b6\u6001\uff08\u65e7\uff09", "\u517c\u5bb9\u65e7\u914d\u7f6e\uff1b\u5b9e\u9645\u72b6\u6001 HUD \u73b0\u5728\u7531\u63d2\u4ef6\u81ea\u8eab\u8bbe\u7f6e\u76f4\u63a5\u6e32\u67d3\u3002", ElytraFinderStatusHud::new);

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

