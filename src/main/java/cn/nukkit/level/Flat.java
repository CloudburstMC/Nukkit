package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.object.OreType;
import cn.nukkit.level.generator.populator.Ore;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.Vector3;

import java.util.*;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Flat extends Generator {

    private ChunkManager level;

    private FullChunk chunk;

    private Random random;

    private List<Populator> populators = new ArrayList<>();

    private int[][] structure;

    private Map<String, String> options;

    private int floorLevel;

    private String preset;


    @Override
    public Map<String, String> getSettings() {
        return this.options;
    }

    @Override
    public String getName() {
        return "flat";
    }

    public Flat() {
        this(new HashMap<>());
    }

    public Flat(Map<String, String> options) {
        this.preset = "2;7,2x3,2;1;";
        this.options = options;
        this.chunk = null;

        if (this.options.containsKey("decoration")) {
            Ore ores = new Ore();
            ores.setOreTypes(new OreType[]{
                    new OreType(new CoalOre(), 20, 16, 0, 128),
                    new OreType(new IronOre(), 20, 8, 0, 64),
                    new OreType(new RedstoneOre(), 8, 7, 0, 16),
                    new OreType(new LapisOre(), 1, 6, 0, 32),
                    new OreType(new GoldOre(), 2, 8, 0, 32),
                    new OreType(new DiamondOre(), 1, 7, 0, 16),
                    new OreType(new Dirt(), 20, 32, 0, 128),
                    new OreType(new Gravel(), 20, 16, 0, 128),
            });
            this.populators.add(ores);
        }
    }

    protected void parsePreset(String preset, int chunkX, int chunkZ) {
        try {
            this.preset = preset;
            String[] presetArray = preset.split(";");
            int version = Integer.valueOf(presetArray[0]);
            String blocks = presetArray.length > 1 ? presetArray[1] : "";
            int biome = presetArray.length > 2 ? Integer.valueOf(presetArray[2]) : 1;
            String options = presetArray.length > 3 ? presetArray[1] : "";
            String[] blockArray = blocks.split(",");
            this.structure = new int[256][];
            int y = 0;
            for (String block : blockArray) {
                int id = 0, meta = 0, cnt = 1;
                if (Pattern.matches("^[0-9]{1,3}x[0-9]$", block)) {
                    //AxB
                    String[] s = block.split("x");
                    id = Integer.valueOf(s[0]);
                    cnt = Integer.valueOf(s[1]);
                } else if (Pattern.matches("^[0-9]{1,3}:[0-9]{0,2}$", block)) {
                    //A:B
                    String[] s = block.split(":");
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


        } catch (Exception e) {
            Server.getInstance().getLogger().error("error while parsing the preset: " + e.getMessage());
        }
    }


    @Override
    public void init(ChunkManager level, Random random) {

    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {

    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {

    }

    @Override
    public Vector3 getSpawn() {
        return null;
    }
}
