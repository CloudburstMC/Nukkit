package cn.nukkit.level.generator;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.level.provider.LegacyBlockConverter;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.Vector3f;
import cn.nukkit.registry.BlockRegistry;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static cn.nukkit.block.BlockIds.AIR;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class FlatGenerator implements Generator {

    public static final GeneratorFactory FACTORY = FlatGenerator::new;

    private final List<Populator> populators = new ArrayList<>();

    private Block[] structure;

    private final String options;

    private int floorLevel;

    private String preset;

    private int biome;

    @Override
    public String getSettings() {
        return this.options;
    }

    private FlatGenerator(BedrockRandom random, String options) {
        this.options = options;

        this.preset = options == null || options.isEmpty() ? "2;7,2x3,2;1;" : options;

        this.parsePreset(this.preset);
    }

    protected void parsePreset(String preset) {
        try {
            this.preset = preset;
            String[] presetArray = preset.split(";");
            int version = Integer.parseInt(presetArray[0]);
            String blocks = presetArray.length > 1 ? presetArray[1] : "";
            this.biome = presetArray.length > 2 ? Integer.parseInt(presetArray[2]) : 1;
            String options = presetArray.length > 3 ? presetArray[1] : "";
            this.structure = new Block[256];
            int y = 0;
            LegacyBlockConverter legacyBlockConverter = LegacyBlockConverter.get();
            for (String block : blocks.split(",")) {
                int id, meta = 0, cnt = 1;
                if (Pattern.matches("^[0-9]{1,3}x[0-9]$", block)) {
                    //AxB
                    String[] s = block.split("x");
                    cnt = Integer.parseInt(s[0]);
                    id = Integer.parseInt(s[1]);
                } else if (Pattern.matches("^[0-9]{1,3}:[0-9]{0,2}$", block)) {
                    //A:B
                    String[] s = block.split(":");
                    id = Integer.parseInt(s[0]);
                    meta = Integer.parseInt(s[1]);
                } else if (Pattern.matches("^[0-9]{1,3}$", block)) {
                    //A
                    id = Integer.parseInt(block);
                } else {
                    continue;
                }
                int cY = y;
                y += cnt;
                if (y > 0xFF) {
                    y = 0xFF;
                }
                int[] blockState = new int[] { id, meta };
                LegacyBlockConverter.get().convertBlockState(blockState);
                for (; cY < y; ++cY) {
                    this.structure[cY] = BlockRegistry.get().getBlock(blockState[0], blockState[1]);
                }
            }
            this.floorLevel = y;
            for (; y <= 0xFF; ++y) {
                this.structure[y] = Block.get(AIR);
            }
        } catch (Exception e) {
            log.error("error while parsing the preset", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void generateChunk(BedrockRandom random, IChunk chunk) {
        for (int Z = 0; Z < 16; ++Z) {
            for (int X = 0; X < 16; ++X) {
                chunk.setBiome(X, Z, biome);

                for (int y = 0; y < 256; ++y) {
                    chunk.setBlock(X, y, Z, this.structure[y]);
                }
            }
        }
    }

    @Override
    public void populateChunk(ChunkManager level, BedrockRandom random, int chunkX, int chunkZ) {
        IChunk chunk = level.getChunk(chunkX, chunkZ);
        for (Populator populator : this.populators) {
            populator.populate(level, chunkX, chunkZ, random, chunk);
        }
    }

    @Override
    public Vector3f getSpawn() {
        return new Vector3f(128, this.floorLevel, 128);
    }
}
