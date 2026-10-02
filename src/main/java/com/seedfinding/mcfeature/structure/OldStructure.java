/*
 * Decompiled with CFR 0.152.
 */
package com.seedfinding.mcfeature.structure;

import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.structure.RegionStructure;
import com.seedfinding.mcfeature.structure.UniformStructure;

public abstract class OldStructure<T extends OldStructure<T>>
extends UniformStructure<T> {
    public OldStructure(RegionStructure.Config config, MCVersion version) {
        super(config, version);
    }

    public static String name() {
        return "old_structure";
    }

    public static class Config
    extends RegionStructure.Config {
        public static final int SPACING;
        public static final int SEPARATION;

        public Config(int salt) {
            super(32, 8, salt);
        }

        static {
            SEPARATION = 8;
            SPACING = 32;
        }
    }
}

