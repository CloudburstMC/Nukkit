package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLayer;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.persistence.PersistentDataContainer;
import cn.nukkit.level.persistence.impl.PersistentDataContainerBlockWrapper;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.LevelException;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.lang.reflect.Constructor;

/**
 * @author MagicDroidX
 */
public abstract class BlockEntity extends Position {

    //WARNING: DO NOT CHANGE ANY NAME HERE, OR THE CLIENT WILL CRASH
    public static final String BANNER = "Banner";
    public static final String BARREL = "Barrel";
    public static final String BEACON = "Beacon";
    public static final String BED = "Bed";
    public static final String BEEHIVE = "Beehive";
    public static final String BELL = "Bell";
    public static final String BLAST_FURNACE = "BlastFurnace";
    public static final String BREWING_STAND = "BrewingStand";
    public static final String BRUSHABLE_BLOCK = "BrushableBlock";
    public static final String CALIBRATED_SCULK_SENSOR = "CalibratedSculkSensor";
    public static final String CAMPFIRE = "Campfire";
    public static final String CAULDRON = "Cauldron";
    public static final String CHEST = "Chest";
    public static final String CHISELED_BOOKSHELF = "ChiseledBookshelf";
    public static final String COMMAND_BLOCK = "CommandBlock";
    public static final String COMPARATOR = "Comparator";
    public static final String CONDUIT = "Conduit";
    public static final String CRAFTER = "Crafter";
    public static final String CREAKING_HEART = "CreakingHeart";
    public static final String DAYLIGHT_DETECTOR = "DaylightDetector";
    public static final String DECORATED_POT = "DecoratedPot";
    public static final String DISPENSER = "Dispenser";
    public static final String DROPPER = "Dropper";
    public static final String ENCHANT_TABLE = "EnchantTable";
    public static final String ENDER_CHEST = "EnderChest";
    public static final String END_GATEWAY = "EndGateway";
    public static final String END_PORTAL = "EndPortal";
    public static final String FLOWER_POT = "FlowerPot";
    public static final String FURNACE = "Furnace";
    public static final String GLOW_ITEM_FRAME = "GlowItemFrame";
    public static final String HANGING_SIGN = "HangingSign";
    public static final String HOPPER = "Hopper";
    public static final String ITEM_FRAME = "ItemFrame";
    public static final String JIGSAW = "Jigsaw";
    public static final String JIGSAW_BLOCK = "JigsawBlock";
    public static final String JUKEBOX = "Jukebox";
    public static final String LECTERN = "Lectern";
    public static final String LODESTONE = "Lodestone";
    public static final String MOB_SPAWNER = "MobSpawner";
    public static final String MOVING_BLOCK = "MovingBlock";
    public static final String MUSIC = "Music";
    public static final String PISTON_ARM = "PistonArm";
    public static final String SCULK_CATALYST = "SculkCatalyst";
    public static final String SCULK_SENSOR = "SculkSensor";
    public static final String SCULK_SHRIEKER = "SculkShrieker";
    public static final String SHULKER_BOX = "ShulkerBox";
    public static final String SIGN = "Sign";
    public static final String SKULL = "Skull";
    public static final String SMOKER = "Smoker";
    public static final String SPORE_BLOSSOM = "SporeBlossom";
    public static final String STRUCTURE_BLOCK = "StructureBlock";
    public static final String TRIAL_SPAWNER = "TrialSpawner";
    public static final String VAULT = "Vault";

    // Not a vanilla block entity
    public static final String PERSISTENT_CONTAINER = "PersistentContainer";

    public static long count = 1;

    private static final BiMap<String, Class<? extends BlockEntity>> knownBlockEntities = HashBiMap.create(30);

    public FullChunk chunk;
    public String name;
    public long id;

    public boolean movable;

    public boolean closed = false;
    public CompoundTag namedTag;
    protected Server server;

    private PersistentDataContainer persistentContainer;

    public BlockEntity(FullChunk chunk, CompoundTag nbt) {
        if (chunk == null || chunk.getProvider() == null) {
            throw new ChunkException("Invalid garbage chunk given to BlockEntity");
        }

        this.server = chunk.getProvider().getLevel().getServer();
        this.chunk = chunk;
        this.setLevel(chunk.getProvider().getLevel());
        this.namedTag = nbt;
        this.name = "";
        this.id = BlockEntity.count++;
        this.x = this.namedTag.getInt("x");
        this.y = this.namedTag.getInt("y");
        this.z = this.namedTag.getInt("z");
        this.movable = this.namedTag.getBoolean("isMovable", true);

        this.initBlockEntity();

        this.chunk.addBlockEntity(this);
        this.getLevel().addBlockEntity(this);
    }

    protected void initBlockEntity() {

    }

    public static BlockEntity createBlockEntity(String type, FullChunk chunk, CompoundTag nbt, Object... args) {
        BlockEntity blockEntity = null;

        Class<? extends BlockEntity> clazz = knownBlockEntities.get(type);
        if (clazz != null) {

            if (clazz == null) {
                return null;
            }

            for (Constructor<?> constructor : clazz.getConstructors()) {
                if (blockEntity != null) {
                    break;
                }

                if (constructor.getParameterCount() != (args == null ? 2 : args.length + 2)) {
                    continue;
                }

                try {
                    if (args == null || args.length == 0) {
                        blockEntity = (BlockEntity) constructor.newInstance(chunk, nbt);
                    } else {
                        Object[] objects = new Object[args.length + 2];

                        objects[0] = chunk;
                        objects[1] = nbt;
                        System.arraycopy(args, 0, objects, 2, args.length);
                        blockEntity = (BlockEntity) constructor.newInstance(objects);

                    }
                } catch (Exception ignored) {}
            }
        } else {
            Server.getInstance().getLogger().warning("Tried to create block entity that doesn't exists: " + type);
        }

        return blockEntity;
    }

    public static boolean registerBlockEntity(String name, Class<? extends BlockEntity> c) {
        if (c == null) {
            return false;
        }

        knownBlockEntities.put(name, c);
        return true;
    }

    public final String getSaveId() {
        return knownBlockEntities.inverse().get(getClass());
    }

    public long getId() {
        return id;
    }

    public void saveNBT() {
        this.namedTag.putString("id", this.getSaveId());
        this.namedTag.putInt("x", (int) this.getX());
        this.namedTag.putInt("y", (int) this.getY());
        this.namedTag.putInt("z", (int) this.getZ());
        this.namedTag.putBoolean("isMovable", this.movable);
    }

    public CompoundTag getCleanedNBT() {
        this.saveNBT();
        CompoundTag tag = this.namedTag.clone();
        tag.remove("x").remove("y").remove("z").remove("id");
        if (!tag.isEmpty()) {
            return tag;
        } else {
            return null;
        }
    }

    public Block getBlock() {
        return this.getLevelBlock();
    }

    @Override
    public Block getLevelBlock() {
        if (this.isValid()) return this.level.getBlock(this.chunk, this.getFloorX(), this.getFloorY(), this.getFloorZ(), BlockLayer.NORMAL, true);
        else throw new LevelException("Undefined Level reference");
    }

    @Override
    public Block getLevelBlock(BlockLayer layer) {
        if (this.isValid()) return this.level.getBlock(this.chunk, this.getFloorX(), this.getFloorY(), this.getFloorZ(), layer, true);
        else throw new LevelException("Undefined Level reference");
    }

    public abstract boolean isBlockEntityValid();

    public boolean onUpdate() {
        return false;
    }

    public final void scheduleUpdate() {
        if (this.level.isBeingConverted) {
            return;
        }
        this.level.scheduleBlockEntityUpdate(this);
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
            if (this.chunk != null) {
                this.chunk.removeBlockEntity(this);
            }
            if (this.level != null) {
                this.level.removeBlockEntity(this);
            }
            this.level = null;
        }
    }

    public void onBreak() {

    }

    public void setDirty() {
        if (this.chunk != null) {
            this.chunk.setChanged();
        }
    }

    public String getName() {
        return name;
    }

    public boolean isMovable() {
        return movable;
    }

    public static CompoundTag getDefaultCompound(Vector3 pos, String id) {
        return new CompoundTag()
                .putString("id", id)
                .putInt("x", pos.getFloorX())
                .putInt("y", pos.getFloorY())
                .putInt("z", pos.getFloorZ());
    }

    public PersistentDataContainer getPersistentDataContainer() {
        if (this.persistentContainer == null) {
            this.persistentContainer = new PersistentDataContainerBlockWrapper(this);
        }
        return this.persistentContainer;
    }

    public boolean hasPersistentDataContainer() {
        return !this.getPersistentDataContainer().isEmpty();
    }

    public void onReplacedWith(BlockEntity blockEntity) {
        blockEntity.getPersistentDataContainer().setStorage(this.getPersistentDataContainer().getStorage().clone());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BlockEntity && this.getClass().equals(obj.getClass()) && super.equals(obj);
    }

    public boolean canSaveToStorage() {
        return true;
    }

    protected int getBlockIdIfLoaded(int bx, int by, int bz) {
        if (by < this.level.getMinBlockY() || by > this.level.getMaxBlockY()) return 0;
        int cx = bx >> 4;
        int cz = bz >> 4;
        FullChunk fullChunk = this.chunk;
        if (fullChunk == null || cx != fullChunk.getX() || cz != fullChunk.getZ()) {
            fullChunk = level.getChunkIfLoaded(cx, cz);
            if (fullChunk == null) {
                return -1;
            }
        }
        return fullChunk.getBlockId(bx & 0x0f, by, bz & 0x0f);
    }
}