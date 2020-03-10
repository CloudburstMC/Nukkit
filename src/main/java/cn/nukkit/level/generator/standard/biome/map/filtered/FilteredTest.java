package cn.nukkit.level.generator.standard.biome.map.filtered;

import cn.nukkit.Nukkit;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static net.daporkchop.lib.random.impl.FastPRandom.*;

/**
 * @author DaPorkchop_
 */
public class FilteredTest {
    public static void main(String... args) throws IOException {
        BiomeFilter filter;
        try (InputStream in = StandardGeneratorUtils.read("test", Identifier.fromString("nukkitx:test"))) {
            filter = Nukkit.YAML_MAPPER.readValue(in, BiomeFilter.class);
        }

        filter.init(1234L, new FastPRandom(1234L));

        int size = 512;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        int[] arr = filter.get(-27985 - (size >> 1), -51602 - (size >> 1), size, size, IntArrayAllocator.DEFAULT.get());

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                img.setRGB(x, z, mix32(arr[x * size + z]) | 0xFF000000);
            }
        }
        PorkUtil.simpleDisplayImage(true, img);
    }
}
