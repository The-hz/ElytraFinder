/*
 * Decompiled with CFR 0.152.
 */
package com.seedfinding.mccore.nbt;

import com.seedfinding.mccore.nbt.tag.NBTByte;
import com.seedfinding.mccore.nbt.tag.NBTByteArray;
import com.seedfinding.mccore.nbt.tag.NBTCompound;
import com.seedfinding.mccore.nbt.tag.NBTDouble;
import com.seedfinding.mccore.nbt.tag.NBTEnd;
import com.seedfinding.mccore.nbt.tag.NBTFloat;
import com.seedfinding.mccore.nbt.tag.NBTInt;
import com.seedfinding.mccore.nbt.tag.NBTIntArray;
import com.seedfinding.mccore.nbt.tag.NBTList;
import com.seedfinding.mccore.nbt.tag.NBTLong;
import com.seedfinding.mccore.nbt.tag.NBTLongArray;
import com.seedfinding.mccore.nbt.tag.NBTShort;
import com.seedfinding.mccore.nbt.tag.NBTString;
import com.seedfinding.mccore.nbt.tag.NBTTag;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NBTType {
    public static final Map<Byte, Supplier<? extends NBTTag<?>>> REGISTRY;
    public static final byte END;
    public static final byte BYTE;
    public static final byte SHORT;
    public static final byte INT;
    public static final byte LONG;
    public static final byte FLOAT;
    public static final byte DOUBLE;
    public static final byte BYTE_ARRAY;
    public static final byte STRING;
    public static final byte LIST;
    public static final byte COMPOUND;
    public static final byte INT_ARRAY;
    public static final byte LONG_ARRAY;
    private static final Map<Class<? extends NBTTag<?>>, Byte> TYPE_REGISTRY;

    public static <T extends NBTTag<?>> void register(Class<T> tagClass, int type, Supplier<T> supplier) {
        if (REGISTRY.containsKey((byte)type)) {
            throw new RuntimeException("Type " + type + " is already taken");
        }
        REGISTRY.put((byte)type, supplier);
        TYPE_REGISTRY.put(tagClass, (byte)type);
    }

    public static byte getTypeOf(Class<? extends NBTTag<?>> tagClass) {
        return TYPE_REGISTRY.getOrDefault(tagClass, (byte)-1);
    }

    public static NBTTag<?> createEmpty(byte type) {
        Supplier<? extends NBTTag<?>> supplier = REGISTRY.get(type);
        if (supplier == null) {
            throw new RuntimeException("Deserializing unregistered tag of type " + type);
        }
        return supplier.get();
    }

    static {
        LONG_ARRAY = (byte)12;
        INT_ARRAY = (byte)11;
        COMPOUND = (byte)10;
        LIST = (byte)9;
        STRING = (byte)8;
        BYTE_ARRAY = (byte)7;
        DOUBLE = (byte)6;
        FLOAT = (byte)5;
        LONG = (byte)4;
        INT = (byte)3;
        SHORT = (byte)2;
        BYTE = 1;
        END = 0;
        REGISTRY = new HashMap();
        TYPE_REGISTRY = new HashMap();
        NBTType.register(NBTEnd.class, 0, NBTEnd::new);
        NBTType.register(NBTByte.class, 1, NBTByte::new);
        NBTType.register(NBTShort.class, 2, NBTShort::new);
        NBTType.register(NBTInt.class, 3, NBTInt::new);
        NBTType.register(NBTLong.class, 4, NBTLong::new);
        NBTType.register(NBTFloat.class, 5, NBTFloat::new);
        NBTType.register(NBTDouble.class, 6, NBTDouble::new);
        NBTType.register(NBTByteArray.class, 7, NBTByteArray::new);
        NBTType.register(NBTString.class, 8, NBTString::new);
        NBTType.register(NBTList.class, 9, NBTList::new);
        NBTType.register(NBTCompound.class, 10, NBTCompound::new);
        NBTType.register(NBTIntArray.class, 11, NBTIntArray::new);
        NBTType.register(NBTLongArray.class, 12, NBTLongArray::new);
    }
}

