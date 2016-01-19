package cn.nukkit.tile;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Location;
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
public abstract class Tile extends Location {
    public static final String SIGN = "Sign";
    public static final String CHEST = "Chest";
    public static final String FURNACE = "Furnace";
    public static final String FLOWER_POT = "FlowerPot";
    public static final String MOB_SPAWNER = "MobSpawner";
    public static final String SKULL = "Skull";
    public static final String BREWING_STAND = "BrewingStand";
    public static final String ENCHANT_TABLE = "EnchantTable";

    public static long tileCount = 1;

    private static Map<String, Class<? extends Tile>> knownTiles = new HashMap<>();
    private static Map<String, String> shortNames = new HashMap<>();

    public FullChunk chunk;
    public String name;
    public long id;

    public boolean closed = false;
    public CompoundTag namedTag;
    protected long lastUpdate;
    protected Server server;

    public Tile(FullChunk chunk, CompoundTag nbt) {
        if (chunk == null || chunk.getProvider() == null) {
            throw new ChunkException("Invalid garbage Chunk given to Tile");
        }

        this.server = chunk.getProvider().getLevel().getServer();
        this.chunk = chunk;
        this.setLevel(chunk.getProvider().getLevel());
        this.namedTag = nbt;
        this.name = "";
        this.lastUpdate = System.currentTimeMillis();
        this.id = Tile.tileCount++;
        this.x = this.namedTag.getInt("x");
        this.y = this.namedTag.getInt("y");
        this.z = this.namedTag.getInt("z");

        this.chunk.addTile(this);
        this.getLevel().addTile(this);
    }

    public static Tile createTile(String type, FullChunk chunk, CompoundTag nbt, Object... args) {
        Tile tile = null;

        if (knownTiles.containsKey(type)) {
            Class<? extends Tile> clazz = knownTiles.get(type);

            if (clazz == null) {
                return null;
            }

            for (Constructor constructor : clazz.getConstructors()) {
                if (tile != null) {
                    break;
                }

                if (constructor.getParameterCount() != (args == null ? 2 : args.length + 2)) {
                    continue;
                }

                try {
                    if (args == null || args.length == 0) {
                        tile = (Tile) constructor.newInstance(chunk, nbt);
                    } else {
                        Object[] objects = new Object[args.length + 2];

                        objects[0] = chunk;
                        objects[1] = nbt;
                        System.arraycopy(args, 0, objects, 2, args.length);
                        tile = (Tile) constructor.newInstance(objects);

                    }
                } catch (Exception e) {
                    //ignore
                }

            }
        }

        return tile;
    }

    public static boolean registerTile(Class<? extends Tile> c) {
        if (c == null) {
            return false;
        }

        knownTiles.put(c.getSimpleName(), c);
        shortNames.put(c.getName(), c.getSimpleName());
        return true;
    }

    public String getSaveId() {
        return shortNames.get(this.getClass().getName());
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

    public boolean onUpdate() {
        return false;
    }

    public final void scheduleUpdate() {
        this.level.updateTiles.put(this.id, this);
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.level.updateTiles.remove(this.id);
            if (this.chunk != null) {
                this.chunk.removeTile(this);
            }
            if (this.level != null) {
                this.level.removeTile(this);
            }
            this.level = null;
        }
    }

    public String getName() {
        return name;
    }

}
