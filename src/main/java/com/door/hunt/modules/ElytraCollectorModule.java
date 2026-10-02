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
    private static final MCVersion a = MCVersion.v1_16_2;
    private volatile SimplexNoiseSampler b;
    private volatile InterpolatedNoiseSampler c;
    private final SettingGroup d;
    private final SettingGroup e;
    private final Setting<String> f;
    private final Setting<Integer> g;
    private final Setting<Integer> h;
    private final Setting<Integer> i;
    private final Setting<Integer> j;
    private final Setting<Double> k;
    private final Setting<Double> l;
    private final Setting<Double> m;
    private final Setting<Double> n;
    private final Setting<List<String>> o;
    private final Setting<List<String>> p;
    private final Setting<Boolean> q;
    private final Setting<Boolean> r;
    private final SettingGroup s;
    private final Setting<Integer> t;
    private final Setting<List<String>> u;
    private final Setting<Boolean> v;
    private final Setting<Integer> w;
    private State x;
    private CollectStep y;
    private final List<ShipTarget> z;
    private int aa;
    private volatile Thread ab;
    private volatile boolean ac;
    private volatile int ad;
    private ShipTarget ae;
    private ShipWaypoints af;
    private int ag;
    private int ah;
    private long ai;
    private boolean aj;
    private boolean ak;
    private BlockPos al;
    private int am;
    private int an;
    private boolean ao;
    private final Set<String> ap;
    private int aq;
    private Direction ar;
    private int as;
    private boolean at;
    private boolean au;
    private boolean av;
    private int aw;
    private boolean ax;
    private boolean ay;
    private int az;
    private boolean ba;
    private double bb;
    private float bc;
    private int bd;
    private int be;
    private StoragePhase bf;
    private int bg;
    private int bh;
    private BlockPos bi;
    private BlockPos bj;
    private boolean bk;
    private int bl;
    private int bm;
    private int bn;
    private boolean bo;
    private boolean bp;
    private StoragePhase bq;
    private int br;
    private List<BlockPos> bs;
    private int bt;
    private int bu;
    private int bv;
    private final ArrayDeque<MoveOp> bw;
    private int bx;
    private int by;
    private int bz;
    private int ca;
    private int cb;
    private int cc;
    private int cd;
    private int ce;
    private int cf;
    private int cg;
    private int ch;
    private int ci;
    private int cj;
    private final Set<Item> ck;
    private int cl;
    private int cm;
    private int cn;
    private int co;
    private boolean cp;
    private boolean cq;
    private int cr;
    private int cs;
    private int ct;
    private boolean cu;
    private boolean cv;
    private boolean cw;
    private final Object cx;
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

    private static SimplexNoiseSampler a(long worldSeed) {
        JRand rand = new JRand(worldSeed);
        rand.advance(LCG.JAVA.combine(17292L));
        return new SimplexNoiseSampler(rand);
    }

    public ElytraCollectorModule() {
        super(AddonTemplate.CATEGORY, "鞘翅收集器", (String)"全自动找末地城鞘翅：种子定位 + 龙头精确定位 + 高度保持飞行 + 缓降 + Baritone 寻路 + 打展示框捡鞘翅.");
        this.d = this.settings.getDefaultGroup();
        this.e = this.settings.createGroup((String)"Flight");
        this.f = this.d.add((Setting)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("种子")).description((String)"世界种子 (0 = 当前世界).")).defaultValue("-7346913998703726680")).build());
        this.g = this.d.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("搜索范围")).description((String)"搜索半径，单位方块 (从玩家位置).")).defaultValue(5000)).range(320, 100000).sliderRange(320, 20000).build());
        this.h = this.e.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("最低高度")).description((String)"飞行中低于此高度时触发爬升.")).defaultValue(180)).range(100, 300).sliderRange(120, 260).build());
        this.i = this.e.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("最高高度")).description((String)"飞行中高于此高度时停止爬升、转为平缓下滑 (与 min-height 组成滞回区间).")).defaultValue(220)).range(120, 320).sliderRange(140, 300).build());
        this.j = this.e.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("拉升高度")).description((String)"起飞后抬头爬升到的目标高度 (需高于 max-height).")).defaultValue(230)).range(200, 320).sliderRange(200, 300).build());
        this.k = this.e.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("俯仰速度")).description((String)"飞行 (pitch40) 时上下转动视角 (pitch) 的速度 (度/tick).")).defaultValue(10.0).min(1.0).sliderMax(45.0).build());
        this.l = this.e.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("偏航速度")).description((String)"左右转动视角 (yaw) 及鞘翅缓降的转向速度 (度/tick).")).defaultValue(30.0).min(1.0).sliderMax(90.0).build());
        this.m = this.e.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("Kill Aura 攻击距离")).description((String)"攻击展示框 (item_frame) 的判定距离.")).defaultValue(3.0).min(1.0).sliderMax(6.0).build());
        this.n = this.e.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("烟花间隔")).description((String)"展开鞘翅后立即使用第一个烟花，之后每隔这么多秒再用一个 (秒).")).defaultValue(2.0).min(0.5).sliderRange(0.5, 10.0).build());
        this.o = this.d.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("已访问末地船")).description((String)"黑名单：已经去过的船 (x,z)，搜索时会自动跳过. 自动维护.")).defaultValue(new ArrayList())).visible(() -> false)).build());
        this.p = this.d.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("搜索结果")).description((String)"上次搜索结果列表 (x,y,z,朝向)，下一次搜索完成后覆盖.")).defaultValue(new ArrayList())).build());
        this.q = this.d.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("开始")).description((String)"开始自动采集.")).defaultValue(false)).onChanged(b -> {
            if (b.booleanValue()) {
                this.c();
            }
        })).build());
        this.r = this.d.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("调试")).description((String)"输出调试日志 (用于校准高度公式).")).defaultValue(false)).build());
        this.s = this.settings.createGroup((String)"Storage");
        this.t = this.s.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("清仓保留空槽")).description((String)"背包可用空格 ≤ 此值时，拿到鞘翅后自动去末影箱存鞘翅.")).defaultValue(3)).range(0, 36).sliderRange(0, 36).build());
        this.u = this.s.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("补给品")).description((String)"物资列表，格式: 物品ID;最低值;目标库存 (如 minecraft:firework_rocket;32;256). 背包物资低于最低值时自动从末影箱补货，拿到目标库存为止.")).defaultValue(new ArrayList<String>(List.of((String)"minecraft:firework_rocket;4;16", (String)"minecraft:cooked_beef;8;32")))).build());
        this.v = this.s.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("低 Y 退出")).description((String)"Y 低于阈值时自动退出游戏 (防虚空掉物).")).defaultValue(true)).build());
        this.w = this.s.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("低 Y 阈值")).description((String)"低于此 Y 自动退出游戏.")).defaultValue(30)).range(0, 100).sliderRange(0, 100).build());
        this.x = State.IDLE;
        this.y = CollectStep.TO_P1;
        this.z = Collections.synchronizedList(new ArrayList());
        this.aa = 0;
        this.ac = true;
        this.ad = 0;
        this.ag = 0;
        this.ah = 0;
        this.ai = 0L;
        this.aj = false;
        this.ak = false;
        this.al = null;
        this.am = 0;
        this.an = 0;
        this.ao = false;
        this.ap = Collections.synchronizedSet(new HashSet());
        this.aq = 0;
        this.ar = null;
        this.as = 0;
        this.at = false;
        this.au = false;
        this.av = false;
        this.aw = 0;
        this.ax = false;
        this.ay = false;
        this.az = 0;
        this.ba = false;
        this.bb = 0.0;
        this.bc = 0.0f;
        this.bd = 0;
        this.be = 0;
        this.bf = StoragePhase.NONE;
        this.bg = 0;
        this.bh = -1;
        this.bk = false;
        this.bl = -1;
        this.bm = 0;
        this.bn = 0;
        this.bo = false;
        this.bp = false;
        this.bq = StoragePhase.NONE;
        this.br = 0;
        this.bs = new ArrayList<BlockPos>();
        this.bt = 0;
        this.bu = -100;
        this.bv = 0;
        this.bw = new ArrayDeque();
        this.bx = 0;
        this.by = -1;
        this.bz = -1;
        this.ca = 0;
        this.cb = -1;
        this.cc = -1;
        this.cd = -1;
        this.ce = 0;
        this.cf = -1;
        this.cg = 0;
        this.ch = -1;
        this.ci = 0;
        this.cj = 0;
        this.ck = new HashSet<Item>();
        this.cl = -1;
        this.cm = 0;
        this.cn = 0;
        this.co = -1;
        this.cp = false;
        this.cq = false;
        this.cr = 0;
        this.cs = 0;
        this.ct = 0;
        this.cu = false;
        this.cv = false;
        this.cw = false;
        this.cx = new Object();
        this.startClimbAngle = this.e.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("起飞拉升角度")).description("首次起飞以及滑翔降到最低高度后的重新爬升角度（度）。达到最高高度后自动切换滑翔。")).defaultValue(45.0).range(5.0, 80.0).sliderRange(5.0, 80.0).build());
        this.cruiseClimbAngle = this.e.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("巡航爬升角度")).description("长距离巡航到最低高度后使用的抬头角度（度）。")).defaultValue(54.77).range(5.0, 80.0).sliderRange(5.0, 80.0).build());
        this.cruiseGlideAngle = this.e.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("巡航滑翔角度")).description("达到最高高度后使用的向下滑翔角度（度）。滑翔阶段不使用烟花，降到最低高度后重新爬升。")).defaultValue(37.72).range(0.0, 70.0).sliderRange(0.0, 70.0).build());
        this.t18DirectionGroup = this.settings.createGroup("搜索方向");
        this.t18North = this.t18DirectionGroup.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("搜索北方")).description("允许搜索以开始搜索时的位置为中心，北方（-Z）扇区内的末地船。")).defaultValue(true)).build());
        this.t18South = this.t18DirectionGroup.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("搜索南方")).description("允许搜索以开始搜索时的位置为中心，南方（+Z）扇区内的末地船。")).defaultValue(true)).build());
        this.t18East = this.t18DirectionGroup.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("搜索东方")).description("允许搜索以开始搜索时的位置为中心，东方（+X）扇区内的末地船。")).defaultValue(true)).build());
        this.t18West = this.t18DirectionGroup.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("搜索西方")).description("允许搜索以开始搜索时的位置为中心，西方（-X）扇区内的末地船。")).defaultValue(true)).build());
        this.t19VisitedGroup = this.settings.createGroup("访问记录");
        this.t19IgnoreVisited = this.t19VisitedGroup.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("忽略已访问记录")).description("开启后重新扫描所有预测末地船，不使用旧的“已访问船”列表。适合旧版本记录异常时临时恢复搜索。")).defaultValue(false)).build());
        ElytraFlightUi20.remove(this.d, this.j);
        ElytraFlightUi20.remove(this.e, this.cruiseClimbAngle);
        ElytraFinderStatusHud.attach((Object)this);
    }

    public WWidget getWidget(GuiTheme theme) {
        WSection section = theme.section((String)"黑名单", true);
        WButton clear = (WButton)section.add((WWidget)theme.button((String)"清除黑名单")).expandX().widget();
        clear.action = () -> {
            List list = (List)this.o.get();
            synchronized (list) {
                ((List)this.o.get()).clear();
            }
            this.info((String)"黑名单已清空.", new Object[0]);
        };
        ElytraVisitedUi22.enhance((Object)this, theme, section);
        return section;
    }

    public void onDeactivate() {
        this.ac = true;
        ++this.ad;
        this.ab = null;
        this.x = State.IDLE;
        this.z.clear();
        this.ao = false;
        this.ba = false;
        this.bc = 0.0f;
        this.bd = 0;
        this.bf = StoragePhase.NONE;
        this.bg = 0;
        this.bl = -1;
        this.b();
        this.cw();
        PathManagers.get().stop();
    }

    private void b() {
        this.bw.clear();
        this.bx = 0;
        this.by = -1;
        this.bz = -1;
        this.ca = 0;
        this.cb = -1;
        this.cc = -1;
        this.cd = -1;
        this.ce = 0;
        this.cf = -1;
        this.cg = 0;
        this.ch = -1;
        this.ci = 0;
        this.cj = 0;
        this.ck.clear();
        this.cl = -1;
        this.cm = 0;
        this.cn = 0;
        this.cp = false;
        this.cs = 0;
        this.ct = 0;
        this.cw = false;
    }

    private void c() {
        if (this.x != State.IDLE && this.x != State.DONE) {
            this.q.set(false);
            return;
        }
        this.x = State.SEARCHING;
        this.aq = 0;
        this.ar = null;
        this.ba = false;
        this.bc = 0.0f;
        this.bd = 0;
        this.bf = StoragePhase.NONE;
        this.bg = 0;
        this.bl = -1;
        this.info("开始搜索末地城 (范围=" + String.valueOf(this.g.get()) + " 方块)...", new Object[0]);
        if (((Boolean)this.r.get()).booleanValue()) {
            this.info("调试日志文件: " + String.valueOf(this.cl().toAbsolutePath()), new Object[0]);
        }
        this.d();
    }

    private void d() {
        this.ac = false;
        int gen = ++this.ad;
        Thread t = new Thread(() -> this.e(gen), (String)"ElytraCollector-Search");
        t.setDaemon(true);
        this.ab = t;
        t.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void e(int generation) {
        block18: {
            try {
                if (this.ac || generation != this.ad) {
                    return;
                }
                this.ai = this.dn((String)this.f.get());
                EndCity endCity = new EndCity(a);
                EndCityGenerator generator = new EndCityGenerator(a);
                ChunkRand rand = new ChunkRand();
                EndBiomeSource biomeSource = new EndBiomeSource(a, this.ai);
                this.b = ElytraCollectorModule.a(this.ai);
                this.c = new InterpolatedNoiseSampler((Random)new CheckedRandom(this.ai), 0.25, 0.25, 80.0, 160.0, 4.0);
                EndTerrainGenerator terrainGen = new EndTerrainGenerator(biomeSource);
                int px = (int)Math.floor(this.mc.player.getX());
                int pz = (int)Math.floor(this.mc.player.getZ());
                int spacing = endCity.getSpacing();
                int range = (Integer)this.g.get() + this.aq * 500;
                int regionRange = range / (spacing * 16) + 1;
                int prx = Math.floorDiv(px >> 4, spacing);
                int prz = Math.floorDiv(pz >> 4, spacing);
                ArrayList<ShipTarget> found = new ArrayList<ShipTarget>();
                for (int drx = -regionRange; drx <= regionRange; ++drx) {
                    if (this.ac || generation != this.ad) {
                        return;
                    }
                    for (int drz = -regionRange; drz <= regionRange; ++drz) {
                        ShipTarget t;
                        int rx = prx + drx;
                        int rz = prz + drz;
                        CPos city = endCity.getInRegion(this.ai, rx, rz, rand);
                        if (city == null || !endCity.canSpawn(city, (BiomeSource)biomeSource) || !endCity.canGenerate(city, (TerrainGenerator)terrainGen)) continue;
                        generator.generate(terrainGen, city.getX(), city.getZ(), rand);
                        boolean hasShip = generator.hasShip();
                        if (hasShip && (t = this.f(generator, city)) != null && !this.co(t.d) && !this.ap.contains(this.cn(t.d))) {
                            found.add(t);
                            if (((Boolean)this.r.get()).booleanValue()) {
                                this.cm("ship city=(" + city.getX() + "," + city.getZ() + ") head=(" + t.d.getX() + "," + t.d.getY() + "," + t.d.getZ() + ") facing=" + String.valueOf(t.b) + " E=" + String.format((String)"%.4f", this.ce(city.getX() * 16 + 8, city.getZ() * 16 + 8)));
                            }
                        }
                        generator.reset();
                    }
                }
                if (this.ac || generation != this.ad) {
                    return;
                }
                ElytraSearchFallback.rebuildIfEmpty((Object)this, found, this.ai, px, pz, range);
                ElytraDirectionFilterV2.filter(found, this.t18North, this.t18South, this.t18East, this.t18West, px, pz);
                found.sort(Comparator.comparingInt(s -> (s.d.getX() - px) * (s.d.getX() - px) + (s.d.getZ() - pz) * (s.d.getZ() - pz)));
                this.z.clear();
                this.z.addAll(found);
                this.aa = 0;
                List list = (List)this.p.get();
                synchronized (list) {
                    ((List)this.p.get()).clear();
                    for (ShipTarget t : found) {
                        ((List)this.p.get()).add(t.d.getX() + "," + t.d.getY() + "," + t.d.getZ() + "," + String.valueOf(t.b));
                    }
                }
                this.info("找到 " + this.z.size() + " 艘带船末地城 (已排除黑名单, 范围=" + range + ").", new Object[0]);
                if (this.z.isEmpty()) {
                    if (this.aq < 10) {
                        ++this.aq;
                        this.info("范围内没有末地船，扩大范围 +" + this.aq * 500 + " 格重新搜索...", new Object[0]);
                        this.d();
                    } else {
                        this.aq = 0;
                        this.p((String)"范围内没有末地船，任务完成.");
                    }
                } else {
                    this.aq = 0;
                    if (this.x == State.SEARCHING) {
                        this.ay = false;
                        this.x = State.RISING;
                        this.ag = 0;
                    }
                }
            }
            catch (Exception e) {
                if (this.ac || generation != this.ad) break block18;
                this.error("搜索失败: " + e.getMessage(), new Object[0]);
                this.x = State.IDLE;
                this.q.set(false);
            }
        }
    }

    private ShipTarget f(EndCityGenerator generator, CPos chunk) {
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

    private void g() {
        if (this.aa >= this.z.size()) {
            this.p((String)"全部末地城已处理，任务完成.");
            return;
        }
        this.ae = this.z.get(this.aa);
        this.af = ShipWaypoints.j(this.ae.d, this.ae.b);
        this.aj = false;
        this.ak = false;
        this.au = false;
        this.av = false;
        this.aw = 0;
        this.al = null;
        this.am = 0;
        this.an = 0;
        this.ay = false;
        this.ba = false;
        this.be = 0;
        this.ag = 0;
        this.info("飞向最近未去过的船 近似龙头=" + String.valueOf(this.ae.d) + " 朝向=" + String.valueOf(this.ae.b), new Object[0]);
        this.x = State.FLYING;
    }

    @EventHandler
    private void h(TickEvent.Pre event) {
        try {
            if (this.mc.player == null || this.mc.world == null) {
                return;
            }
            ++this.ag;
            if (this.x != State.IDLE && this.x != State.DONE && ((Boolean)this.v.get()).booleanValue() && this.mc.player.getY() < (double)((Integer)this.w.get()).intValue()) {
                this.warning("Y=" + String.format((String)"%.1f", this.mc.player.getY()) + " 低于阈值 " + String.valueOf(this.w.get()) + "，任务已停止.", new Object[0]);
                LowYSafetyLogout.trigger((Object)this, (String)"低高度任务停止.");
                return;
            }
            if (this.ao) {
                return;
            }
            if (this.bf != StoragePhase.NONE) {
                this.u();
                return;
            }
            if (StorageReturn.tick((Object)this)) {
                return;
            }
            switch (this.x.ordinal()) {
                case 2: {
                    this.i();
                    break;
                }
                case 3: {
                    this.j();
                    break;
                }
                case 4: {
                    this.k();
                    break;
                }
                case 5: {
                    this.m();
                    break;
                }
            }
        }
        catch (Throwable t) {
            this.error("任务异常: " + String.valueOf(t), new Object[0]);
            t.printStackTrace();
            this.o((String)"任务异常，停止任务.");
        }
    }

    private void i() {
        if (this.ba && ElytraSafeEscape.tick((Object)this)) {
            return;
        }
        if (!this.ba && this.ag > 400) {
            this.error((String)"起飞超时 (可能没穿鞘翅或没有烟花).", new Object[0]);
            this.x = State.IDLE;
            this.q.set(false);
            this.cw();
            return;
        }
        if (this.mc.player.isOnGround() && this.ag <= 2) {
            this.r();
            if (this.bf != StoragePhase.NONE) {
                return;
            }
        }
        if (this.ba) {
            ++this.bd;
            if (this.cr == 0) {
                this.db(this.bc, this.mc.player.getPitch());
            } else {
                this.da(this.cy(this.af.j), 37.72f);
            }
        } else if (this.ar != null) {
            this.cx(this.ar);
        }
        if (!this.ba || this.cr != 1) {
            this.mc.player.setPitch(-((float)((Double)this.startClimbAngle.get()).doubleValue()));
        }
        this.mc.options.forwardKey.setPressed(true);
        if (!this.mc.player.isGliding()) {
            if (this.mc.player.isOnGround()) {
                PathManagers.get().stop();
                this.mc.player.jump();
            } else if (this.ag % 4 == 0) {
                this.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
            return;
        }
        if (!this.ay) {
            this.cv();
            this.ay = true;
            this.az = this.ag;
        } else if (!(this.ba && this.cr == 1 || (long)(this.ag - this.az) < Math.max(1L, Math.round((Double)this.n.get() * 20.0)))) {
            this.cv();
            this.az = this.ag;
        }
        if (this.ba) {
            BlockPos lp = this.af.j;
            if (this.cr == 0) {
                if (this.mc.player.getY() >= (double)(lp.getY() + 25) || this.bd > 600) {
                    this.cr = 1;
                    this.bd = 0;
                    this.info((String)"已拉升到降落点上方 25 格，用 pitch40 模式飞向降落点.", new Object[0]);
                }
            } else if (this.cz(lp) <= 3.0 || this.bd > 600) {
                this.cw();
                this.ba = false;
                this.cr = 0;
                this.bd = 0;
                this.info((String)"已回到降落点上方，进入缓降.", new Object[0]);
                this.x = State.LANDING;
                this.ag = 0;
            }
        } else if (this.mc.player.getY() >= (double)((Integer)this.i.get()).intValue()) {
            if (this.z.isEmpty()) {
                this.mc.player.setPitch(-20.0f);
                this.mc.options.forwardKey.setPressed(true);
            } else {
                this.cw();
                this.ay = false;
                this.g();
            }
        }
    }

    private void j() {
        if (!this.mc.player.isGliding()) {
            if (this.mc.player.isOnGround()) {
                this.ay = false;
                this.ar = null;
                this.x = State.RISING;
                this.ag = 0;
                return;
            }
            this.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            return;
        }
        BlockPos lp = this.af.j;
        // path: 0 = run dragon-head detection (the search loop), then lbl40/lbl60/lbl62
        //       1 = skip detection, jump to lbl40 (the !ak && an>=7 fallback)
        //       2 = skip detection AND lbl40, jump to lbl60 (the ag reset)
        //       3 = skip everything except lbl62 (the elytra-display check)
        int path;
        if (this.ak) {
            path = 3;
        } else if (!(this.cz(lp) < 80.0)) {
            this.am = 0;
            path = 2;
        } else {
            ++this.am;
            if (this.am == 1 && ((Boolean)this.r.get()).booleanValue() && !this.cg()) {
                this.cm((String)"注意: 地形高度判断不足 (船可能在虚空上)，改由龙头扫描确认.");
            }
            if (this.am % 20 != 0) {
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
                        if (this.mc.world.isChunkLoaded((this.ae.d.getX() >> 4) + dx, (this.ae.d.getZ() >> 4) + dz)) continue;
                        loaded = false;
                        break;
                    }
                    ++dx;
                    continue;
                }
                if (loaded) {
                    ++this.an;
                    DragonHead exact = this.de(this.ae.d, 10, 40);
                    if (exact != null) {
                        this.al = exact.a;
                        this.af = ShipWaypoints.j(this.al, this.ae.b);
                        this.ak = true;
                        this.info("已确认龙头: " + String.valueOf(this.al) + " 朝向=" + String.valueOf(this.ae.b), new Object[0]);
                        if (((Boolean)this.r.get()).booleanValue()) {
                            int realTop = this.mc.world.getTopY(Heightmap.Type.WORLD_SURFACE, this.al.getX(), this.al.getZ());
                            this.cm("head=(" + this.al.getX() + "," + this.al.getY() + "," + this.al.getZ() + ") 龙头下地表=" + realTop + " seedY=" + this.ae.d.getY());
                        }
                    } else {
                        this.info("检测龙头第 " + this.an + "/7 次未找到 (目标=" + this.ae.d.getX() + "," + this.ae.d.getZ() + ").", new Object[0]);
                    }
                }
                break;
            }
        }
        if (path <= 1) {
            if (!this.ak && this.an >= 7) {
                DragonHead last = this.de(this.ae.d, 10, 200);
                if (last == null) {
                    this.warning((String)"检测 7 次且整列无龙头，判定为假城，加入黑名单并重新搜索.", new Object[0]);
                    this.cp(this.ae.d);
                    this.ap.add(this.cn(this.ae.d));
                    this.z.clear();
                    this.aa = 0;
                    this.aq = 0;
                    this.ar = null;
                    this.ay = false;
                    this.x = State.RISING;
                    this.ag = 0;
                    this.d();
                    return;
                }
                this.al = last.a;
                this.af = ShipWaypoints.j(this.al, this.ae.b);
                this.ak = true;
                this.info("整列扫描发现龙头: " + String.valueOf(this.al) + " 朝向=" + String.valueOf(this.ae.b), new Object[0]);
            }
        }
        if (path <= 2) {
            if (this.ak) {
                this.ag = 0;
            }
        }
        if (this.ak && !this.au) {
            ItemFrameEntity airFrame;
            if (this.aw % 20 == 0 && (airFrame = this.dg(this.af.m)) != null) {
                this.au = true;
                this.av = airFrame.getHeldItemStack().getItem() != Items.ELYTRA;
                if (this.av) {
                    this.info((String)"展示框里没有鞘翅，不降落：拉升到 max-height 以上后跳过该船.", new Object[0]);
                    this.t();
                    return;
                }
                this.info((String)"展示框里有鞘翅，按原计划降落.", new Object[0]);
            }
            ++this.aw;
            if (this.aw > 120) {
                this.au = true;
                this.av = true;
                this.t();
                return;
            }
        }
        if (ElytraApproachSafety22.tick((Object)this)) {
            return;
        }
        float yaw = this.cy(lp);
        double y = this.mc.player.getY();
        if (this.aj) {
            if (y >= (double)((Integer)this.i.get()).intValue()) {
                this.aj = false;
            }
        } else if (y <= (double)((Integer)this.h.get()).intValue()) {
            this.aj = true;
        }
        float pitch = this.aj != false ? -((float)((Double)this.startClimbAngle.get()).doubleValue()) : (float)((Double)this.cruiseGlideAngle.get()).doubleValue();
        this.da(yaw, pitch);
        this.mc.options.forwardKey.setPressed(true);
        if (!this.aj) {
            if (this.au == false) return;
            if (!(this.cz(lp) <= 80.0)) return;
            this.x = State.LANDING;
            this.ag = 0;
            return;
        }
        if (this.mc.player.getY() < (double)((Integer)this.i.get()).intValue()) {
            this.aj = true;
            if ((long)(this.ag - this.az) < Math.max(1L, Math.round((Double)this.n.get() * 20.0))) return;
            this.cv();
            this.az = this.ag;
            return;
        }
        this.t();
        return;
    }

    void abortStorageForPullUp() {
        if (this.bf == StoragePhase.NONE) {
            return;
        }
        if (this.ap()) {
            this.mc.player.closeHandledScreen();
        }
        PathManagers.get().stop();
        this.cw();
        this.bf = StoragePhase.NONE;
        this.bg = 0;
        this.b();
        this.info((String)"危险高度: 中止存储会话，强制拉升.", new Object[0]);
    }

    boolean isLandingOrRecovering() {
        return this.x == State.LANDING || this.ba;
    }

    boolean isPullUpSuppressed() {
        return this.ao;
    }

    void setPullUpSuppressed(boolean suppressed) {
        this.ao = suppressed;
        if (!suppressed) {
            this.cw();
        }
    }

    private void k() {
        double ddz;
        double ddy;
        BlockPos lp = this.af.j;
        double hDist = this.cz(lp);
        if (!this.ba && !this.mc.player.isOnGround() && this.mc.player.getY() < (double)(lp.getY() - 10)) {
            ++this.be;
            if (this.be > 3) {
                this.o((String)"多次偏离降落点无法降落，停止任务.");
                return;
            }
            this.ba = true;
            this.cr = 0;
            this.bd = 0;
            this.bc = this.cy(lp) + 180.0f;
            this.ay = false;
            this.info((String)"低于降落点 10 格，反向拉升 (目标降落点上方 25 格).", new Object[0]);
            this.x = State.RISING;
            this.ag = 0;
            return;
        }
        if (this.mc.player.isOnGround()) {
            if (hDist < 3.0) {
                this.cw();
                this.be = 0;
                this.info((String)"已降落在降落点.", new Object[0]);
                this.x = State.COLLECTING;
                this.y = CollectStep.TO_P1;
                this.ag = 0;
                this.l();
                return;
            }
            if (this.ag < 6 || !PathManagers.get().isPathing()) {
                PathManagers.get().moveTo(lp, false);
            }
            if (this.ag > 6 && !PathManagers.get().isPathing() && hDist < 2.5) {
                this.cw();
                this.x = State.COLLECTING;
                this.y = CollectStep.TO_P1;
                this.ag = 0;
                this.l();
            }
            return;
        }
        if (!this.mc.player.isGliding()) {
            this.mc.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this.mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            return;
        }
        double ddx = this.mc.player.getX() - ((double)lp.getX() + 0.5);
        if (ddx * ddx + (ddy = this.mc.player.getY() - ((double)lp.getY() + 0.5)) * ddy + (ddz = this.mc.player.getZ() - ((double)lp.getZ() + 0.5)) * ddz > 400.0) {
            this.mc.player.setYaw(this.cy(lp));
            this.mc.player.setPitch(55.0f);
            this.mc.options.forwardKey.setPressed(true);
            return;
        }
        if (hDist > 3.0) {
            double dy = this.mc.player.getY() - (double)(lp.getY() + 1);
            this.mc.player.setYaw(this.cy(lp));
            this.mc.player.setPitch((float)Math.min(45.0, Math.max(5.0, dy * 0.8)));
            this.mc.options.forwardKey.setPressed(true);
        } else {
            this.cw();
            this.mc.player.setPitch(0.0f);
            if (this.ag % 2 == 0) {
                this.mc.player.setYaw(this.mc.player.getYaw() + 180.0f);
            }
        }
    }

    private void l() {
        if (this.af == null) {
            this.x = State.RISING;
            return;
        }
        switch (this.y.ordinal()) {
            case 0: {
                PathManagers.get().moveTo(this.af.k, false);
                break;
            }
            case 1: {
                PathManagers.get().moveTo(this.af.l, false);
                break;
            }
            case 3: {
                PathManagers.get().moveTo(this.af.m, false);
                break;
            }
            case 4: {
                this.br = this.bv();
                break;
            }
            case 5: {
                this.cr();
                break;
            }
            case 6: {
                this.cs();
                break;
            }
            case 7: {
                PathManagers.get().moveTo(this.af.k, false);
                break;
            }
            case 8: {
                PathManagers.get().moveTo(this.af.j.down(4), false);
                break;
            }
            case 9: {
                PathManagers.get().moveTo(this.af.j.down(4), false);
                break;
            }
        }
    }

    private void m() {
        switch (this.y.ordinal()) {
            case 0: {
                if (!this.cb(this.af.k, 2.5)) break;
                this.n(CollectStep.TO_P2);
                break;
            }
            case 1: {
                if (!this.cb(this.af.l, 2.5)) break;
                this.n(CollectStep.WAIT_SHULKER);
                break;
            }
            case 2: {
                if (!this.dh()) {
                    this.n(CollectStep.TO_P3);
                    break;
                }
                if (this.ag - this.ah < 20) break;
                PathManagers.get().moveTo(this.af.l, false);
                this.ah = this.ag;
                break;
            }
            case 3: {
                if (!this.cb(this.af.m, 2.0)) break;
                this.n(CollectStep.HIT_FRAME);
                break;
            }
            case 4: {
                ItemFrameEntity frame = this.df((Double)this.m.get() + 2.0);
                if (frame != null && this.ag == 1) {
                    boolean bl = this.at = frame.getHeldItemStack().getItem() == Items.ELYTRA;
                    if (!this.at) {
                        this.info((String)"展示框里没有鞘翅，直接离开该船.", new Object[0]);
                        this.safeExitAfterCollectFailure();
                        return;
                    }
                }
                if ((this.ag <= 1 || this.ag % 20 == 0) && this.as < 3) {
                    ++this.as;
                    this.cq();
                }
                if (this.di() != null || this.bv() > this.br) {
                    this.n(CollectStep.PICK_UP);
                    break;
                }
                if (this.ag <= 80) break;
                this.info((String)"攻击展示框后没有鞘翅掉落，直接离开该船.", new Object[0]);
                this.safeExitAfterCollectFailure();
                break;
            }
            case 5: {
                ItemEntity item = this.di();
                if (item != null) {
                    this.cu(item.getX(), item.getZ());
                    if (!(item.squaredDistanceTo((Entity)this.mc.player) < 1.2)) break;
                    this.mc.options.forwardKey.setPressed(false);
                    break;
                }
                if (this.bv() > this.br) {
                    this.mc.options.forwardKey.setPressed(false);
                    this.n(CollectStep.EQUIP);
                    break;
                }
                if (this.ag <= 60) break;
                this.info((String)"鞘翅掉落消失未拾取，直接离开该船.", new Object[0]);
                this.safeExitAfterCollectFailure();
                break;
            }
            case 6: {
                if (this.ct()) {
                    if (!this.ax) {
                        this.ax = true;
                        this.r();
                        if (this.bf != StoragePhase.NONE) {
                            return;
                        }
                    }
                    this.n(CollectStep.EXIT_P1);
                    break;
                }
                this.cs();
                break;
            }
            case 7: {
                if (!this.cb(this.af.k, 2.5)) break;
                this.n(CollectStep.EXIT_LANDING);
                break;
            }
            case 8: {
                if (!this.cb(this.af.j, 2.5)) break;
                this.cp(this.ae.d);
                this.info((String)"该船完成，加入黑名单.", new Object[0]);
                this.q();
                break;
            }
            case 9: {
                if (!this.cb(this.af.j.down(4), 2.5)) break;
                this.cw();
                this.info((String)"已回到降落点，起飞离开.", new Object[0]);
                this.x = State.RISING;
                this.ag = 0;
            }
        }
    }

    private void n(CollectStep next) {
        this.y = next;
        this.ag = 0;
        this.ah = 0;
        this.as = 0;
        this.at = false;
        this.ax = false;
        this.l();
    }

    private void o(String reason) {
        this.info(reason, new Object[0]);
        this.ac = true;
        ++this.ad;
        this.ab = null;
        this.bf = StoragePhase.NONE;
        this.bg = 0;
        this.ba = false;
        this.bc = 0.0f;
        this.bd = 0;
        this.be = 0;
        this.cw();
        this.b();
        if (this.x != State.IDLE) {
            this.x = State.IDLE;
        }
        PathManagers.get().stop();
        this.q.set(false);
    }

    private void p(String reason) {
        this.o(reason);
    }

    private void q() {
        this.r();
        if (this.bf == StoragePhase.NONE) {
            this.s();
        }
    }

    private void r() {
        this.bo = this.ca() <= (Integer)this.t.get();
        this.bp = this.bs();
        if (this.bo || this.bp) {
            this.info("存储会话: 存鞘翅=" + this.bo + ", 补货=" + this.bp, new Object[0]);
            StorageReturn.markStart((Object)this);
            this.bf = StoragePhase.PLACE_EC;
            this.bg = 0;
            this.bi = null;
            this.bj = null;
            this.bl = -1;
            this.bk = false;
            this.ct = 0;
            this.cu = false;
            this.cv = false;
            this.cw();
            PathManagers.get().stop();
        }
    }

    private void s() {
        this.z.clear();
        this.aa = 0;
        this.aq = 0;
        this.ar = this.ae != null ? this.ae.b : null;
        this.ay = false;
        this.ba = false;
        this.x = State.RISING;
        this.ag = 0;
        this.d();
    }

    private void t() {
        this.cp(this.ae.d);
        this.info((String)"该船跳过并加入黑名单，重新搜索最近未去过的船.", new Object[0]);
        this.z.clear();
        this.aa = 0;
        this.aq = 0;
        this.ar = this.ae.b;
        this.ay = false;
        this.ba = false;
        this.cw();
        this.d();
        if (this.mc.player.isOnGround() && this.af != null && this.cz(this.af.j) > 2.5) {
            this.info((String)"已降落：先回降落点修正起飞方向后再离开.", new Object[0]);
            this.x = State.COLLECTING;
            this.y = CollectStep.LEAVE_SHIP;
            this.ag = 0;
            this.l();
        } else {
            this.x = State.RISING;
            this.ag = 0;
        }
    }

    private void u() {
        try {
            ++this.bg;
            if (this.bg > 1200) {
                this.o("存储会话超时 (阶段=" + String.valueOf((Object)this.bf) + ")，停止任务.");
                return;
            }
            this.cw();
            this.ax();
            switch (this.bf.ordinal()) {
                case 1: {
                    this.v();
                    break;
                }
                case 2: {
                    this.x();
                    break;
                }
                case 3: {
                    this.y();
                    break;
                }
                case 4: {
                    this.z();
                    break;
                }
                case 5: {
                    this.aa();
                    break;
                }
                case 6: {
                    this.ab();
                    break;
                }
                case 7: {
                    this.ac();
                    break;
                }
                case 8: {
                    if (this.ca() <= (Integer)this.t.get() && this.bv() > 0) {
                        this.cu = true;
                        this.cv = true;
                        this.bo = true;
                        this.info("补给暂停: 背包剩余 " + this.ca() + " 槽，先回收补给盒、存鞘翅腾背包.", new Object[0]);
                        this.bf = StoragePhase.MINE_BOX;
                        this.bg = 0;
                        break;
                    }
                    this.ad();
                    break;
                }
                case 9: {
                    this.ae();
                    break;
                }
                case 10: {
                    this.af();
                    break;
                }
            }
        }
        catch (Throwable t) {
            this.error("存储会话异常: " + String.valueOf(t), new Object[0]);
            t.printStackTrace();
            this.o((String)"存储会话异常，停止任务.");
        }
    }

    private void v() {
        if (this.bg == 1) {
            BlockPos existing;
            if (this.cs == 0 && (existing = this.w(5)) != null) {
                this.bi = existing;
                this.info("存储: 发现周围已有末影箱 " + String.valueOf(existing) + "，直接打开.", new Object[0]);
                this.bf = StoragePhase.OPEN_EC;
                this.bg = 0;
                this.cn = 0;
                return;
            }
            int ecSlot = this.bx(Items.ENDER_CHEST);
            if (ecSlot == -1) {
                this.o((String)"背包里没有末影箱，停止任务.");
                return;
            }
            this.bn = this.bu(Items.ENDER_CHEST);
            this.aw(ecSlot);
            this.bs = this.bm(null);
            if (this.bs.isEmpty() && this.af != null && this.af.l != null) {
                this.info("附近无空位，改用检测潜影贝的位置 " + String.valueOf(this.af.l) + " 附近放置.", new Object[0]);
                this.bs = this.bn(this.af.l, null);
            }
            if (this.bs.isEmpty()) {
                this.o((String)"找不到放置末影箱的位置，停止任务.");
                return;
            }
            this.bt = 0;
            this.bu = -100;
            this.bv = 0;
        }
        if (this.bg >= 4 && this.bg - this.bu >= 8) {
            if (this.bt >= this.bs.size()) {
                this.o((String)"末影箱放置失败 (已尝试所有候选位置)，停止任务.");
                return;
            }
            BlockPos spot = this.bs.get(this.bt);
            if (this.mc.player.squaredDistanceTo((double)spot.getX() + 0.5, (double)spot.getY() + 0.5, (double)spot.getZ() + 0.5) > 16.0) {
                if (!PathManagers.get().isPathing()) {
                    PathManagers.get().moveTo(spot, false);
                }
                return;
            }
            this.bu = this.bg;
            ++this.bv;
            this.bi = spot;
            this.ak(spot.down(), Direction.UP);
            this.ai(spot.down());
            if (this.bv >= 2) {
                ++this.bt;
                this.bv = 0;
            }
        }
        if (this.bi != null && this.bj(this.mc.world.getBlockState(this.bi))) {
            PathManagers.get().stop();
            this.bf = StoragePhase.OPEN_EC;
            this.bg = 0;
            this.cn = 0;
            return;
        }
        if (this.bg > 80) {
            this.o((String)"末影箱放置失败 (超时)，停止任务.");
        }
    }

    private BlockPos w(int radius) {
        BlockPos.Mutable m = new BlockPos.Mutable();
        BlockPos p = this.mc.player.getBlockPos();
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                for (int dy = -2; dy <= 2; ++dy) {
                    m.set(p.getX() + dx, p.getY() + dy, p.getZ() + dz);
                    if (!this.bj(this.mc.world.getBlockState((BlockPos)m))) continue;
                    return m.toImmutable();
                }
            }
        }
        return null;
    }

    private void x() {
        if (this.bg == 1) {
            if (this.bi == null || !this.bj(this.mc.world.getBlockState(this.bi))) {
                this.info((String)"末影箱不在原位，重新放置.", new Object[0]);
                this.bf = StoragePhase.PLACE_EC;
                this.bg = 0;
                return;
            }
            this.cw = false;
        }
        if (!this.cw && this.an(this.bi)) {
            BlockPos off = this.ao(this.bi);
            if (off != null && !PathManagers.get().isPathing()) {
                PathManagers.get().moveTo(off, false);
                this.info((String)"存储: 玩家站在末影箱上，先走下来再打开.", new Object[0]);
            }
            if (this.an(this.bi)) {
                return;
            }
            this.cw = true;
        }
        if (this.cn < 5 && this.bg == 1 + this.cn * 20) {
            this.ak(this.bi, Direction.UP);
            this.ai(this.bi);
            ++this.cn;
        }
        if (this.ap()) {
            if (this.bg < 3) {
                return;
            }
            this.cs = 0;
            this.bf = StoragePhase.PICK_BOX;
            this.bg = 0;
        } else if (this.bg > 93) {
            if (this.an(this.bi)) {
                this.cw = false;
                this.bg = 0;
                this.info((String)"打开末影箱失败且玩家站在末影箱上，先走下来再重试.", new Object[0]);
                return;
            }
            this.warning((String)"打开末影箱失败 (已尝试 5 次)，放置新的末影箱再试.", new Object[0]);
            ++this.cs;
            this.bf = StoragePhase.PLACE_EC;
            this.bg = 0;
            this.bi = null;
            this.cn = 0;
        }
    }

    private void y() {
        ScreenHandler sh = this.mc.player.currentScreenHandler;
        int rows = this.aq();
        if (sh == null || rows <= 0) {
            this.o((String)"末影箱界面异常，停止任务.");
            return;
        }
        if (this.bg < 3) {
            return;
        }
        if (this.ca == 1) {
            if (!this.ay()) {
                return;
            }
            int emptyScreen = this.bb(sh, rows);
            if (emptyScreen != -1) {
                this.cc = emptyScreen - rows * 9 + 9;
                this.bw.add(new MoveOp(-1, emptyScreen));
                this.ca = 2;
            } else {
                this.bw.add(new MoveOp(-1, this.cb));
                this.ca = 3;
            }
            return;
        }
        if (this.ca == 2) {
            if (!this.ay()) {
                return;
            }
            this.bl = this.cc;
            this.bk = false;
            this.ca = 0;
            this.bq = StoragePhase.PLACE_BOX;
            this.bf = StoragePhase.CLOSE_SCREEN;
            this.bg = 0;
            return;
        }
        if (this.ca == 3) {
            if (!this.ay()) {
                return;
            }
            this.ca = 0;
            if (this.cp) {
                this.o((String)"背包无空位放潜影盒，无法补货，停止任务.");
                return;
            }
            this.o((String)"背包无空位放潜影盒，无法存鞘翅，停止任务.");
            return;
        }
        if (!this.ay()) {
            return;
        }
        if (this.bl != -1) {
            int screenSlot = this.az(rows, this.bl);
            if (this.ba(sh, rows) == -1) {
                this.o((String)"末影箱已满，无法归还潜影盒，停止任务.");
                return;
            }
            this.ah(SlotActionType.QUICK_MOVE, screenSlot);
            this.bl = -1;
            return;
        }
        int nonEmpty = 0;
        StringBuilder items = new StringBuilder();
        for (int i = 0; i <= this.ar(rows); ++i) {
            ItemStack st = sh.getSlot(i).getStack();
            if (st.isEmpty()) continue;
            ++nonEmpty;
            if (items.length() >= 80) continue;
            Item item = st.getItem();
            items.append(item.getName(item.getDefaultStack()).getString()).append((String)"(").append(st.getCount()).append((String)") ");
        }
        this.info("存储: 末影箱行数=" + rows + " 非空槽=" + nonEmpty + " [" + String.valueOf(items) + "]", new Object[0]);
        if (this.bo) {
            boolean bl = this.bo = this.bv() > 0;
        }
        if (!this.bo && !this.bp) {
            this.bf = StoragePhase.MINE_EC;
            this.bg = 0;
            return;
        }
        if (this.bo) {
            int bpBox = this.be();
            if (bpBox != -1) {
                this.bl = bpBox;
                this.bk = true;
                this.cc = bpBox;
                this.cp = false;
                this.bq = StoragePhase.PLACE_BOX;
                this.bf = StoragePhase.CLOSE_SCREEN;
                this.bg = 0;
                return;
            }
            int ecBox = this.bd(sh, rows);
            if (ecBox != -1) {
                this.cb = ecBox;
                this.cp = false;
                this.ca = 1;
                this.bw.add(new MoveOp(ecBox, -1));
                return;
            }
            if (this.ca() == 0) {
                this.p((String)"背包与末影箱全部放满，任务完成.");
                return;
            }
            this.info((String)"没有空位潜影盒，鞘翅留在背包.", new Object[0]);
            this.bo = false;
        }
        if (this.bp) {
            if (this.cv) {
                this.cv = false;
                this.ct = 0;
                this.info((String)"存储: 存鞘翅腾出空间，从末影箱开头重新扫描补给.", new Object[0]);
            }
            if (this.ct > this.ar(rows)) {
                if (this.bs()) {
                    this.o((String)"物资不足 (末影箱内已遍历完所有潜影盒)，停止任务.");
                    return;
                }
                this.info((String)"存储: 补货遍历完成.", new Object[0]);
                this.bp = false;
                this.bf = StoragePhase.MINE_EC;
                this.bg = 0;
                return;
            }
            List<SupplyRule> rules = this.br();
            for (int i = this.ct; i <= this.ar(rows); ++i) {
                ItemStack st = sh.getSlot(i).getStack();
                if (!st.isEmpty()) {
                    if (!this.bh(st)) {
                        if (!this.ck.contains(st.getItem()) && this.bt(st.getItem())) {
                            this.ct = i + 1;
                            this.ah(SlotActionType.QUICK_MOVE, i);
                            Item item = st.getItem();
                            this.info("存储: 从末影箱直接拿取散放物资 " + item.getName(item.getDefaultStack()).getString() + ".", new Object[0]);
                        } else {
                            this.ct = i + 1;
                        }
                        return;
                    }
                    boolean hasSupply = false;
                    for (SupplyRule r : rules) {
                        if (this.ck.contains(r.e) || !this.bt(r.e) || !this.bl(st).contains(r.e)) continue;
                        hasSupply = true;
                        break;
                    }
                    if (hasSupply) {
                        this.ct = i + 1;
                        this.cb = i;
                        this.cp = true;
                        this.ca = 1;
                        this.bw.add(new MoveOp(i, -1));
                        return;
                    }
                    this.ct = i + 1;
                    return;
                }
                this.ct = i + 1;
            }
            this.ct = this.ar(rows) + 1;
            return;
        }
        this.bf = StoragePhase.MINE_EC;
        this.bg = 0;
    }

    private void z() {
        if (this.ap()) {
            if (this.bg == 1) {
                this.mc.player.closeHandledScreen();
            }
            if (this.bg > 40) {
                this.o((String)"关闭容器界面超时，停止任务.");
                return;
            }
            return;
        }
        if (this.bg < 4) {
            return;
        }
        this.bf = this.bq;
        this.bq = StoragePhase.NONE;
        this.bg = 0;
    }

    private void aa() {
        if (this.bg == 1) {
            if (this.bl == -1) {
                this.o((String)"潜影盒槽位丢失，停止任务.");
                return;
            }
            this.bl = this.aw(this.bl);
            this.bs = this.bo(this.bi);
            this.cq = false;
            if (this.bs.isEmpty()) {
                this.info((String)"附近没有挂放位置 (空气+上方实心)，改用地板放置.", new Object[0]);
                this.bs = this.bp(this.bi);
                this.cq = true;
            }
            if (this.bs.isEmpty()) {
                this.o((String)"找不到放置潜影盒的位置 (需要空气格且上方有方块)，停止任务.");
                return;
            }
            this.bt = 0;
            this.bu = -100;
            this.bv = 0;
        }
        if (this.bg >= 4 && this.bg - this.bu >= 8) {
            BlockPos spot;
            if (this.bt >= this.bs.size()) {
                if (!this.cq) {
                    this.info((String)"挂放位置全部放置失败，改用地板放置.", new Object[0]);
                    this.bs = this.bp(this.bi);
                    this.cq = true;
                    this.bt = 0;
                    this.bv = 0;
                    if (this.bs.isEmpty()) {
                        this.o((String)"潜影盒放置失败 (找不到地板放置位置)，停止任务.");
                        return;
                    }
                } else {
                    this.o((String)"潜影盒放置失败 (已尝试所有候选位置)，停止任务.");
                    return;
                }
            }
            if (this.mc.player.squaredDistanceTo((double)(spot = this.bs.get(this.bt)).getX() + 0.5, (double)spot.getY() + 0.5, (double)spot.getZ() + 0.5) > 16.0) {
                if (!PathManagers.get().isPathing()) {
                    PathManagers.get().moveTo(spot, false);
                }
                return;
            }
            this.bu = this.bg;
            ++this.bv;
            this.bj = spot;
            this.bm = this.bw();
            if (this.cq) {
                this.ak(spot.down(), Direction.UP);
                this.aj(spot.down(), Direction.UP);
            } else {
                this.ak(spot.up(), Direction.DOWN);
                this.aj(spot.up(), Direction.DOWN);
            }
            if (this.bv >= 2) {
                ++this.bt;
                this.bv = 0;
            }
        }
        if (this.bj != null && this.bi(this.mc.world.getBlockState(this.bj))) {
            PathManagers.get().stop();
            this.bf = StoragePhase.OPEN_BOX;
            this.bg = 0;
            return;
        }
        if (this.bg > 160) {
            this.o((String)"潜影盒放置失败 (超时)，停止任务.");
        }
    }

    private void ab() {
        if (this.bg == 1) {
            if (this.bj == null || !this.bi(this.mc.world.getBlockState(this.bj))) {
                this.warning((String)"潜影盒不在原位，挖掉重来.", new Object[0]);
                this.bf = StoragePhase.MINE_BOX;
                this.bg = 0;
                return;
            }
            this.cw = false;
        }
        if (!this.cw && this.an(this.bj)) {
            BlockPos off = this.ao(this.bj);
            if (off != null && !PathManagers.get().isPathing()) {
                PathManagers.get().moveTo(off, false);
                this.info((String)"存储: 玩家站在潜影盒上，先走下来再打开.", new Object[0]);
            }
            if (this.an(this.bj)) {
                return;
            }
            this.cw = true;
        }
        if (this.cn < 5 && this.bg == 1 + this.cn * 20) {
            this.al();
            ++this.cn;
        }
        if (this.cn > 0 && this.co == -1 && this.am()) {
            this.co = this.bg;
            Utils.rightClick();
        } else if (this.co != -1 && !this.ap() && this.bg - this.co > 8) {
            this.co = -1;
        }
        if (this.ap()) {
            if (this.bg < 3) {
                return;
            }
            this.bf = this.cp ? StoragePhase.TAKE_SUPPLIES : StoragePhase.FILL_BOX;
            this.bg = 0;
            this.cn = 0;
            this.co = -1;
        } else if (this.bg > 93) {
            if (this.an(this.bj)) {
                this.cw = false;
                this.bg = 0;
                this.cn = 0;
                this.co = -1;
                this.info((String)"打开潜影盒失败且玩家站在潜影盒上，先走下来再重试.", new Object[0]);
                return;
            }
            this.warning((String)"打开潜影盒失败 (已尝试 5 次)，挖掉重来.", new Object[0]);
            this.bf = StoragePhase.MINE_BOX;
            this.bg = 0;
            this.cn = 0;
            this.co = -1;
        }
    }

    private void ac() {
        ScreenHandler sh = this.mc.player.currentScreenHandler;
        int rows = this.aq();
        if (sh == null || rows <= 0) {
            StorageRecovery.onInvalidScreen((Object)this);
            return;
        }
        StorageRecovery.onValidScreen((Object)this);
        if (this.bg < 3) {
            return;
        }
        if (!this.ay()) {
            return;
        }
        if (this.cd == -1) {
            this.cd = this.as(rows);
            this.info((String)"存储: 开始往潜影盒存鞘翅.", new Object[0]);
        }
        if (this.cl >= 0) {
            if (sh.getSlot(this.cl).getStack().getItem() == Items.ELYTRA) {
                ++this.cm;
                if (this.cm > 20) {
                    this.cd = -1;
                    this.cl = -1;
                    this.cm = 0;
                    this.info((String)"存储: 潜影盒已满.", new Object[0]);
                    this.bq = StoragePhase.MINE_BOX;
                    this.bf = StoragePhase.CLOSE_SCREEN;
                    this.bg = 0;
                }
                return;
            }
            this.cl = -1;
            this.cm = 0;
        }
        int src = -1;
        for (int s = this.cd; s <= this.at(rows); ++s) {
            if (sh.getSlot(s).getStack().getItem() != Items.ELYTRA) continue;
            src = s;
            break;
        }
        if (src == -1) {
            this.cd = -1;
            this.info((String)"存储: 鞘翅已全部存入潜影盒.", new Object[0]);
            this.bq = StoragePhase.MINE_BOX;
            this.bf = StoragePhase.CLOSE_SCREEN;
            this.bg = 0;
            return;
        }
        this.cd = src + 1;
        this.cl = src;
        this.cm = 0;
        this.ah(SlotActionType.QUICK_MOVE, src);
    }

    private void ad() {
        ScreenHandler sh = this.mc.player.currentScreenHandler;
        int rows = this.aq();
        if (sh == null || rows <= 0) {
            this.o((String)"潜影盒界面异常，停止任务.");
            return;
        }
        if (this.bg < 3) {
            return;
        }
        if (!this.ay()) {
            return;
        }
        SupplyRule target = null;
        List<SupplyRule> rules = this.br();
        for (SupplyRule rule : rules) {
            if (this.ck.contains(rule.e) || rule.g - this.bu(rule.e) <= 0) continue;
            for (int c = 0; c <= this.ar(rows); ++c) {
                ItemStack st = sh.getSlot(c).getStack();
                if (st.isEmpty() || st.getItem() != rule.e) continue;
                target = rule;
                break;
            }
            if (target == null) continue;
            break;
        }
        if (target != null) {
            for (int c = 0; c <= this.ar(rows); ++c) {
                ItemStack st = sh.getSlot(c).getStack();
                if (st.isEmpty() || st.getItem() != target.e) continue;
                if (this.ch == c) {
                    ++this.ci;
                    if (this.ci > 30) {
                        this.ch = -1;
                        this.ci = 0;
                        int boxFree = this.ba(sh, rows);
                        if (boxFree != -1) {
                            int elytra = this.bf(sh, rows);
                            if (elytra != -1) {
                                this.bw.add(new MoveOp(elytra, boxFree));
                                this.info((String)"存储: 背包空间不足，先把鞘翅放入盒子腾出空位.", new Object[0]);
                            } else {
                                int junk = this.bg(sh, rows, rules);
                                if (junk != -1) {
                                    this.bw.add(new MoveOp(junk, boxFree));
                                    this.info((String)"存储: 背包已满，先把杂物放入盒子腾出空位.", new Object[0]);
                                } else {
                                    this.ck.add(target.e);
                                    Item item = target.e;
                                    this.info("存储: 背包无空位且无杂物可腾，补货 " + item.getName(item.getDefaultStack()).getString() + " 跳过.", new Object[0]);
                                }
                            }
                        } else {
                            this.ck.add(target.e);
                            Item item = target.e;
                            this.info("存储: 盒子已满无法腾位，补货 " + item.getName(item.getDefaultStack()).getString() + " 跳过.", new Object[0]);
                        }
                    }
                    return;
                }
                this.ch = c;
                this.ci = 0;
                this.ah(SlotActionType.QUICK_MOVE, c);
                return;
            }
            this.ch = -1;
            this.ci = 0;
        }
        ++this.cj;
        if (this.cj < 10) {
            return;
        }
        this.cj = 0;
        this.ch = -1;
        this.ci = 0;
        this.info((String)"存储: 本盒物资已拿取完毕 (所有能拿且需要的).", new Object[0]);
        this.bq = StoragePhase.MINE_BOX;
        this.bf = StoragePhase.CLOSE_SCREEN;
        this.bg = 0;
    }

    private void ae() {
        BlockState s = this.mc.world.getBlockState(this.bj);
        if (this.bi(s)) {
            if (this.bg == 1) {
                this.bh = -1;
                PathManagers.get().stop();
                if (this.by() == -1) {
                    this.o((String)"需要精准采集工具才能回收潜影盒，停止任务.");
                    return;
                }
                PathManagers.get().mine(s.getBlock());
                this.info((String)"用 baritone (精准采集) 挖掘潜影盒.", new Object[0]);
            } else if (this.bg > 600) {
                this.o((String)"baritone 挖掘潜影盒超时，停止任务.");
            }
            return;
        }
        if (this.bh == -1) {
            this.bh = this.bg;
            PathManagers.get().stop();
        }
        if (this.bw() >= this.bm) {
            this.bl = this.cc >= 0 && this.cc < 36 && this.bh((ItemStack)this.mc.player.getInventory().getMainStacks().get(this.cc)) ? this.cc : this.bz();
            this.bf = this.bo || this.bp ? StoragePhase.OPEN_EC : StoragePhase.MINE_EC;
            this.bg = 0;
            this.bh = -1;
            return;
        }
        if (this.bg > this.bh + 200) {
            this.warning((String)"潜影盒掉落未拾取，继续任务.", new Object[0]);
            this.bl = -1;
            this.bk = false;
            this.bf = this.bo || this.bp ? StoragePhase.OPEN_EC : StoragePhase.MINE_EC;
            this.bg = 0;
            this.bh = -1;
            return;
        }
        ItemEntity it = this.bq(this.bj, 8.0);
        if (it != null && !PathManagers.get().isPathing()) {
            PathManagers.get().moveTo(it.getBlockPos(), false);
        }
    }

    private void af() {
        BlockState s = this.mc.world.getBlockState(this.bi);
        if (this.bj(s)) {
            if (this.bg == 1) {
                this.bh = -1;
                PathManagers.get().stop();
                if (this.by() == -1) {
                    this.o((String)"需要精准采集工具才能回收末影箱，停止任务.");
                    return;
                }
                PathManagers.get().mine(Blocks.ENDER_CHEST);
                this.info((String)"用 baritone (精准采集) 挖掘末影箱.", new Object[0]);
            } else if (this.bg > 600) {
                this.o((String)"baritone 挖掘末影箱超时，停止任务.");
            }
            return;
        }
        if (this.bh == -1) {
            this.bh = this.bg;
            PathManagers.get().stop();
        }
        if (this.bu(Items.ENDER_CHEST) >= this.bn) {
            this.ag();
            return;
        }
        if (this.bg > this.bh + 400) {
            this.warning((String)"末影箱掉落未拾取 (挖末影箱需要精准采集)，继续任务.", new Object[0]);
            this.ag();
            return;
        }
        ItemEntity it = this.bq(this.bi, 8.0);
        if (it != null && !PathManagers.get().isPathing()) {
            PathManagers.get().moveTo(it.getBlockPos(), false);
        }
    }

    private void ag() {
        this.bf = StoragePhase.NONE;
        this.bg = 0;
        this.bh = -1;
        if (this.x == State.COLLECTING && this.y == CollectStep.EQUIP) {
            this.info((String)"船旁存储会话结束，先走回降落点再起飞.", new Object[0]);
            this.n(CollectStep.EXIT_P1);
        } else {
            StorageReturn.begin((Object)this);
        }
    }

    private void ah(SlotActionType type, int slotId) {
        ScreenHandler sh = this.mc.player.currentScreenHandler;
        if (sh == null) {
            return;
        }
        this.mc.interactionManager.clickSlot(sh.syncId, slotId, 0, type, (PlayerEntity)this.mc.player);
    }

    private void ai(BlockPos pos) {
        this.aj(pos, Direction.UP);
    }

    private void aj(BlockPos pos, Direction face) {
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter((Vec3i)pos), face, pos, false);
        this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, hit);
    }

    private void ak(BlockPos target, Direction face) {
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

    private void al() {
        double cx = (double)this.bj.getX() + 0.5;
        double cy = (double)this.bj.getY() + 0.5;
        double cz = (double)this.bj.getZ() + 0.5;
        double dx = cx - this.mc.player.getX();
        double dy = cy - (this.mc.player.getY() + (double)this.mc.player.getStandingEyeHeight());
        double dz = cz - this.mc.player.getZ();
        float yaw = (float)Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float)(-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))));
        this.mc.player.setYaw(yaw);
        this.mc.player.setPitch(pitch);
        this.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, this.mc.player.isOnGround(), this.mc.player.horizontalCollision));
    }

    private boolean am() {
        BlockHitResult bhr;
        HitResult hitResult = this.mc.crosshairTarget;
        return hitResult instanceof BlockHitResult && (bhr = (BlockHitResult)hitResult).getBlockPos().equals((Object)this.bj);
    }

    private boolean an(BlockPos pos) {
        if (pos == null) {
            return false;
        }
        BlockPos feet = this.mc.player.getBlockPos();
        return feet.getX() == pos.getX() && feet.getY() == pos.getY() + 1 && feet.getZ() == pos.getZ();
    }

    private BlockPos ao(BlockPos pos) {
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

    private boolean ap() {
        ScreenHandler sh = this.mc.player.currentScreenHandler;
        return sh != null && !(sh instanceof PlayerScreenHandler);
    }

    private int aq() {
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

    private int ar(int rows) {
        return rows * 9 - 1;
    }

    private int as(int rows) {
        return rows * 9;
    }

    private int at(int rows) {
        return rows * 9 + 35;
    }

    private int au(int rows) {
        return rows * 9 + 26;
    }

    private int av(int rows, int invSlot) {
        return rows * 9 + 27 + invSlot;
    }

    private int aw(int invSlot) {
        if (invSlot <= 8) {
            InvUtils.swap((int)invSlot, (boolean)false);
            return invSlot;
        }
        InvUtils.move().from(invSlot).toHotbar(0);
        InvUtils.swap((int)0, (boolean)false);
        return 0;
    }

    private void ax() {
        if (this.bx == 0) {
            if (this.bw.isEmpty()) {
                return;
            }
            MoveOp op = this.bw.poll();
            this.by = op.h;
            this.bz = op.i;
            if (this.by == -1) {
                this.ah(SlotActionType.PICKUP, this.bz);
                this.bx = 2;
            } else {
                this.ah(SlotActionType.PICKUP, this.by);
                this.bx = this.bz == -1 ? 3 : 1;
            }
            return;
        }
        ScreenHandler sh = this.mc.player.currentScreenHandler;
        if (sh == null) {
            return;
        }
        ItemStack cursor = sh.getCursorStack();
        if (this.bx == 3) {
            if (!cursor.isEmpty()) {
                this.bx = 0;
            }
        } else if (this.bx == 1) {
            if (!cursor.isEmpty()) {
                this.ah(SlotActionType.PICKUP, this.bz);
                this.bx = 2;
            }
        } else if (this.bx == 2 && cursor.isEmpty()) {
            this.bx = 0;
        }
    }

    private boolean ay() {
        return this.bx == 0 && this.bw.isEmpty();
    }

    private int az(int rows, int invSlot) {
        return invSlot <= 8 ? this.av(rows, invSlot) : rows * 9 + (invSlot - 9);
    }

    private int ba(ScreenHandler sh, int rows) {
        for (int i = 0; i <= this.ar(rows); ++i) {
            if (!sh.getSlot(i).getStack().isEmpty()) continue;
            return i;
        }
        return -1;
    }

    private int bb(ScreenHandler sh, int rows) {
        for (int i = this.as(rows); i <= this.at(rows); ++i) {
            if (!sh.getSlot(i).getStack().isEmpty()) continue;
            return i;
        }
        return -1;
    }

    private int bc(ScreenHandler sh, int rows, Item item) {
        for (int i = this.as(rows); i <= this.at(rows); ++i) {
            ItemStack st = sh.getSlot(i).getStack();
            if (st.getItem() != item || st.getCount() >= st.getMaxCount()) continue;
            return i;
        }
        return -1;
    }

    private int bd(ScreenHandler sh, int rows) {
        for (int i = 0; i <= this.ar(rows); ++i) {
            ItemStack st = sh.getSlot(i).getStack();
            if (!this.bh(st) || this.bk(st) >= 27) continue;
            return i;
        }
        return -1;
    }

    private int be() {
        for (int i = 0; i < 36; ++i) {
            ItemStack st = (ItemStack)this.mc.player.getInventory().getMainStacks().get(i);
            if (!this.bh(st) || this.bk(st) >= 27) continue;
            return i;
        }
        return -1;
    }

    private int bf(ScreenHandler sh, int rows) {
        for (int i = this.as(rows); i <= this.at(rows); ++i) {
            if (sh.getSlot(i).getStack().getItem() != Items.ELYTRA) continue;
            return i;
        }
        return -1;
    }

    private int bg(ScreenHandler sh, int rows, List<SupplyRule> rules) {
        HashSet<Item> keep = new HashSet<Item>();
        for (SupplyRule r : rules) {
            keep.add(r.e);
        }
        keep.add(Items.ELYTRA);
        keep.add(Items.ENDER_CHEST);
        int silkSlot = this.by();
        for (int i = this.as(rows); i <= this.at(rows); ++i) {
            ItemStack st = sh.getSlot(i).getStack();
            if (st.isEmpty() || this.bh(st) || keep.contains(st.getItem()) || silkSlot != -1 && i == this.az(rows, silkSlot)) continue;
            return i;
        }
        return -1;
    }

    private boolean bh(ItemStack stack) {
        BlockItem bi;
        Item item = stack.getItem();
        return item instanceof BlockItem && (bi = (BlockItem)item).getBlock() instanceof ShulkerBoxBlock;
    }

    private boolean bi(BlockState state) {
        return state.getBlock() instanceof ShulkerBoxBlock;
    }

    private boolean bj(BlockState state) {
        return state.getBlock() == Blocks.ENDER_CHEST;
    }

    private int bk(ItemStack box) {
        ContainerComponent container = (ContainerComponent)box.get(DataComponentTypes.CONTAINER);
        if (container == null) {
            return 0;
        }
        return (int)container.streamNonEmpty().count();
    }

    private Set<Item> bl(ItemStack box) {
        HashSet<Item> out = new HashSet<Item>();
        ContainerComponent container = (ContainerComponent)box.get(DataComponentTypes.CONTAINER);
        if (container == null) {
            return out;
        }
        container.streamNonEmpty().forEach(st -> out.add(st.getItem()));
        return out;
    }

    private List<BlockPos> bm(BlockPos exclude) {
        return this.bn(this.mc.player.getBlockPos(), exclude);
    }

    private List<BlockPos> bn(BlockPos center, BlockPos exclude) {
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

    private List<BlockPos> bo(BlockPos exclude) {
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

    private List<BlockPos> bp(BlockPos exclude) {
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

    private ItemEntity bq(BlockPos pos, double radius) {
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

    private List<SupplyRule> br() {
        ArrayList<SupplyRule> out = new ArrayList<SupplyRule>();
        for (String entry : (List<String>)this.u.get()) {
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

    private boolean bs() {
        for (SupplyRule r : this.br()) {
            if (this.bu(r.e) >= r.f) continue;
            return true;
        }
        return false;
    }

    private boolean bt(Item item) {
        for (SupplyRule r : this.br()) {
            if (r.e != item || this.bu(item) >= r.g) continue;
            return true;
        }
        return false;
    }

    private int bu(Item item) {
        int n = 0;
        for (int i = 0; i < 36; ++i) {
            ItemStack st = (ItemStack)this.mc.player.getInventory().getMainStacks().get(i);
            if (st.getItem() != item) continue;
            n += st.getCount();
        }
        return n;
    }

    private int bv() {
        return this.bu(Items.ELYTRA);
    }

    private int bw() {
        int n = 0;
        for (int i = 0; i < 36; ++i) {
            if (!this.bh((ItemStack)this.mc.player.getInventory().getMainStacks().get(i))) continue;
            ++n;
        }
        return n;
    }

    private int bx(Item item) {
        for (int i = 0; i < 36; ++i) {
            if (((ItemStack)this.mc.player.getInventory().getMainStacks().get(i)).getItem() != item) continue;
            return i;
        }
        return -1;
    }

    private int by() {
        Registry reg = this.mc.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        RegistryEntry entry = reg.getEntry((Object)((Enchantment)reg.get(Enchantments.SILK_TOUCH)));
        for (int i = 0; i < 36; ++i) {
            ItemStack st = (ItemStack)this.mc.player.getInventory().getMainStacks().get(i);
            if (EnchantmentHelper.getLevel((RegistryEntry)entry, (ItemStack)st) <= 0) continue;
            return i;
        }
        return -1;
    }

    private int bz() {
        for (int i = 0; i < 36; ++i) {
            if (!this.bh((ItemStack)this.mc.player.getInventory().getMainStacks().get(i))) continue;
            return i;
        }
        return -1;
    }

    private int ca() {
        int n = 0;
        for (int i = 0; i < 36; ++i) {
            if (!((ItemStack)this.mc.player.getInventory().getMainStacks().get(i)).isEmpty()) continue;
            ++n;
        }
        return n;
    }

    private boolean cb(BlockPos pos, double tolerance) {
        double dz;
        if (this.ag < 6) {
            return false;
        }
        if (PathManagers.get().isPathing()) {
            return false;
        }
        double dx = this.mc.player.getX() - ((double)pos.getX() + 0.5);
        return dx * dx + (dz = this.mc.player.getZ() - ((double)pos.getZ() + 0.5)) * dz <= tolerance * tolerance;
    }

    private float cc(int x, int z) {
        int i = x / 2;
        int j = z / 2;
        int k = x % 2;
        int l = z % 2;
        float f = 100.0f - (float)Math.sqrt((double)x * (double)x + (double)z * (double)z) * 8.0f;
        f = ElytraCollectorModule.cd(f);
        for (int rx = -12; rx <= 12; ++rx) {
            for (int rz = -12; rz <= 12; ++rz) {
                long k1 = i + rx;
                long l1 = j + rz;
                if (this.b == null || k1 * k1 + l1 * l1 <= 4096L || !(this.b.sample2D(k1, l1) < (double)-0.9f)) continue;
                float f1 = (Math.abs((float)k1) * 3439.0f + Math.abs((float)l1) * 147.0f) % 13.0f + 9.0f;
                float f2 = k - rx * 2;
                float f3 = l - rz * 2;
                float f4 = 100.0f - (float)Math.sqrt(f2 * f2 + f3 * f3) * f1;
                f4 = ElytraCollectorModule.cd(f4);
                f = Math.max(f, f4);
            }
        }
        return f;
    }

    private static float cd(float value) {
        if (value < -100.0f) {
            return -100.0f;
        }
        return Math.min(value, 80.0f);
    }

    private double ce(int blockX, int blockZ) {
        return ((double)this.cc(blockX / 8, blockZ / 8) - 8.0) / 128.0;
    }

    private boolean cf(int chunkX, int chunkZ) {
        int blockX = chunkX * 16 + 8;
        long sx = blockX >> 4;
        int blockZ = chunkZ * 16 + 8;
        long sz = blockZ >> 4;
        if (sx * sx + sz * sz <= 4096L) {
            return false;
        }
        return this.ce(blockX, blockZ) >= -0.0625;
    }

    private boolean cg() {
        int[][] cols;
        int chunkX = this.ae.c.getX();
        int chunkZ = this.ae.c.getZ();
        ChunkRand r = new ChunkRand();
        r.setSeed(this.ai);
        long a2 = r.nextLong();
        long b = r.nextLong();
        r.setSeed((long)chunkX * a2 ^ (long)chunkZ * b ^ this.ai);
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
        if (((Boolean)this.r.get()).booleanValue()) {
            this.cm("city=(" + chunkX + "," + chunkZ + ") rotation=" + String.valueOf((Object)rotation) + " min=" + min);
            this.ch(posX, posZ, h1);
            this.ch(posX, posZ + zOff, h2);
            this.ch(posX + xOff, posZ, h3);
            this.ch(posX + xOff, posZ + zOff, h4);
        }
        return min >= 60;
    }

    private void ch(int x, int z, int realHeight) {
        float rawF = this.cc(x / 8, z / 8);
        double e = ((double)rawF - 8.0) / 128.0;
        int vanilla = this.ci(x, z);
        this.cm("col(" + x + "," + z + ") real=" + realHeight + " vanilla=" + vanilla + " E=" + String.format((String)"%.4f", e) + " rawF=" + String.format((String)"%.1f", Float.valueOf(rawF)));
    }

    private int ci(int x, int z) {
        if (this.c == null) {
            return 0;
        }
        int x0 = Math.floorDiv(x, 4) * 4;
        int z0 = Math.floorDiv(z, 4) * 4;
        double xf = (double)(x - x0) / 4.0;
        double zf = (double)(z - z0) / 4.0;
        double e00 = ((double)this.cc(x0 / 8, z0 / 8) - 8.0) / 128.0;
        double e10 = ((double)this.cc((x0 + 4) / 8, z0 / 8) - 8.0) / 128.0;
        double e01 = ((double)this.cc(x0 / 8, (z0 + 4) / 8) - 8.0) / 128.0;
        double e11 = ((double)this.cc((x0 + 4) / 8, (z0 + 4) / 8) - 8.0) / 128.0;
        double n00 = this.c.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0, 256, z0));
        double n10 = this.c.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0 + 4, 256, z0));
        double n01 = this.c.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0, 256, z0 + 4));
        double n11 = this.c.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0 + 4, 256, z0 + 4));
        for (int cell = 63; cell >= 0; --cell) {
            int yb = cell * 4;
            double b00 = this.c.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0, yb, z0));
            double b10 = this.c.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0 + 4, yb, z0));
            double b01 = this.c.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0, yb, z0 + 4));
            double b11 = this.c.sample((DensityFunction.NoisePos)new DensityFunction.UnblendedNoisePos(x0 + 4, yb, z0 + 4));
            double c000 = ElytraCollectorModule.cj(e00 + b00, yb);
            double c010 = ElytraCollectorModule.cj(e00 + n00, yb + 4);
            double c100 = ElytraCollectorModule.cj(e10 + b10, yb);
            double c110 = ElytraCollectorModule.cj(e10 + n10, yb + 4);
            double c001 = ElytraCollectorModule.cj(e01 + b01, yb);
            double c011 = ElytraCollectorModule.cj(e01 + n01, yb + 4);
            double c101 = ElytraCollectorModule.cj(e11 + b11, yb);
            double c111 = ElytraCollectorModule.cj(e11 + n11, yb + 4);
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

    private static double cj(double f, int y) {
        double top = y <= 56 ? 1.0 : (y >= 312 ? 0.0 : (312.0 - (double)y) / 256.0);
        double middle = -23.4375 + top * (f + 23.4375);
        double bottom = y <= 4 ? 0.0 : (y >= 32 ? 1.0 : ((double)y - 4.0) / 28.0);
        return -0.234375 + bottom * (middle + 0.234375);
    }

    private boolean ck(int chunkX, int chunkZ) {
        ChunkRand r = new ChunkRand();
        r.setSeed(this.ai);
        long a2 = r.nextLong();
        long b = r.nextLong();
        r.setSeed((long)chunkX * a2 ^ (long)chunkZ * b ^ this.ai);
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
        int min = this.ci(posX, posZ);
        min = Math.min(min, this.ci(posX, posZ + zOff));
        min = Math.min(min, this.ci(posX + xOff, posZ));
        min = Math.min(min, this.ci(posX + xOff, posZ + zOff));
        if (((Boolean)this.r.get()).booleanValue()) {
            this.cm("city=(" + chunkX + "," + chunkZ + ") rotation=" + String.valueOf((Object)rotation) + " predMin=" + min);
        }
        return min >= 60;
    }

    private Path cl() {
        return this.mc.runDirectory.toPath().resolve((String)"elytra-collector-debug.log");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cm(String msg) {
        if (!((Boolean)this.r.get()).booleanValue()) {
            return;
        }
        try {
            String line = LocalTime.now().format(DateTimeFormatter.ofPattern((String)"HH:mm:ss")) + " " + msg + System.lineSeparator();
            Object object = this.cx;
            synchronized (object) {
                Files.writeString(this.cl(), (CharSequence)line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        }
        catch (Exception e) {
            this.error("调试日志写入失败: " + e.getMessage(), new Object[0]);
        }
    }

    private String cn(BlockPos head) {
        return head.getX() + "," + head.getZ();
    }

    private boolean co(BlockPos blockPos) {
        return ElytraVisitedFix.isVisited((Object)this, blockPos, this.t19IgnoreVisited);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cp(BlockPos head) {
        List list = (List)this.o.get();
        synchronized (list) {
            String key = this.cn(head);
            if (!((List)this.o.get()).contains(key)) {
                ((List)this.o.get()).add(key);
            }
        }
    }

    private void cq() {
        ItemFrameEntity frame = this.df((Double)this.m.get());
        if (frame == null) {
            if (this.af != null) {
                this.cx(this.af.b);
            }
            frame = this.df((Double)this.m.get() + 2.0);
        }
        if (frame != null) {
            this.mc.interactionManager.attackEntity((PlayerEntity)this.mc.player, (Entity)frame);
            this.info((String)"攻击展示框.", new Object[0]);
        } else {
            this.info((String)"未找到展示框.", new Object[0]);
        }
    }

    private void cr() {
        ItemEntity item = this.di();
        if (item != null) {
            PathManagers.get().moveTo(item.getBlockPos(), false);
        }
    }

    private void cs() {
        if (this.ct()) {
            return;
        }
        int slot = this.dm();
        if (slot == -1) {
            return;
        }
        InvUtils.move().from(slot).toArmor(2);
        this.info((String)"已穿上鞘翅.", new Object[0]);
    }

    private boolean ct() {
        ItemStack chest = this.mc.player.getEquippedStack(EquipmentSlot.CHEST);
        return chest.getItem() == Items.ELYTRA && chest.getDamage() == 0;
    }

    private void cu(double x, double z) {
        double dx = x - this.mc.player.getX();
        double dz = z - this.mc.player.getZ();
        this.mc.player.setYaw((float)Math.toDegrees(Math.atan2(-dx, dz)));
        this.mc.options.forwardKey.setPressed(true);
    }

    private void cv() {
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

    private void cw() {
        this.mc.options.forwardKey.setPressed(false);
    }

    private void cx(Direction d) {
        float yaw = switch (d) {
            case Direction.SOUTH -> 0.0f;
            case Direction.WEST -> 90.0f;
            case Direction.NORTH -> 180.0f;
            case Direction.EAST -> -90.0f;
            default -> 0.0f;
        };
        this.db(yaw, this.mc.player.getPitch());
    }

    private float cy(BlockPos pos) {
        double dx = (double)pos.getX() + 0.5 - this.mc.player.getX();
        double dz = (double)pos.getZ() + 0.5 - this.mc.player.getZ();
        return (float)Math.toDegrees(Math.atan2(-dx, dz));
    }

    private double cz(BlockPos pos) {
        double dx = (double)pos.getX() + 0.5 - this.mc.player.getX();
        double dz = (double)pos.getZ() + 0.5 - this.mc.player.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private void da(float targetYaw, float targetPitch) {
        this.mc.player.setYaw(ElytraCollectorModule.dc(this.mc.player.getYaw(), targetYaw, ((Double)this.l.get()).floatValue()));
        this.mc.player.setPitch(ElytraCollectorModule.dc(this.mc.player.getPitch(), targetPitch, ((Double)this.k.get()).floatValue()));
    }

    private void db(float targetYaw, float targetPitch) {
        float speed = ((Double)this.l.get()).floatValue();
        this.mc.player.setYaw(ElytraCollectorModule.dc(this.mc.player.getYaw(), targetYaw, speed));
        this.mc.player.setPitch(ElytraCollectorModule.dc(this.mc.player.getPitch(), targetPitch, speed));
    }

    private static float dc(float current, float target, float speed) {
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

    private static boolean dd(BlockState state) {
        String id = Registries.BLOCK.getId(state.getBlock()).getPath();
        return id.equals((String)"dragon_head") || id.equals((String)"dragon_wall_head");
    }

    private DragonHead de(BlockPos center, int radius, int yRange) {
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                for (int dy = -yRange; dy <= yRange; ++dy) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState s = this.mc.world.getBlockState((BlockPos)pos);
                    if (!ElytraCollectorModule.dd(s)) continue;
                    Direction facing = s.contains((Property)Properties.FACING) ? (Direction)s.get((Property)Properties.FACING) : Direction.NORTH;
                    return new DragonHead(pos.toImmutable(), facing);
                }
            }
        }
        return null;
    }

    private ItemFrameEntity df(double reach) {
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

    private ItemFrameEntity dg(BlockPos pos) {
        for (Entity entity : this.mc.world.getOtherEntities(null, new Box(pos), e -> e instanceof ItemFrameEntity)) {
            if (!(entity instanceof ItemFrameEntity)) continue;
            ItemFrameEntity frame = (ItemFrameEntity)entity;
            return frame;
        }
        return null;
    }

    private boolean dh() {
        if (this.af == null) {
            return false;
        }
        Box box = new Box(this.af.n);
        for (Entity entity : this.mc.world.getOtherEntities(null, box, e -> e.getType() == EntityType.SHULKER)) {
            if (!entity.getBlockPos().equals((Object)this.af.n)) continue;
            return true;
        }
        return false;
    }

    private ItemEntity di() {
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

    private boolean dj() {
        return this.dm() != -1 || this.dl() != -1;
    }

    private boolean dk() {
        return this.mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
    }

    private int dl() {
        for (int i = 0; i < this.mc.player.getInventory().getMainStacks().size(); ++i) {
            if (((ItemStack)this.mc.player.getInventory().getMainStacks().get(i)).getItem() != Items.ELYTRA) continue;
            return i;
        }
        return -1;
    }

    private int dm() {
        for (int i = 0; i < this.mc.player.getInventory().getMainStacks().size(); ++i) {
            ItemStack s = (ItemStack)this.mc.player.getInventory().getMainStacks().get(i);
            if (s.getItem() != Items.ELYTRA || s.getDamage() != 0) continue;
            return i;
        }
        return -1;
    }

    private long dn(String s) {
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
        this.cp(this.ae.d);
        this.z.clear();
        this.aa = 0;
        this.aq = 0;
        this.ar = this.ae.b;
        this.ay = false;
        this.ba = false;
        this.cw();
        this.d();
        this.x = State.COLLECTING;
        this.y = CollectStep.EXIT_P1;
        this.ag = 0;
        this.l();
    }

    private enum State {
        IDLE,
        SEARCHING,
        RISING,
        FLYING,
        LANDING,
        COLLECTING,
        DONE;


        private static /* synthetic */ State[] f() {
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


        private static /* synthetic */ CollectStep[] c() {
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


        private static /* synthetic */ StoragePhase[] h() {
            return new StoragePhase[]{NONE, PLACE_EC, OPEN_EC, PICK_BOX, CLOSE_SCREEN, PLACE_BOX, OPEN_BOX, FILL_BOX, TAKE_SUPPLIES, MINE_BOX, MINE_EC};
        }

        
    }

    private record ShipTarget(CPos c, BlockPos d, Direction b) {
    }

    private record ShipWaypoints(BlockPos j, BlockPos k, BlockPos l, BlockPos m, BlockPos n, Direction b) {
        static ShipWaypoints j(BlockPos head, Direction facing) {
            return new ShipWaypoints(ShipWaypoints.k(head, facing, 7, 0, 4), ShipWaypoints.k(head, facing, 23, -1, -1), ShipWaypoints.k(head, facing, 9, -1, -4), ShipWaypoints.k(head, facing, 7, 0, -3), ShipWaypoints.k(head, facing, 8, 0, -4), facing);
        }

        static BlockPos k(BlockPos head, Direction facing, int behind, int left, int dy) {
            return head.offset(facing.getOpposite(), behind).offset(facing.rotateYCounterclockwise(), left).offset(Direction.UP, dy);
        }

    }

    private record DragonHead(BlockPos a, Direction b) {
    }

    private record MoveOp(int h, int i) {
    }

    private record SupplyRule(Item e, int f, int g) {
    }
}

