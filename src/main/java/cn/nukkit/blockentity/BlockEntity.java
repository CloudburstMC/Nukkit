package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.RegistryName;
import cn.nukkit.registry.function.BiObjectObjectFunction;
import cn.nukkit.registry.impl.BlockEntityRegistry;
import cn.nukkit.utils.ChunkException;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;

/**
 * @author MagicDroidX
 */
public abstract class BlockEntity extends Position {
    //WARNING: DO NOT CHANGE ANY NAME HERE, OR THE CLIENT WILL CRASH
    public static final String CHEST = "Chest";
    public static final String ENDER_CHEST = "EnderChest";
    public static final String FURNACE = "Furnace";
    public static final String SIGN = "Sign";
    public static final String MOB_SPAWNER = "MobSpawner";
    public static final String ENCHANT_TABLE = "EnchantTable";
    public static final String SKULL = "Skull";
    public static final String FLOWER_POT = "FlowerPot";
    public static final String BREWING_STAND = "BrewingStand";
    public static final String DAYLIGHT_DETECTOR = "DaylightDetector";
    public static final String MUSIC = "Music";
    public static final String ITEM_FRAME = "ItemFrame";
    public static final String CAULDRON = "Cauldron";
    public static final String BEACON = "Beacon";
    public static final String PISTON_ARM = "PistonArm";
    public static final String MOVING_BLOCK = "MovingBlock";
    public static final String COMPARATOR = "Comparator";
    public static final String HOPPER = "Hopper";
    public static final String BED = "Bed";
    public static final String JUKEBOX = "Jukebox";


    public static long count = 1;

    public FullChunk chunk;
    public String name;
    public long id;

    public boolean movable = true;

    public boolean closed = false;
    public CompoundTag namedTag;
    protected long lastUpdate;
    protected Server server;
    protected Timing timing;

    public BlockEntity(FullChunk chunk, CompoundTag nbt) {
        if (chunk == null || chunk.getProvider() == null) {
            throw new ChunkException("Invalid garbage Chunk given to Block Entity");
        }

        this.timing = Timings.getBlockEntityTiming(this);
        this.server = chunk.getProvider().getLevel().getServer();
        this.chunk = chunk;
        this.setLevel(chunk.getProvider().getLevel());
        this.namedTag = nbt;
        this.name = "";
        this.lastUpdate = System.currentTimeMillis();
        this.id = BlockEntity.count++;
        this.x = this.namedTag.getInt("x");
        this.y = this.namedTag.getInt("y");
        this.z = this.namedTag.getInt("z");
        this.movable = this.namedTag.getBoolean("isMovable");

        this.initBlockEntity();

        this.chunk.addBlockEntity(this);
        this.getLevel().addBlockEntity(this);
    }

    protected void initBlockEntity() {

    }

    public static BlockEntity createBlockEntity(String type, FullChunk chunk, CompoundTag nbt) {
        return createBlockEntity(new RegistryName("nukkit", type), chunk, nbt);
    }

    public static BlockEntity createBlockEntity(RegistryName name, FullChunk chunk, CompoundTag nbt)    {
        return BlockEntityRegistry.INSTANCE.getInstance(name, 0, chunk, nbt);
    }

    public static BlockEntity createBlockEntity(Class<? extends BlockEntity> clazz, FullChunk chunk, CompoundTag nbt) {
        return BlockEntityRegistry.INSTANCE.getInstance(clazz, 0, chunk, nbt);
    }

    public static boolean registerBlockEntity(RegistryName name, BiObjectObjectFunction<FullChunk, CompoundTag, BlockEntity> function, Class<? extends BlockEntity> clazz)    {
        return registerBlockEntity(name, function, clazz, false);
    }

    public static boolean registerBlockEntity(RegistryName name, BiObjectObjectFunction<FullChunk, CompoundTag, BlockEntity> function, Class<? extends BlockEntity> clazz, boolean force) {
        if (clazz == null) {
            return false;
        }

        return BlockEntityRegistry.INSTANCE.addEntry(name, function, clazz, force);
    }

    public final String getSaveId() {
        //TODO: this might break things
        return BlockEntityRegistry.INSTANCE.getName(this.getClass()).id;
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
        if (tag.getTags().size() > 0) {
            return tag;
        } else {
            return null;
        }
    }

    public Block getBlock() {
        return this.getLevelBlock();
    }

    public abstract boolean isBlockEntityValid();

    public boolean onUpdate() {
        return false;
    }

    public final void scheduleUpdate() {
        this.level.updateBlockEntities.put(this.id, this);
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.level.updateBlockEntities.remove(this.id);
            if (this.chunk != null) {
                this.chunk.removeBlockEntity(this);
            }
            if (this.level != null) {
                this.level.removeBlockEntity(this);
            }
            this.level = null;
        }
    }

    public String getName() {
        return name;
    }

    public boolean isMovable() {
        return movable;
    }

    public static CompoundTag getDefaultCompound(Vector3 pos, String id) {
        return new CompoundTag("")
                .putString("id", id)
                .putInt("x", pos.getFloorX())
                .putInt("y", pos.getFloorY())
                .putInt("z", pos.getFloorZ());
    }
}
