/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import com.seedfinding.mcbiome.source.BiomeSource;
import com.seedfinding.mcbiome.source.EndBiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.structure.EndCity;
import com.seedfinding.mcfeature.structure.generator.structure.EndCityGenerator;
import com.seedfinding.mcterrain.TerrainGenerator;
import com.seedfinding.mcterrain.terrain.EndTerrainGenerator;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public final class ElytraSearchFallback {
    private static final int MAX_EXPANDED_RANGE = 50000;

    private ElytraSearchFallback() {
    }

    public static void rebuildIfEmpty(Object object, List list, long l, int n, int n2, int n3) {
        if (list == null || !list.isEmpty() || object == null) {
            return;
        }
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        int n8 = 0;
        int n9 = n3;
        try {
            Class<?> clazz = object.getClass();
            Method method = ElytraSearchFallback.findMethod(clazz, "f", 2);
            Method method2 = ElytraSearchFallback.findMethod(clazz, "co", 1);
            Method method3 = ElytraSearchFallback.findMethod(clazz, "cn", 1);
            Field field = ElytraSearchFallback.findField(clazz, "ap");
            Set set = (Set)field.get(object);
            MCVersion mCVersion = MCVersion.v1_21;
            EndCity endCity = new EndCity(mCVersion);
            EndCityGenerator endCityGenerator = new EndCityGenerator(mCVersion);
            ChunkRand chunkRand = new ChunkRand();
            EndBiomeSource endBiomeSource = new EndBiomeSource(mCVersion, l);
            EndTerrainGenerator endTerrainGenerator = new EndTerrainGenerator(endBiomeSource);
            int n10 = endCity.getSpacing();
            int n11 = n10 * 16;
            int n12 = Math.floorDiv(n >> 4, n10);
            int n13 = Math.floorDiv(n2 >> 4, n10);
            int n14 = Math.max(1, n3 / n11 + 1);
            int n15 = Math.max(n14, 50000 / n11 + 1);
            int[] nArray = new int[5];
            ElytraSearchFallback.scanSquare(object, list, l, n12, n13, n14, endCity, endCityGenerator, chunkRand, endBiomeSource, endTerrainGenerator, method, method2, method3, set, nArray);
            n4 += nArray[0];
            n5 += nArray[1];
            n6 += nArray[2];
            n7 += nArray[3];
            n8 += nArray[4];
            if (list.isEmpty()) {
                for (int i = n14 + 1; i <= n15; ++i) {
                    int n16 = list.size();
                    int[] nArray2 = new int[5];
                    ElytraSearchFallback.scanRing(object, list, l, n12, n13, i, endCity, endCityGenerator, chunkRand, endBiomeSource, endTerrainGenerator, method, method2, method3, set, nArray2);
                    n4 += nArray2[0];
                    n5 += nArray2[1];
                    n6 += nArray2[2];
                    n7 += nArray2[3];
                    n8 += nArray2[4];
                    n9 = i * n11;
                    if (list.size() > n16) break;
                }
            }
            System.out.println("[Ying] 搜船修复回退(Test32): seed=" + l + ", 基准=(" + n + "," + n2 + "), 请求范围=" + n3 + ", 实际扩展范围=" + n9 + ", 有船=" + n4 + ", 可构造目标=" + n5 + ", 本次排除=" + n6 + ", 已访问排除=" + n7 + ", 恢复候选=" + list.size() + ", 单点异常=" + n8);
        }
        catch (Throwable throwable) {
            System.out.println("[Ying] 搜船修复回退(Test32)异常: " + String.valueOf(throwable));
        }
    }

    private static void scanSquare(Object object, List list, long l, int n, int n2, int n3, EndCity endCity, EndCityGenerator endCityGenerator, ChunkRand chunkRand, EndBiomeSource endBiomeSource, EndTerrainGenerator endTerrainGenerator, Method method, Method method2, Method method3, Set set, int[] nArray) {
        for (int i = -n3; i <= n3; ++i) {
            for (int j = -n3; j <= n3; ++j) {
                ElytraSearchFallback.scanCell(object, list, l, n + i, n2 + j, endCity, endCityGenerator, chunkRand, endBiomeSource, endTerrainGenerator, method, method2, method3, set, nArray);
            }
        }
    }

    private static void scanRing(Object object, List list, long l, int n, int n2, int n3, EndCity endCity, EndCityGenerator endCityGenerator, ChunkRand chunkRand, EndBiomeSource endBiomeSource, EndTerrainGenerator endTerrainGenerator, Method method, Method method2, Method method3, Set set, int[] nArray) {
        int n4;
        for (n4 = -n3; n4 <= n3; ++n4) {
            ElytraSearchFallback.scanCell(object, list, l, n + n4, n2 - n3, endCity, endCityGenerator, chunkRand, endBiomeSource, endTerrainGenerator, method, method2, method3, set, nArray);
            if (n3 == 0) continue;
            ElytraSearchFallback.scanCell(object, list, l, n + n4, n2 + n3, endCity, endCityGenerator, chunkRand, endBiomeSource, endTerrainGenerator, method, method2, method3, set, nArray);
        }
        for (n4 = -n3 + 1; n4 <= n3 - 1; ++n4) {
            ElytraSearchFallback.scanCell(object, list, l, n - n3, n2 + n4, endCity, endCityGenerator, chunkRand, endBiomeSource, endTerrainGenerator, method, method2, method3, set, nArray);
            if (n3 == 0) continue;
            ElytraSearchFallback.scanCell(object, list, l, n + n3, n2 + n4, endCity, endCityGenerator, chunkRand, endBiomeSource, endTerrainGenerator, method, method2, method3, set, nArray);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void scanCell(Object object, List list, long l, int n, int n2, EndCity endCity, EndCityGenerator endCityGenerator, ChunkRand chunkRand, EndBiomeSource endBiomeSource, EndTerrainGenerator endTerrainGenerator, Method method, Method method2, Method method3, Set set, int[] nArray) {
        CPos cPos = endCity.getInRegion(l, n, n2, chunkRand);
        if (cPos == null) {
            return;
        }
        if (!endCity.canSpawn(cPos, (BiomeSource)endBiomeSource)) {
            return;
        }
        if (!endCity.canGenerate(cPos, (TerrainGenerator)endTerrainGenerator)) {
            return;
        }
        try {
            endCityGenerator.generate(endTerrainGenerator, cPos.getX(), cPos.getZ(), chunkRand);
            if (!endCityGenerator.hasShip()) {
                return;
            }
            nArray[0] = nArray[0] + 1;
            Object object2 = method.invoke(object, endCityGenerator, cPos);
            if (object2 == null) {
                return;
            }
            nArray[1] = nArray[1] + 1;
            Field field = ElytraSearchFallback.findField(object2.getClass(), "d");
            Object object3 = field.get(object2);
            if (Boolean.TRUE.equals(method2.invoke(object, object3))) {
                nArray[2] = nArray[2] + 1;
                return;
            }
            Object object4 = method3.invoke(object, object3);
            if (set != null && set.contains(object4)) {
                nArray[3] = nArray[3] + 1;
                return;
            }
            list.add(object2);
        }
        catch (Throwable throwable) {
            nArray[4] = nArray[4] + 1;
        }
        finally {
            endCityGenerator.reset();
        }
    }

    private static Field findField(Class<?> clazz, String string) throws Exception {
        for (Class<?> clazz2 = clazz; clazz2 != null; clazz2 = clazz2.getSuperclass()) {
            try {
                Field field = clazz2.getDeclaredField(string);
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                continue;
            }
        }
        throw new NoSuchFieldException(string);
    }

    private static Method findMethod(Class<?> clazz, String string, int n) throws Exception {
        for (Class<?> clazz2 = clazz; clazz2 != null; clazz2 = clazz2.getSuperclass()) {
            for (Method method : clazz2.getDeclaredMethods()) {
                if (!method.getName().equals(string) || method.getParameterCount() != n) continue;
                method.setAccessible(true);
                return method;
            }
        }
        throw new NoSuchMethodException(string + "/" + n);
    }
}

