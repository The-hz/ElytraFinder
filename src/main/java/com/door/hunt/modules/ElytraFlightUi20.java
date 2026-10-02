/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import java.util.Iterator;

public final class ElytraFlightUi20 {
    private ElytraFlightUi20() {
    }

    public static void remove(Object object, Object object2) {
        Iterable iterable;
        block6: {
            block5: {
                if (!(object instanceof Iterable)) break block5;
                iterable = (Iterable)object;
                if (object2 != null) break block6;
            }
            return;
        }
        try {
            Iterator iterator = iterable.iterator();
            while (iterator.hasNext()) {
                if (iterator.next() != object2) continue;
                iterator.remove();
                return;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

