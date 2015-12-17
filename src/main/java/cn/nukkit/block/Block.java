package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Block extends Position implements Metadatable, Cloneable {
    public static final int AIR = 0;
    public static final int STONE = 1;
    public static final int GRASS = 2;
    public static final int DIRT = 3;
    public static final int COBBLESTONE = 4;
    public static final int COBBLE = 4;
    public static final int PLANK = 5;
    public static final int PLANKS = 5;
    public static final int WOODEN_PLANK = 5;
    public static final int WOODEN_PLANKS = 5;
    public static final int SAPLING = 6;
    public static final int SAPLINGS = 6;
    public static final int BEDROCK = 7;
    public static final int WATER = 8;
    public static final int STILL_WATER = 9;
    public static final int LAVA = 10;
    public static final int STILL_LAVA = 11;
    public static final int SAND = 12;
    public static final int GRAVEL = 13;
    public static final int GOLD_ORE = 14;
    public static final int IRON_ORE = 15;
    public static final int COAL_ORE = 16;
    public static final int WOOD = 17;
    public static final int TRUNK = 17;
    public static final int LOG = 17;
    public static final int LEAVES = 18;
    public static final int LEAVE = 18;
    public static final int SPONGE = 19;
    public static final int GLASS = 20;
    public static final int LAPIS_ORE = 21;
    public static final int LAPIS_BLOCK = 22;

    public static final int SANDSTONE = 24;

    public static final int BED_BLOCK = 26;


    public static final int COBWEB = 30;
    public static final int TALL_GRASS = 31;
    public static final int BUSH = 32;
    public static final int DEAD_BUSH = 32;
    public static final int WOOL = 35;
    public static final int DANDELION = 37;
    public static final int ROSE = 38;
    public static final int POPPY = 38;
    public static final int FLOWER = 38;
    public static final int RED_FLOWER = 38;
    public static final int BROWN_MUSHROOM = 39;
    public static final int RED_MUSHROOM = 40;
    public static final int GOLD_BLOCK = 41;
    public static final int IRON_BLOCK = 42;
    public static final int DOUBLE_SLAB = 43;
    public static final int DOUBLE_SLABS = 43;
    public static final int SLAB = 44;
    public static final int SLABS = 44;
    public static final int BRICKS = 45;
    public static final int BRICKS_BLOCK = 45;
    public static final int TNT = 46;
    public static final int BOOKSHELF = 47;
    public static final int MOSS_STONE = 48;
    public static final int MOSSY_STONE = 48;
    public static final int OBSIDIAN = 49;
    public static final int TORCH = 50;
    public static final int FIRE = 51;
    public static final int MONSTER_SPAWNER = 52;
    public static final int WOOD_STAIRS = 53;
    public static final int WOODEN_STAIRS = 53;
    public static final int OAK_WOOD_STAIRS = 53;
    public static final int OAK_WOODEN_STAIRS = 53;
    public static final int CHEST = 54;

    public static final int DIAMOND_ORE = 56;
    public static final int DIAMOND_BLOCK = 57;
    public static final int CRAFTING_TABLE = 58;
    public static final int WORKBENCH = 58;
    public static final int WHEAT_BLOCK = 59;
    public static final int FARMLAND = 60;
    public static final int FURNACE = 61;
    public static final int BURNING_FURNACE = 62;
    public static final int LIT_FURNACE = 62;
    public static final int SIGN_POST = 63;
    public static final int DOOR_BLOCK = 64;
    public static final int WOODEN_DOOR_BLOCK = 64;
    public static final int WOOD_DOOR_BLOCK = 64;
    public static final int LADDER = 65;

    public static final int COBBLE_STAIRS = 67;
    public static final int COBBLESTONE_STAIRS = 67;
    public static final int WALL_SIGN = 68;

    public static final int IRON_DOOR_BLOCK = 71;

    public static final int REDSTONE_ORE = 73;
    public static final int GLOWING_REDSTONE_ORE = 74;
    public static final int LIT_REDSTONE_ORE = 74;

    public static final int SNOW = 78;
    public static final int SNOW_LAYER = 78;
    public static final int ICE = 79;
    public static final int SNOW_BLOCK = 80;
    public static final int CACTUS = 81;
    public static final int CLAY_BLOCK = 82;
    public static final int REEDS = 83;
    public static final int SUGARCANE_BLOCK = 83;

    public static final int FENCE = 85;
    public static final int PUMPKIN = 86;
    public static final int NETHERRACK = 87;
    public static final int SOUL_SAND = 88;
    public static final int GLOWSTONE = 89;
    public static final int GLOWSTONE_BLOCK = 89;


    public static final int LIT_PUMPKIN = 91;
    public static final int JACK_O_LANTERN = 91;
    public static final int CAKE_BLOCK = 92;

    public static final int TRAPDOOR = 96;

    public static final int STONE_BRICKS = 98;
    public static final int STONE_BRICK = 98;

    public static final int IRON_BAR = 101;
    public static final int IRON_BARS = 101;
    public static final int GLASS_PANE = 102;
    public static final int GLASS_PANEL = 102;
    public static final int MELON_BLOCK = 103;
    public static final int PUMPKIN_STEM = 104;
    public static final int MELON_STEM = 105;
    public static final int VINE = 106;
    public static final int VINES = 106;
    public static final int FENCE_GATE = 107;
    public static final int FENCE_GATE_OAK = 107;
    public static final int BRICK_STAIRS = 108;
    public static final int STONE_BRICK_STAIRS = 109;
    public static final int MYCELIUM = 110;
    public static final int WATER_LILY = 111;
    public static final int LILY_PAD = 111;
    public static final int NETHER_BRICKS = 112;
    public static final int NETHER_BRICK_BLOCK = 112;
    public static final int NETHER_BRICK_FENCE = 113;
    public static final int NETHER_BRICKS_STAIRS = 114;

    public static final int ENCHANTING_TABLE = 116;
    public static final int ENCHANT_TABLE = 116;
    public static final int ENCHANTMENT_TABLE = 116;
    public static final int BREWING_STAND_BLOCK = 117;
    public static final int BREWING_BLOCK = 117;

    public static final int END_PORTAL_FRAME = 120;
    public static final int END_STONE = 121;

    public static final int SANDSTONE_STAIRS = 128;
    public static final int EMERALD_ORE = 129;

    public static final int EMERALD_BLOCK = 133;
    public static final int SPRUCE_WOOD_STAIRS = 134;
    public static final int SPRUCE_WOODEN_STAIRS = 134;
    public static final int BIRCH_WOOD_STAIRS = 135;
    public static final int BIRCH_WOODEN_STAIRS = 135;
    public static final int JUNGLE_WOOD_STAIRS = 136;
    public static final int JUNGLE_WOODEN_STAIRS = 136;

    public static final int COBBLE_WALL = 139;
    public static final int STONE_WALL = 139;
    public static final int COBBLESTONE_WALL = 139;

    public static final int CARROT_BLOCK = 141;
    public static final int POTATO_BLOCK = 142;

    public static final int ANVIL = 145;

    public static final int REDSTONE_BLOCK = 152;

    public static final int QUARTZ_BLOCK = 155;
    public static final int QUARTZ_STAIRS = 156;
    public static final int DOUBLE_WOOD_SLAB = 157;
    public static final int DOUBLE_WOODEN_SLAB = 157;
    public static final int DOUBLE_WOOD_SLABS = 157;
    public static final int DOUBLE_WOODEN_SLABS = 157;
    public static final int WOOD_SLAB = 158;
    public static final int WOODEN_SLAB = 158;
    public static final int WOOD_SLABS = 158;
    public static final int WOODEN_SLABS = 158;
    public static final int STAINED_CLAY = 159;
    public static final int STAINED_HARDENED_CLAY = 159;

    public static final int LEAVES2 = 161;
    public static final int LEAVE2 = 161;
    public static final int WOOD2 = 162;
    public static final int TRUNK2 = 162;
    public static final int LOG2 = 162;
    public static final int ACACIA_WOOD_STAIRS = 163;
    public static final int ACACIA_WOODEN_STAIRS = 163;
    public static final int DARK_OAK_WOOD_STAIRS = 164;
    public static final int DARK_OAK_WOODEN_STAIRS = 164;

    public static final int HAY_BALE = 170;
    public static final int CARPET = 171;
    public static final int HARDENED_CLAY = 172;
    public static final int COAL_BLOCK = 173;
    public static final int PACKED_ICE = 174;
    public static final int DOUBLE_PLANT = 175;

    public static final int FENCE_GATE_SPRUCE = 183;
    public static final int FENCE_GATE_BIRCH = 184;
    public static final int FENCE_GATE_JUNGLE = 185;
    public static final int FENCE_GATE_DARK_OAK = 186;
    public static final int FENCE_GATE_ACACIA = 187;

    public static final int GRASS_PATH = 198;

    public static final int PODZOL = 243;
    public static final int BEETROOT_BLOCK = 244;
    public static final int STONECUTTER = 245;
    public static final int GLOWING_OBSIDIAN = 246;

    public static Class[] list = null;
    public static Block[] fullList = null;
    public static int[] light = null;
    public static int[] lightFilter = null;
    public static boolean[] solid = null;
    public static double[] hardness = null;
    public static boolean[] transparent = null;

    protected int meta = 0;

    public AxisAlignedBB boundingBox = null;

    protected Block(Integer meta) {
        this.meta = (meta != null ? meta : 0);
    }

    @SuppressWarnings("unchecked")
    public static void init() {
        if (list == null) {
            list = new Class[256];
            fullList = new Block[4096];
            light = new int[256];
            lightFilter = new int[256];
            solid = new boolean[256];
            hardness = new double[256];
            transparent = new boolean[256];

            list[AIR] = Air.class;
            list[STONE] = Stone.class;
            list[GRASS] = Grass.class;
            list[DIRT] = Dirt.class;
            list[COBBLESTONE] = Cobblestone.class;
            list[PLANKS] = Planks.class;
            list[SAPLING] = Sapling.class;
            list[BEDROCK] = Bedrock.class;
            list[WATER] = Water.class;
            list[STILL_WATER] = StillWater.class;
            list[LAVA] = Lava.class;
            list[STILL_LAVA] = StillLava.class;
            list[SAND] = Sand.class;
            list[GRAVEL] = Gravel.class;
            list[GOLD_ORE] = GoldOre.class;
            list[IRON_ORE] = IronOre.class;
            list[COAL_ORE] = CoalOre.class;
            list[WOOD] = Wood.class;
            list[LEAVES] = Leaves.class;
            list[SPONGE] = Sponge.class;
            list[GLASS] = Glass.class;
            list[LAPIS_ORE] = LapisOre.class;
            list[LAPIS_BLOCK] = Lapis.class;
            list[SANDSTONE] = Sandstone.class;
            list[BED_BLOCK] = Bed.class;
            list[COBWEB] = Cobweb.class;
            list[TALL_GRASS] = TallGrass.class;
            list[DEAD_BUSH] = DeadBush.class;
            list[WOOL] = Wool.class;
            list[DANDELION] = Dandelion.class;

            list[FLOWER] = Flower.class;

            list[BROWN_MUSHROOM] = BrownMushroom.class;
            //list[RED_MUSHROOM] = RedMushroom.class;
            list[GOLD_BLOCK] = Gold.class;
            list[IRON_BLOCK] = Iron.class;
            list[DOUBLE_SLAB] = DoubleSlab.class;
            list[SLAB] = Slab.class;
            list[BRICKS_BLOCK] = Bricks.class;
            list[TNT] = TNT.class;
            list[BOOKSHELF] = Bookshelf.class;
            list[MOSS_STONE] = MossStone.class;
            list[OBSIDIAN] = Obsidian.class;
            list[TORCH] = Torch.class;
            list[FIRE] = Fire.class;
            //list[MONSTER_SPAWNER] = MonsterSpawner.class;
            list[WOOD_STAIRS] = WoodStairs.class;
            list[CHEST] = Chest.class;

            list[DIAMOND_ORE] = DiamondOre.class;
            list[DIAMOND_BLOCK] = Diamond.class;
            list[WORKBENCH] = Workbench.class;
            list[WHEAT_BLOCK] = Wheat.class;
            list[FARMLAND] = Farmland.class;
            list[FURNACE] = Furnace.class;
            list[BURNING_FURNACE] = BurningFurnace.class;
            list[SIGN_POST] = SignPost.class;
            //list[WOOD_DOOR_BLOCK] = WoodDoor.class;
            list[LADDER] = Ladder.class;

            list[COBBLESTONE_STAIRS] = CobblestoneStairs.class;
            //list[WALL_SIGN] = WallSign.class;

            //list[IRON_DOOR_BLOCK] = IronDoor.class;
            list[REDSTONE_ORE] = RedstoneOre.class;
            list[GLOWING_REDSTONE_ORE] = GlowingRedstoneOre.class;

            list[SNOW_LAYER] = SnowLayer.class;
            list[ICE] = Ice.class;
            list[SNOW_BLOCK] = SnowBlock.class;
            list[CACTUS] = Cactus.class;
            //list[CLAY_BLOCK] = Clay.class;
            //list[SUGARCANE_BLOCK] = Sugarcane.class;
            list[FENCE] = Fence.class;
            list[PUMPKIN] = Pumpkin.class;
            //list[NETHERRACK] = Netherrack.class;
            //list[SOUL_SAND] = SoulSand.class;
            list[GLOWSTONE_BLOCK] = Glowstone.class;

            list[LIT_PUMPKIN] = LitPumpkin.class;
            list[CAKE_BLOCK] = Cake.class;

            //list[TRAPDOOR] = Trapdoor.class;

            list[STONE_BRICKS] = StoneBricks.class;

            list[IRON_BARS] = IronBars.class;
            list[GLASS_PANE] = GlassPane.class;
            list[MELON_BLOCK] = Melon.class;
            /*list[PUMPKIN_STEM] = PumpkinStem.class;
            list[MELON_STEM] = MelonStem.class;
            list[VINE] = Vine.class;
            */
            list[FENCE_GATE] = FenceGate.class;

            list[BRICK_STAIRS] = BrickStairs.class;
            list[STONE_BRICK_STAIRS] = StoneBrickStairs.class;

            //list[MYCELIUM] = Mycelium.class;
            list[WATER_LILY] = WaterLily.class;
            list[NETHER_BRICKS] = NetherBrick.class;
            list[NETHER_BRICK_FENCE] = NetherBrickFence.class;
            list[NETHER_BRICKS_STAIRS] = NetherBrickStairs.class;

            //list[ENCHANTING_TABLE] = EnchantingTable.class;

            //list[END_PORTAL_FRAME] = EndPortalFrame.class;
            list[END_STONE] = EndStone.class;

            list[SANDSTONE_STAIRS] = SandstoneStairs.class;

            list[EMERALD_ORE] = EmeraldOre.class;
            list[EMERALD_BLOCK] = Emerald.class;

            list[SPRUCE_WOOD_STAIRS] = SpruceWoodStairs.class;
            list[BIRCH_WOOD_STAIRS] = BirchWoodStairs.class;
            list[JUNGLE_WOOD_STAIRS] = JungleWoodStairs.class;
            list[STONE_WALL] = StoneWall.class;

            //list[CARROT_BLOCK] = Carrot.class;
            //list[POTATO_BLOCK] = Potato.class;
            //list[ANVIL] = Anvil.class;

            list[REDSTONE_BLOCK] = Redstone.class;

            list[QUARTZ_BLOCK] = Quartz.class;
            list[QUARTZ_STAIRS] = QuartzStairs.class;
            list[DOUBLE_WOOD_SLAB] = DoubleWoodSlab.class;
            list[WOOD_SLAB] = WoodSlab.class;
            list[STAINED_CLAY] = StainedClay.class;

            list[LEAVES2] = Leaves2.class;
            list[WOOD2] = Wood2.class;
            list[ACACIA_WOOD_STAIRS] = AcaciaWoodStairs.class;
            list[DARK_OAK_WOOD_STAIRS] = DarkOakWoodStairs.class;

            list[HAY_BALE] = HayBale.class;
            list[CARPET] = Carpet.class;
            list[HARDENED_CLAY] = HardenedClay.class;
            list[COAL_BLOCK] = Coal.class;
            list[PACKED_ICE] = PackedIce.class;
            list[DOUBLE_PLANT] = DoublePlant.class;

            list[FENCE_GATE_SPRUCE] = FenceGateSpruce.class;
            list[FENCE_GATE_BIRCH] = FenceGateBirch.class;
            list[FENCE_GATE_JUNGLE] = FenceGateJungle.class;
            list[FENCE_GATE_DARK_OAK] = FenceGateDarkOak.class;
            list[FENCE_GATE_ACACIA] = FenceGateAcacia.class;

            list[GRASS_PATH] = GrassPath.class;

            list[PODZOL] = Podzol.class;
            list[BEETROOT_BLOCK] = Beetroot.class;
            list[STONECUTTER] = Stonecutter.class;
            list[GLOWING_OBSIDIAN] = GlowingObsidian.class;
            list[BREWING_STAND_BLOCK] = BrewingStand.class;

            for (int id = 0; id < 256; id++) {
                Class c = list[id];
                if (c != null) {
                    Block block;
                    try {
                        block = (Block) c.newInstance();
                        Constructor constructor = c.getDeclaredConstructor(int.class);
                        constructor.setAccessible(true);
                        for (int data = 0; data < 16; ++data) {
                            fullList[(id << 4) | data] = (Block) constructor.newInstance(data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Server.getInstance().getLogger().error("Error while registering " + c.getName());
                        return;
                    }

                    solid[id] = block.isSolid();
                    transparent[id] = block.isTransparent();
                    hardness[id] = block.getHardness();
                    light[id] = block.getLightLevel();

                    if (block.isSolid()) {
                        if (block.isTransparent()) {
                            if (block instanceof Liquid || block instanceof Ice) {
                                lightFilter[id] = 2;
                            } else {
                                lightFilter[id] = 1;
                            }
                        } else {
                            lightFilter[id] = 15;
                        }
                    } else {
                        lightFilter[id] = 1;
                    }
                } else {
                    lightFilter[id] = 1;
                    for (int data = 0; data < 16; ++data) {
                        fullList[(id) << 4 | data] = new UnknownBlock(id, data);
                    }
                }
            }
        }
    }

    public static Block get(int id) {
        return get(id, 0);
    }

    public static Block get(int id, Integer meta) {
        return get(id, meta, null);
    }

    @SuppressWarnings("unchecked")
    public static Block get(int id, Integer meta, Position pos) {
        Block block;
        try {
            Class c = list[id];
            if (c != null) {
                Constructor constructor = c.getDeclaredConstructor(int.class);
                constructor.setAccessible(true);
                block = (Block) constructor.newInstance(meta);
            } else {
                block = new UnknownBlock(id, meta);
            }
        } catch (Exception e) {
            block = new UnknownBlock(id, meta);
        }

        if (pos != null) {
            block.x = pos.x;
            block.y = pos.y;
            block.z = pos.z;
            block.level = pos.level;
        }
        return block;
    }

    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        return this.getLevel().setBlock(this, this, true, true);
    }

    public boolean isBreakable(Item item) {
        return true;
    }

    public boolean onBreak(Item item) {
        return this.getLevel().setBlock(this, new Air(), true, true);
    }

    public int onUpdate(int type) {
        return 0;
    }

    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    public boolean onActivate(Item item, Player player) {
        return false;
    }

    public double getHardness() {
        return 10;
    }

    public double getResistance() {
        return 1;
    }

    public int getToolType() {
        return Tool.TYPE_NONE;
    }

    public double getFrictionFactor() {
        return 0.6;
    }

    public int getLightLevel() {
        return 0;
    }

    public boolean canBePlaced() {
        return true;
    }

    public boolean canBeReplaced() {
        return false;
    }

    public boolean isTransparent() {
        return false;
    }

    public boolean isSolid() {
        return true;
    }

    public boolean canBeFlowedInto() {
        return false;
    }

    public boolean canBeActivated() {
        return false;
    }

    public boolean hasEntityCollision() {
        return false;
    }

    public boolean canPassThrough() {
        return false;
    }

    public abstract String getName();

    public abstract int getId();

    public void addVelocityToEntity(Entity entity, Vector3 vector) {

    }

    public final int getDamage() {
        return this.meta;
    }

    public final void setDamage(Integer meta) {
        this.meta = (meta == null ? 0 : meta & 0x0f);
    }

    final public void position(Position v) {
        this.x = (int) v.x;
        this.y = (int) v.y;
        this.z = (int) v.z;
        this.level = v.level;
        this.boundingBox = null;
    }

    public int[][] getDrops(Item item) {
        if (this.getId() < 0 || this.getId() > list.length) { //Unknown blocks
            return new int[0][0];
        } else {
            return new int[][]{
                    {this.getId(), this.getDamage(), 1}
            };
        }
    }

    public double getBreakTime(Item item) {
        double base = this.getHardness() * 1.5;
        if (this.canBeBrokenWith(item)) {
            if (this.getToolType() == Tool.TYPE_SHEARS && item.isShears()) {
                base /= 15;
            } else if (
                    (this.getToolType() == Tool.TYPE_PICKAXE && item.isPickaxe()) ||
                            (this.getToolType() == Tool.TYPE_AXE && item.isAxe()) ||
                            (this.getToolType() == Tool.TYPE_SHOVEL && item.isShovel())
                    ) {
                int tier = item.getTier();
                switch (tier) {
                    case Tool.TIER_WOODEN:
                        base /= 2;
                        break;
                    case Tool.TIER_STONE:
                        base /= 4;
                        break;
                    case Tool.TIER_IRON:
                        base /= 6;
                        break;
                    case Tool.TIER_DIAMOND:
                        base /= 8;
                        break;
                    case Tool.TIER_GOLD:
                        base /= 12;
                        break;
                }
            }
        } else {
            base *= 3.33;
        }

        if (item.isSword()) {
            base *= 0.5;
        }

        return base;
    }

    public boolean canBeBrokenWith(Item item) {
        return this.getHardness() != -1;
    }

    public Block getSide(int side) {
        return this.getSide(side, 1);
    }

    public Block getSide(int side, int step) {
        if (this.isValid()) {
            return this.getLevel().getBlock(super.getSide(side, step));
        }
        return Block.get(Item.AIR, 0, Position.fromObject(super.getSide(side, step)));
    }

    @Override
    public String toString() {
        return "Block[" + this.getName() + "] (" + this.getId() + ":" + this.getDamage() + ")";
    }

    public boolean collidesWithBB(AxisAlignedBB bb) {
        AxisAlignedBB bb1 = this.getBoundingBox();
        return bb1 != null && bb.intersectsWith(bb1);
    }

    public void onEntityCollide(Entity entity) {

    }

    public AxisAlignedBB getBoundingBox() {
        if (this.boundingBox == null) {
            this.boundingBox = this.recalculateBoundingBox();
        }
        return this.boundingBox;
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                this.x,
                this.y,
                this.z,
                this.x + 1,
                this.y + 1,
                this.z + 1
        );
    }

    public MovingObjectPosition calculateIntercept(Vector3 pos1, Vector3 pos2) {
        AxisAlignedBB bb = this.getBoundingBox();
        if (bb == null) {
            return null;
        }

        Vector3 v1 = pos1.getIntermediateWithXValue(pos2, bb.minX);
        Vector3 v2 = pos1.getIntermediateWithXValue(pos2, bb.maxX);
        Vector3 v3 = pos1.getIntermediateWithYValue(pos2, bb.minY);
        Vector3 v4 = pos1.getIntermediateWithYValue(pos2, bb.maxY);
        Vector3 v5 = pos1.getIntermediateWithZValue(pos2, bb.minZ);
        Vector3 v6 = pos1.getIntermediateWithZValue(pos2, bb.maxZ);

        if (v1 != null && !bb.isVectorInYZ(v1)) {
            v1 = null;
        }

        if (v2 != null && !bb.isVectorInYZ(v2)) {
            v2 = null;
        }

        if (v3 != null && !bb.isVectorInXZ(v3)) {
            v3 = null;
        }

        if (v4 != null && !bb.isVectorInXZ(v4)) {
            v4 = null;
        }

        if (v5 != null && !bb.isVectorInXY(v5)) {
            v5 = null;
        }

        if (v6 != null && !bb.isVectorInXY(v6)) {
            v6 = null;
        }

        Vector3 vector = v1;

        if (v2 != null && (vector == null || pos1.distanceSquared(v2) < pos1.distanceSquared(vector))) {
            vector = v2;
        }

        if (v3 != null && (vector == null || pos1.distanceSquared(v3) < pos1.distanceSquared(vector))) {
            vector = v3;
        }

        if (v4 != null && (vector == null || pos1.distanceSquared(v4) < pos1.distanceSquared(vector))) {
            vector = v4;
        }

        if (v5 != null && (vector == null || pos1.distanceSquared(v5) < pos1.distanceSquared(vector))) {
            vector = v5;
        }

        if (v6 != null && (vector == null || pos1.distanceSquared(v6) < pos1.distanceSquared(vector))) {
            vector = v6;
        }

        if (vector == null) {
            return null;
        }

        int f = -1;

        if (vector.equals(v1)) {
            f = 4;
        } else if (vector.equals(v2)) {
            f = 5;
        } else if (vector.equals(v3)) {
            f = 0;
        } else if (vector.equals(v4)) {
            f = 1;
        } else if (vector.equals(v5)) {
            f = 2;
        } else if (vector.equals(v6)) {
            f = 3;
        }

        return MovingObjectPosition.fromBlock((int) this.x, (int) this.y, (int) this.z, f, vector.add(this.x, this.y, this.z));

    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) throws Exception {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
        }
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) throws Exception {
        if (this.getLevel() != null) {
            return this.getLevel().getBlockMetadata().getMetadata(this, metadataKey);

        }
        return null;
    }

    @Override
    public boolean hasMetadata(String metadataKey) throws Exception {
        return this.getLevel() != null && this.getLevel().getBlockMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) throws Exception {
        if (this.getLevel() != null) {
            this.getLevel().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
        }
    }

    public Block clone() {
        return (Block) super.clone();
    }

}
