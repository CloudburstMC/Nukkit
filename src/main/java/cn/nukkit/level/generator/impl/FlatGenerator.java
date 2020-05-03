package cn.nukkit.level.generator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.random.PRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A basic generator for superflat worlds.
 *
 * @author DaPorkchop_
 */
public final class FlatGenerator implements Generator {
    public static final Identifier ID = Identifier.from("minecraft", "flat");

    private static final Pattern PRESET_PATTERN = Pattern.compile("^(?:([0-9]+)\\*)?((?:[a-z0-9_]+:)?[a-z0-9_]+)(?:#([0-9]+))?$", Pattern.CASE_INSENSITIVE);
    private static final String DEFAULT_PRESET = "bedrock,3*dirt,grass";

    private final Layer[] layers;

    public FlatGenerator(long seed, String options) {
        if (options == null || options.isEmpty()) {
            options = DEFAULT_PRESET;
        }

        List<Layer> layers = new ArrayList<>();
        Matcher matcher = PRESET_PATTERN.matcher("");
        String[] split = options.split(",");
        for (String s : split) {
            Preconditions.checkArgument(matcher.reset(s).find(), "Invalid layer: \"%s\"", s);

            String size = matcher.group(1);
            String id = matcher.group(2);
            String meta = matcher.group(3);
            layers.add(new Layer(
                    BlockRegistry.get().getRuntimeId(Identifier.fromString(id), meta == null ? 0 : Integer.parseInt(meta)),
                    size == null ? 1 : Integer.parseInt(size)));
        }
        Preconditions.checkArgument(!layers.isEmpty(), "No layers configured!");
        this.layers = layers.toArray(new Layer[layers.size()]);
    }

    @Override
    public void generate(PRandom random, IChunk chunk, int chunkX, int chunkZ) {
        int y = 0;

        for (Layer layer : this.layers) {
            for (int i = 0; i < layer.size; i++) {
                for (int x = 15; x >= 0; x--) {
                    for (int z = 15; z >= 0; z--) {
                        chunk.setBlockRuntimeIdUnsafe(x, y, z, layer.runtimeId);
                    }
                }
                y++;
            }
        }
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        //no-op
    }

    @Override
    public void finish(PRandom random, ChunkManager level, int chunkX, int chunkZ) {
        //no-op
    }

    @RequiredArgsConstructor
    private static final class Layer {
        private final int runtimeId;
        private final int size;
    }
}
