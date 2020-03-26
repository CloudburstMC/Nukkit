package cn.nukkit.level.generator.standard.biome.map.complex;

import cn.nukkit.Nukkit;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import static net.daporkchop.lib.random.impl.FastPRandom.*;

/**
 * @author DaPorkchop_
 */
public class ComplexTest {
    public static final int SIZE = 400;
    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_COUNT = SIZE / CHUNK_SIZE;

    public static void main(String... args) throws IOException {
        BiomeFilter filter;
        try (InputStream in = StandardGeneratorUtils.read("test", Identifier.fromString("nukkitx:test"))) {
            filter = Nukkit.YAML_MAPPER.readValue(in, BiomeFilter.class);
        }

        long seed = 0xDEADBEEF13370000L;
        filter.init(seed, new FastPRandom(seed));

        BufferedImage img = new BufferedImage(SIZE * 3, SIZE, BufferedImage.TYPE_INT_ARGB);

        CompletableFuture<Void> oneShot = CompletableFuture.runAsync(() -> {
            IntArrayAllocator alloc = IntArrayAllocator.DEFAULT.get();
            int[] arr = filter.get(0, 0, SIZE, SIZE, alloc);
            for (int x = 0; x < SIZE; x++) {
                for (int z = 0; z < SIZE; z++) {
                    img.setRGB(x, z, mix32(arr[x * SIZE + z]) | 0xFF000000);
                }
            }
            alloc.release(arr);
        });

        CompletableFuture<Void> chunk = CompletableFuture.runAsync(() -> {
            IntArrayAllocator alloc = IntArrayAllocator.DEFAULT.get();
            for (int chunkX = 0; chunkX < CHUNK_COUNT; chunkX++) {
                for (int chunkZ = 0; chunkZ < CHUNK_COUNT; chunkZ++) {
                    int[] arr = filter.get(chunkX * CHUNK_SIZE, chunkZ * CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE, alloc);
                    for (int x = 0; x < CHUNK_SIZE; x++) {
                        for (int z = 0; z < CHUNK_SIZE; z++) {
                            img.setRGB(chunkX * CHUNK_SIZE + x + SIZE, chunkZ * CHUNK_SIZE + z, mix32(arr[x * CHUNK_SIZE + z]) | 0xFF000000);
                        }
                    }
                    alloc.release(arr);
                }
            }
        });

        CompletableFuture<Void> pixel = CompletableFuture.runAsync(() -> {
            IntArrayAllocator alloc = IntArrayAllocator.DEFAULT.get();
            for (int x = 0; x < SIZE; x++) {
                for (int z = 0; z < SIZE; z++) {
                    int[] arr = filter.get(x, z, 1, 1, alloc);
                    img.setRGB(x + (SIZE << 1), z, mix32(arr[0]) | 0xFF000000);
                    alloc.release(arr);
                }
            }
        });

        oneShot.runAfterBoth(chunk, () -> {}).runAfterBoth(pixel, () -> {}).join();
        PorkUtil.simpleDisplayImage(true, img);
    }
}
