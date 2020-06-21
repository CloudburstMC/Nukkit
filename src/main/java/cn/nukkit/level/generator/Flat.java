package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
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
public class Flat extends Generator {

    private final List<Populator> populators = new ArrayList<>();

    private final Map<String, Object> options;

    private ChunkManager level;

    private NukkitRandom random;

    private int[][] structure;

    private int floorLevel;

    private String preset;

    private boolean init = false;

    private int biome;

    public Flat() {
        this(new HashMap<>());
    }

    public Flat(final Map<String, Object> options) {
        this.preset = "2;7,2x3,2;1;";
        this.options = options;

        if (this.options.containsKey("decoration")) {
            final PopulatorOre ores = new PopulatorOre();
            ores.setOreTypes(new OreType[]{
                new OreType(Block.get(BlockID.COAL_ORE), 20, 16, 0, 128),
                new OreType(Block.get(BlockID.IRON_ORE), 20, 8, 0, 64),
                new OreType(Block.get(BlockID.REDSTONE_ORE), 8, 7, 0, 16),
                new OreType(Block.get(BlockID.LAPIS_ORE), 1, 6, 0, 32),
                new OreType(Block.get(BlockID.GOLD_ORE), 2, 8, 0, 32),
                new OreType(Block.get(BlockID.DIAMOND_ORE), 1, 7, 0, 16),
                new OreType(Block.get(BlockID.DIRT), 20, 32, 0, 128),
                new OreType(Block.get(BlockID.GRAVEL), 20, 16, 0, 128),
            });
            this.populators.add(ores);
        }
    }

    @Override
    public int getId() {
        return Generator.TYPE_FLAT;
    }

    @Override
    public void init(final ChunkManager level, final NukkitRandom random) {
        this.level = level;
        this.random = random;
    }

    @Override
    public void generateChunk(final int chunkX, final int chunkZ) {
        if (!this.init) {
            this.init = true;
            if (this.options.containsKey("preset") && !"".equals(this.options.get("preset"))) {
                this.parsePreset((String) this.options.get("preset"), chunkX, chunkZ);
            } else {
                this.parsePreset(this.preset, chunkX, chunkZ);
            }
        }
        this.generateChunk(this.level.getChunk(chunkX, chunkZ));
    }

    @Override
    public void populateChunk(final int chunkX, final int chunkZ) {
        final BaseFullChunk chunk = this.level.getChunk(chunkX, chunkZ);
        this.random.setSeed(0xdeadbeef ^ chunkX << 8 ^ chunkZ ^ this.level.getSeed());
        for (final Populator populator : this.populators) {
            populator.populate(this.level, chunkX, chunkZ, this.random, chunk);
        }
    }

    @Override
    public Map<String, Object> getSettings() {
        return this.options;
    }

    @Override
    public String getName() {
        return "flat";
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(128, this.floorLevel, 128);
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.level;
    }

    protected void parsePreset(final String preset, final int chunkX, final int chunkZ) {
        try {
            this.preset = preset;
            final String[] presetArray = preset.split(";");
            final int version = Integer.valueOf(presetArray[0]);
            final String blocks = presetArray.length > 1 ? presetArray[1] : "";
            this.biome = presetArray.length > 2 ? Integer.valueOf(presetArray[2]) : 1;
            final String options = presetArray.length > 3 ? presetArray[1] : "";
            this.structure = new int[256][];
            int y = 0;
            for (final String block : blocks.split(",")) {
                final int id;
                int meta = 0;
                int cnt = 1;
                if (Pattern.matches("^[0-9]{1,3}x[0-9]$", block)) {
                    //AxB
                    final String[] s = block.split("x");
                    cnt = Integer.valueOf(s[0]);
                    id = Integer.valueOf(s[1]);
                } else if (Pattern.matches("^[0-9]{1,3}:[0-9]{0,2}$", block)) {
                    //A:B
                    final String[] s = block.split(":");
                    id = Integer.valueOf(s[0]);
                    meta = Integer.valueOf(s[1]);
                } else if (Pattern.matches("^[0-9]{1,3}$", block)) {
                    //A
                    id = Integer.valueOf(block);
                } else {
                    continue;
                }
                int cY = y;
                y += cnt;
                if (y > 0xFF) {
                    y = 0xFF;
                }
                for (; cY < y; ++cY) {
                    this.structure[cY] = new int[]{id, meta};
                }
            }
            this.floorLevel = y;
            for (; y <= 0xFF; ++y) {
                this.structure[y] = new int[]{0, 0};
            }
            for (final String option : options.split(",")) {
                if (Pattern.matches("^[0-9a-z_]+$", option)) {
                    this.options.put(option, true);
                } else if (Pattern.matches("^[0-9a-z_]+\\([0-9a-z_ =]+\\)$", option)) {
                    final String name = option.substring(0, option.indexOf("("));
                    final String extra = option.substring(option.indexOf("(") + 1, option.indexOf(")"));
                    final Map<String, Float> map = new HashMap<>();
                    for (final String kv : extra.split(" ")) {
                        final String[] data = kv.split("=");
                        map.put(data[0], Float.valueOf(data[1]));
                    }
                    this.options.put(name, map);
                }
            }
        } catch (final Exception e) {
            Server.getInstance().getLogger().error("error while parsing the preset", e);
            throw new RuntimeException(e);
        }
    }

    private void generateChunk(final FullChunk chunk) {
        chunk.setGenerated();

        for (int Z = 0; Z < 16; ++Z) {
            for (int X = 0; X < 16; ++X) {
                chunk.setBiomeId(X, Z, this.biome);

                for (int y = 0; y < 256; ++y) {
                    final int k = this.structure[y][0];
                    final int l = this.structure[y][1];
                    chunk.setBlock(X, y, Z, this.structure[y][0], this.structure[y][1]);
                }
            }
        }
    }

}
