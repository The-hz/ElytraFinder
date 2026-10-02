/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.pathing;

import meteordevelopment.meteorclient.utils.player.ChatUtils;
import com.door.hunt.pathing.BaritonePathManager;
import com.door.hunt.pathing.IPathManager;
import com.door.hunt.pathing.NopPathManager;

public class PathManagers {
    private static IPathManager instance = new NopPathManager();

    public static IPathManager get() {
        return instance;
    }

    static {
        if (BaritonePathManager.isAvailable()) {
            try {
                instance = new BaritonePathManager();
                ChatUtils.info("[Elytra Finder] Baritone \u5df2\u8fde\u63a5\uff0c\u9632\u5361\u4f4f\u5bfb\u8def\u53ef\u7528\u3002");
                ChatUtils.warning("[Elytra Finder] Baritone \u5df2\u8fde\u63a5\uff0c\u9632\u5361\u4f4f\u5bfb\u8def\u53ef\u7528\u3002");
            }
            catch (Throwable e) {
                ChatUtils.info("[Ying] Baritone \u521d\u59cb\u5316\u5931\u8d25: " + String.valueOf(e));
                ChatUtils.warning("[Ying] Baritone \u521d\u59cb\u5316\u5931\u8d25: " + String.valueOf(e));
            }
        } else {
            ChatUtils.info("[Elytra Finder] \u672a\u68c0\u6d4b\u5230 Baritone\uff0c\u9632\u5361\u4f4f\u5bfb\u8def\u4e0d\u53ef\u7528\u3002");
            ChatUtils.warning("[Elytra Finder] \u672a\u68c0\u6d4b\u5230 Baritone\uff0c\u9632\u5361\u4f4f\u5bfb\u8def\u4e0d\u53ef\u7528\u3002");
        }
    }
}

