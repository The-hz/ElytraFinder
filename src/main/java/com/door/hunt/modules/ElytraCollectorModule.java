/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.modules;

import com.door.hunt.AddonTemplate;
import com.door.hunt.hud.ElytraFinderStatusHud;
import com.door.hunt.modules.ElytraApproachSafety22;
import com.door.hunt.modules.ElytraDirectionFilterV2;
import com.door.hunt.modules.ElytraFlightUi20;
import com.door.hunt.modules.ElytraSafeEscape;
import com.door.hunt.modules.ElytraSearchFallback;
import com.door.hunt.modules.ElytraVisitedFix;
import com.door.hunt.modules.ElytraVisitedUi22;
import com.door.hunt.modules.LowYSafetyLogout;
import com.door.hunt.modules.StorageRecovery;
import com.door.hunt.modules.StorageReturn;
import com.door.hunt.pathing.PathManagers;
import com.seedfinding.mcbiome.source.BiomeSource;
import com.seedfinding.mcbiome.source.EndBiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.block.BlockRotation;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.structure.EndCity;
import com.seedfinding.mcfeature.structure.generator.Generator;
import com.seedfinding.mcfeature.structure.generator.structure.EndCityGenerator;
import com.seedfinding.mcnoise.simplex.SimplexNoiseSampler;
import com.seedfinding.mcseed.lcg.LCG;
import com.seedfinding.mcseed.rand.JRand;
import com.seedfinding.mcterrain.TerrainGenerator;
import com.seedfinding.mcterrain.terrain.EndTerrainGenerator;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.Key;
import java.security.MessageDigest;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.densityfunction.DensityFunction;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class ElytraCollectorModule
extends Module {
    private static final MCVersion VERSION = MCVersion.v1_16_2;
    private volatile SimplexNoiseSampler islandNoise;
    private volatile InterpolatedNoiseSampler base3dNoise;
    private final SettingGroup sgGeneral;
    private final SettingGroup sgFlight;
    private final Setting<String> seedSetting;
    private final Setting<Integer> searchRange;
    private final Setting<Integer> minHeight;
    private final Setting<Integer> maxHeight;
    private final Setting<Integer> riseHeight;
    private final Setting<Double> pitchSpeed;
    private final Setting<Double> yawSpeed;
    private final Setting<Double> killAuraReach;
    private final Setting<Double> fireworkInterval;
    private final Setting<List<String>> blacklist;
    private final Setting<List<String>> searchResults;
    private final Setting<Boolean> btnStart;
    private final Setting<Boolean> debugSetting;
    private final SettingGroup sgStorage;
    private final Setting<Integer> freeSlotDump;
    private final Setting<List<String>> supplies;
    private final Setting<Boolean> lowYExit;
    private final Setting<Integer> lowYThreshold;
    private State state;
    private CollectStep collectStep;
    private final List<ShipTarget> ships;
    private int targetIndex;
    private volatile Thread searchThread;
    private volatile boolean searchCancelled;
    private volatile int searchGeneration;
    private ShipTarget current;
    private ShipWaypoints waypoints;
    private int stateTick;
    private int lastGotoTick;
    private long worldSeed;
    private boolean climbing;
    private boolean headFound;
    private BlockPos exactHead;
    private int nearTicks;
    private int scanAttempts;
    private boolean pullUpSuppressed;
    private final Set<String> sessionSkip;
    private int searchExtends;
    private Direction takeoffFacing;
    private int noElytraTicks;
    private boolean frameHadElytra;
    private boolean frameAirChecked;
    private boolean frameAirEmpty;
    private int frameAirScanTicks;
    private boolean pickupSessionDone;
    private boolean firstFireworkDone;
    private int lastFireworkTick;
    private boolean landingRecover;
    private double recoveryTargetY;
    private float recoveryYaw;
    private int recoverStageTick;
    private int recoverCount;
    private StoragePhase storagePhase;
    private int storageTick;
    private int pickupWaitStart;
    private BlockPos ecPos;
    private BlockPos boxPos;
    private boolean boxFromInventory;
    private int boxInventorySlot;
    private int boxBaseline;
    private int ecBaseline;
    private boolean dumpNeeded;
    private boolean resupplyNeeded;
    private StoragePhase afterClose;
    private int elytraBeforeFrame;
    private List<BlockPos> placeCandidates;
    private int placeIndex;
    private int lastPlaceClick;
    private int placeClickAttempts;
    private final ArrayDeque<MoveOp> moveQueue;
    private int moveState;
    private int moveSrc;
    private int moveDst;
    private int pickStep;
    private int pickEcSlot;
    private int pickInvSlot;
    private int fillIndex;
    private int takeRule;
    private int takeSlot;
    private int takeNeed;
    private int takeStuckSlot;
    private int takeStuckTicks;
    private int takeEmptyTicks;
    private final Set<Item> takeFailedItems;
    private int fillStuckSlot;
    private int fillStuckTicks;
    private int openRetry;
    private int boxOpenClickTick;
    private boolean boxPurposeSupply;
    private boolean boxPlaceFloorFallback;
    private int recoverStage;
    private int ecOpenFailCount;
    private int supplyScanStart;
    private boolean supplyPauseDump;
    private boolean resupplyRescan;
    private boolean standingMovedOff;
    private final Object debugLock;
    private final Setting startClimbAngle;
    private final Setting cruiseClimbAngle;
    private final Setting cruiseGlideAngle;
    private final SettingGroup t18DirectionGroup;
    private final Setting t18North;
    private final Setting t18South;
    private final Setting t18East;
    private final Setting t18West;
    private final SettingGroup t19VisitedGroup;
    private final Setting t19IgnoreVisited;

    private static SimplexNoiseSampler createIslandNoise(long worldSeed) {
        JRand rand = new JRand(worldSeed);
        rand.advance(LCG.JAVA.combine(17292L));
        return new SimplexNoiseSampler(rand);
    }

    public ElytraCollectorModule() {
        super(AddonTemplate.CATEGORY, "鞘翅收集器", (String)"全自动找末地城鞘翅：种子定位 + 龙头精确定位 + 高度保持飞行 + 缓降 + Baritone 寻路 + 打展示框捡鞘翅.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.sgFlight = this.settings.createGroup((String)"Flight");
        this.seedSetting = this.sgGeneral.add((Setting)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("种子")).description((String)"世界种子 (0 = 当前世界).")).defaultValue("-7346913998703726680")).build());
        this.searchRange = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("搜索范围")).description((String)"搜索半径，单位方块 (从玩家位置).")).defaultValue(5000)).range(320, 100000).sliderRange(320, 20000).build());
        this.minHeight = this.sgFlight.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("最低高度")).description((String)"飞行中低于此高度时触发爬升.")).defaultValue(180)).range(100, 300).sliderRange(120, 260).build());
        this.maxHeight = this.sgFlight.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("最高高度")).description((String)"飞行中高于此高度时停止爬升、转为平缓下滑 (与 min-height 组成滞回区间).")).defaultValue(220)).range(120, 320).sliderRange(140, 300).build());
        this.riseHeight = this.sgFlight.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("拉升高度")).description((String)"起飞后抬头爬升到的目标高度 (需高于 max-height).")).defaultValue(230)).range(200, 320).sliderRange(200, 300).build());
        this.pitchSpeed = this.sgFlight.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("俯仰速度")).description((String)"飞行 (pitch40) 时上下转动视角 (pitch) 的速度 (度/tick).")).defaultValue(10.0).min(1.0).sliderMax(45.0).build());
        this.yawSpeed = this.sgFlight.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("偏航速度")).description((String)"左右转动视角 (yaw) 及鞘翅缓降的转向速度 (度/tick).")).defaultValue(30.0).min(1.0).sliderMax(90.0).build());
        this.killAuraReach = this.sgFlight.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("Kill Aura 攻击距离")).description((String)"攻击展示框 (item_frame) 的判定距离.")).defaultValue(3.0).min(1.0).sliderMax(6.0).build());
        this.fireworkInterval = this.sgFlight.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("烟花间隔")).description((String)"展开鞘翅后立即使用第一个烟花，之后每隔这么多秒再用一个 (秒).")).defaultValue(2.0).min(0.5).sliderRange(0.5, 10.0).build());
        this.blacklist = this.sgGeneral.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("已访问末地船")).description((String)"黑名单：已经去过的船 (x,z)，搜索时会自动跳过. 自动维护.")).defaultValue(new ArrayList())).visible(() -> false)).build());
        this.searchResults = this.sgGeneral.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("搜索结果")).description((String)"上次搜索结果列表 (x,y,z,朝向)，下一次搜索完成后覆盖.")).defaultValue(new ArrayList())).build());
        this.btnStart = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("开始")).description((String)"开始自动采集.")).defaultValue(false)).onChanged(b -> {
            if (b.booleanValue()) {
                this.start();
            }
        })).build());
        this.debugSetting = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("调试")).description((String)"输出调试日志 (用于校准高度公式).")).defaultValue(false)).build());
        this.sgStorage = this.settings.createGroup((String)"Storage");
        this.freeSlotDump = this.sgStorage.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("清仓保留空槽")).description((String)"背包可用空格 ≤ 此值时，拿到鞘翅后自动去末影箱存鞘翅.")).defaultValue(3)).range(0, 36).sliderRange(0, 36).build());
        this.supplies = this.sgStorage.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("补给品")).description((String)"物资列表，格式: 物品ID;最低值;目标库存 (如 minecraft:firework_rocket;32;256). 背包物资低于最低值时自动从末影箱补货，拿到目标库存为止.")).defaultValue(new ArrayList<String>(List.of((String)"minecraft:firework_rocket;4;16", (String)"minecraft:cooked_beef;8;32")))).build());
        this.lowYExit = this.sgStorage.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("低 Y 退出")).description((String)"Y 低于阈值时自动退出游戏 (防虚空掉物).")).defaultValue(true)).build());
        this.lowYThreshold = this.sgStorage.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("低 Y 阈值")).description((String)"低于此 Y 自动退出游戏.")).defaultValue(30)).range(0, 100).sliderRange(0, 100).build());
        this.state = State.IDLE;
        this.collectStep = CollectStep.TO_P1;
        this.ships = Collections.synchronizedList(new ArrayList());
        this.targetIndex = 0;
        this.searchCancelled = true;
        this.searchGeneration = 0;
        this.stateTick = 0;
        this.lastGotoTick = 0;
        this.worldSeed = 0L;
        this.climbing = false;
        this.headFound = false;
        this.exactHead = null;
        this.nearTicks = 0;
        this.scanAttempts = 0;
        this.pullUpSuppressed = false;
        this.sessionSkip = Collections.synchronizedSet(new HashSet());
        this.searchExtends = 0;
        this.takeoffFacing = null;
        this.noElytraTicks = 0;
        this.frameHadElytra = false;
        this.frameAirChecked = false;
        this.frameAirEmpty = false;
        this.frameAirScanTicks = 0;
        this.pickupSessionDone = false;
        this.firstFireworkDone = false;
        this.lastFireworkTick = 0;
        this.landingRecover = false;
        this.recoveryTargetY = 0.0;
        this.recoveryYaw = 0.0f;
        this.recoverStageTick = 0;
        this.recoverCount = 0;
        this.storagePhase = StoragePhase.NONE;
        this.storageTick = 0;
        this.pickupWaitStart = -1;
        this.boxFromInventory = false;
        this.boxInventorySlot = -1;
        this.boxBaseline = 0;
        this.ecBaseline = 0;
        this.dumpNeeded = false;
        this.resupplyNeeded = false;
        this.afterClose = StoragePhase.NONE;
        this.elytraBeforeFrame = 0;
        this.placeCandidates = new ArrayList<BlockPos>();
        this.placeIndex = 0;
        this.lastPlaceClick = -100;
        this.placeClickAttempts = 0;
        this.moveQueue = new ArrayDeque();
        this.moveState = 0;
        this.moveSrc = -1;
        this.moveDst = -1;
        this.pickStep = 0;
        this.pickEcSlot = -1;
        this.pickInvSlot = -1;
        this.fillIndex = -1;
        this.takeRule = 0;
        this.takeSlot = -1;
        this.takeNeed = 0;
        this.takeStuckSlot = -1;
        this.takeStuckTicks = 0;
        this.takeEmptyTicks = 0;
        this.takeFailedItems = new HashSet<Item>();
        this.fillStuckSlot = -1;
        this.fillStuckTicks = 0;
        this.openRetry = 0;
        this.boxOpenClickTick = -1;
        this.boxPurposeSupply = false;
        this.boxPlaceFloorFallback = false;
        this.recoverStage = 0;
        this.ecOpenFailCount = 0;
        this.supplyScanStart = 0;
        this.supplyPauseDump = false;
        this.resupplyRescan = false;
        this.standingMovedOff = false;
        this.debugLock = new Object();
        this.startClimbAngle = this.sgFlight.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("起飞拉升角度")).description("首次起飞以及滑翔降到最低高度后的重新爬升角度（度）。达到最高高度后自动切换滑翔。")).defaultValue(45.0).range(5.0, 80.0).sliderRange(5.0, 80.0).build());
        this.cruiseClimbAngle = this.sgFlight.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("巡航爬升角度")).description("长距离巡航到最低高度后使用的抬头角度（度）。")).defaultValue(54.77).range(5.0, 80.0).sliderRange(5.0, 80.0).build());
        this.cruiseGlideAngle = this.sgFlight.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("巡航滑翔角度")).description("达到最高高度后使用的向下滑翔角度（度）。滑翔阶段不使用烟花，降到最低高度后重新爬升。")).defaultValue(37.72).range(0.0, 70.0).sliderRange(0.0, 70.0).build());
        this.t18DirectionGroup = this.settings.createGroup("搜索方向");
        this.t18North = this.t18DirectionGroup.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("搜索北方")).description("允许搜索以开始搜索时的位置为中心，北方（-Z）扇区内的末地船。")).defaultValue(true)).build());
        this.t18South = this.t18DirectionGroup.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("搜索南方")).description("允许搜索以开始搜索时的位置为中心，南方（+Z）扇区内的末地船。")).defaultValue(true)).build());
        this.t18East = this.t18DirectionGroup.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("搜索东方")).description("允许搜索以开始搜索时的位置为中心，东方（+X）扇区内的末地船。")).defaultValue(true)).build());
        this.t18West = this.t18DirectionGroup.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("搜索西方")).description("允许搜索以开始搜索时的位置为中心，西方（-X）扇区内的末地船。")).defaultValue(true)).build());
        this.t19VisitedGroup = this.settings.createGroup("访问记录");
        this.t19IgnoreVisited = this.t19VisitedGroup.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("忽略已访问记录")).description("开启后重新扫描所有预测末地船，不使用旧的“已访问船”列表。适合旧版本记录异常时临时恢复搜索。")).defaultValue(false)).build());
        ElytraFlightUi20.remove(this.sgGeneral, this.riseHeight);
        ElytraFlightUi20.remove(this.sgFlight, this.cruiseClimbAngle);
        ElytraFinderStatusHud.attach((Object)this);
    }

    public WWidget getWidget(GuiTheme theme) {
        WSection section = theme.section((String)"黑名单", true);
        WButton clear = (WButton)section.add((WWidget)theme.button((String)"清除黑名单")).expandX().widget();
        clear.action = () -> {
            List list = (List)this.blacklist.get();
            synchronized (list) {
                ((List)this.blacklist.get()).clear();
            }
            this.info((String)"黑名单已清空.", new Object[0]);
        };
        ElytraVisitedUi22.enhance((Object)this, theme, section);
        return section;
    }

    public void onDeactivate() {
        this.searchCancelled = true;
        ++this.searchGeneration;
        this.searchThread = null;
        this.state = State.IDLE;
        this.ships.clear();
        this.pullUpSuppressed = false;
        this.landingRecover = false;
        this.recoveryYaw = 0.0f;
        this.recoverStageTick = 0;
        this.storagePhase = StoragePhase.NONE;
        this.storageTick = 0;
        this.boxInventorySlot = -1;
        this.resetStorageClick();
        this.releaseForward();
        PathManagers.get().stop();
    }

    private void resetStorageClick() {
        this.moveQueue.clear();
        this.moveState = 0;
        this.moveSrc = -1;
        this.moveDst = -1;
        this.pickStep = 0;
        this.pickEcSlot = -1;
        this.pickInvSlot = -1;
        this.fillIndex = -1;
        this.takeRule = 0;
        this.takeSlot = -1;
        this.takeNeed = 0;
        this.takeStuckSlot = -1;
        this.takeStuckTicks = 0;
        this.takeEmptyTicks = 0;
        this.sessionSkip.clear();
        this.fillStuckSlot = -1;
        this.fillStuckTicks = 0;
        this.openRetry = 0;
        this.boxPurposeSupply = false;
        this.ecOpenFailCount = 0;
        this.supplyScanStart = 0;
        this.standingMovedOff = false;
    }

    private void start() {
        if (this.state != State.IDLE && this.state != State.DONE) {
            this.btnStart.set(false);
            return;
        }
        this.state = State.SEARCHING;
        this.searchExtends = 0;
        this.takeoffFacing = null;
        this.landingRecover = false;
        this.recoveryYaw = 0.0f;
        this.recoverStageTick = 0;
        this.storagePhase = StoragePhase.NONE;
        this.storageTick = 0;
        this.boxInventorySlot = -1;
        this.info("开始搜索末地城 (范围=" + String.valueOf(this.searchRange.get()) + " 方块)...", new Object[0]);
        if (((Boolean)this.debugSetting.get()).booleanValue()) {
            this.info("调试日志文件: " + String.valueOf(this.cl().toAbsolutePath()), new Object[0]);
        }
        this.launchSearch();
    }

    private void launchSearch() {
        this.searchCancelled = false;
        int gen = ++this.searchGeneration;
        Thread t = new Thread(() -> this.searchShips(gen), (String)"ElytraCollector-Search");
        t.setDaemon(true);
        this.searchThread = t;
        t.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void searchShips(int generation) {
        block18: {
            try {
                if (this.searchCancelled || generation != this.searchGeneration) {
                    return;
                }
                this.worldSeed = this.parseSeed((String)this.seedSetting.get());
                EndCity endCity = new EndCity(VERSION);
                EndCityGenerator generator = new EndCityGenerator(VERSION);
                ChunkRand rand = new ChunkRand();
                EndBiomeSource biomeSource = new EndBiomeSource(VERSION, this.worldSeed);
                this.islandNoise = ElytraCollectorModule.createIslandNoise(this.worldSeed);
                this.base3dNoise = new InterpolatedNoiseSampler((Random)new CheckedRandom(this.worldSeed), 0.25, 0.25, 80.0, 160.0, 4.0);
                EndTerrainGenerator terrainGen = new EndTerrainGenerator(biomeSource);
                int px = (int)Math.floor(this.mc.player.getX());
                int pz = (int)Math.floor(this.mc.player.getZ());
                int spacing = endCity.getSpacing();
                int range = (Integer)this.searchRange.get() + this.searchExtends * 500;
                int regionRange = range / (spacing * 16) + 1;
                int prx = Math.floorDiv(px >> 4, spacing);
                int prz = Math.floorDiv(pz >> 4, spacing);
                ArrayList<ShipTarget> found = new ArrayList<ShipTarget>();
                for (int drx = -regionRange; drx <= regionRange; ++drx) {
                    if (this.searchCancelled || generation != this.searchGeneration) {
                        return;
                    }
                    for (int drz = -regionRange; drz <= regionRange; ++drz) {
                        ShipTarget t;
                        int rx = prx + drx;
                        int rz = prz + drz;
                        CPos city = endCity.getInRegion(this.worldSeed, rx, rz, rand);
                        if (city == null || !endCity.canSpawn(city, (BiomeSource)biomeSource) || !endCity.canGenerate(city, (TerrainGenerator)terrainGen)) continue;
                        generator.generate(terrainGen, city.getX(), city.getZ(), rand);
                        boolean hasShip = generator.hasShip();
                        if (hasShip && (t = this.extractShip(generator, city)) != null && !this.isBlacklisted(t.headPos) && !this.sessionSkip.contains(this.blacklistKey(t.headPos))) {
                            found.add(t);
                            if (((Boolean)this.debugSetting.get()).booleanValue()) {
                                this.debugLog("ship city=(" + city.getX() + "," + city.getZ() + ") head=(" + t.headPos.getX() + "," + t.headPos.getY() + "," + t.headPos.getZ() + ") facing=" + String.valueOf(t.facing) + " E=" + String.format((String)"%.4f", this.endIslandDensity(city.getX() * 16 + 8, city.getZ() * 16 + 8)));
                            }
                        }
                        generator.reset();
                    }
                }
                if (this.searchCancelled || generation != this.searchGeneration) {
                    return;
                }
                ElytraSearchFallback.rebuildIfEmpty((Object)this, found, this.worldSeed, px, pz, range);
                ElytraDirectionFilterV2.filter(found, this.t18North, this.t18South, this.t18East, this.t18West, px, pz);
                found.sort(Comparator.comparingInt(s -> (s.headPos.getX() - px) * (s.headPos.getX() - px) + (s.headPos.getZ() - pz) * (s.headPos.getZ() - pz)));
                this.ships.clear();
                this.ships.addAll(found);
                this.targetIndex = 0;
                List list = (List)this.searchResults.get();
                synchronized (list) {
                    ((List)this.searchResults.get()).clear();
                    for (ShipTarget t : found) {
                        ((List)this.searchResults.get()).add(t.headPos.getX() + "," + t.headPos.getY() + "," + t.headPos.getZ() + "," + String.valueOf(t.facing));
                    }
                }
                this.info("找到 " + this.ships.size() + " 艘带船末地城 (已排除黑名单, 范围=" + range + ").", new Object[0]);
                if (this.ships.isEmpty()) {
                    if (this.searchExtends < 10) {
                        ++this.searchExtends;
                        this.info("范围内没有末地船，扩大范围 +" + this.searchExtends * 500 + " 格重新搜索...", new Object[0]);
                        this.launchSearch();
                    } else {
                        this.searchExtends = 0;
                        this.completeTask((String)"范围内没有末地船，任务完成.");
                    }
                } else {
                    this.searchExtends = 0;
                    if (this.state == State.SEARCHING) {
                        this.firstFireworkDone = false;
                        this.state = State.RISING;
                        this.stateTick = 0;
                    }
                }
            }
            catch (Exception e) {
                if (this.searchCancelled || generation != this.searchGeneration) break block18;
                this.error("搜索失败: " + e.getMessage(), new Object[0]);
                this.state = State.IDLE;
                this.btnStart.set(false);
            }
        }
    }

    private ShipTarget extractShip(EndCityGenerator generator, CPos chunk) {
        try {
            BlockPos itemFrame = null;
            int sternX = 0;
            int sternZ = 0;
            int sternCount = 0;
            for (Pair<Generator.ILootType, BPos> p : generator.getChestsPos()) {
                Generator.ILootType lt = p.getFirst();
                BPos b = p.getSecond();
                if (lt == EndCityGenerator.LootType.SHIP_ELYTRA) {
                    itemFrame = new BlockPos(b.getX(), b.getY(), b.getZ());
                    continue;
                }
                if (lt != EndCityGenerator.LootType.SHIP_SENTRY_2 && lt != EndCityGenerator.LootType.SHIP_SENTRY_3) continue;
                sternX += b.getX();
                sternZ += b.getZ();
                ++sternCount;
            }
            if (itemFrame == null || sternCount == 0) {
                return null;
            }
            int dx = sternX / sternCount - itemFrame.getX();
            int dz = sternZ / sternCount - itemFrame.getZ();
            Direction behind = Math.abs(dx) > Math.abs(dz) ? (dx > 0 ? Direction.EAST : Direction.WEST) : (dz > 0 ? Direction.SOUTH : Direction.NORTH);
            Direction facing = behind.getOpposite();
            BlockPos head = itemFrame.offset(facing, 7).offset(Direction.UP, 3);
            return new ShipTarget(chunk, head, facing);
        }
        catch (Exception e) {
            return null;
        }
    }

    private void beginFlyToTarget() {
        if (this.targetIndex >= this.ships.size()) {
            this.completeTask((String)"全部末地城已处理，任务完成.");
            return;
        }
        this.current = this.ships.get(this.targetIndex);
        this.waypoints = ShipWaypoints.from(this.current.headPos, this.current.facing);
        this.climbing = false;
        this.headFound = false;
        this.frameAirChecked = false;
        this.frameAirEmpty = false;
        this.frameAirScanTicks = 0;
        this.exactHead = null;
        this.nearTicks = 0;
        this.scanAttempts = 0;
        this.firstFireworkDone = false;
        this.landingRecover = false;
        this.recoverCount = 0;
        this.stateTick = 0;
        this.info("飞向最近未去过的船 近似龙头=" + String.valueOf(this.current.headPos) + " 朝向=" + String.valueOf(this.current.facing), new Object[0]);
        this.state = State.FLYING;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        try {
            if (this.mc.player == null || this.mc.world == null) {
                return;
            }
            ++this.stateTick;
            if (this.state != State.IDLE && this.state != State.DONE && ((Boolean)this.lowYExit.get()).booleanValue() && this.mc.player.getY() < (double)((Integer)this.lowYThreshold.get()).intValue()) {
                this.warning("Y=" + String.format((String)"%.1f", this.mc.player.getY()) + " 低于阈值 " + String.valueOf(this.lowYThreshold.get()) + "，任务已停止.", new Object[0]);
                LowYSafetyLogout.trigger((Object)this, (String)"低高度任务停止.");
                return;
            }
            if (this.pullUpSuppressed) {
                return;
            }
            if (this.storagePhase != StoragePhase.NONE) {
                this.onStorageTick();
                return;
            }
            if (StorageReturn.tick((Object)this)) {
                return;
            }
            switch (this.state.ordinal()) {
                case 2: {
                    this.onRising();
                    break;
                }
                case 3: {
                    this.onFlying();
                    break;
                }
                case 4: {
                    this.onLanding();
                    break;
                }
                case 5: {
                    this.onCollecting();
                    break;
                }
            }
        }
        catch (Throwable t) {
            this.error("任务异常: " + String.valueOf(t), new Object[0]);
            t.printStackTrace();
            this.stopTask((String)"任务异常，停止任务.");
        }
    }

    private void onRising() {
        if (this.landingRecover && ElytraSafeEscape.tick((Object)this)) {
            return;
        }
        if (!this.landingRecover && this.stateTick > 400) {
            this.error((String)"起飞超时 (可能没穿鞘翅或没有烟花).", new Object[0]);
            this.state = State.IDLE;
            this.btnStart.set(false);
            this.releaseForward();
            return;
        }
        if (this.mc.player.isOnGround() && this.stateTick <= 2) {
            this.maybeStartSession();
            if (this.storagePhase != StoragePhase.NONE) {
                return;
            }
        }
        if (this.landingRecover) {
            ++this.recoverStageTick;
            if (this.recoverStage == 0) {
                this.rotateFastTo(this.recoveryYaw, this.mc.player.getPitch());
            } else {
                this.rotateTo(this.yawTowards(this.waypoints.landing), 37.72f);
            }
        } else if (this.takeoffFacing != null) {
            this.faceDirection(this.takeoffFacing);
        }
        if (!this.landingRecover || this.recoverStage != 1) {
            this.mc.player.setPitch(-((float)((Double)this.startClimbAngle.get()).doubleValue()));
        }
        this.mc.options.forwardKey.setPressed(true);
        if (!this.mc.player.isGliding()) {
            if (this.mc.player.isOnGround()) {
                PathManagers.get().stop();
                this.mc.player.jump();
            } else if (this.stateTick % 4 == 0) {
                this.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
            return;
        }
        if (!this.firstFireworkDone) {
            this.useFirework();
            this.firstFireworkDone = true;
            this.lastFireworkTick = this.stateTick;
        } else if (!(this.landingRecover && this.recoverStage == 1 || (long)(this.stateTick - this.lastFireworkTick) < Math.max(1L, Math.round((Double)this.fireworkInterval.get() * 20.0)))) {
            this.useFirework();
            this.lastFireworkTick = this.stateTick;
        }
        if (this.landingRecover) {
            BlockPos lp = this.waypoints.landing;
            if (this.recoverStage == 0) {
                if (this.mc.player.getY() >= (double)(lp.getY() + 25) || this.recoverStageTick > 600) {
                    this.recoverStage = 1;
                    this.recoverStageTick = 0;
                    this.info((String)"已拉升到降落点上方 25 格，用 pitch40 模式飞向降落点.", new Object[0]);
                }
            } else if (this.horizontalDistance(lp) <= 3.0 || this.recoverStageTick > 600) {
                this.releaseForward();
                this.landingRecover = false;
                this.recoverStage = 0;
                this.recoverStageTick = 0;
                this.info((String)"已回到降落点上方，进入缓降.", new Object[0]);
                this.state = State.LANDING;
                this.stateTick = 0;
            }
        } else if (this.mc.player.getY() >= (double)((Integer)this.maxHeight.get()).intValue()) {
            if (this.ships.isEmpty()) {
                this.mc.player.setPitch(-20.0f);
                this.mc.options.forwardKey.setPressed(true);
            } else {
                this.releaseForward();
                this.firstFireworkDone = false;
                this.beginFlyToTarget();
            }
        }
    }

    private void onFlying() {
        if (!this.mc.player.isGliding()) {
            if (this.mc.player.isOnGround()) {
                this.firstFireworkDone = false;
                this.takeoffFacing = null;
                this.state = State.RISING;
                this.stateTick = 0;
                return;
            }
            this.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            return;
        }
        BlockPos lp = this.waypoints.landing;
        // path: 0 = run dragon-head detection (the search loop), then lbl40/lbl60/lbl62
        //       1 = skip detection, jump to lbl40 (the !ak && an>=7 fallback)
        //       2 = skip detection AND lbl40, jump to lbl60 (the ag reset)
        //       3 = skip everything except lbl62 (the elytra-display check)
        int path;
        if (this.headFound) {
            path = 3;
        } else if (!(this.horizontalDistance(lp) < 80.0)) {
            this.nearTicks = 0;
            path = 2;
        } else {
            ++this.nearTicks;
            if (this.nearTicks == 1 && ((Boolean)this.debugSetting.get()).booleanValue() && !this.cityTerrainHigh()) {
                this.debugLog((String)"注意: 地形高度判断不足 (船可能在虚空上)，改由龙头扫描确认.");
            }
            if (this.nearTicks % 20 != 0) {
                path = 1;
            } else {
                path = 0;
            }
        }
        if (path == 0) {
            boolean loaded = true;
            int dx = -1;
            while (true) {
                if (dx <= 1 && loaded) {
                    for (int dz = -1; dz <= 1; ++dz) {
                        if (this.mc.world.isChunkLoaded((this.current.headPos.getX() >> 4) + dx, (this.current.headPos.getZ() >> 4) + dz)) continue;
                        loaded = false;
                        break;
                    }
                    ++dx;
                    continue;
                }
                if (loaded) {
                    ++this.scanAttempts;
                    DragonHead exact = this.findDragonHead(this.current.headPos, 10, 40);
                    if (exact != null) {
                        this.exactHead = exact.pos;
                        this.waypoints = ShipWaypoints.from(this.exactHead, this.current.facing);
                        this.headFound = true;
                        this.info("已确认龙头: " + String.valueOf(this.exactHead) + " 朝向=" + String.valueOf(this.current.facing), new Object[0]);
                        if (((Boolean)this.debugSetting.get()).booleanValue()) {
                            int realTop = this.mc.world.getTopY(Heightmap.Type.WORLD_SURFACE, this.exactHead.getX(), this.exactHead.getZ());
                            this.debugLog("head=(" + this.exactHead.getX() + "," + this.exactHead.getY() + "," + this.exactHead.getZ() + ") 龙头下地表=" + realTop + " seedY=" + this.current.headPos.getY());
                        }
                    } else {
                        this.info("检测龙头第 " + this.scanAttempts + "/7 次未找到 (目标=" + this.current.headPos.getX() + "," + this.current.headPos.getZ() + ").", new Object[0]);
                    }
                }
                break;
            }
        }
        if (path <= 1) {
            if (!this.headFound && this.scanAttempts >= 7) {
                DragonHead last = this.findDragonHead(this.current.headPos, 10, 200);
                if (last == null) {
                    this.warning((String)"检测 7 次且整列无龙头，判定为假城，加入黑名单并重新搜索.", new Object[0]);
                    this.addBlacklist(this.current.headPos);
                    this.sessionSkip.add(this.blacklistKey(this.current.headPos));
                    this.ships.clear();
                    this.targetIndex = 0;
                    this.searchExtends = 0;
                    this.takeoffFacing = null;
                    this.firstFireworkDone = false;
                    this.state = State.RISING;
                    this.stateTick = 0;
                    this.launchSearch();
                    return;
                }
                this.exactHead = last.pos;
                this.waypoints = ShipWaypoints.from(this.exactHead, this.current.facing);
                this.headFound = true;
                this.info("整列扫描发现龙头: " + String.valueOf(this.exactHead) + " 朝向=" + String.valueOf(this.current.facing), new Object[0]);
            }
        }
        if (path <= 2) {
            if (this.headFound) {
                this.stateTick = 0;
            }
        }
        if (this.headFound && !this.frameAirChecked) {
            ItemFrameEntity airFrame;
            if (this.frameAirScanTicks % 20 == 0 && (airFrame = this.findFrameAt(this.waypoints.p3)) != null) {
                this.frameAirChecked = true;
                this.frameAirEmpty = airFrame.getHeldItemStack().getItem() != Items.ELYTRA;
                if (this.frameAirEmpty) {
                    this.info((String)"展示框里没有鞘翅，不降落：拉升到 max-height 以上后跳过该船.", new Object[0]);
                    this.leaveShipNow();
                    return;
                }
                this.info((String)"展示框里有鞘翅，按原计划降落.", new Object[0]);
            }
            ++this.frameAirScanTicks;
            if (this.frameAirScanTicks > 120) {
                this.frameAirChecked = true;
                this.frameAirEmpty = true;
                this.leaveShipNow();
                return;
            }
        }
        if (ElytraApproachSafety22.tick((Object)this)) {
            return;
        }
        float yaw = this.yawTowards(lp);
        double y = this.mc.player.getY();
        if (this.climbing) {
            if (y >= (double)((Integer)this.maxHeight.get()).intValue()) {
                this.climbing = false;
            }
        } else if (y <= (double)((Integer)this.minHeight.get()).intValue()) {
            this.climbing = true;
        }
        float pitch = this.climbing != false ? -((float)((Double)this.startClimbAngle.get()).doubleValue()) : (float)((Double)this.cruiseGlideAngle.get()).doubleValue();
        this.rotateTo(yaw, pitch);
        this.mc.options.forwardKey.setPressed(true);
        if (!this.climbing) {
            if (this.frameAirChecked == false) return;
            if (!(this.horizontalDistance(lp) <= 80.0)) return;
            this.state = State.LANDING;
            this.stateTick = 0;
            return;
        }
        if (this.mc.player.getY() < (double)((Integer)this.maxHeight.get()).intValue()) {
            this.climbing = true;
            if ((long)(this.stateTick - this.lastFireworkTick) < Math.max(1L, Math.round((Double)this.fireworkInterval.get() * 20.0))) return;
            this.useFirework();
            this.lastFireworkTick = this.stateTick;
            return;
        }
        this.leaveShipNow();
        return;
    }

    void abortStorageForPullUp() {
        if (this.storagePhase == StoragePhase.NONE) {
            return;
        }
        if (this.isContainerOpen()) {
            this.mc.player.closeHandledScreen();
        }
        PathManagers.get().stop();
        this.releaseForward();
        this.storagePhase = StoragePhase.NONE;
        this.storageTick = 0;
        this.resetStorageClick();
        this.info((String)"危险高度: 中止存储会话，强制拉升.", new Object[0]);
    }

    boolean isLandingOrRecovering() {
        return this.state == State.LANDING || this.landingRecover;
    }

    boolean isPullUpSuppressed() {
        return this.pullUpSuppressed;
    }

    void setPullUpSuppressed(boolean suppressed) {
        this.pullUpSuppressed = suppressed;
        if (!suppressed) {
            this.releaseForward();
        }
    }

    private void onLanding() {
        double ddz;
        double ddy;
        BlockPos lp = this.waypoints.landing;
        double hDist = this.horizontalDistance(lp);
        if (!this.landingRecover && !this.mc.player.isOnGround() && this.mc.player.getY() < (double)(lp.getY() - 10)) {
            ++this.recoverCount;
            if (this.recoverCount > 3) {
                this.stopTask((String)"多次偏离降落点无法降落，停止任务.");
                return;
            }
            this.landingRecover = true;
            this.recoverStage = 0;
            this.recoverStageTick = 0;
            this.recoveryYaw = this.yawTowards(lp) + 180.0f;
            this.firstFireworkDone = false;
            this.info((String)"低于降落点 10 格，反向拉升 (目标降落点上方 25 格).", new Object[0]);
            this.state = State.RISING;
            this.stateTick = 0;
            return;
        }
        if (this.mc.player.isOnGround()) {
            if (hDist < 3.0) {
                this.releaseForward();
                this.recoverCount = 0;
                this.info((String)"已降落在降落点.", new Object[0]);
                this.state = State.COLLECTING;
                this.collectStep = CollectStep.TO_P1;
                this.stateTick = 0;
                this.beginCollectStep();
                return;
            }
            if (this.stateTick < 6 || !PathManagers.get().isPathing()) {
                PathManagers.get().moveTo(lp, false);
            }
            if (this.stateTick > 6 && !PathManagers.get().isPathing() && hDist < 2.5) {
                this.releaseForward();
                this.state = State.COLLECTING;
                this.collectStep = CollectStep.TO_P1;
                this.stateTick = 0;
                this.beginCollectStep();
            }
            return;
        }
        if (!this.mc.player.isGliding()) {
            this.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            return;
        }
        double ddx = this.mc.player.getX() - ((double)lp.getX() + 0.5);
        if (ddx * ddx + (ddy = this.mc.player.getY() - ((double)lp.getY() + 0.5)) * ddy + (ddz = this.mc.player.getZ() - ((double)lp.getZ() + 0.5)) * ddz > 400.0) {
            this.mc.player.setYaw(this.yawTowards(lp));
            this.mc.player.setPitch(55.0f);
            this.mc.options.forwardKey.setPressed(true);
            return;
        }
        if (hDist > 3.0) {
            double dy = this.mc.player.getY() - (double)(lp.getY() + 1);
            this.mc.player.setYaw(this.yawTowards(lp));
            this.mc.player.setPitch((float)Math.min(45.0, Math.max(5.0, dy * 0.8)));
            this.mc.options.forwardKey.setPressed(true);
        } else {
            this.releaseForward();
            this.mc.player.setPitch(0.0f);
            if (this.stateTick % 2 == 0) {
                this.mc.player.setYaw(this.mc.player.getYaw() + 180.0f);
            }
        }
    }

    private void beginCollectStep() {
        if (this.waypoints == null) {
            this.state = State.RISING;
            return;
        }
        switch (this.collectStep.ordinal()) {
            case 0: {
                PathManagers.get().moveTo(this.waypoints.p1, false);
                break;
            }
            case 1: {
                PathManagers.get().moveTo(this.waypoints.p2, false);
                break;
            }
            case 3: {
                PathManagers.get().moveTo(this.waypoints.p3, false);
                break;
            }
            case 4: {
                this.elytraBeforeFrame = this.countElytraMain();
                break;
            }
            case 5: {
                this.pickUpElytra();
                break;
            }
            case 6: {
                this.equipElytra();
                break;
            }
            case 7: {
                PathManagers.get().moveTo(this.waypoints.p1, false);
                break;
            }
            case 8: {
                PathManagers.get().moveTo(this.waypoints.landing.down(4), false);
                break;
            }
            case 9: {
                PathManagers.get().moveTo(this.waypoints.landing.down(4), false);
                break;
            }
        }
    }

    private void onCollecting() {
        switch (this.collectStep.ordinal()) {
            case 0: {
                if (!this.reached(this.waypoints.p1, 2.5)) break;
                this.advance(CollectStep.TO_P2);
                break;
            }
            case 1: {
                if (!this.reached(this.waypoints.p2, 2.5)) break;
                this.advance(CollectStep.WAIT_SHULKER);
                break;
            }
            case 2: {
                if (!this.hasShulkerAtGuard()) {
                    this.advance(CollectStep.TO_P3);
                    break;
                }
                if (this.stateTick - this.lastGotoTick < 20) break;
                PathManagers.get().moveTo(this.waypoints.p2, false);
                this.lastGotoTick = this.stateTick;
                break;
            }
            case 3: {
                if (!this.reached(this.waypoints.p3, 2.0)) break;
                this.advance(CollectStep.HIT_FRAME);
                break;
            }
            case 4: {
                ItemFrameEntity frame = this.getNearestItemFrame((Double)this.killAuraReach.get() + 2.0);
                if (frame != null && this.stateTick == 1) {
                    boolean bl = this.frameHadElytra = frame.getHeldItemStack().getItem() == Items.ELYTRA;
                    if (!this.frameHadElytra) {
                        this.info((String)"展示框里没有鞘翅，直接离开该船.", new Object[0]);
                        this.safeExitAfterCollectFailure();
                        return;
                    }
                }
                if ((this.stateTick <= 1 || this.stateTick % 20 == 0) && this.noElytraTicks < 3) {
                    ++this.noElytraTicks;
                    this.hitItemFrame();
                }
                if (this.findDroppedElytra() != null || this.countElytraMain() > this.elytraBeforeFrame) {
                    this.advance(CollectStep.PICK_UP);
                    break;
                }
                if (this.stateTick <= 80) break;
                this.info((String)"攻击展示框后没有鞘翅掉落，直接离开该船.", new Object[0]);
                this.safeExitAfterCollectFailure();
                break;
            }
            case 5: {
                ItemEntity item = this.findDroppedElytra();
                if (item != null) {
                    this.walkTowards(item.getX(), item.getZ());
                    if (!(item.squaredDistanceTo((Entity)this.mc.player) < 1.2)) break;
                    this.mc.options.forwardKey.setPressed(false);
                    break;
                }
                if (this.countElytraMain() > this.elytraBeforeFrame) {
                    this.mc.options.forwardKey.setPressed(false);
                    this.advance(CollectStep.EQUIP);
                    break;
                }
                if (this.stateTick <= 60) break;
                this.info((String)"鞘翅掉落消失未拾取，直接离开该船.", new Object[0]);
                this.safeExitAfterCollectFailure();
                break;
            }
            case 6: {
                if (this.isWearingFullDurabilityElytra()) {
                    if (!this.pickupSessionDone) {
                        this.pickupSessionDone = true;
                        this.maybeStartSession();
                        if (this.storagePhase != StoragePhase.NONE) {
                            return;
                        }
                    }
                    this.advance(CollectStep.EXIT_P1);
                    break;
                }
                this.equipElytra();
                break;
            }
            case 7: {
                if (!this.reached(this.waypoints.p1, 2.5)) break;
                this.advance(CollectStep.EXIT_LANDING);
                break;
            }
            case 8: {
                if (!this.reached(this.waypoints.landing, 2.5)) break;
                this.addBlacklist(this.current.headPos);
                this.info((String)"该船完成，加入黑名单.", new Object[0]);
                this.finishShip();
                break;
            }
            case 9: {
                if (!this.reached(this.waypoints.landing.down(4), 2.5)) break;
                this.releaseForward();
                this.info((String)"已回到降落点，起飞离开.", new Object[0]);
                this.state = State.RISING;
                this.stateTick = 0;
            }
        }
    }

    private void advance(CollectStep next) {
        this.collectStep = next;
        this.stateTick = 0;
        this.lastGotoTick = 0;
        this.noElytraTicks = 0;
        this.frameHadElytra = false;
        this.pickupSessionDone = false;
        this.beginCollectStep();
    }

    private void stopTask(String reason) {
        this.info(reason, new Object[0]);
        this.searchCancelled = true;
        ++this.searchGeneration;
        this.searchThread = null;
        this.storagePhase = StoragePhase.NONE;
        this.storageTick = 0;
        this.landingRecover = false;
        this.recoveryYaw = 0.0f;
        this.recoverStageTick = 0;
        this.recoverCount = 0;
        this.releaseForward();
        this.resetStorageClick();
        if (this.state != State.IDLE) {
            this.state = State.IDLE;
        }
        PathManagers.get().stop();
        this.btnStart.set(false);
    }

    private void completeTask(String reason) {
        this.stopTask(reason);
    }

    private void finishShip() {
        this.maybeStartSession();
        if (this.storagePhase == StoragePhase.NONE) {
            this.relaunchAfterShip();
        }
    }

    private void maybeStartSession() {
        this.dumpNeeded = this.freeSlots() <= (Integer)this.freeSlotDump.get();
        this.resupplyNeeded = this.anyDeficit();
        if (this.dumpNeeded || this.resupplyNeeded) {
            this.info("存储会话: 存鞘翅=" + this.dumpNeeded + ", 补货=" + this.resupplyNeeded, new Object[0]);
            StorageReturn.markStart((Object)this);
            this.storagePhase = StoragePhase.PLACE_EC;
            this.storageTick = 0;
            this.ecPos = null;
            this.boxPos = null;
            this.boxInventorySlot = -1;
            this.boxFromInventory = false;
            this.supplyScanStart = 0;
            this.supplyPauseDump = false;
            this.resupplyRescan = false;
            this.releaseForward();
            PathManagers.get().stop();
        }
    }

    private void relaunchAfterShip() {
        this.ships.clear();
        this.targetIndex = 0;
        this.searchExtends = 0;
        this.takeoffFacing = this.current != null ? this.current.facing : null;
        this.firstFireworkDone = false;
        this.landingRecover = false;
        this.state = State.RISING;
        this.stateTick = 0;
        this.launchSearch();
    }

    private void leaveShipNow() {
        this.addBlacklist(this.current.headPos);
        this.info((String)"该船跳过并加入黑名单，重新搜索最近未去过的船.", new Object[0]);
        this.ships.clear();
        this.targetIndex = 0;
        this.searchExtends = 0;
        this.takeoffFacing = this.current.facing;
        this.firstFireworkDone = false;
        this.landingRecover = false;
        this.releaseForward();
        this.launchSearch();
        if (this.mc.player.isOnGround() && this.waypoints != null && this.horizontalDistance(this.waypoints.landing) > 2.5) {
            this.info((String)"已降落：先回降落点修正起飞方向后再离开.", new Object[0]);
            this.state = State.COLLECTING;
            this.collectStep = CollectStep.LEAVE_SHIP;
            this.stateTick = 0;
            this.beginCollectStep();
        } else {
            this.state = State.RISING;
            this.stateTick = 0;
        }
    }

    private void onStorageTick() {
        try {
            ++this.storageTick;
            if (this.storageTick > 1200) {
                this.stopTask("存储会话超时 (阶段=" + String.valueOf((Object)this.storagePhase) + ")，停止任务.");
                return;
            }
            this.releaseForward();
            this.execMoves();
            switch (this.storagePhase.ordinal()) {
                case 1: {
                    this.onPlaceEc();
                    break;
                }
                case 2: {
                    this.onOpenEc();
                    break;
                }
                case 3: {
                    this.onPickBox();
                    break;
                }
                case 4: {
                    this.onCloseScreen();
                    break;
                }
                case 5: {
                    this.onPlaceBox();
                    break;
                }
                case 6: {
                    this.onOpenBox();
                    break;
                }
                case 7: {
                    this.onFillBox();
                    break;
                }
                case 8: {
                    if (this.freeSlots() <= (Integer)this.freeSlotDump.get() && this.countElytraMain() > 0) {
                        this.supplyPauseDump = true;
                        this.resupplyRescan = true;
                        this.dumpNeeded = true;
                        this.info("补给暂停: 背包剩余 " + this.freeSlots() + " 槽，先回收补给盒、存鞘翅腾背包.", new Object[0]);
                        this.storagePhase = StoragePhase.MINE_BOX;
                        this.storageTick = 0;
                        break;
                    }
                    this.onTakeSupplies();
                    break;
                }
                case 9: {
                    this.onMineBox();
                    break;
                }
                case 10: {
                    this.onMineEc();
                    break;
                }
            }
        }
        catch (Throwable t) {
            this.error("存储会话异常: " + String.valueOf(t), new Object[0]);
            t.printStackTrace();
            this.stopTask((String)"存储会话异常，停止任务.");
        }
    }

    private void onPlaceEc() {
        if (this.storageTick == 1) {
            BlockPos existing;
            if (this.ecOpenFailCount == 0 && (existing = this.findNearbyEnderChest(5)) != null) {
                this.ecPos = existing;
                this.info("存储: 发现周围已有末影箱 " + String.valueOf(existing) + "，直接打开.", new Object[0]);
                this.storagePhase = StoragePhase.OPEN_EC;
                this.storageTick = 0;
                this.openRetry = 0;
                return;
            }
            int ecSlot = this.findItemSlot(Items.ENDER_CHEST);
            if (ecSlot == -1) {
                this.stopTask((String)"背包里没有末影箱，停止任务.");
                return;
            }
            this.ecBaseline = this.countItem(Items.ENDER_CHEST);
            this.ensureItemSelected(ecSlot);
            this.placeCandidates = this.findPlaceSpots(null);
            if (this.placeCandidates.isEmpty() && this.waypoints != null && this.waypoints.p2 != null) {
                this.info("附近无空位，改用检测潜影贝的位置 " + String.valueOf(this.waypoints.p2) + " 附近放置.", new Object[0]);
                this.placeCandidates = this.findPlaceSpotsAt(this.waypoints.p2, null);
            }
            if (this.placeCandidates.isEmpty()) {
                this.stopTask((String)"找不到放置末影箱的位置，停止任务.");
                return;
            }
            this.placeIndex = 0;
            this.lastPlaceClick = -100;
            this.placeClickAttempts = 0;
        }
        if (this.storageTick >= 4 && this.storageTick - this.lastPlaceClick >= 8) {
            if (this.placeIndex >= this.placeCandidates.size()) {
                this.stopTask((String)"末影箱放置失败 (已尝试所有候选位置)，停止任务.");
                return;
            }
            BlockPos spot = this.placeCandidates.get(this.placeIndex);
            if (this.mc.player.squaredDistanceTo((double)spot.getX() + 0.5, (double)spot.getY() + 0.5, (double)spot.getZ() + 0.5) > 16.0) {
                if (!PathManagers.get().isPathing()) {
                    PathManagers.get().moveTo(spot, false);
                }
                return;
            }
            this.lastPlaceClick = this.storageTick;
            ++this.placeClickAttempts;
            this.ecPos = spot;
            this.faceBlockForPlace(spot.down(), Direction.UP);
            this.interactAt(spot.down());
            if (this.placeClickAttempts >= 2) {
                ++this.placeIndex;
                this.placeClickAttempts = 0;
            }
        }
        if (this.ecPos != null && this.isEnderChestBlock(this.mc.world.getBlockState(this.ecPos))) {
            PathManagers.get().stop();
            this.storagePhase = StoragePhase.OPEN_EC;
            this.storageTick = 0;
            this.openRetry = 0;
            return;
        }
        if (this.storageTick > 80) {
            this.stopTask((String)"末影箱放置失败 (超时)，停止任务.");
        }
    }

    private BlockPos findNearbyEnderChest(int radius) {
        BlockPos.Mutable m = new BlockPos.Mutable();
        BlockPos p = this.mc.player.getBlockPos();
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                for (int dy = -2; dy <= 2; ++dy) {
                    m.set(p.getX() + dx, p.getY() + dy, p.getZ() + dz);
                    if (!this.isEnderChestBlock(this.mc.world.getBlockState((BlockPos)m))) continue;
                    return m.toImmutable();
                }
            }
        }
        return null;
    }

    private void onOpenEc() {
        if (this.storageTick == 1) {
            if (this.ecPos == null || !this.isEnderChestBlock(this.mc.world.getBlockState(this.ecPos))) {
                this.info((String)"末影箱不在原位，重新放置.", new Object[0]);
                this.storagePhase = StoragePhase.PLACE_EC;
                this.storageTick = 0;
                return;
            }
            this.standingMovedOff = false;
        }
        if (!this.standingMovedOff && this.isStandingOnBlock(this.ecPos)) {
            BlockPos off = this.findStandOffSpot(this.ecPos);
            if (off != null && !PathManagers.get().isPathing()) {
                PathManagers.get().moveTo(off, false);
                this.info((String)"存储: 玩家站在末影箱上，先走下来再打开.", new Object[0]);
            }
            if (this.isStandingOnBlock(this.ecPos)) {
                return;
            }
            this.standingMovedOff = true;
        }
        if (this.openRetry < 5 && this.storageTick == 1 + this.openRetry * 20) {
            this.faceBlockForPlace(this.ecPos, Direction.UP);
            this.interactAt(this.ecPos);
            ++this.openRetry;
        }
        if (this.isContainerOpen()) {
            if (this.storageTick < 3) {
                return;
            }
            this.ecOpenFailCount = 0;
            this.storagePhase = StoragePhase.PICK_BOX;
            this.storageTick = 0;
        } else if (this.storageTick > 93) {
            if (this.isStandingOnBlock(this.ecPos)) {
                this.standingMovedOff = false;
                this.storageTick = 0;
                this.info((String)"打开末影箱失败且玩家站在末影箱上，先走下来再重试.", new Object[0]);
                return;
            }
            this.warning((String)"打开末影箱失败 (已尝试 5 次)，放置新的末影箱再试.", new Object[0]);
            ++this.ecOpenFailCount;
            this.storagePhase = StoragePhase.PLACE_EC;
            this.storageTick = 0;
            this.ecPos = null;
            this.openRetry = 0;
        }
    }

    private void onPickBox() {
        ScreenHandler sh = this.mc.player.currentScreenHandler;
        int rows = this.currentRows();
        if (sh == null || rows <= 0) {
            this.stopTask((String)"末影箱界面异常，停止任务.");
            return;
        }
        if (this.storageTick < 3) {
            return;
        }
        if (this.pickStep == 1) {
            if (!this.moveIdle()) {
                return;
            }
            int emptyScreen = this.firstEmptyPlayerScreenSlot(sh, rows);
            if (emptyScreen != -1) {
                this.pickInvSlot = emptyScreen - rows * 9 + 9;
                this.moveQueue.add(new MoveOp(-1, emptyScreen));
                this.pickStep = 2;
            } else {
                this.moveQueue.add(new MoveOp(-1, this.pickEcSlot));
                this.pickStep = 3;
            }
            return;
        }
        if (this.pickStep == 2) {
            if (!this.moveIdle()) {
                return;
            }
            this.boxInventorySlot = this.pickInvSlot;
            this.boxFromInventory = false;
            this.pickStep = 0;
            this.afterClose = StoragePhase.PLACE_BOX;
            this.storagePhase = StoragePhase.CLOSE_SCREEN;
            this.storageTick = 0;
            return;
        }
        if (this.pickStep == 3) {
            if (!this.moveIdle()) {
                return;
            }
            this.pickStep = 0;
            if (this.boxPurposeSupply) {
                this.stopTask((String)"背包无空位放潜影盒，无法补货，停止任务.");
                return;
            }
            this.stopTask((String)"背包无空位放潜影盒，无法存鞘翅，停止任务.");
            return;
        }
        if (!this.moveIdle()) {
            return;
        }
        if (this.boxInventorySlot != -1) {
            int screenSlot = this.invSlotToScreen(rows, this.boxInventorySlot);
            if (this.firstEmptyContainerSlot(sh, rows) == -1) {
                this.stopTask((String)"末影箱已满，无法归还潜影盒，停止任务.");
                return;
            }
            this.click(SlotActionType.QUICK_MOVE, screenSlot);
            this.boxInventorySlot = -1;
            return;
        }
        int nonEmpty = 0;
        StringBuilder items = new StringBuilder();
        for (int i = 0; i <= this.containerLast(rows); ++i) {
            ItemStack st = sh.getSlot(i).getStack();
            if (st.isEmpty()) continue;
            ++nonEmpty;
            if (items.length() >= 80) continue;
            Item item = st.getItem();
            items.append(item.getName(item.getDefaultStack()).getString()).append((String)"(").append(st.getCount()).append((String)") ");
        }
        this.info("存储: 末影箱行数=" + rows + " 非空槽=" + nonEmpty + " [" + String.valueOf(items) + "]", new Object[0]);
        if (this.dumpNeeded) {
            boolean bl = this.dumpNeeded = this.countElytraMain() > 0;
        }
        if (!this.dumpNeeded && !this.resupplyNeeded) {
            this.storagePhase = StoragePhase.MINE_EC;
            this.storageTick = 0;
            return;
        }
        if (this.dumpNeeded) {
            int bpBox = this.findBackpackBoxWithSpace();
            if (bpBox != -1) {
                this.boxInventorySlot = bpBox;
                this.boxFromInventory = true;
                this.pickInvSlot = bpBox;
                this.boxPurposeSupply = false;
                this.afterClose = StoragePhase.PLACE_BOX;
                this.storagePhase = StoragePhase.CLOSE_SCREEN;
                this.storageTick = 0;
                return;
            }
            int ecBox = this.findEcBoxWithSpace(sh, rows);
            if (ecBox != -1) {
                this.pickEcSlot = ecBox;
                this.boxPurposeSupply = false;
                this.pickStep = 1;
                this.moveQueue.add(new MoveOp(ecBox, -1));
                return;
            }
            if (this.freeSlots() == 0) {
                this.completeTask((String)"背包与末影箱全部放满，任务完成.");
                return;
            }
            this.info((String)"没有空位潜影盒，鞘翅留在背包.", new Object[0]);
            this.dumpNeeded = false;
        }
        if (this.resupplyNeeded) {
            if (this.resupplyRescan) {
                this.resupplyRescan = false;
                this.supplyScanStart = 0;
                this.info((String)"存储: 存鞘翅腾出空间，从末影箱开头重新扫描补给.", new Object[0]);
            }
            if (this.supplyScanStart > this.containerLast(rows)) {
                if (this.anyDeficit()) {
                    this.stopTask((String)"物资不足 (末影箱内已遍历完所有潜影盒)，停止任务.");
                    return;
                }
                this.info((String)"存储: 补货遍历完成.", new Object[0]);
                this.resupplyNeeded = false;
                this.storagePhase = StoragePhase.MINE_EC;
                this.storageTick = 0;
                return;
            }
            List<SupplyRule> rules = this.parseSupplies();
            for (int i = this.supplyScanStart; i <= this.containerLast(rows); ++i) {
                ItemStack st = sh.getSlot(i).getStack();
                if (!st.isEmpty()) {
                    if (!this.isShulkerBox(st)) {
                        if (!this.takeFailedItems.contains(st.getItem()) && this.needsItem(st.getItem())) {
                            this.supplyScanStart = i + 1;
                            this.click(SlotActionType.QUICK_MOVE, i);
                            Item item = st.getItem();
                            this.info("存储: 从末影箱直接拿取散放物资 " + item.getName(item.getDefaultStack()).getString() + ".", new Object[0]);
                        } else {
                            this.supplyScanStart = i + 1;
                        }
                        return;
                    }
                    boolean hasSupply = false;
                    for (SupplyRule r : rules) {
                        if (this.takeFailedItems.contains(r.item) || !this.needsItem(r.item) || !this.shulkerItems(st).contains(r.item)) continue;
                        hasSupply = true;
                        break;
                    }
                    if (hasSupply) {
                        this.supplyScanStart = i + 1;
                        this.pickEcSlot = i;
                        this.boxPurposeSupply = true;
                        this.pickStep = 1;
                        this.moveQueue.add(new MoveOp(i, -1));
                        return;
                    }
                    this.supplyScanStart = i + 1;
                    return;
                }
                this.supplyScanStart = i + 1;
            }
            this.supplyScanStart = this.containerLast(rows) + 1;
            return;
        }
        this.storagePhase = StoragePhase.MINE_EC;
        this.storageTick = 0;
    }

    private void onCloseScreen() {
        if (this.isContainerOpen()) {
            if (this.storageTick == 1) {
                this.mc.player.closeHandledScreen();
            }
            if (this.storageTick > 40) {
                this.stopTask((String)"关闭容器界面超时，停止任务.");
                return;
            }
            return;
        }
        if (this.storageTick < 4) {
            return;
        }
        this.storagePhase = this.afterClose;
        this.afterClose = StoragePhase.NONE;
        this.storageTick = 0;
    }

    private void onPlaceBox() {
        if (this.storageTick == 1) {
            if (this.boxInventorySlot == -1) {
                this.stopTask((String)"潜影盒槽位丢失，停止任务.");
                return;
            }
            this.boxInventorySlot = this.ensureItemSelected(this.boxInventorySlot);
            this.placeCandidates = this.findBoxPlaceSpots(this.ecPos);
            this.boxPlaceFloorFallback = false;
            if (this.placeCandidates.isEmpty()) {
                this.info((String)"附近没有挂放位置 (空气+上方实心)，改用地板放置.", new Object[0]);
                this.placeCandidates = this.findBoxFloorSpots(this.ecPos);
                this.boxPlaceFloorFallback = true;
            }
            if (this.placeCandidates.isEmpty()) {
                this.stopTask((String)"找不到放置潜影盒的位置 (需要空气格且上方有方块)，停止任务.");
                return;
            }
            this.placeIndex = 0;
            this.lastPlaceClick = -100;
            this.placeClickAttempts = 0;
        }
        if (this.storageTick >= 4 && this.storageTick - this.lastPlaceClick >= 8) {
            BlockPos spot;
            if (this.placeIndex >= this.placeCandidates.size()) {
                if (!this.boxPlaceFloorFallback) {
                    this.info((String)"挂放位置全部放置失败，改用地板放置.", new Object[0]);
                    this.placeCandidates = this.findBoxFloorSpots(this.ecPos);
                    this.boxPlaceFloorFallback = true;
                    this.placeIndex = 0;
                    this.placeClickAttempts = 0;
                    if (this.placeCandidates.isEmpty()) {
                        this.stopTask((String)"潜影盒放置失败 (找不到地板放置位置)，停止任务.");
                        return;
                    }
                } else {
                    this.stopTask((String)"潜影盒放置失败 (已尝试所有候选位置)，停止任务.");
                    return;
                }
            }
            if (this.mc.player.squaredDistanceTo((double)(spot = this.placeCandidates.get(this.placeIndex)).getX() + 0.5, (double)spot.getY() + 0.5, (double)spot.getZ() + 0.5) > 16.0) {
                if (!PathManagers.get().isPathing()) {
                    PathManagers.get().moveTo(spot, false);
                }
                return;
            }
            this.lastPlaceClick = this.storageTick;
            ++this.placeClickAttempts;
            this.boxPos = spot;
            this.boxBaseline = this.countBoxes();
            if (this.boxPlaceFloorFallback) {
                this.faceBlockForPlace(spot.down(), Direction.UP);
                this.interactAt(spot.down(), Direction.UP);
            } else {
                this.faceBlockForPlace(spot.up(), Direction.DOWN);
                this.interactAt(spot.up(), Direction.DOWN);
            }
            if (this.placeClickAttempts >= 2) {
                ++this.placeIndex;
                this.placeClickAttempts = 0;
            }
        }
        if (this.boxPos != null && this.isShulkerBlock(this.mc.world.getBlockState(this.boxPos))) {
            PathManagers.get().stop();
            this.storagePhase = StoragePhase.OPEN_BOX;
            this.storageTick = 0;
            return;
        }
        if (this.storageTick > 160) {
            this.stopTask((String)"潜影盒放置失败 (超时)，停止任务.");
        }
    }

    private void onOpenBox() {
        if (this.storageTick == 1) {
            if (this.boxPos == null || !this.isShulkerBlock(this.mc.world.getBlockState(this.boxPos))) {
                this.warning((String)"潜影盒不在原位，挖掉重来.", new Object[0]);
                this.storagePhase = StoragePhase.MINE_BOX;
                this.storageTick = 0;
                return;
            }
            this.standingMovedOff = false;
        }
        if (!this.standingMovedOff && this.isStandingOnBlock(this.boxPos)) {
            BlockPos off = this.findStandOffSpot(this.boxPos);
            if (off != null && !PathManagers.get().isPathing()) {
                PathManagers.get().moveTo(off, false);
                this.info((String)"存储: 玩家站在潜影盒上，先走下来再打开.", new Object[0]);
            }
            if (this.isStandingOnBlock(this.boxPos)) {
                return;
            }
            this.standingMovedOff = true;
        }
        if (this.openRetry < 5 && this.storageTick == 1 + this.openRetry * 20) {
            this.lookAtBoxCenter();
            ++this.openRetry;
        }
        if (this.openRetry > 0 && this.boxOpenClickTick == -1 && this.isLookingAtBox()) {
            this.boxOpenClickTick = this.storageTick;
            Utils.rightClick();
        } else if (this.boxOpenClickTick != -1 && !this.isContainerOpen() && this.storageTick - this.boxOpenClickTick > 8) {
            this.boxOpenClickTick = -1;
        }
        if (this.isContainerOpen()) {
            if (this.storageTick < 3) {
                return;
            }
            this.storagePhase = this.boxPurposeSupply ? StoragePhase.TAKE_SUPPLIES : StoragePhase.FILL_BOX;
            this.storageTick = 0;
            this.openRetry = 0;
            this.boxOpenClickTick = -1;
        } else if (this.storageTick > 93) {
            if (this.isStandingOnBlock(this.boxPos)) {
                this.standingMovedOff = false;
                this.storageTick = 0;
                this.openRetry = 0;
                this.boxOpenClickTick = -1;
                this.info((String)"打开潜影盒失败且玩家站在潜影盒上，先走下来再重试.", new Object[0]);
                return;
            }
            this.warning((String)"打开潜影盒失败 (已尝试 5 次)，挖掉重来.", new Object[0]);
            this.storagePhase = StoragePhase.MINE_BOX;
            this.storageTick = 0;
            this.openRetry = 0;
            this.boxOpenClickTick = -1;
        }
    }

    private void onFillBox() {
        ScreenHandler sh = this.mc.player.currentScreenHandler;
        int rows = this.currentRows();
        if (sh == null || rows <= 0) {
            StorageRecovery.onInvalidScreen((Object)this);
            return;
        }
        StorageRecovery.onValidScreen((Object)this);
        if (this.storageTick < 3) {
            return;
        }
        if (!this.moveIdle()) {
            return;
        }
        if (this.fillIndex == -1) {
            this.fillIndex = this.playerFirst(rows);
            this.info((String)"存储: 开始往潜影盒存鞘翅.", new Object[0]);
        }
        if (this.fillStuckSlot >= 0) {
            if (sh.getSlot(this.fillStuckSlot).getStack().getItem() == Items.ELYTRA) {
                ++this.fillStuckTicks;
                if (this.fillStuckTicks > 20) {
                    this.fillIndex = -1;
                    this.fillStuckSlot = -1;
                    this.fillStuckTicks = 0;
                    this.info((String)"存储: 潜影盒已满.", new Object[0]);
                    this.afterClose = StoragePhase.MINE_BOX;
                    this.storagePhase = StoragePhase.CLOSE_SCREEN;
                    this.storageTick = 0;
                }
                return;
            }
            this.fillStuckSlot = -1;
            this.fillStuckTicks = 0;
        }
        int src = -1;
        for (int s = this.fillIndex; s <= this.playerLast(rows); ++s) {
            if (sh.getSlot(s).getStack().getItem() != Items.ELYTRA) continue;
            src = s;
            break;
        }
        if (src == -1) {
            this.fillIndex = -1;
            this.info((String)"存储: 鞘翅已全部存入潜影盒.", new Object[0]);
            this.afterClose = StoragePhase.MINE_BOX;
            this.storagePhase = StoragePhase.CLOSE_SCREEN;
            this.storageTick = 0;
            return;
        }
        this.fillIndex = src + 1;
        this.fillStuckSlot = src;
        this.fillStuckTicks = 0;
        this.click(SlotActionType.QUICK_MOVE, src);
    }

    private void onTakeSupplies() {
        ScreenHandler sh = this.mc.player.currentScreenHandler;
        int rows = this.currentRows();
        if (sh == null || rows <= 0) {
            this.stopTask((String)"潜影盒界面异常，停止任务.");
            return;
        }
        if (this.storageTick < 3) {
            return;
        }
        if (!this.moveIdle()) {
            return;
        }
        SupplyRule target = null;
        List<SupplyRule> rules = this.parseSupplies();
        for (SupplyRule rule : rules) {
            if (this.takeFailedItems.contains(rule.item) || rule.batch - this.countItem(rule.item) <= 0) continue;
            for (int c = 0; c <= this.containerLast(rows); ++c) {
                ItemStack st = sh.getSlot(c).getStack();
                if (st.isEmpty() || st.getItem() != rule.item) continue;
                target = rule;
                break;
            }
            if (target == null) continue;
            break;
        }
        if (target != null) {
            for (int c = 0; c <= this.containerLast(rows); ++c) {
                ItemStack st = sh.getSlot(c).getStack();
                if (st.isEmpty() || st.getItem() != target.item) continue;
                if (this.takeStuckSlot == c) {
                    ++this.takeStuckTicks;
                    if (this.takeStuckTicks > 30) {
                        this.takeStuckSlot = -1;
                        this.takeStuckTicks = 0;
                        int boxFree = this.firstEmptyContainerSlot(sh, rows);
                        if (boxFree != -1) {
                            int elytra = this.findUnwornElytraSlot(sh, rows);
                            if (elytra != -1) {
                                this.moveQueue.add(new MoveOp(elytra, boxFree));
                                this.info((String)"存储: 背包空间不足，先把鞘翅放入盒子腾出空位.", new Object[0]);
                            } else {
                                int junk = this.findJunkSlotInPlayer(sh, rows, rules);
                                if (junk != -1) {
                                    this.moveQueue.add(new MoveOp(junk, boxFree));
                                    this.info((String)"存储: 背包已满，先把杂物放入盒子腾出空位.", new Object[0]);
                                } else {
                                    this.takeFailedItems.add(target.item);
                                    Item item = target.item;
                                    this.info("存储: 背包无空位且无杂物可腾，补货 " + item.getName(item.getDefaultStack()).getString() + " 跳过.", new Object[0]);
                                }
                            }
                        } else {
                            this.takeFailedItems.add(target.item);
                            Item item = target.item;
                            this.info("存储: 盒子已满无法腾位，补货 " + item.getName(item.getDefaultStack()).getString() + " 跳过.", new Object[0]);
                        }
                    }
                    return;
                }
                this.takeStuckSlot = c;
                this.takeStuckTicks = 0;
                this.click(SlotActionType.QUICK_MOVE, c);
                return;
            }
            this.takeStuckSlot = -1;
            this.takeStuckTicks = 0;
        }
        ++this.takeEmptyTicks;
        if (this.takeEmptyTicks < 10) {
            return;
        }
        this.takeEmptyTicks = 0;
        this.takeStuckSlot = -1;
        this.takeStuckTicks = 0;
        this.info((String)"存储: 本盒物资已拿取完毕 (所有能拿且需要的).", new Object[0]);
        this.afterClose = StoragePhase.MINE_BOX;
        this.storagePhase = StoragePhase.CLOSE_SCREEN;
        this.storageTick = 0;
    }

    private void onMineBox() {
        BlockState s = this.mc.world.getBlockState(this.boxPos);
        if (this.isShulkerBlock(s)) {
            if (this.storageTick == 1) {
                this.pickupWaitStart = -1;
                PathManagers.get().stop();
                if (this.findSilkTouchSlot() == -1) {
                    this.stopTask((String)"需要精准采集工具才能回收潜影盒，停止任务.");
                    return;
                }
                PathManagers.get().mine(s.getBlock());
                this.info((String)"用 baritone (精准采集) 挖掘潜影盒.", new Object[0]);
            } else if (this.storageTick > 600) {
                this.stopTask((String)"baritone 挖掘潜影盒超时，停止任务.");
            }
            return;
        }
        if (this.pickupWaitStart == -1) {
            this.pickupWaitStart = this.storageTick;
            PathManagers.get().stop();
        }
        if (this.countBoxes() >= this.boxBaseline) {
            this.boxInventorySlot = this.pickInvSlot >= 0 && this.pickInvSlot < 36 && this.isShulkerBox((ItemStack)this.mc.player.getInventory().getMainStacks().get(this.pickInvSlot)) ? this.pickInvSlot : this.findBackpackShulkerBox();
            this.storagePhase = this.dumpNeeded || this.resupplyNeeded ? StoragePhase.OPEN_EC : StoragePhase.MINE_EC;
            this.storageTick = 0;
            this.pickupWaitStart = -1;
            return;
        }
        if (this.storageTick > this.pickupWaitStart + 200) {
            this.warning((String)"潜影盒掉落未拾取，继续任务.", new Object[0]);
            this.boxInventorySlot = -1;
            this.boxFromInventory = false;
            this.storagePhase = this.dumpNeeded || this.resupplyNeeded ? StoragePhase.OPEN_EC : StoragePhase.MINE_EC;
            this.storageTick = 0;
            this.pickupWaitStart = -1;
            return;
        }
        ItemEntity it = this.findItemEntityNear(this.boxPos, 8.0);
        if (it != null && !PathManagers.get().isPathing()) {
            PathManagers.get().moveTo(it.getBlockPos(), false);
        }
    }

    private void onMineEc() {
        BlockState s = this.mc.world.getBlockState(this.ecPos);
        if (this.isEnderChestBlock(s)) {
            if (this.storageTick == 1) {
                this.pickupWaitStart = -1;
                PathManagers.get().stop();
                if (this.findSilkTouchSlot() == -1) {
                    this.stopTask((String)"需要精准采集工具才能回收末影箱，停止任务.");
                    return;
                }
                PathManagers.get().mine(Blocks.ENDER_CHEST);
                this.info((String)"用 baritone (精准采集) 挖掘末影箱.", new Object[0]);
            } else if (this.storageTick > 600) {
                this.stopTask((String)"baritone 挖掘末影箱超时，停止任务.");
            }
            return;
        }
        if (this.pickupWaitStart == -1) {
            this.pickupWaitStart = this.storageTick;
            PathManagers.get().stop();
        }
        if (this.countItem(Items.ENDER_CHEST) >= this.ecBaseline) {
            this.afterStorageSession();
            return;
        }
        if (this.storageTick > this.pickupWaitStart + 400) {
            this.warning((String)"末影箱掉落未拾取 (挖末影箱需要精准采集)，继续任务.", new Object[0]);
            this.afterStorageSession();
            return;
        }
        ItemEntity it = this.findItemEntityNear(this.ecPos, 8.0);
        if (it != null && !PathManagers.get().isPathing()) {
            PathManagers.get().moveTo(it.getBlockPos(), false);
        }
    }

    private void afterStorageSession() {
        this.storagePhase = StoragePhase.NONE;
        this.storageTick = 0;
        this.pickupWaitStart = -1;
        if (this.state == State.COLLECTING && this.collectStep == CollectStep.EQUIP) {
            this.info((String)"船旁存储会话结束，先走回降落点再起飞.", new Object[0]);
            this.advance(CollectStep.EXIT_P1);
        } else {
            StorageReturn.begin((Object)this);
        }
    }

    private void click(SlotActionType type, int slotId) {
        ScreenHandler sh = this.mc.player.currentScreenHandler;
        if (sh == null) {
            return;
        }
        this.mc.interactionManager.clickSlot(sh.syncId, slotId, 0, type, (PlayerEntity)this.mc.player);
    }

    private void interactAt(BlockPos pos) {
        this.interactAt(pos, Direction.UP);
    }

    private void interactAt(BlockPos pos, Direction face) {
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter((Vec3i)pos), face, pos, false);
        this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, hit);
    }

    private void faceBlockForPlace(BlockPos target, Direction face) {
        double cx = (double)target.getX() + 0.5 + (double)face.getOffsetX() * 0.5;
        double cy = (double)target.getY() + 0.5 + (double)face.getOffsetY() * 0.5;
        double cz = (double)target.getZ() + 0.5 + (double)face.getOffsetZ() * 0.5;
        double dx = cx - this.mc.player.getX();
        double dy = cy - (this.mc.player.getY() + (double)this.mc.player.getStandingEyeHeight());
        double dz = cz - this.mc.player.getZ();
        float yaw = (float)Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float)(-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))));
        this.mc.player.setYaw(yaw);
        this.mc.player.setPitch(pitch);
        this.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, this.mc.player.isOnGround(), this.mc.player.horizontalCollision));
    }

    private void lookAtBoxCenter() {
        double cx = (double)this.boxPos.getX() + 0.5;
        double cy = (double)this.boxPos.getY() + 0.5;
        double cz = (double)this.boxPos.getZ() + 0.5;
        double dx = cx - this.mc.player.getX();
        double dy = cy - (this.mc.player.getY() + (double)this.mc.player.getStandingEyeHeight());
        double dz = cz - this.mc.player.getZ();
        float yaw = (float)Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float)(-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))));
        this.mc.player.setYaw(yaw);
        this.mc.player.setPitch(pitch);
        this.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, this.mc.player.isOnGround(), this.mc.player.horizontalCollision));
    }

    private boolean isLookingAtBox() {
        BlockHitResult bhr;
        HitResult hitResult = this.mc.crosshairTarget;
        return hitResult instanceof BlockHitResult && (bhr = (BlockHitResult)hitResult).getBlockPos().equals((Object)this.boxPos);
    }

    private boolean isStandingOnBlock(BlockPos pos) {
        if (pos == null) {
            return false;
        }
        BlockPos feet = this.mc.player.getBlockPos();
        return feet.getX() == pos.getX() && feet.getY() == pos.getY() + 1 && feet.getZ() == pos.getZ();
    }

    private BlockPos findStandOffSpot(BlockPos pos) {
        if (pos == null) {
            return null;
        }
        BlockPos.Mutable m = new BlockPos.Mutable();
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                if (dx == 0 && dz == 0) continue;
                m.set(pos.getX() + dx, pos.getY() + 1, pos.getZ() + dz);
                if (!this.mc.world.getBlockState((BlockPos)m).isReplaceable() || this.mc.world.getBlockState(m.down()).isReplaceable()) continue;
                return m.toImmutable();
            }
        }
        return null;
    }

    private boolean isContainerOpen() {
        ScreenHandler sh = this.mc.player.currentScreenHandler;
        return sh != null && !(sh instanceof PlayerScreenHandler);
    }

    private int currentRows() {
        ScreenHandler sh = this.mc.player.currentScreenHandler;
        if (sh instanceof GenericContainerScreenHandler) {
            GenericContainerScreenHandler g = (GenericContainerScreenHandler)sh;
            return g.getRows();
        }
        if (sh instanceof ShulkerBoxScreenHandler) {
            return 3;
        }
        return -1;
    }

    private int containerLast(int rows) {
        return rows * 9 - 1;
    }

    private int playerFirst(int rows) {
        return rows * 9;
    }

    private int playerLast(int rows) {
        return rows * 9 + 35;
    }

    private int mainLast(int rows) {
        return rows * 9 + 26;
    }

    private int hotbarScreen(int rows, int invSlot) {
        return rows * 9 + 27 + invSlot;
    }

    private int ensureItemSelected(int invSlot) {
        if (invSlot <= 8) {
            InvUtils.swap((int)invSlot, (boolean)false);
            return invSlot;
        }
        InvUtils.move().from(invSlot).toHotbar(0);
        InvUtils.swap((int)0, (boolean)false);
        return 0;
    }

    private void execMoves() {
        if (this.moveState == 0) {
            if (this.moveQueue.isEmpty()) {
                return;
            }
            MoveOp op = this.moveQueue.poll();
            this.moveSrc = op.source;
            this.moveDst = op.target;
            if (this.moveSrc == -1) {
                this.click(SlotActionType.PICKUP, this.moveDst);
                this.moveState = 2;
            } else {
                this.click(SlotActionType.PICKUP, this.moveSrc);
                this.moveState = this.moveDst == -1 ? 3 : 1;
            }
            return;
        }
        ScreenHandler sh = this.mc.player.currentScreenHandler;
        if (sh == null) {
            return;
        }
        ItemStack cursor = sh.getCursorStack();
        if (this.moveState == 3) {
            if (!cursor.isEmpty()) {
                this.moveState = 0;
            }
        } else if (this.moveState == 1) {
            if (!cursor.isEmpty()) {
                this.click(SlotActionType.PICKUP, this.moveDst);
                this.moveState = 2;
            }
        } else if (this.moveState == 2 && cursor.isEmpty()) {
            this.moveState = 0;
        }
    }

    private boolean moveIdle() {
        return this.moveState == 0 && this.moveQueue.isEmpty();
    }

    private int invSlotToScreen(int rows, int invSlot) {
        return invSlot <= 8 ? this.hotbarScreen(rows, invSlot) : rows * 9 + (invSlot - 9);
    }

    private int firstEmptyContainerSlot(ScreenHandler sh, int rows) {
        for (int i = 0; i <= this.containerLast(rows); ++i) {
            if (!sh.getSlot(i).getStack().isEmpty()) continue;
            return i;
        }
        return -1;
    }

    private int firstEmptyPlayerScreenSlot(ScreenHandler sh, int rows) {
        for (int i = this.playerFirst(rows); i <= this.playerLast(rows); ++i) {
            if (!sh.getSlot(i).getStack().isEmpty()) continue;
            return i;
        }
        return -1;
    }

    private int findMergeSlotInPlayer(ScreenHandler sh, int rows, Item item) {
        for (int i = this.playerFirst(rows); i <= this.playerLast(rows); ++i) {
            ItemStack st = sh.getSlot(i).getStack();
            if (st.getItem() != item || st.getCount() >= st.getMaxCount()) continue;
            return i;
        }
        return -1;
    }

    private int findEcBoxWithSpace(ScreenHandler sh, int rows) {
        for (int i = 0; i <= this.containerLast(rows); ++i) {
            ItemStack st = sh.getSlot(i).getStack();
            if (!this.isShulkerBox(st) || this.shulkerUsedSlots(st) >= 27) continue;
            return i;
        }
        return -1;
    }

    private int findBackpackBoxWithSpace() {
        for (int i = 0; i < 36; ++i) {
            ItemStack st = (ItemStack)this.mc.player.getInventory().getMainStacks().get(i);
            if (!this.isShulkerBox(st) || this.shulkerUsedSlots(st) >= 27) continue;
            return i;
        }
        return -1;
    }

    private int findUnwornElytraSlot(ScreenHandler sh, int rows) {
        for (int i = this.playerFirst(rows); i <= this.playerLast(rows); ++i) {
            if (sh.getSlot(i).getStack().getItem() != Items.ELYTRA) continue;
            return i;
        }
        return -1;
    }

    private int findJunkSlotInPlayer(ScreenHandler sh, int rows, List<SupplyRule> rules) {
        HashSet<Item> keep = new HashSet<Item>();
        for (SupplyRule r : rules) {
            keep.add(r.item);
        }
        keep.add(Items.ELYTRA);
        keep.add(Items.ENDER_CHEST);
        int silkSlot = this.findSilkTouchSlot();
        for (int i = this.playerFirst(rows); i <= this.playerLast(rows); ++i) {
            ItemStack st = sh.getSlot(i).getStack();
            if (st.isEmpty() || this.isShulkerBox(st) || keep.contains(st.getItem()) || silkSlot != -1 && i == this.invSlotToScreen(rows, silkSlot)) continue;
            return i;
        }
        return -1;
    }

    private boolean isShulkerBox(ItemStack stack) {
        BlockItem bi;
        Item item = stack.getItem();
        return item instanceof BlockItem && (bi = (BlockItem)item).getBlock() instanceof ShulkerBoxBlock;
    }

    private boolean isShulkerBlock(BlockState state) {
        return state.getBlock() instanceof ShulkerBoxBlock;
    }

    private boolean isEnderChestBlock(BlockState state) {
        return state.getBlock() == Blocks.ENDER_CHEST;
    }

    private int shulkerUsedSlots(ItemStack box) {
        ContainerComponent container = (ContainerComponent)box.get(DataComponentTypes.CONTAINER);
        if (container == null) {
            return 0;
        }
        return (int)container.streamNonEmpty().count();
    }

    private Set<Item> shulkerItems(ItemStack box) {
        HashSet<Item> out = new HashSet<Item>();
        ContainerComponent container = (ContainerComponent)box.get(DataComponentTypes.CONTAINER);
        if (container == null) {
            return out;
        }
        container.streamNonEmpty().forEach(st -> out.add(st.getItem()));
        return out;
    }

    private List<BlockPos> findPlaceSpots(BlockPos exclude) {
        return this.findPlaceSpotsAt(this.mc.player.getBlockPos(), exclude);
    }

    private List<BlockPos> findPlaceSpotsAt(BlockPos center, BlockPos exclude) {
        ArrayList<BlockPos> out = new ArrayList<BlockPos>();
        BlockPos.Mutable m = new BlockPos.Mutable();
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                for (int dy = 0; dy <= 2; ++dy) {
                    BlockState s;
                    m.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (exclude != null && Math.abs(m.getX() - exclude.getX()) <= 1 && Math.abs(m.getZ() - exclude.getZ()) <= 1 && Math.abs(m.getY() - exclude.getY()) <= 1 || !(s = this.mc.world.getBlockState((BlockPos)m)).isReplaceable() || !this.mc.world.getBlockState(m.down()).isSolidBlock((BlockView)this.mc.world, m.down())) continue;
                    out.add(m.toImmutable());
                }
            }
        }
        out.sort(Comparator.comparingDouble(p -> this.mc.player.squaredDistanceTo((double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5)));
        return out;
    }

    private List<BlockPos> findBoxPlaceSpots(BlockPos exclude) {
        ArrayList<BlockPos> out = new ArrayList<BlockPos>();
        BlockPos.Mutable m = new BlockPos.Mutable();
        BlockPos center = this.mc.player.getBlockPos();
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                for (int dy = 0; dy <= 2; ++dy) {
                    m.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (exclude != null && Math.abs(m.getX() - exclude.getX()) <= 1 && Math.abs(m.getZ() - exclude.getZ()) <= 1 && Math.abs(m.getY() - exclude.getY()) <= 1 || !this.mc.world.getBlockState((BlockPos)m).isReplaceable() || !this.mc.world.getBlockState(m.up()).isSolidBlock((BlockView)this.mc.world, m.up())) continue;
                    out.add(m.toImmutable());
                }
            }
        }
        out.sort(Comparator.comparingDouble(p -> this.mc.player.squaredDistanceTo((double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5)));
        return out;
    }

    private List<BlockPos> findBoxFloorSpots(BlockPos exclude) {
        ArrayList<BlockPos> out = new ArrayList<BlockPos>();
        BlockPos.Mutable m = new BlockPos.Mutable();
        BlockPos center = this.mc.player.getBlockPos();
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                for (int dy = 0; dy <= 2; ++dy) {
                    m.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (exclude != null && Math.abs(m.getX() - exclude.getX()) <= 1 && Math.abs(m.getZ() - exclude.getZ()) <= 1 && Math.abs(m.getY() - exclude.getY()) <= 1 || !this.mc.world.getBlockState((BlockPos)m).isReplaceable() || !this.mc.world.getBlockState(m.down()).isSolidBlock((BlockView)this.mc.world, m.down()) || !this.mc.world.getBlockState(m.up()).isReplaceable()) continue;
                    out.add(m.toImmutable());
                }
            }
        }
        out.sort(Comparator.comparingDouble(p -> this.mc.player.squaredDistanceTo((double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5)));
        return out;
    }

    private ItemEntity findItemEntityNear(BlockPos pos, double radius) {
        Box box = new Box(pos).expand(radius);
        ItemEntity best = null;
        double bestD = Double.MAX_VALUE;
        for (Entity entity : this.mc.world.getOtherEntities(null, box, e -> true)) {
            ItemEntity item;
            double d;
            if (!(entity instanceof ItemEntity) || !((d = (item = (ItemEntity)entity).squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5)) < bestD)) continue;
            bestD = d;
            best = item;
        }
        return best;
    }

    private List<SupplyRule> parseSupplies() {
        ArrayList<SupplyRule> out = new ArrayList<SupplyRule>();
        for (String entry : (List<String>)this.supplies.get()) {
            String[] p = entry.split((String)";");
            if (p.length < 3) {
                this.warning("物资配置格式错误: " + entry, new Object[0]);
                continue;
            }
            Identifier id = Identifier.tryParse((String)p[0].trim());
            if (id == null || Registries.ITEM.get(id) == Items.AIR) {
                this.warning("未知物品: " + p[0], new Object[0]);
                continue;
            }
            try {
                out.add(new SupplyRule((Item)Registries.ITEM.get(id), Integer.parseInt(p[1].trim()), Integer.parseInt(p[2].trim())));
            }
            catch (NumberFormatException e) {
                this.warning("物资数量格式错误: " + entry, new Object[0]);
            }
        }
        return out;
    }

    private boolean anyDeficit() {
        for (SupplyRule r : this.parseSupplies()) {
            if (this.countItem(r.item) >= r.min) continue;
            return true;
        }
        return false;
    }

    private boolean needsItem(Item item) {
        for (SupplyRule r : this.parseSupplies()) {
            if (r.item != item || this.countItem(item) >= r.batch) continue;
            return true;
        }
        return false;
    }

    private int countItem(Item item) {
        int n = 0;
        for (int i = 0; i < 36; ++i) {
            ItemStack st = (ItemStack)this.mc.player.getInventory().getMainStacks().get(i);
            if (st.getItem() != item) continue;
            n += st.getCount();
        }
        return n;
    }

    private int countElytraMain() {
        return this.countItem(Items.ELYTRA);
    }

    private int countBoxes() {
        int n = 0;
        for (int i = 0; i < 36; ++i) {
            if (!this.isShulkerBox((ItemStack)this.mc.player.getInventory().getMainStacks().get(i))) continue;
            ++n;
        }
        return n;
    }

    private int findItemSlot(Item item) {
        for (int i = 0; i < 36; ++i) {
            if (((ItemStack)this.mc.player.getInventory().getMainStacks().get(i)).getItem() != item) continue;
            return i;
        }
        return -1;
    }

    private int findSilkTouchSlot() {
        Registry reg = this.mc.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        RegistryEntry entry = reg.getEntry((Object)((Enchantment)reg.get(Enchantments.SILK_TOUCH)));
        for (int i = 0; i < 36; ++i) {
            ItemStack st = (ItemStack)this.mc.player.getInventory().getMainStacks().get(i);
            if (EnchantmentHelper.getLevel((RegistryEntry)entry, (ItemStack)st) <= 0) continue;
            return i;
        }
        return -1;
    }

    private int findBackpackShulkerBox() {
        for (int i = 0; i < 36; ++i) {
            if (!this.isShulkerBox((ItemStack)this.mc.player.getInventory().getMainStacks().get(i))) continue;
            return i;
        }
        return -1;
    }

    private int freeSlots() {
        int n = 0;
        for (int i = 0; i < 36; ++i) {
            if (!((ItemStack)this.mc.player.getInventory().getMainStacks().get(i)).isEmpty()) continue;
            ++n;
        }
        return n;
    }

    private boolean reached(BlockPos pos, double tolerance) {
        double dz;
        if (this.stateTick < 6) {
            return false;
        }
        if (PathManagers.get().isPathing()) {
            return false;
        }
        double dx = this.mc.player.getX() - ((double)pos.getX() + 0.5);
        return dx * dx + (dz = this.mc.player.getZ() - ((double)pos.getZ() + 0.5)) * dz <= tolerance * tolerance;
    }

    private float endIslandHeightValue(int x, int z) {
        int i = x / 2;
        int j = z / 2;
        int k = x % 2;
        int l = z % 2;
        float f = 100.0f - (float)Math.sqrt((double)x * (double)x + (double)z * (double)z) * 8.0f;
        f = ElytraCollectorModule.clampIsland(f);
        for (int rx = -12; rx <= 12; ++rx) {
            for (int rz = -12; rz <= 12; ++rz) {
                long k1 = i + rx;
                long l1 = j + rz;
                if (this.islandNoise == null || k1 * k1 + l1 * l1 <= 4096L || !(this.islandNoise.sample2D(k1, l1) < (double)-0.9f)) continue;
                float f1 = (Math.abs((float)k1) * 3439.0f + Math.abs((float)l1) * 147.0f) % 13.0f + 9.0f;
                float f2 = k - rx * 2;
                float f3 = l - rz * 2;
                float f4 = 100.0f - (float)Math.sqrt(f2 * f2 + f3 * f3) * f1;
                f4 = ElytraCollectorModule.clampIsland(f4);
                f = Math.max(f, f4);
            }
        }
        return f;
    }

    private static float clampIsland(float value) {
        if (value < -100.0f) {
            return -100.0f;
        }
        return Math.min(value, 80.0f);
    }

    private double endIslandDensity(int blockX, int blockZ) {
        return ((double)this.endIslandHeightValue(blockX / 8, blockZ / 8) - 8.0) / 128.0;
    }

    private boolean isEndCityBiome(int chunkX, int chunkZ) {
        int blockX = chunkX * 16 + 8;
        long sx = blockX >> 4;
        int blockZ = chunkZ * 16 + 8;
        long sz = blockZ >> 4;
        if (sx * sx + sz * sz <= 4096L) {
            return false;
        }
        return this.endIslandDensity(blockX, blockZ) >= -0.0625;
    }

    private boolean cityTerrainHigh() {
        int[][] cols;
        int chunkX = this.current.chunk.getX();
        int chunkZ = this.current.chunk.getZ();
        ChunkRand r = new ChunkRand();
        r.setSeed(this.worldSeed);
        long a2 = r.nextLong();
        long b = r.nextLong();
        r.setSeed((long)chunkX * a2 ^ (long)chunkZ * b ^ this.worldSeed);
        BlockRotation rotation = BlockRotation.getRandom(r);
        int xOff = 5;
        int zOff = 5;
        if (rotation == BlockRotation.CLOCKWISE_90) {
            xOff = -5;
        } else if (rotation == BlockRotation.CLOCKWISE_180) {
            xOff = -5;
            zOff = -5;
        } else if (rotation == BlockRotation.COUNTERCLOCKWISE_90) {
            zOff = -5;
        }
        int posX = (chunkX << 4) + 7;
        int posZ = (chunkZ << 4) + 7;
        for (int[] c : cols = new int[][]{{posX, posZ}, {posX, posZ + zOff}, {posX + xOff, posZ}, {posX + xOff, posZ + zOff}}) {
            if (this.mc.world.isChunkLoaded(c[0] >> 4, c[1] >> 4)) continue;
            return true;
        }
        int h1 = this.mc.world.getTopY(Heightmap.Type.WORLD_SURFACE, cols[0][0], cols[0][1]);
        int h2 = this.mc.world.getTopY(Heightmap.Type.WORLD_SURFACE, cols[1][0], cols[1][1]);
        int h3 = this.mc.world.getTopY(Heightmap.Type.WORLD_SURFACE, cols[2][0], cols[2][1]);
        int h4 = this.mc.world.getTopY(Heightmap.Type.WORLD_SURFACE, cols[3][0], cols[3][1]);
        int min = Math.min(Math.min(h1, h2), Math.min(h3, h4));
        if (((Boolean)this.debugSetting.get()).booleanValue()) {
            this.debugLog("city=(" + chunkX + "," + chunkZ + ") rotation=" + String.valueOf((Object)rotation) + " min=" + min);
            this.debugColumn(posX, posZ, h1);
            this.debugColumn(posX, posZ + zOff, h2);
            this.debugColumn(posX + xOff, posZ, h3);
            this.debugColumn(posX + xOff, posZ + zOff, h4);
        }
        return min >= 60;
    }

    private void debugColumn(int x, int z, int realHeight) {
        float rawF = this.endIslandHeightValue(x / 8, z / 8);
        double e = ((double)rawF - 8.0) / 128.0;
        int vanilla = this.vanillaWorldSurfaceWg(x, z);
        this.debugLog("col(" + x + "," + z + ") real=" + realHeight + " vanilla=" + vanilla + " E=" + String.format((String)"%.4f", e) + " rawF=" + String.format((String)"%.1f", Float.valueOf(rawF)));
    }

    private int vanillaWorldSurfaceWg(int x, int z) {
        if (this.base3dNoise == null) {
            return 0;
        }
        int x0 = Math.floorDiv(x, 4) * 4;
        int z0 = Math.floorDiv(z, 4) * 4;
        double xf = (double)(x - x0) / 4.0;
        double zf = (double)(z - z0) / 4.0;
        double e00 = ((double)this.endIslandHeightValue(x0 / 8, z0 / 8) - 8.0) / 128.0;
        double e10 = ((double)this.endIslandHeightValue((x0 + 4) / 8, z0 / 8) - 8.0) / 128.0;
        double e01 = ((double)this.endIslandHeightValue(x0 / 8, (z0 + 4) / 8) - 8.0) / 128.0;
        double e11 = ((double)this.endIslandHeightValue((x0 + 4) / 8, (z0 + 4) / 8) - 8.0) / 128.0;
        double n00 = this.base3dNoise.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0, 256, z0));
        double n10 = this.base3dNoise.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0 + 4, 256, z0));
        double n01 = this.base3dNoise.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0, 256, z0 + 4));
        double n11 = this.base3dNoise.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0 + 4, 256, z0 + 4));
        for (int cell = 63; cell >= 0; --cell) {
            int yb = cell * 4;
            double b00 = this.base3dNoise.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0, yb, z0));
            double b10 = this.base3dNoise.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0 + 4, yb, z0));
            double b01 = this.base3dNoise.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0, yb, z0 + 4));
            double b11 = this.base3dNoise.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0 + 4, yb, z0 + 4));
            double c000 = ElytraCollectorModule.endSlide(e00 + b00, yb);
            double c010 = ElytraCollectorModule.endSlide(e00 + n00, yb + 4);
            double c100 = ElytraCollectorModule.endSlide(e10 + b10, yb);
            double c110 = ElytraCollectorModule.endSlide(e10 + n10, yb + 4);
            double c001 = ElytraCollectorModule.endSlide(e01 + b01, yb);
            double c011 = ElytraCollectorModule.endSlide(e01 + n01, yb + 4);
            double c101 = ElytraCollectorModule.endSlide(e11 + b11, yb);
            double c111 = ElytraCollectorModule.endSlide(e11 + n11, yb + 4);
            for (int y = yb + 3; y >= yb; --y) {
                double c = 0.0;
                double yf = (double)(y - yb) / 4.0;
                double x0z0 = c000 + yf * (c010 - c000);
                double x1z0 = c100 + yf * (c110 - c100);
                double z0v = x0z0 + xf * (x1z0 - x0z0);
                double x0z1 = c001 + yf * (c011 - c001);
                double x1z1 = c101 + yf * (c111 - c101);
                double z1v = x0z1 + xf * (x1z1 - x0z1);
                double v = z0v + zf * (z1v - z0v);
                double d = 0.64 * v;
                double d2 = d < -1.0 ? -1.0 : (c = d > 1.0 ? 1.0 : d);
                if (!(c / 2.0 - c * c * c / 24.0 > 0.0)) continue;
                return y + 1;
            }
            n00 = b00;
            n10 = b10;
            n01 = b01;
            n11 = b11;
        }
        return 0;
    }

    private static double endSlide(double f, int y) {
        double top = y <= 56 ? 1.0 : (y >= 312 ? 0.0 : (312.0 - (double)y) / 256.0);
        double middle = -23.4375 + top * (f + 23.4375);
        double bottom = y <= 4 ? 0.0 : (y >= 32 ? 1.0 : ((double)y - 4.0) / 28.0);
        return -0.234375 + bottom * (middle + 0.234375);
    }

    private boolean vanillaCityTerrainOk(int chunkX, int chunkZ) {
        ChunkRand r = new ChunkRand();
        r.setSeed(this.worldSeed);
        long a2 = r.nextLong();
        long b = r.nextLong();
        r.setSeed((long)chunkX * a2 ^ (long)chunkZ * b ^ this.worldSeed);
        BlockRotation rotation = BlockRotation.getRandom(r);
        int xOff = 5;
        int zOff = 5;
        if (rotation == BlockRotation.CLOCKWISE_90) {
            xOff = -5;
        } else if (rotation == BlockRotation.CLOCKWISE_180) {
            xOff = -5;
            zOff = -5;
        } else if (rotation == BlockRotation.COUNTERCLOCKWISE_90) {
            zOff = -5;
        }
        int posX = (chunkX << 4) + 7;
        int posZ = (chunkZ << 4) + 7;
        int min = this.vanillaWorldSurfaceWg(posX, posZ);
        min = Math.min(min, this.vanillaWorldSurfaceWg(posX, posZ + zOff));
        min = Math.min(min, this.vanillaWorldSurfaceWg(posX + xOff, posZ));
        min = Math.min(min, this.vanillaWorldSurfaceWg(posX + xOff, posZ + zOff));
        if (((Boolean)this.debugSetting.get()).booleanValue()) {
            this.debugLog("city=(" + chunkX + "," + chunkZ + ") rotation=" + String.valueOf((Object)rotation) + " predMin=" + min);
        }
        return min >= 60;
    }

    private Path cl() {
        return this.mc.runDirectory.toPath().resolve((String)"elytra-collector-debug.log");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void debugLog(String msg) {
        if (!((Boolean)this.debugSetting.get()).booleanValue()) {
            return;
        }
        try {
            String line = LocalTime.now().format(DateTimeFormatter.ofPattern((String)"HH:mm:ss")) + " " + msg + System.lineSeparator();
            Object object = this.debugLock;
            synchronized (object) {
                Files.writeString(this.cl(), (CharSequence)line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        }
        catch (Exception e) {
            this.error("调试日志写入失败: " + e.getMessage(), new Object[0]);
        }
    }

    private String blacklistKey(BlockPos head) {
        return head.getX() + "," + head.getZ();
    }

    private boolean isBlacklisted(BlockPos blockPos) {
        return ElytraVisitedFix.isVisited((Object)this, blockPos, this.t19IgnoreVisited);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addBlacklist(BlockPos head) {
        List list = (List)this.blacklist.get();
        synchronized (list) {
            String key = this.blacklistKey(head);
            if (!((List)this.blacklist.get()).contains(key)) {
                ((List)this.blacklist.get()).add(key);
            }
        }
    }

    private void hitItemFrame() {
        ItemFrameEntity frame = this.getNearestItemFrame((Double)this.killAuraReach.get());
        if (frame == null) {
            if (this.waypoints != null) {
                this.faceDirection(this.waypoints.facing);
            }
            frame = this.getNearestItemFrame((Double)this.killAuraReach.get() + 2.0);
        }
        if (frame != null) {
            this.mc.interactionManager.attackEntity((PlayerEntity)this.mc.player, (Entity)frame);
            this.info((String)"攻击展示框.", new Object[0]);
        } else {
            this.info((String)"未找到展示框.", new Object[0]);
        }
    }

    private void pickUpElytra() {
        ItemEntity item = this.findDroppedElytra();
        if (item != null) {
            PathManagers.get().moveTo(item.getBlockPos(), false);
        }
    }

    private void equipElytra() {
        if (this.isWearingFullDurabilityElytra()) {
            return;
        }
        int slot = this.findFullDurabilityElytraSlot();
        if (slot == -1) {
            return;
        }
        InvUtils.move().from(slot).toArmor(2);
        this.info((String)"已穿上鞘翅.", new Object[0]);
    }

    private boolean isWearingFullDurabilityElytra() {
        ItemStack chest = this.mc.player.getEquippedStack(EquipmentSlot.CHEST);
        return chest.getItem() == Items.ELYTRA && chest.getDamage() == 0;
    }

    private void walkTowards(double x, double z) {
        double dx = x - this.mc.player.getX();
        double dz = z - this.mc.player.getZ();
        this.mc.player.setYaw((float)Math.toDegrees(Math.atan2(-dx, dz)));
        this.mc.options.forwardKey.setPressed(true);
    }

    private void useFirework() {
        FindItemResult fw = InvUtils.findInHotbar((Item[])new Item[]{Items.FIREWORK_ROCKET});
        if (!fw.found()) {
            FindItemResult inv = InvUtils.find((Item[])new Item[]{Items.FIREWORK_ROCKET});
            if (inv.found()) {
                InvUtils.move().from(inv.slot()).toHotbar(1);
                this.info((String)"物品栏没有烟花，从背包调取放到第 2 个槽位.", new Object[0]);
            }
            return;
        }
        if (fw.isOffhand()) {
            this.mc.interactionManager.interactItem((PlayerEntity)this.mc.player, Hand.OFF_HAND);
        } else {
            InvUtils.swap((int)fw.slot(), (boolean)true);
            this.mc.interactionManager.interactItem((PlayerEntity)this.mc.player, Hand.MAIN_HAND);
            InvUtils.swapBack();
        }
    }

    private void releaseForward() {
        this.mc.options.forwardKey.setPressed(false);
    }

    private void faceDirection(Direction d) {
        float yaw = switch (d) {
            case Direction.SOUTH -> 0.0f;
            case Direction.WEST -> 90.0f;
            case Direction.NORTH -> 180.0f;
            case Direction.EAST -> -90.0f;
            default -> 0.0f;
        };
        this.rotateFastTo(yaw, this.mc.player.getPitch());
    }

    private float yawTowards(BlockPos pos) {
        double dx = (double)pos.getX() + 0.5 - this.mc.player.getX();
        double dz = (double)pos.getZ() + 0.5 - this.mc.player.getZ();
        return (float)Math.toDegrees(Math.atan2(-dx, dz));
    }

    private double horizontalDistance(BlockPos pos) {
        double dx = (double)pos.getX() + 0.5 - this.mc.player.getX();
        double dz = (double)pos.getZ() + 0.5 - this.mc.player.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private void rotateTo(float targetYaw, float targetPitch) {
        this.mc.player.setYaw(ElytraCollectorModule.stepAngle(this.mc.player.getYaw(), targetYaw, ((Double)this.yawSpeed.get()).floatValue()));
        this.mc.player.setPitch(ElytraCollectorModule.stepAngle(this.mc.player.getPitch(), targetPitch, ((Double)this.pitchSpeed.get()).floatValue()));
    }

    private void rotateFastTo(float targetYaw, float targetPitch) {
        float speed = ((Double)this.yawSpeed.get()).floatValue();
        this.mc.player.setYaw(ElytraCollectorModule.stepAngle(this.mc.player.getYaw(), targetYaw, speed));
        this.mc.player.setPitch(ElytraCollectorModule.stepAngle(this.mc.player.getPitch(), targetPitch, speed));
    }

    private static float stepAngle(float current, float target, float speed) {
        float d;
        for (d = target - current; d > 180.0f; d -= 360.0f) {
        }
        while (d < -180.0f) {
            d += 360.0f;
        }
        if (Math.abs(d) <= speed) {
            return target;
        }
        return current + Math.signum(d) * speed;
    }

    private static boolean isDragonHead(BlockState state) {
        String id = Registries.BLOCK.getId(state.getBlock()).getPath();
        return id.equals((String)"dragon_head") || id.equals((String)"dragon_wall_head");
    }

    private DragonHead findDragonHead(BlockPos center, int radius, int yRange) {
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                for (int dy = -yRange; dy <= yRange; ++dy) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState s = this.mc.world.getBlockState((BlockPos)pos);
                    if (!ElytraCollectorModule.isDragonHead(s)) continue;
                    Direction facing = s.contains((Property)Properties.FACING) ? (Direction)s.get((Property)Properties.FACING) : Direction.NORTH;
                    return new DragonHead(pos.toImmutable(), facing);
                }
            }
        }
        return null;
    }

    private ItemFrameEntity getNearestItemFrame(double reach) {
        Box box = this.mc.player.getBoundingBox().expand(reach);
        ItemFrameEntity best = null;
        double bestD = Double.MAX_VALUE;
        for (Entity entity : this.mc.world.getOtherEntities((Entity)this.mc.player, box, e -> true)) {
            ItemFrameEntity frame;
            double d;
            if (!(entity instanceof ItemFrameEntity) || !((d = (frame = (ItemFrameEntity)entity).squaredDistanceTo((Entity)this.mc.player)) < bestD)) continue;
            bestD = d;
            best = frame;
        }
        return best;
    }

    private ItemFrameEntity findFrameAt(BlockPos pos) {
        for (Entity entity : this.mc.world.getOtherEntities(null, new Box(pos), e -> e instanceof ItemFrameEntity)) {
            if (!(entity instanceof ItemFrameEntity)) continue;
            ItemFrameEntity frame = (ItemFrameEntity)entity;
            return frame;
        }
        return null;
    }

    private boolean hasShulkerAtGuard() {
        if (this.waypoints == null) {
            return false;
        }
        Box box = new Box(this.waypoints.guard);
        for (Entity entity : this.mc.world.getOtherEntities(null, box, e -> e.getType() == EntityType.SHULKER)) {
            if (!entity.getBlockPos().equals((Object)this.waypoints.guard)) continue;
            return true;
        }
        return false;
    }

    private ItemEntity findDroppedElytra() {
        Box box = this.mc.player.getBoundingBox().expand(32.0);
        ItemEntity best = null;
        double bestD = Double.MAX_VALUE;
        for (Entity entity : this.mc.world.getOtherEntities((Entity)this.mc.player, box, e -> true)) {
            double d;
            ItemEntity item;
            if (!(entity instanceof ItemEntity) || (item = (ItemEntity)entity).getStack().getItem() != Items.ELYTRA || !((d = item.squaredDistanceTo((Entity)this.mc.player)) < bestD)) continue;
            bestD = d;
            best = item;
        }
        return best;
    }

    private boolean hasElytraInInventory() {
        return this.findFullDurabilityElytraSlot() != -1 || this.findAnyElytraSlot() != -1;
    }

    private boolean hasElytraEquipped() {
        return this.mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
    }

    private int findAnyElytraSlot() {
        for (int i = 0; i < this.mc.player.getInventory().getMainStacks().size(); ++i) {
            if (((ItemStack)this.mc.player.getInventory().getMainStacks().get(i)).getItem() != Items.ELYTRA) continue;
            return i;
        }
        return -1;
    }

    private int findFullDurabilityElytraSlot() {
        for (int i = 0; i < this.mc.player.getInventory().getMainStacks().size(); ++i) {
            ItemStack s = (ItemStack)this.mc.player.getInventory().getMainStacks().get(i);
            if (s.getItem() != Items.ELYTRA || s.getDamage() != 0) continue;
            return i;
        }
        return -1;
    }

    private long parseSeed(String s) {
        if (s == null || s.equals((String)"0") || s.isEmpty()) {
            return -7346913998703726680L;
        }
        try {
            return Long.parseLong(s);
        }
        catch (NumberFormatException e) {
            return s.hashCode();
        }
    }

    

    

    private void safeExitAfterCollectFailure() {
        this.addBlacklist(this.current.headPos);
        this.ships.clear();
        this.targetIndex = 0;
        this.searchExtends = 0;
        this.takeoffFacing = this.current.facing;
        this.firstFireworkDone = false;
        this.landingRecover = false;
        this.releaseForward();
        this.launchSearch();
        this.state = State.COLLECTING;
        this.collectStep = CollectStep.EXIT_P1;
        this.stateTick = 0;
        this.beginCollectStep();
    }

    private enum State {
        IDLE,
        SEARCHING,
        RISING,
        FLYING,
        LANDING,
        COLLECTING,
        DONE;


        private static /* synthetic */ State[] extractShip() {
            return new State[]{IDLE, SEARCHING, RISING, FLYING, LANDING, COLLECTING, DONE};
        }

        
    }

    private enum CollectStep {
        TO_P1,
        TO_P2,
        WAIT_SHULKER,
        TO_P3,
        HIT_FRAME,
        PICK_UP,
        EQUIP,
        EXIT_P1,
        EXIT_LANDING,
        LEAVE_SHIP;


        private static /* synthetic */ CollectStep[] start() {
            return new CollectStep[]{TO_P1, TO_P2, WAIT_SHULKER, TO_P3, HIT_FRAME, PICK_UP, EQUIP, EXIT_P1, EXIT_LANDING, LEAVE_SHIP};
        }

        
    }

    private enum StoragePhase {
        NONE,
        PLACE_EC,
        OPEN_EC,
        PICK_BOX,
        CLOSE_SCREEN,
        PLACE_BOX,
        OPEN_BOX,
        FILL_BOX,
        TAKE_SUPPLIES,
        MINE_BOX,
        MINE_EC;


        private static /* synthetic */ StoragePhase[] onTick() {
            return new StoragePhase[]{NONE, PLACE_EC, OPEN_EC, PICK_BOX, CLOSE_SCREEN, PLACE_BOX, OPEN_BOX, FILL_BOX, TAKE_SUPPLIES, MINE_BOX, MINE_EC};
        }

        
    }

    private record ShipTarget(CPos chunk, BlockPos headPos, Direction facing) {
    }

    private record ShipWaypoints(BlockPos landing, BlockPos p1, BlockPos p2, BlockPos p3, BlockPos guard, Direction facing) {
        static ShipWaypoints from(BlockPos head, Direction facing) {
            return new ShipWaypoints(ShipWaypoints.offsetFromHead(head, facing, 7, 0, 4), ShipWaypoints.offsetFromHead(head, facing, 23, -1, -1), ShipWaypoints.offsetFromHead(head, facing, 9, -1, -4), ShipWaypoints.offsetFromHead(head, facing, 7, 0, -3), ShipWaypoints.offsetFromHead(head, facing, 8, 0, -4), facing);
        }

        static BlockPos offsetFromHead(BlockPos head, Direction facing, int behind, int left, int dy) {
            return head.offset(facing.getOpposite(), behind).offset(facing.rotateYCounterclockwise(), left).offset(Direction.UP, dy);
        }

    }

    private record DragonHead(BlockPos pos, Direction facing) {
    }

    private record MoveOp(int source, int target) {
    }

    private record SupplyRule(Item item, int min, int batch) {
    }
}

