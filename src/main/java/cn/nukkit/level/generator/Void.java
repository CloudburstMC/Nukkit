package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Void extends Generator {

    private ChunkManager level;

    private NukkitRandom random;

    public Void() {
        this(new HashMap<>());
    }

    public Void(final Map<String, Object> options) {
    }

    @Override
    public int getId() {
        return Generator.TYPE_VOID;
    }

    @Override
    public void init(final ChunkManager level, final NukkitRandom random) {
        this.level = level;
        this.random = random;
    }

    @Override
    public void generateChunk(final int chunkX, final int chunkZ) {
        this.generateChunk(this.level.getChunk(chunkX, chunkZ));
    }

    @Override
    public void populateChunk(final int chunkX, final int chunkZ) {
    }

    @Override
    public Map<String, Object> getSettings() {
        return new HashMap<>();
    }

    @Override
    public String getName() {
        return "void";
    }

    @Override
    public Vector3 getSpawn() {
        return ((Level) level).getSafeSpawn();
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.level;
    }

    private void generateChunk(final FullChunk chunk) {
        chunk.setGenerated();
    }

}
