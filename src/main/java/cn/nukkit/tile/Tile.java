package cn.nukkit.tile;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.CompoundTag;
import cn.nukkit.utils.ChunkException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Tile extends Position {
    public static final String SIGN = "Sign";
    public static final String CHEST = "Chest";
    public static final String FURNACE = "Furnace";

    public static int tileCount = 1;

    private static TreeMap<String, Class> knownTiles = new TreeMap<>();
    private static TreeMap<String, String> shortNames = new TreeMap<>();

    public FullChunk chunk;
    public String name;
    public int id;
    public int x;
    public int y;
    public int z;

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

    public static Tile createTile(String type, FullChunk chunk, CompoundTag nbt) {
        return createTile(type, chunk, nbt, null);
    }

    public static Tile createTile(String type, FullChunk chunk, CompoundTag nbt, Object[] args) {
        if (knownTiles.containsKey(type)) {
            try {
                Class c = knownTiles.get(type);
                Constructor constructor = c.getDeclaredConstructor(String.class, FullChunk.class, CompoundTag.class, Object[].class);
                constructor.setAccessible(true);
                return (Tile) constructor.newInstance(type, chunk, nbt, args);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean registerTile(Class c) {
        if (Tile.class.isAssignableFrom(c)) {
            knownTiles.put(c.getSimpleName(), c);
            shortNames.put(c.getName(), c.getSimpleName());
            return true;
        }
        return false;
    }

    public String getSaveId() {
        return shortNames.get(this.getClass().getName());
    }

    public int getId() {
        return id;
    }

    public void saveNBT() {
        this.namedTag.putString("id", this.getSaveId());
        this.namedTag.putInt("x", this.x);
        this.namedTag.putInt("y", this.y);
        this.namedTag.putInt("z", this.z);
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
