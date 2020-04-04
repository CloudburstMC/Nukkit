package cn.nukkit.level.generator.standard.biome.map.complex;

import cn.nukkit.Nukkit;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.nukkitx.math.vector.Vector2i;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static net.daporkchop.lib.random.impl.FastPRandom.*;

/**
 * @author DaPorkchop_
 */
public class ComplexTest {
    public static final int SIZE = 512;
    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_COUNT = SIZE / CHUNK_SIZE;

    public static void main(String... args) throws IOException {
        BiomeFilter filter;
        try (InputStream in = StandardGeneratorUtils.read("biomemap", Identifier.fromString("nukkitx:overworld"))) {
            JsonNode tree = Nukkit.YAML_MAPPER.readTree(in);
            filter = Nukkit.YAML_MAPPER.treeToValue(tree.get("root"), BiomeFilter.class);
        }

        Int2ObjectMap<GenerationBiome> internalIdLookup = new Int2ObjectOpenHashMap<>();
        filter.getAllBiomes().stream()
                .distinct()
                .forEach(biome -> internalIdLookup.put(biome.getInternalId(), biome));

        System.out.printf("Total of %d biomes registered.\n", internalIdLookup.size());

        long seed = 0xDEADBEEF13370000L;
        filter.init(seed, new FastPRandom(seed));

        if (true)   {
            IntSet pending = new IntOpenHashSet(internalIdLookup.keySet());
            IntStream.range(0, 10000 * 4).parallel()
                    .forEach(i -> {
                        IntArrayAllocator alloc = IntArrayAllocator.DEFAULT.get();
                        int[] arr = filter.get(i * SIZE, 0, SIZE, SIZE, alloc);
                        synchronized (pending) {
                            for (int j = 0; j < SIZE * SIZE; j++) {
                                if (pending.remove(arr[i])) {
                                    System.out.printf("Found biome \"%s\"\n", internalIdLookup.get(arr[i]).getId());
                                }
                            }
                        }
                        alloc.release(arr);
                    });

            System.out.println("\nDidn't generate the following biomes:");
            pending.stream().mapToInt(Integer::intValue)
                    .mapToObj(internalIdLookup::get)
                    .map(GenerationBiome::getId)
                    .forEach(System.out::println);
        }

        BufferedImage img = new BufferedImage(SIZE/* * 3*/, SIZE, BufferedImage.TYPE_INT_ARGB);

        IntStream.range(0, 4).parallel()
                .mapToObj(i -> Vector2i.from(i & 1, i >>> 1))
                .forEach(v -> {
                    int dx = v.getX() * (SIZE >>> 1);
                    int dz = v.getY() * (SIZE >>> 1);
                    IntArrayAllocator alloc = IntArrayAllocator.DEFAULT.get();
                    int[] arr = filter.get(dx, dz, SIZE >>> 1, SIZE >>> 1, alloc);
                    for (int x = 0; x < (SIZE >>> 1); x++) {
                        for (int z = 0; z < (SIZE >>> 1); z++) {
                            GenerationBiome biome = internalIdLookup.get(arr[x * (SIZE >>> 1) + z]);
                            int color = biome == null ? 0 : mix32(biome.getId().toString().hashCode());
                            img.setRGB(dx + x, dz + z, color | 0xFF000000);
                        }
                    }
                    alloc.release(arr);
                });

        PorkUtil.simpleDisplayImage(true, img);
    }
}
