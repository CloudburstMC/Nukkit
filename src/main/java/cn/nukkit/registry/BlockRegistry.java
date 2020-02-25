package cn.nukkit.registry;

import cn.nukkit.block.*;
import cn.nukkit.item.ItemIds;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.nukkit.block.BlockIds.*;

@Log4j2
public class BlockRegistry implements Registry {
    public static final BlockFactory UNKNOWN_FACTORY = BlockUnknown::new;
    private static final BlockRegistry INSTANCE;
    private static final List<CompoundTag> VANILLA_PALETTE;

    static {
        InputStream stream = RegistryUtils.getOrAssertResource("runtime_block_states.dat");
        try {
            //noinspection unchecked
            VANILLA_PALETTE = ((ListTag<CompoundTag>) NBTIO.readTag(stream, ByteOrder.LITTLE_ENDIAN, false)).getAll();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        INSTANCE = new BlockRegistry();
    }

    private final Map<Identifier, BlockFactory> factoryMap = new IdentityHashMap<>();
    private final HashBiMap<Identifier, Integer> idLegacyMap = HashBiMap.create();
    private final Int2IntMap stateRuntimeMap = new Int2IntOpenHashMap();
    private final Int2ObjectMap<Block> runtimeStateMap = new Int2ObjectOpenHashMap<>();
    private final AtomicInteger runtimeIdAllocator = new AtomicInteger();
    private final AtomicInteger customIdAllocator = new AtomicInteger(1000);
    private byte[] cachedPalette;
    private byte[] cachedProperties;

    private volatile boolean closed;

    private BlockRegistry() {
        this.registerVanillaBlocks();
        try {
            this.registerVanillaPalette();
        } catch (RegistryException e) {
            throw new IllegalStateException("Unable to register vanilla block palette", e);
        }
    }

    public static BlockRegistry get() {
        return INSTANCE;
    }

    private static int getFullId(int id, int meta) {
        return (id << 8) | (meta & 0xff);
    }

    public synchronized void registerBlock(Identifier id, BlockFactory factory) throws RegistryException {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(factory, "factory");
        checkClosed();
        if (this.factoryMap.containsKey(id)) throw new RegistryException(id + " is already registered");

        // generate legacy ID (Not sure why we need to but it's a requirement)
        int legacyId = this.customIdAllocator.getAndIncrement();
        this.idLegacyMap.put(id, legacyId);
        this.factoryMap.put(id, factory);
        int runtimeId = this.registerBlockState(id, legacyId, 0);
        log.debug("Registered custom block {} => {}", id, runtimeId);
    }

    private synchronized int registerBlockState(Identifier id, int legacyId, int meta) {
        // generate runtime ID
        int runtimeId = this.runtimeIdAllocator.getAndIncrement();
        if (this.factoryMap.putIfAbsent(id, UNKNOWN_FACTORY) == null) {
            log.debug("Non-implemented block found {}", id);
        }
        Block block = this.factoryMap.get(id).create(id);
        block.setDamage(meta);
        this.runtimeStateMap.put(runtimeId, block);
        this.stateRuntimeMap.put(getFullId(legacyId, meta), runtimeId);
        return runtimeId;
    }

    private void registerVanillaPalette() throws RegistryException {
        checkClosed();
        for (CompoundTag entry : VANILLA_PALETTE) {
            String name = entry.getCompound("block").getString("name");
            Identifier id = Identifier.fromString(name);
            int legacyId = entry.getShort("id");
            this.idLegacyMap.putIfAbsent(id, legacyId);

            if (!entry.contains("meta")) {
                this.runtimeIdAllocator.getAndIncrement();
                continue;
            }
            int[] meta = entry.getIntArray("meta");

            int runtimeId = this.registerBlockState(id, legacyId, meta[0]);

            for (int i = 1; i < meta.length; i++) {
                this.stateRuntimeMap.put(getFullId(legacyId, meta[i]), runtimeId);
            }

            entry.remove("meta"); // No point in sending this since the client doesn't use it.
        }
    }

    boolean isBlock(Identifier id) {
        return this.idLegacyMap.containsKey(id);
    }

    public int getRuntimeId(Block block) {
        return getRuntimeId(block.getId(), block.getDamage());
    }

    public int getRuntimeId(Identifier identifier, int meta) {
        return getRuntimeId(getLegacyId(identifier), meta);
    }

    public int getRuntimeId(int id, int meta) {
        int fullId = getFullId(id, meta);
        int runtimeId = this.stateRuntimeMap.get(fullId);
        if (runtimeId == -1) {
            throw new RegistryException("No runtime ID for block " + id + ":" + meta);
        }
        return runtimeId;
    }

    public Block getBlock(Identifier identifier, int meta) {
        return getBlock(getRuntimeId(identifier, meta));
    }

    public Block getBlock(int id, int meta) {
        return getBlock(getRuntimeId(id, meta));
    }

    public Block getBlock(int runtimeId) {
        Block block = this.runtimeStateMap.get(runtimeId);
        if (block == null) {
            throw new RegistryException("No block for runtime ID " + runtimeId + " registered");
        }
        return block;
    }

    public int getLegacyId(String name) {
        return getLegacyId(Identifier.fromString(name));
    }

    public int getLegacyId(Identifier identifier) {
        int legacyId = this.idLegacyMap.getOrDefault(identifier, -1);
        if (legacyId == -1) {
            throw new RegistryException("No legacy ID found for " + identifier);
        }
        return legacyId;
    }

    public Identifier getNameFromLegacyId(int id) {
        Identifier identifier = idLegacyMap.inverse().get(id);
        if (identifier == null) {
            throw new RegistryException("No block found for ID " + id);
        }
        return identifier;
    }

    public List<Identifier> getCustomBlocks() {
        int start = VANILLA_PALETTE.size();
        int end = this.runtimeIdAllocator.get();
        if (start == (end - 1)) {
            return Collections.emptyList();
        }
        List<Identifier> ids = new ArrayList<>();
        for (int i = start; i < end; i++) {
            ids.add(runtimeStateMap.get(i).getId());
        }
        return ids;
    }

    public boolean hasMeta(Identifier id, int meta) {
        return this.stateRuntimeMap.containsKey(getFullId(getLegacyId(id), meta));
    }

    @Override
    public synchronized void close() throws RegistryException {
        checkClosed();
        this.closed = true;

        // generate cache

        List<CompoundTag> palette = new ArrayList<>(VANILLA_PALETTE); // Add all vanilla palette entries

        int startId = VANILLA_PALETTE.size();
        int size = this.runtimeIdAllocator.get();

        CompoundTag propertiesTag = new CompoundTag();

        // add custom blocks
        for (int i = startId; i < size; i++) {
            Block block = this.runtimeStateMap.get(i);

            //noinspection ConstantConditions
            CompoundTag tag = new CompoundTag()
                    .putShort("id", this.idLegacyMap.get(block.getId()))
                    .putCompound("block", new CompoundTag()
                            .putString("name", block.getId().toString())
                            .putCompound("states", new CompoundTag())); // custom blocks can't have states

            palette.add(tag);

            // this doesn't have to be sent
            propertiesTag.putCompound(block.getId().toString(), new CompoundTag()
                    .putCompound("minecraft:block_light_absorption", new CompoundTag()
                            .putInt("value", 1))
                    .putCompound("minecraft:block_light_emission", new CompoundTag()
                            .putFloat("emission", 0.0f))
                    .putCompound("minecraft:destroy_time", new CompoundTag()
                            .putFloat("value", 1)));
        }

        ListTag<CompoundTag> paletteTag = new ListTag<>();
        paletteTag.setAll(palette);

        try {
            this.cachedPalette = NBTIO.write(paletteTag, ByteOrder.LITTLE_ENDIAN, true);
            this.cachedProperties = NBTIO.write(propertiesTag, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new RegistryException("Unable to create cached block palette", e);
        }
    }

    private void checkClosed() throws RegistryException {
        if (this.closed) {
            throw new RegistryException("Registration has been closed");
        }
    }

    public byte[] getCachedPalette() {
        return cachedPalette;
    }

    public byte[] getCachedProperties() {
        return cachedProperties;
    }

    private void registerVanillaBlocks() {
        this.factoryMap.put(AIR, BlockAir::new); //0
        this.factoryMap.put(STONE, BlockStone::new); //1
        this.factoryMap.put(GRASS, BlockGrass::new); //2
        this.factoryMap.put(DIRT, BlockDirt::new); //3
        this.factoryMap.put(COBBLESTONE, BlockCobblestone::new); //4
        this.factoryMap.put(PLANKS, BlockPlanks::new); //5
        this.factoryMap.put(SAPLING, BlockSapling::new); //6
        this.factoryMap.put(BEDROCK, BlockBedrock::new); //7
        this.factoryMap.put(FLOWING_WATER, BlockWater.factory(WATER)); //8
        this.factoryMap.put(WATER, BlockWaterStill.factory(FLOWING_WATER)); //9
        this.factoryMap.put(FLOWING_LAVA, BlockLava.factory(LAVA)); //10
        this.factoryMap.put(LAVA, BlockLavaStill.factory(FLOWING_LAVA)); //11
        this.factoryMap.put(SAND, BlockSand::new); //12
        this.factoryMap.put(GRAVEL, BlockGravel::new); //13
        this.factoryMap.put(GOLD_ORE, BlockOreGold::new); //14
        this.factoryMap.put(IRON_ORE, BlockOreIron::new); //15
        this.factoryMap.put(COAL_ORE, BlockOreCoal::new); //16
        this.factoryMap.put(LOG, BlockLog::new); //17
        this.factoryMap.put(LEAVES, BlockLeaves::new); //18
        this.factoryMap.put(SPONGE, BlockSponge::new); //19
        this.factoryMap.put(GLASS, BlockGlass::new); //20
        this.factoryMap.put(LAPIS_ORE, BlockOreLapis::new); //21
        this.factoryMap.put(LAPIS_BLOCK, BlockLapis::new); //22
        this.factoryMap.put(DISPENSER, BlockDispenser::new); //23
        this.factoryMap.put(SANDSTONE, BlockSandstone::new); //24
        this.factoryMap.put(NOTEBLOCK, BlockNoteblock::new); //25
        this.factoryMap.put(BED, BlockBed::new); //26
        this.factoryMap.put(GOLDEN_RAIL, BlockRailPowered::new); //27
        this.factoryMap.put(DETECTOR_RAIL, BlockRailDetector::new); //28
        this.factoryMap.put(STICKY_PISTON, BlockPistonSticky::new); //29
        this.factoryMap.put(WEB, BlockCobweb::new); //30
        this.factoryMap.put(TALL_GRASS, BlockTallGrass::new); //31
        this.factoryMap.put(DEADBUSH, BlockDeadBush::new); //32
        this.factoryMap.put(PISTON, BlockPiston::new); //33
        this.factoryMap.put(PISTON_ARM_COLLISION, BlockPistonHead::new); //34
        this.factoryMap.put(WOOL, BlockWool::new); //35
        this.factoryMap.put(YELLOW_FLOWER, BlockDandelion::new); //37
        this.factoryMap.put(RED_FLOWER, BlockFlower::new); //38
        this.factoryMap.put(BROWN_MUSHROOM, BlockMushroomBrown::new); //39
        this.factoryMap.put(RED_MUSHROOM, BlockMushroomRed::new); //40
        this.factoryMap.put(GOLD_BLOCK, BlockGold::new); //41
        this.factoryMap.put(IRON_BLOCK, BlockIron::new); //42
        this.factoryMap.put(DOUBLE_STONE_SLAB, BlockDoubleSlabStone::new); //43
        this.factoryMap.put(STONE_SLAB, BlockSlabStone::new); //44
        this.factoryMap.put(BRICK_BLOCK, BlockBricks::new); //45
        this.factoryMap.put(TNT, BlockTNT::new); //46
        this.factoryMap.put(BOOKSHELF, BlockBookshelf::new); //47
        this.factoryMap.put(MOSSY_COBBLESTONE, BlockMossStone::new); //48
        this.factoryMap.put(OBSIDIAN, BlockObsidian::new); //49
        this.factoryMap.put(TORCH, BlockTorch::new); //50
        this.factoryMap.put(FIRE, BlockFire::new); //51
        this.factoryMap.put(MOB_SPAWNER, BlockMobSpawner::new); //52
        this.factoryMap.put(OAK_STAIRS, BlockStairsWood::new); //53
        this.factoryMap.put(CHEST, BlockChest::new); //54
        this.factoryMap.put(REDSTONE_WIRE, BlockRedstoneWire::new); //55
        this.factoryMap.put(DIAMOND_ORE, BlockOreDiamond::new); //56
        this.factoryMap.put(DIAMOND_BLOCK, BlockDiamond::new); //57
        this.factoryMap.put(CRAFTING_TABLE, BlockCraftingTable::new); //58
        this.factoryMap.put(WHEAT, BlockWheat::new); //59
        this.factoryMap.put(FARMLAND, BlockFarmland::new); //60
        this.factoryMap.put(FURNACE, BlockFurnace::new); //61
        this.factoryMap.put(LIT_FURNACE, BlockFurnaceBurning::new); //62
        this.factoryMap.put(STANDING_SIGN, BlockSignPost.factory(WALL_SIGN, ItemIds.SIGN)); //63
        this.factoryMap.put(WOODEN_DOOR, BlockDoorWood::new); //64
        this.factoryMap.put(LADDER, BlockLadder::new); //65
        this.factoryMap.put(RAIL, BlockRail::new); //66
        this.factoryMap.put(STONE_STAIRS, BlockStairsCobblestone::new); //67
        this.factoryMap.put(WALL_SIGN, BlockWallSign.factory(STANDING_SIGN, ItemIds.SIGN)); //68
        this.factoryMap.put(LEVER, BlockLever::new); //69
        this.factoryMap.put(STONE_PRESSURE_PLATE, BlockPressurePlateStone::new); //70
        this.factoryMap.put(IRON_DOOR, BlockDoorIron::new); //71
        this.factoryMap.put(WOODEN_PRESSURE_PLATE, BlockPressurePlateWood::new); //72
        this.factoryMap.put(REDSTONE_ORE, BlockOreRedstone::new); //73
        this.factoryMap.put(LIT_REDSTONE_ORE, BlockOreRedstoneGlowing::new); //74
        this.factoryMap.put(UNLIT_REDSTONE_TORCH, BlockRedstoneTorchUnlit::new);
        this.factoryMap.put(REDSTONE_TORCH, BlockRedstoneTorch::new); //76
        this.factoryMap.put(STONE_BUTTON, BlockButtonStone::new); //77
        this.factoryMap.put(SNOW_LAYER, BlockSnowLayer::new); //78
        this.factoryMap.put(ICE, BlockIce::new); //79
        this.factoryMap.put(SNOW, BlockSnow::new); //80
        this.factoryMap.put(CACTUS, BlockCactus::new); //81
        this.factoryMap.put(CLAY, BlockClay::new); //82
        this.factoryMap.put(REEDS, ReedsBlock::new); //83
        this.factoryMap.put(JUKEBOX, BlockJukebox::new); //84
        this.factoryMap.put(FENCE, BlockFenceWooden::new); //85
        this.factoryMap.put(PUMPKIN, BlockPumpkin::new); //86
        this.factoryMap.put(NETHERRACK, BlockNetherrack::new); //87
        this.factoryMap.put(SOUL_SAND, BlockSoulSand::new); //88
        this.factoryMap.put(GLOWSTONE, BlockGlowstone::new); //89
        this.factoryMap.put(PORTAL, BlockNetherPortal::new); //90
        this.factoryMap.put(LIT_PUMPKIN, BlockPumpkinLit::new); //91
        this.factoryMap.put(CAKE, BlockCake::new); //92
        this.factoryMap.put(UNPOWERED_REPEATER, BlockRedstoneRepeaterUnpowered::new); //93
        this.factoryMap.put(POWERED_REPEATER, BlockRedstoneRepeaterPowered::new); //94
        this.factoryMap.put(INVISIBLE_BEDROCK, BlockBedrockInvisible::new); //95
        this.factoryMap.put(TRAPDOOR, BlockTrapdoor::new); //96
        this.factoryMap.put(MONSTER_EGG, BlockMonsterEgg::new); //97
        this.factoryMap.put(STONEBRICK, BlockBricksStone::new); //98
        this.factoryMap.put(BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown::new); //99
        this.factoryMap.put(RED_MUSHROOM_BLOCK, BlockHugeMushroomRed::new); //100
        this.factoryMap.put(IRON_BARS, BlockIronBars::new); //101
        this.factoryMap.put(GLASS_PANE, BlockGlassPane::new); //102
        this.factoryMap.put(MELON_BLOCK, BlockMelon::new); //103
        this.factoryMap.put(PUMPKIN_STEM, BlockStemPumpkin::new); //104
        this.factoryMap.put(MELON_STEM, BlockStemMelon::new); //105
        this.factoryMap.put(VINE, BlockVine::new); //106
        this.factoryMap.put(FENCE_GATE, BlockFenceGate::new); //107
        this.factoryMap.put(BRICK_STAIRS, BlockStairsBrick::new); //108
        this.factoryMap.put(STONE_BRICK_STAIRS, BlockStairsStoneBrick::new); //109
        this.factoryMap.put(MYCELIUM, BlockMycelium::new); //110
        this.factoryMap.put(WATERLILY, BlockWaterLily::new); //111
        this.factoryMap.put(NETHER_BRICK, BlockBricksNether::new); //112
        this.factoryMap.put(NETHER_BRICK_FENCE, BlockFenceNetherBrick::new); //113
        this.factoryMap.put(NETHER_BRICK_STAIRS, BlockStairsNetherBrick::new); //114
        this.factoryMap.put(NETHER_WART, BlockNetherWart::new); //115
        this.factoryMap.put(ENCHANTING_TABLE, BlockEnchantingTable::new); //116
        this.factoryMap.put(BREWING_STAND, BlockBrewingStand::new); //117
        this.factoryMap.put(CAULDRON, BlockCauldron::new); //118
        this.factoryMap.put(END_PORTAL, BlockEndPortal::new); //119
        this.factoryMap.put(END_PORTAL_FRAME, BlockEndPortalFrame::new); //120
        this.factoryMap.put(END_STONE, BlockEndStone::new); //121
        this.factoryMap.put(DRAGON_EGG, BlockDragonEgg::new); //122
        this.factoryMap.put(REDSTONE_LAMP, BlockRedstoneLamp::new); //123
        this.factoryMap.put(LIT_REDSTONE_LAMP, BlockRedstoneLampLit::new); //124
        //TODO: list.put(DROPPER, BlockDropper::new); //125
        this.factoryMap.put(ACTIVATOR_RAIL, BlockRailActivator::new); //126
        this.factoryMap.put(COCOA, BlockCocoa::new); //127
        this.factoryMap.put(SANDSTONE_STAIRS, BlockStairsSandstone::new); //128
        this.factoryMap.put(EMERALD_ORE, BlockOreEmerald::new); //129
        this.factoryMap.put(ENDER_CHEST, BlockEnderChest::new); //130
        this.factoryMap.put(TRIPWIRE_HOOK, BlockTripWireHook::new); //131
        this.factoryMap.put(TRIPWIRE, BlockTripWire::new); //132
        this.factoryMap.put(EMERALD_BLOCK, BlockEmerald::new); //133
        this.factoryMap.put(SPRUCE_STAIRS, BlockStairsWood::new); //134
        this.factoryMap.put(BIRCH_STAIRS, BlockStairsWood::new); //135
        this.factoryMap.put(JUNGLE_STAIRS, BlockStairsWood::new); //136
        //137: impulse_command_block
        this.factoryMap.put(BEACON, BlockBeacon::new); //138
        this.factoryMap.put(COBBLESTONE_WALL, BlockWall::new); //139
        this.factoryMap.put(FLOWER_POT, BlockFlowerPot::new); //140
        this.factoryMap.put(CARROTS, BlockCarrot::new); //141
        this.factoryMap.put(POTATOES, BlockPotato::new); //142
        this.factoryMap.put(WOODEN_BUTTON, BlockButtonWooden::new); //143
        this.factoryMap.put(SKULL, BlockSkull::new); //144
        this.factoryMap.put(ANVIL, BlockAnvil::new); //145
        this.factoryMap.put(TRAPPED_CHEST, BlockTrappedChest::new); //146
        this.factoryMap.put(LIGHT_WEIGHTED_PRESSURE_PLATE, BlockWeightedPressurePlateLight::new); //147
        this.factoryMap.put(HEAVY_WEIGHTED_PRESSURE_PLATE, BlockWeightedPressurePlateHeavy::new); //148
        this.factoryMap.put(UNPOWERED_COMPARATOR, BlockRedstoneComparatorUnpowered::new); //149
        this.factoryMap.put(POWERED_COMPARATOR, BlockRedstoneComparatorPowered::new); //150
        this.factoryMap.put(DAYLIGHT_DETECTOR, BlockDaylightDetector::new); //151
        this.factoryMap.put(REDSTONE_BLOCK, BlockRedstone::new); //152
        this.factoryMap.put(QUARTZ_ORE, BlockOreQuartz::new); //153
        this.factoryMap.put(HOPPER, BlockHopper::new); //154
        this.factoryMap.put(QUARTZ_BLOCK, BlockQuartz::new); //155
        this.factoryMap.put(QUARTZ_STAIRS, BlockStairsQuartz::new); //156
        this.factoryMap.put(DOUBLE_WOODEN_SLAB, BlockDoubleSlabWood::new); //157
        this.factoryMap.put(WOODEN_SLAB, BlockSlabWood::new); //158
        this.factoryMap.put(STAINED_HARDENED_CLAY, BlockTerracottaStained::new); //159
        this.factoryMap.put(STAINED_GLASS_PANE, BlockGlassPaneStained::new); //160
        this.factoryMap.put(LEAVES2, BlockLeaves2::new); //161
        this.factoryMap.put(LOG2, BlockLog2::new); //162
        this.factoryMap.put(ACACIA_STAIRS, BlockStairsWood::new); //163
        this.factoryMap.put(DARK_OAK_STAIRS, BlockStairsWood::new); //164
        this.factoryMap.put(SLIME, BlockSlime::new); //165
        //166: glow_stick
        this.factoryMap.put(IRON_TRAPDOOR, BlockTrapdoorIron::new); //167
        this.factoryMap.put(PRISMARINE, BlockPrismarine::new); //168
        this.factoryMap.put(SEA_LANTERN, BlockSeaLantern::new); //169
        this.factoryMap.put(HAY_BLOCK, BlockHayBale::new); //170
        this.factoryMap.put(CARPET, BlockCarpet::new); //171
        this.factoryMap.put(HARDENED_CLAY, BlockTerracotta::new); //172
        this.factoryMap.put(COAL_BLOCK, BlockCoal::new); //173
        this.factoryMap.put(PACKED_ICE, BlockIcePacked::new); //174
        this.factoryMap.put(DOUBLE_PLANT, BlockDoublePlant::new); //175
        this.factoryMap.put(STANDING_BANNER, BlockBanner::new); //176
        this.factoryMap.put(WALL_BANNER, BlockWallBanner::new); //177
        this.factoryMap.put(DAYLIGHT_DETECTOR_INVERTED, BlockDaylightDetectorInverted::new); //178
        this.factoryMap.put(RED_SANDSTONE, BlockRedSandstone::new); //179
        this.factoryMap.put(RED_SANDSTONE_STAIRS, BlockStairsRedSandstone::new); //180
        this.factoryMap.put(DOUBLE_STONE_SLAB2, BlockDoubleSlabRedSandstone::new); //181
        this.factoryMap.put(STONE_SLAB2, BlockSlabRedSandstone::new); //182
        this.factoryMap.put(SPRUCE_FENCE_GATE, BlockFenceGate::new); //183
        this.factoryMap.put(BIRCH_FENCE_GATE, BlockFenceGate::new); //184
        this.factoryMap.put(JUNGLE_FENCE_GATE, BlockFenceGate::new); //185
        this.factoryMap.put(DARK_OAK_FENCE_GATE, BlockFenceGate::new); //186
        this.factoryMap.put(ACACIA_FENCE_GATE, BlockFenceGate::new); //187
        //188: repeating_command_block
        //189: chain_command_block
        //190: hard_glass_pane
        //191: hard_stained_glass_pane
        //192: chemical_heat
        this.factoryMap.put(SPRUCE_DOOR, BlockDoorWood::new); //193
        this.factoryMap.put(BIRCH_DOOR, BlockDoorWood::new); //194
        this.factoryMap.put(JUNGLE_DOOR, BlockDoorWood::new); //195
        this.factoryMap.put(ACACIA_DOOR, BlockDoorWood::new); //196
        this.factoryMap.put(DARK_OAK_DOOR, BlockDoorWood::new); //197
        this.factoryMap.put(GRASS_PATH, BlockGrassPath::new); //198
        this.factoryMap.put(FRAME, BlockItemFrame::new); //199
        this.factoryMap.put(CHORUS_FLOWER, BlockChorusFlower::new); //200
        this.factoryMap.put(PURPUR_BLOCK, BlockPurpur::new); //201
        //202: chorus_flower
        this.factoryMap.put(PURPUR_STAIRS, BlockStairsPurpur::new); //203
        //204: colored_torch_bp
        this.factoryMap.put(UNDYED_SHULKER_BOX, BlockUndyedShulkerBox::new); //205
        this.factoryMap.put(END_BRICKS, BlockBricksEndStone::new); //206
        //207: frosted_ice
        this.factoryMap.put(END_ROD, BlockEndRod::new); //208
        this.factoryMap.put(END_GATEWAY, BlockEndGateway::new); //209
        //210: allow
        //211: deny
        //212: border
        this.factoryMap.put(MAGMA, BlockMagma::new); //213
        this.factoryMap.put(NETHER_WART_BLOCK, BlockNetherWartBlock::new); //214
        this.factoryMap.put(RED_NETHER_BRICK, BlockBricksRedNether::new); //215
        this.factoryMap.put(BONE_BLOCK, BlockBone::new); //216
        //217: structure_void
        this.factoryMap.put(SHULKER_BOX, BlockShulkerBox::new); //218
        this.factoryMap.put(PURPLE_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //219
        this.factoryMap.put(WHITE_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //220
        this.factoryMap.put(ORANGE_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //221
        this.factoryMap.put(MAGENTA_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //222
        this.factoryMap.put(LIGHT_BLUE_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //223
        this.factoryMap.put(YELLOW_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //224
        this.factoryMap.put(LIME_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //225
        this.factoryMap.put(PINK_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //226
        this.factoryMap.put(GRAY_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //227
        this.factoryMap.put(SILVER_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //228
        this.factoryMap.put(CYAN_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //229
        //230: chalkboard
        this.factoryMap.put(BLUE_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //231
        this.factoryMap.put(BROWN_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //232
        this.factoryMap.put(GREEN_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //233
        this.factoryMap.put(RED_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //234
        this.factoryMap.put(BLACK_GLAZED_TERRACOTTA, BlockTerracottaGlazed::new); //235
        this.factoryMap.put(CONCRETE, BlockConcrete::new); //236
        this.factoryMap.put(CONCRETE_POWDER, BlockConcretePowder::new); //237
        //238: chemistry_table
        //239: underwater_torch
        this.factoryMap.put(CHORUS_PLANT, BlockChorusPlant::new); //240
        this.factoryMap.put(STAINED_GLASS, BlockGlassStained::new); //241
        //242: camera
        this.factoryMap.put(PODZOL, BlockPodzol::new); //243
        this.factoryMap.put(BEETROOT, BlockBeetroot::new); //244
        this.factoryMap.put(STONECUTTER, BlockStonecutter::new); //245
        this.factoryMap.put(GLOWING_OBSIDIAN, BlockObsidianGlowing::new); //246
        //list.put(NETHER_REACTOR, BlockNetherReactor::new); //247 Should not be removed
        //248: info_update
        //249: info_update2
        //TODO: list.put(PISTON_EXTENSION, BlockPistonExtension::new); //250
        this.factoryMap.put(OBSERVER, BlockObserver::new); //251
        //252: structure_block
        //253: hard_glass
        //254: hard_stained_glass
        //255: reserved6
        //256: unknown
        this.factoryMap.put(PRISMARINE_STAIRS, BlockStairsPrismarine::new); //257
        this.factoryMap.put(DARK_PRISMARINE_STAIRS, BlockStairsDarkPrismarine::new); //258
        this.factoryMap.put(PRISMARINE_BRICKS_STAIRS, BlockStairsPrismarineBricks::new); //259
        this.factoryMap.put(STRIPPED_SPRUCE_LOG, BlockStrippedLog::new); //260
        this.factoryMap.put(STRIPPED_BIRCH_LOG, BlockStrippedLog::new); //261
        this.factoryMap.put(STRIPPED_JUNGLE_LOG, BlockStrippedLog::new); //262
        this.factoryMap.put(STRIPPED_ACACIA_LOG, BlockStrippedLog::new); //263
        this.factoryMap.put(STRIPPED_DARK_OAK_LOG, BlockStrippedLog::new); //264
        this.factoryMap.put(STRIPPED_OAK_LOG, BlockStrippedLog::new); //265
        this.factoryMap.put(BLUE_ICE, BlockBlueIce::new); //266
        //267: element_1
        // ...
        //384: element_118
        //385: seagrass
        //386: coral
        //387: coral_block
        //388: coral_fan
        //389: coral_fan_dead
        //390: coral_fan_hang
        //391: coral_fan_hang2
        //392: coral_fan_hang3
        //393: kelp
        this.factoryMap.put(DRIED_KELP_BLOCK, BlockDriedKelp::new); //394
        this.factoryMap.put(ACACIA_BUTTON,BlockButtonWooden::new);//395
        this.factoryMap.put(BIRCH_BUTTON, BlockButtonWooden::new);//396
        this.factoryMap.put(DARK_OAK_BUTTON, BlockButtonWooden::new);//397
        this.factoryMap.put(JUNGLE_BUTTON, BlockButtonWooden::new);//398
        this.factoryMap.put(SPRUCE_BUTTON, BlockButtonWooden::new);//399
        this.factoryMap.put(ACACIA_TRAPDOOR, BlockTrapdoor.factory(BlockColor.ORANGE_BLOCK_COLOR)); //400
        this.factoryMap.put(BIRCH_TRAPDOOR, BlockTrapdoor.factory(BlockColor.SAND_BLOCK_COLOR)); //401
        this.factoryMap.put(DARK_OAK_TRAPDOOR, BlockTrapdoor.factory(BlockColor.BROWN_BLOCK_COLOR)); //402
        this.factoryMap.put(JUNGLE_TRAPDOOR, BlockTrapdoor.factory(BlockColor.DIRT_BLOCK_COLOR)); //403
        this.factoryMap.put(SPRUCE_TRAPDOOR, BlockTrapdoor.factory(BlockColor.SPRUCE_BLOCK_COLOR)); //404
        this.factoryMap.put(ACACIA_PRESSURE_PLATE, BlockPressurePlateWood::new);//405
        this.factoryMap.put(BIRCH_PRESSURE_PLATE, BlockPressurePlateWood::new);//406
        this.factoryMap.put(DARK_OAK_PRESSURE_PLATE, BlockPressurePlateWood::new);//407
        this.factoryMap.put(JUNGLE_PRESSURE_PLATE, BlockPressurePlateWood::new);//408
        this.factoryMap.put(SPRUCE_PRESSURE_PLATE, BlockPressurePlateWood::new);//409
        //410: carved_pumpkin
        //411: sea_pickle
        //412: conduit
        //413: turtle_egg
        //414: bubble_column
        this.factoryMap.put(BARRIER, BlockBarrier::new); //415
        //416: stone_slab3
        //417: bamboo
        //418: bamboo_sapling
        //419: scaffolding
        //420: stone_slab4
        //421: double_stone_slab3
        //422: double_stone_slab4
        this.factoryMap.put(GRANITE_STAIRS, BlockStairsGranite::new); //423
        this.factoryMap.put(DIORITE_STAIRS, BlockStairsDiorite::new); //424
        this.factoryMap.put(ANDESITE_STAIRS, BlockStairsAndesite::new); //425
        this.factoryMap.put(POLISHED_GRANITE_STAIRS, BlockStairsGranite::new); //426
        this.factoryMap.put(POLISHED_DIORITE_STAIRS, BlockStairsDiorite::new); //427
        this.factoryMap.put(POLISHED_ANDESITE_STAIRS, BlockStairsAndesite::new); //428
        this.factoryMap.put(MOSSY_STONE_BRICK_STAIRS, BlockStairsStoneBrick::new); //429
        this.factoryMap.put(SMOOTH_RED_SANDSTONE_STAIRS, BlockStairsSmoothRedSandstone::new); //430
        this.factoryMap.put(SMOOTH_SANDSTONE_STAIRS, BlockStairsSmoothSandstone::new); //431
        this.factoryMap.put(END_BRICK_STAIRS, BlockStairsEndStoneBrick::new); //432
        this.factoryMap.put(MOSSY_COBBLESTONE_STAIRS, BlockStairsCobblestone::new); //433
        this.factoryMap.put(NORMAL_STONE_STAIRS, BlockStairsStone::new); //434
        this.factoryMap.put(SPRUCE_STANDING_SIGN, BlockSignPost.factory(SPRUCE_WALL_SIGN, ItemIds.SPRUCE_SIGN)); //435
        this.factoryMap.put(SPRUCE_WALL_SIGN, BlockWallSign.factory(SPRUCE_STANDING_SIGN, ItemIds.SPRUCE_SIGN)); // 436
        this.factoryMap.put(SMOOTH_STONE, BlockSmoothStone::new); // 437
        this.factoryMap.put(RED_NETHER_BRICK_STAIRS, BlockStairsNetherBrick::new); //438
        this.factoryMap.put(SMOOTH_QUARTZ_STAIRS, BlockStairsQuartz::new); //439
        this.factoryMap.put(BIRCH_STANDING_SIGN, BlockSignPost.factory(BIRCH_WALL_SIGN, ItemIds.BIRCH_SIGN)); //440
        this.factoryMap.put(BIRCH_WALL_SIGN, BlockWallSign.factory(BIRCH_STANDING_SIGN, ItemIds.BIRCH_SIGN)); //441
        this.factoryMap.put(JUNGLE_STANDING_SIGN, BlockSignPost.factory(JUNGLE_WALL_SIGN, ItemIds.JUNGLE_SIGN)); //442
        this.factoryMap.put(JUNGLE_WALL_SIGN, BlockWallSign.factory(JUNGLE_STANDING_SIGN, ItemIds.JUNGLE_SIGN)); //443
        this.factoryMap.put(ACACIA_STANDING_SIGN, BlockSignPost.factory(ACACIA_WALL_SIGN, ItemIds.ACACIA_SIGN)); //444
        this.factoryMap.put(ACACIA_WALL_SIGN, BlockWallSign.factory(ACACIA_STANDING_SIGN, ItemIds.ACACIA_SIGN)); //445
        this.factoryMap.put(DARK_OAK_STANDING_SIGN, BlockSignPost.factory(DARK_OAK_WALL_SIGN, ItemIds.DARK_OAK_SIGN)); //446
        this.factoryMap.put(DARK_OAK_WALL_SIGN, BlockWallSign.factory(DARK_OAK_STANDING_SIGN, ItemIds.DARK_OAK_SIGN)); //447
        this.factoryMap.put(LECTERN, BlockLectern::new); //448
        //449: grindstone
        this.factoryMap.put(BLAST_FURNACE, BlockFurnace::new); // 450
        //451: stonecutter_block
        this.factoryMap.put(SMOKER, BlockFurnace::new); //452
        this.factoryMap.put(LIT_SMOKER, BlockFurnaceBurning::new); //453
        //454: cartography_table
        //455: fletching_table
        //456: smithing_table
        this.factoryMap.put(BARREL, BlockBarrel::new); // 457
        //458: loom
        //459: bell
        //460: sweet_berry_bush
        //461: lantern
        this.factoryMap.put(CAMPFIRE, BlockCampfire::new); //462
        //463: lava_cauldron
        //464: jigsaw
        this.factoryMap.put(WOOD, BlockWood::new); //465
        //466: composter
        this.factoryMap.put(LIT_BLAST_FURNACE, BlockFurnaceBurning::new); //467
        this.factoryMap.put(LIGHT_BLOCK, BlockLight::new); //468
        //469: wither_rose
        //470: stickypistonarmcollision
        //471: bee_nest
        //472: beehive
        //473: honey_block
        this.factoryMap.put(HONEYCOMB_BLOCK, BlockHoneycombBlock::new); //474
    }
}
