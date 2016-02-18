package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.ChunkException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockEntity extends Position {
    //WARNING: DO NOT CHANGE ANY NAME HERE, OR THE CLIENT WILL CRASH
    public static final String CHEST = "Chest";
    public static final String FURNACE = "Furnace";
    public static final String NETHER_REACTOR = "NetherReactor";
    public static final String SIGN = "Sign";
    public static final String MOB_SPAWNER = "MobSpawner";
    public static final String ENCHANT_TABLE = "EnchantTable";
    public static final String SKULL = "Skull";
    public static final String FLOWER_POT = "FlowerPot";
    public static final String BREWING_STAND = "BrewingStand";
    public static final String DAYLIGHT_DETECTOR = "DaylightDetector";
    public static final String MUSIC = "Music";


    public static long count = 1;

    private static Map<String, Class<? extends BlockEntity>> knownBlockEntities = new HashMap<>();
    private static Map<String, String> shortNames = new HashMap<>();

    public FullChunk chunk;
    public String name;
    public long id;

    public boolean closed = false;
    public CompoundTag namedTag;
    protected long lastUpdate;
    protected Server server;

    public BlockEntity(FullChunk chunk, CompoundTag nbt) {
        if (chunk == null || chunk.getProvider() == null) {
            throw new ChunkException("Invalid garbage Chunk given to Block Entity");
        }

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

        this.chunk.addBlockEntity(this);
        this.getLevel().addBlockEntity(this);
    }

    public static BlockEntity createBlockEntity(String type, FullChunk chunk, CompoundTag nbt, Object... args) {
        type = type.replaceFirst("BlockEntity", ""); //TODO: Remove this after the first release
        BlockEntity blockEntity = null;

        if (knownBlockEntities.containsKey(type)) {
            Class<? extends BlockEntity> clazz = knownBlockEntities.get(type);

            if (clazz == null) {
                return null;
            }

            for (Constructor constructor : clazz.getConstructors()) {
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
                } catch (Exception e) {
                    //ignore
                }

            }
        }

        return blockEntity;
    }

    public static boolean registerBlockEntity(String name, Class<? extends BlockEntity> c) {
        if (c == null) {
            return false;
        }

        knownBlockEntities.put(name, c);
        shortNames.put(c.getSimpleName(), name);
        return true;
    }

    public final String getSaveId() {
        return shortNames.getOrDefault(this.getClass().getSimpleName(), "");
    }

    public long getId() {
        return id;
    }

    public void saveNBT() {
        this.namedTag.putString("id", this.getSaveId());
        this.namedTag.putInt("x", (int) this.getX());
        this.namedTag.putInt("y", (int) this.getY());
        this.namedTag.putInt("z", (int) this.getZ());
    }

    public Block getBlock() {
        return this.level.getBlock(this);
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

}
