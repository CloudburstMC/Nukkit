package cn.nukkit.level.generator.standard;

import java.io.IOException;
import java.util.stream.IntStream;

import static cn.nukkit.level.generator.standard.StandardGenerator.*;

/**
 * @author DaPorkchop_
 */
public class DensityBenchmark {
    public static final int COUNT = 10000;

    public static void main(String... args) throws IOException {
        StandardGenerator generator = (StandardGenerator) StandardGenerator.FACTORY.create(1234L, "minecraft:overworld");
        ThreadLocal<double[]> arr = ThreadLocal.withInitial(() -> null);

        long start = System.currentTimeMillis();
        IntStream.range(0, COUNT).parallel().forEach(i -> {
            arr.set(generator.density.get(arr.get(), generator.biomes, i << 4, 0, 0, CACHE_X, CACHE_Y, CACHE_Z, STEP_X, STEP_Y, STEP_Z));
        });

        double time = (System.currentTimeMillis() - start) / 1000.0d;
        System.out.printf("Generated %d chunks in %.2fs (%.4f chunks/s)\n", COUNT, time, COUNT / time);
    }
}
