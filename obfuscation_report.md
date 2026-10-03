# ElytraFinder 混淆分析报告

本文档列出所有被混淆的字段和方法名，以及自动推断的建议名。

## 使用方法

1. 用 Enigma 打开 `build/libs/addon-template-*.jar`
2. 加载 `elytrafinder.enigma.mapping` 作为初始映射
3. 参考本报告，对 `???` 标记的字段/方法手动标注

## 统计

- 总类数: **13**
- 总字段数: **141**
- 已确认名字段 (有 .name() 调用): **47**
- 基于类型推断的字段: **30**
- 通用占位名字段 (counterXX/flagXX/settingX): **64**
- 总方法数: **132**
- 已推断方法: **60**
- 待手动标注方法: **72**

---

## com.door.hunt.modules.BaritoneFix
**源文件**: `com/door/hunt/modules/BaritoneFix.java`

### ✅ 已确认字段 (基于 .name() 调用)

| 混淆名 | 建议名 | 类型 | Setting 显示名 |
|--------|--------|------|---------------|
| `d` | `defaultGroup` | `SettingGroup` | "(default)" |
| `e` | `enableBaritoneCommandProtection` | `Setting<Boolean>` | "启用 Baritone 命令保护" |
| `f` | `enableAutoJumpFix` | `Setting<Boolean>` | "启用自动跳跃修复" |
| `g` | `enableEmergencyLandingFix` | `Setting<Boolean>` | "启用紧急降落修复" |
| `h` | `emergencyLandingHeight` | `Setting<Double>` | "紧急降落高度" |
| `i` | `baritoneConditionalPause` | `Setting<Boolean>` | "Baritone 条件暂停" |
| `j` | `baritonePauseHotkey` | `Setting<Keybind>` | "Baritone 暂停热键" |
| `k` | `enableDimensionFix` | `Setting<Boolean>` | "启用维度修复" |

### 🟡 基于类型推断的字段

| 混淆名 | 建议名 | 类型 |
|--------|--------|------|
| `a` | `instance` | `BaritoneFix` |

### ❓ 需手动标注的字段 (counterXX/flagXX 占位名)

这些字段没有明显的语义线索，需要在 Enigma 中查看用法后手动命名。

| 混淆名 | 占位名 | 类型 |
|--------|--------|------|
| `b` | `flagB` | `boolean` |
| `c` | `flagC` | `boolean` |

### ✅ 已推断方法 (基于方法体内容)

| 混淆名 | 建议名 | 参数 | 返回类型 |
|--------|--------|------|---------|
| `a` | `logInfo` | `(TickEvent.Pre event)` | `void` |
| `b` | `logInfo` | `(SendMessageEvent event)` | `void` |

### ❓ 需手动标注的方法

| 混淆名 | 参数 | 返回类型 |
|--------|------|---------|
| `c` | `(ClientPlayerEntity p)` | `void` |
| `d` | `()` | `boolean` |

---

## com.door.hunt.modules.ElytraCollectorModule
**源文件**: `com/door/hunt/modules/ElytraCollectorModule.java`

### ✅ 已确认字段 (基于 .name() 调用)

| 混淆名 | 建议名 | 类型 | Setting 显示名 |
|--------|--------|------|---------------|
| `d` | `defaultGroup` | `SettingGroup` | "(default)" |
| `e` | `flightGroup` | `SettingGroup` | "Flight" |
| `f` | `seed` | `Setting<String>` | "种子" |
| `g` | `searchRange` | `Setting<Integer>` | "搜索范围" |
| `h` | `minHeight` | `Setting<Integer>` | "最低高度" |
| `i` | `maxHeight` | `Setting<Integer>` | "最高高度" |
| `j` | `climbHeight` | `Setting<Integer>` | "拉升高度" |
| `k` | `pitchSpeed` | `Setting<Double>` | "俯仰速度" |
| `l` | `yawSpeed` | `Setting<Double>` | "偏航速度" |
| `m` | `killAuraRange` | `Setting<Double>` | "Kill Aura 攻击距离" |
| `n` | `fireworkInterval` | `Setting<Double>` | "烟花间隔" |
| `o` | `visitedShips` | `Setting<List<String>>` | "已访问末地船" |
| `p` | `searchResults` | `Setting<List<String>>` | "搜索结果" |
| `q` | `start` | `Setting<Boolean>` | "开始" |
| `r` | `debug` | `Setting<Boolean>` | "调试" |
| `s` | `storageGroup` | `SettingGroup` | "Storage" |
| `t` | `storageEmptySlots` | `Setting<Integer>` | "清仓保留空槽" |
| `u` | `supplies` | `Setting<List<String>>` | "补给品" |
| `v` | `lowYLogout` | `Setting<Boolean>` | "低 Y 退出" |
| `w` | `lowYThreshold` | `Setting<Integer>` | "低 Y 阈值" |

### 🟡 基于类型推断的字段

| 混淆名 | 建议名 | 类型 |
|--------|--------|------|
| `a` | `mcVersion` | `MCVersion` |
| `ab` | `workerThread` | `Thread` |
| `ae` | `shipTarget` | `ShipTarget` |
| `af` | `shipWaypoints` | `ShipWaypoints` |
| `ai` | `timestampAI` | `long` |
| `al` | `blockPosX` | `BlockPos` |
| `ap` | `stringSet` | `Set<String>` |
| `ar` | `directionX` | `Direction` |
| `b` | `simplexNoise` | `SimplexNoiseSampler` |
| `bb` | `valueBB` | `double` |
| `bc` | `angleBC` | `float` |
| `bf` | `storagePhase` | `StoragePhase` |
| `bi` | `blockPosX` | `BlockPos` |
| `bj` | `blockPosX` | `BlockPos` |
| `bq` | `storagePhase` | `StoragePhase` |
| `bs` | `blockPosList` | `List<BlockPos>` |
| `bw` | `moveOps` | `ArrayDeque<MoveOp>` |
| `c` | `interpolatedNoise` | `InterpolatedNoiseSampler` |
| `ck` | `items` | `Set<Item>` |
| `cx` | `unknownCX` | `Object` |
| `x` | `state` | `State` |
| `y` | `collectStep` | `CollectStep` |
| `z` | `shipTargets` | `List<ShipTarget>` |

### ❓ 需手动标注的字段 (counterXX/flagXX 占位名)

这些字段没有明显的语义线索，需要在 Enigma 中查看用法后手动命名。

| 混淆名 | 占位名 | 类型 |
|--------|--------|------|
| `aa` | `counterAA` | `int` |
| `ac` | `flagAC` | `boolean` |
| `ad` | `counterAD` | `int` |
| `ag` | `counterAG` | `int` |
| `ah` | `counterAH` | `int` |
| `aj` | `flagAJ` | `boolean` |
| `ak` | `flagAK` | `boolean` |
| `am` | `counterAM` | `int` |
| `an` | `counterAN` | `int` |
| `ao` | `flagAO` | `boolean` |
| `aq` | `counterAQ` | `int` |
| `at` | `flagAT` | `boolean` |
| `au` | `flagAU` | `boolean` |
| `av` | `flagAV` | `boolean` |
| `aw` | `counterAW` | `int` |
| `ax` | `flagAX` | `boolean` |
| `ay` | `flagAY` | `boolean` |
| `az` | `counterAZ` | `int` |
| `ba` | `flagBA` | `boolean` |
| `bd` | `counterBD` | `int` |
| `be` | `counterBE` | `int` |
| `bg` | `counterBG` | `int` |
| `bh` | `counterBH` | `int` |
| `bk` | `flagBK` | `boolean` |
| `bl` | `counterBL` | `int` |
| `bm` | `counterBM` | `int` |
| `bn` | `counterBN` | `int` |
| `bo` | `flagBO` | `boolean` |
| `bp` | `flagBP` | `boolean` |
| `br` | `counterBR` | `int` |
| `bt` | `counterBT` | `int` |
| `bu` | `counterBU` | `int` |
| `bv` | `counterBV` | `int` |
| `bx` | `counterBX` | `int` |
| `by` | `counterBY` | `int` |
| `bz` | `counterBZ` | `int` |
| `ca` | `counterCA` | `int` |
| `cb` | `counterCB` | `int` |
| `cc` | `counterCC` | `int` |
| `cd` | `counterCD` | `int` |
| `ce` | `counterCE` | `int` |
| `cf` | `counterCF` | `int` |
| `cg` | `counterCG` | `int` |
| `ch` | `counterCH` | `int` |
| `ci` | `counterCI` | `int` |
| `cj` | `counterCJ` | `int` |
| `cl` | `counterCL` | `int` |
| `cm` | `counterCM` | `int` |
| `cn` | `counterCN` | `int` |
| `co` | `counterCO` | `int` |
| `cp` | `flagCP` | `boolean` |
| `cq` | `flagCQ` | `boolean` |
| `cr` | `counterCR` | `int` |
| `cs` | `counterCS` | `int` |
| `ct` | `counterCT` | `int` |
| `cu` | `flagCU` | `boolean` |
| `cv` | `flagCV` | `boolean` |
| `cw` | `flagCW` | `boolean` |

### ✅ 已推断方法 (基于方法体内容)

| 混淆名 | 建议名 | 参数 | 返回类型 |
|--------|--------|------|---------|
| `aa` | `logInfo` | `()` | `void` |
| `ab` | `logInfo` | `()` | `void` |
| `ac` | `logInfo` | `()` | `void` |
| `ad` | `logInfo` | `()` | `void` |
| `ae` | `logInfo` | `()` | `void` |
| `af` | `logInfo` | `()` | `void` |
| `ag` | `logInfo` | `()` | `void` |
| `an` | `checkAN` | `(BlockPos pos)` | `boolean` |
| `ao` | `findBlock` | `(BlockPos pos)` | `BlockPos` |
| `ap` | `checkAP` | `()` | `boolean` |
| `ax` | `tickAX` | `()` | `void` |
| `ay` | `tickAY` | `()` | `boolean` |
| `b` | `tickB` | `()` | `void` |
| `bm` | `findBlocks` | `(BlockPos exclude)` | `List<BlockPos>` |
| `bn` | `findBlocks` | `(BlockPos center, BlockPos exclude)` | `List<BlockPos>` |
| `bo` | `findBlocks` | `(BlockPos exclude)` | `List<BlockPos>` |
| `bp` | `findBlocks` | `(BlockPos exclude)` | `List<BlockPos>` |
| `bq` | `findItem` | `(BlockPos pos, double radius)` | `ItemEntity` |
| `br` | `logWarning` | `()` | `List<SupplyRule>` |
| `c` | `logInfo` | `()` | `void` |
| `cb` | `checkCB` | `(BlockPos pos, double tolerance)` | `boolean` |
| `cg` | `logMessage` | `()` | `boolean` |
| `ch` | `logMessage` | `(int x, int z, int realHeight)` | `void` |
| `ck` | `logMessage` | `(int chunkX, int chunkZ)` | `boolean` |
| `cl` | `buildPath` | `()` | `Path` |
| `cm` | `logError` | `(String msg)` | `void` |
| `cn` | `formatCN` | `(BlockPos head)` | `String` |
| `cq` | `logInfo` | `()` | `void` |
| `cr` | `moveTo` | `()` | `void` |
| `cs` | `logInfo` | `()` | `void` |
| `ct` | `checkCT` | `()` | `boolean` |
| `cv` | `logInfo` | `()` | `void` |
| `de` | `findDragonHead` | `(BlockPos center, int radius, int yRange)` | `DragonHead` |
| `df` | `findItemFrame` | `(double reach)` | `ItemFrameEntity` |
| `dg` | `findItemFrame` | `(BlockPos pos)` | `ItemFrameEntity` |
| `di` | `findItem` | `()` | `ItemEntity` |
| `dk` | `checkDK` | `()` | `boolean` |
| `e` | `logInfo` | `(int generation)` | `void` |
| `f` | `findShip` | `(EndCityGenerator generator, CPos chunk)` | `ShipTarget` |
| `g` | `logInfo` | `()` | `void` |
| `h` | `logWarning` | `(TickEvent.Pre event)` | `void` |
| `i` | `logInfo` | `()` | `void` |
| `j` | `logInfo` | `()` | `void` |
| `k` | `logInfo` | `()` | `void` |
| `l` | `setState` | `()` | `void` |
| `m` | `logInfo` | `()` | `void` |
| `o` | `logInfo` | `(String reason)` | `void` |
| `r` | `logInfo` | `()` | `void` |
| `s` | `setState` | `()` | `void` |
| `t` | `logInfo` | `()` | `void` |
| `u` | `logInfo` | `()` | `void` |
| `v` | `logInfo` | `()` | `void` |
| `w` | `findBlock` | `(int radius)` | `BlockPos` |
| `x` | `logInfo` | `()` | `void` |
| `y` | `logInfo` | `()` | `void` |

### ❓ 需手动标注的方法

| 混淆名 | 参数 | 返回类型 |
|--------|------|---------|
| `a` | `(long worldSeed)` | `SimplexNoiseSampler` |
| `ah` | `(SlotActionType type, int slotId)` | `void` |
| `ai` | `(BlockPos pos)` | `void` |
| `aj` | `(BlockPos pos, Direction face)` | `void` |
| `ak` | `(BlockPos target, Direction face)` | `void` |
| `al` | `()` | `void` |
| `am` | `()` | `boolean` |
| `aq` | `()` | `int` |
| `ar` | `(int rows)` | `int` |
| `at` | `(int rows)` | `int` |
| `au` | `(int rows)` | `int` |
| `av` | `(int rows, int invSlot)` | `int` |
| `aw` | `(int invSlot)` | `int` |
| `az` | `(int rows, int invSlot)` | `int` |
| `ba` | `(ScreenHandler sh, int rows)` | `int` |
| `bb` | `(ScreenHandler sh, int rows)` | `int` |
| `bc` | `(ScreenHandler sh, int rows, Item item)` | `int` |
| `bd` | `(ScreenHandler sh, int rows)` | `int` |
| `be` | `()` | `int` |
| `bf` | `(ScreenHandler sh, int rows)` | `int` |
| `bg` | `(ScreenHandler sh, int rows, List<SupplyRule> rules)` | `int` |
| `bh` | `(ItemStack stack)` | `boolean` |
| `bi` | `(BlockState state)` | `boolean` |
| `bj` | `(BlockState state)` | `boolean` |
| `bk` | `(ItemStack box)` | `int` |
| `bl` | `(ItemStack box)` | `Set<Item>` |
| `bs` | `()` | `boolean` |
| `bt` | `(Item item)` | `boolean` |
| `bu` | `(Item item)` | `int` |
| `bv` | `()` | `int` |
| `bw` | `()` | `int` |
| `bx` | `(Item item)` | `int` |
| `by` | `()` | `int` |
| `bz` | `()` | `int` |
| `ca` | `()` | `int` |
| `cc` | `(int x, int z)` | `float` |
| `cd` | `(float value)` | `float` |
| `ce` | `(int blockX, int blockZ)` | `double` |
| `cf` | `(int chunkX, int chunkZ)` | `boolean` |
| `ci` | `(int x, int z)` | `int` |
| `cj` | `(double f, int y)` | `double` |
| `co` | `(BlockPos blockPos)` | `boolean` |
| `cp` | `(BlockPos head)` | `void` |
| `cu` | `(double x, double z)` | `void` |
| `cw` | `()` | `void` |
| `cx` | `(Direction d)` | `void` |
| `cy` | `(BlockPos pos)` | `float` |
| `cz` | `(BlockPos pos)` | `double` |
| `d` | `()` | `void` |
| `da` | `(float targetYaw, float targetPitch)` | `void` |
| `db` | `(float targetYaw, float targetPitch)` | `void` |
| `dc` | `(float current, float target, float speed)` | `float` |
| `dd` | `(BlockState state)` | `boolean` |
| `dh` | `()` | `boolean` |
| `dj` | `()` | `boolean` |
| `dl` | `()` | `int` |
| `dm` | `()` | `int` |
| `dn` | `(String s)` | `long` |
| `n` | `(CollectStep next)` | `void` |
| `p` | `(String reason)` | `void` |
| `q` | `()` | `void` |
| `z` | `()` | `void` |

---

## com.door.hunt.modules.PullUp
**源文件**: `com/door/hunt/modules/PullUp.java`

### ✅ 已确认字段 (基于 .name() 调用)

| 混淆名 | 建议名 | 类型 | Setting 显示名 |
|--------|--------|------|---------------|
| `a` | `defaultGroup` | `SettingGroup` | "(default)" |
| `b` | `dangerY` | `Setting<Integer>` | "危险 Y 高度" |
| `c` | `targetY` | `Setting<Integer>` | "目标 Y 高度" |
| `d` | `climbPitch` | `Setting<Double>` | "拉升俯仰角" |
| `e` | `fireworkInterval` | `Setting<Double>` | "烟花间隔" |

### ❓ 需手动标注的字段 (counterXX/flagXX 占位名)

这些字段没有明显的语义线索，需要在 Enigma 中查看用法后手动命名。

| 混淆名 | 占位名 | 类型 |
|--------|--------|------|
| `f` | `flagF` | `boolean` |
| `g` | `counterG` | `int` |
| `h` | `counterH` | `int` |

### ✅ 已推断方法 (基于方法体内容)

| 混淆名 | 建议名 | 参数 | 返回类型 |
|--------|--------|------|---------|
| `a` | `logInfo` | `(TickEvent.Pre event)` | `void` |
| `c` | `logInfo` | `()` | `void` |

### ❓ 需手动标注的方法

| 混淆名 | 参数 | 返回类型 |
|--------|------|---------|
| `b` | `()` | `void` |

---

## com.door.hunt.modules.RocketSafetyLogout
**源文件**: `com/door/hunt/modules/RocketSafetyLogout.java`

### ✅ 已确认字段 (基于 .name() 调用)

| 混淆名 | 建议名 | 类型 | Setting 显示名 |
|--------|--------|------|---------------|
| `sg` | `defaultGroup` | `SettingGroup` | "(default)" |

---

## com.door.hunt.modules.SearchControl
**源文件**: `com/door/hunt/modules/SearchControl.java`

### ✅ 已确认字段 (基于 .name() 调用)

| 混淆名 | 建议名 | 类型 | Setting 显示名 |
|--------|--------|------|---------------|
| `a` | `defaultGroup` | `SettingGroup` | "(default)" |
| `b` | `mode` | `Setting<Mode>` | "模式" |
| `c` | `centerX` | `Setting<Integer>` | "中心 X" |
| `d` | `centerZ` | `Setting<Integer>` | "中心 Z" |
| `e` | `rectRange` | `Setting<Double>` | "矩形范围" |
| `f` | `circleRange` | `Setting<Double>` | "圆形范围" |
| `g` | `spiralRange` | `Setting<Double>` | "螺旋范围" |
| `h` | `stopOnWASD` | `Setting<Boolean>` | "按 WASD 中止" |
| `i` | `flightOnly` | `Setting<Boolean>` | "仅飞行时启用" |
| `j` | `maxDistance` | `Setting<Double>` | "最大距离" |

### ❓ 需手动标注的方法

| 混淆名 | 参数 | 返回类型 |
|--------|------|---------|
| `a` | `(TickEvent.Pre event)` | `void` |
| `b` | `(Vec3d center)` | `void` |
| `c` | `(Vec3d center)` | `void` |
| `d` | `(Vec3d center)` | `void` |

---

## com.door.hunt.modules.UnbreakableElytra
**源文件**: `com/door/hunt/modules/UnbreakableElytra.java`

### ✅ 已确认字段 (基于 .name() 调用)

| 混淆名 | 建议名 | 类型 | Setting 显示名 |
|--------|--------|------|---------------|
| `a` | `defaultGroup` | `SettingGroup` | "(default)" |
| `b` | `protectionInterval` | `Setting<Integer>` | "保护周期" |
| `c` | `durabilityThreshold` | `Setting<Integer>` | "耐久阈值" |

### ❓ 需手动标注的字段 (counterXX/flagXX 占位名)

这些字段没有明显的语义线索，需要在 Enigma 中查看用法后手动命名。

| 混淆名 | 占位名 | 类型 |
|--------|--------|------|
| `d` | `counterD` | `int` |

### ✅ 已推断方法 (基于方法体内容)

| 混淆名 | 建议名 | 参数 | 返回类型 |
|--------|--------|------|---------|
| `a` | `logInfo` | `(TickEvent.Pre event)` | `void` |

### ❓ 需手动标注的方法

| 混淆名 | 参数 | 返回类型 |
|--------|------|---------|
| `b` | `()` | `int` |

---

## com.door.hunt.utils.EntityUtils
**源文件**: `com/door/hunt/utils/EntityUtils.java`

### 🟡 基于类型推断的字段

| 混淆名 | 建议名 | 类型 |
|--------|--------|------|
| `mc` | `mc` | `MinecraftClient` |

---

## com.door.hunt.utils.MathUtils
**源文件**: `com/door/hunt/utils/MathUtils.java`

### ❓ 需手动标注的方法

| 混淆名 | 参数 | 返回类型 |
|--------|------|---------|
| `f` | `(double x)` | `double` |
| `s2` | `(int x)` | `int` |

---

## com.door.hunt.utils.MovEngine
**源文件**: `com/door/hunt/utils/MovEngine.java`

### 🟡 基于类型推断的字段

| 混淆名 | 建议名 | 类型 |
|--------|--------|------|
| `mc` | `mc` | `MinecraftClient` |

---

## com.door.hunt.utils.PlayerInputUtils
**源文件**: `com/door/hunt/utils/PlayerInputUtils.java`

### 🟡 基于类型推断的字段

| 混淆名 | 建议名 | 类型 |
|--------|--------|------|
| `mc` | `mc` | `MinecraftClient` |

---

## com.door.hunt.utils.RaycastUtils
**源文件**: `com/door/hunt/utils/RaycastUtils.java`

### 🟡 基于类型推断的字段

| 混淆名 | 建议名 | 类型 |
|--------|--------|------|
| `mc` | `mc` | `MinecraftClient` |

---

## com.door.hunt.utils.entity.LegalMovementManager
**源文件**: `com/door/hunt/utils/entity/LegalMovementManager.java`

### 🟡 基于类型推断的字段

| 混淆名 | 建议名 | 类型 |
|--------|--------|------|
| `mc` | `mc` | `MinecraftClient` |

---

## com.door.hunt.utils.entity.PredictorImpl
**源文件**: `com/door/hunt/utils/entity/PredictorImpl.java`

### 🟡 基于类型推断的字段

| 混淆名 | 建议名 | 类型 |
|--------|--------|------|
| `mc` | `mc` | `MinecraftClient` |

---
