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
                ChatUtils.info("[Elytra Finder] Baritone 已连接，防卡住寻路可用。");
                ChatUtils.warning("[Elytra Finder] Baritone 已连接，防卡住寻路可用。");
            }
            catch (Throwable e) {
                ChatUtils.info("[Ying] Baritone 初始化失败: " + String.valueOf(e));
                ChatUtils.warning("[Ying] Baritone 初始化失败: " + String.valueOf(e));
            }
        } else {
            ChatUtils.info("[Elytra Finder] 未检测到 Baritone，防卡住寻路不可用。");
            ChatUtils.warning("[Elytra Finder] 未检测到 Baritone，防卡住寻路不可用。");
        }
    }
}

