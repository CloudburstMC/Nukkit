package cn.nukkit.level.generator.standard.biome.map.filtered;

import cn.nukkit.Nukkit;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import net.daporkchop.lib.common.util.PorkUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author DaPorkchop_
 */
public class FilteredTest {
    public static void main(String... args) throws IOException {
        BiomeFilter filter;
        try (InputStream in = StandardGeneratorUtils.read("test", Identifier.fromString("nukkitx:test"))) {
            filter = Nukkit.YAML_MAPPER.readValue(in, BiomeFilter.class);
        }

        int size = 512;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        int[] arr = filter.get(0, 0, size, size, IntArrayAllocator.DEFAULT.get());

        for (int x = 0; x < size; x++)  {
            for (int z = 0; z < size; z++)  {
                int i = arr[x * size + z];
                img.setRGB(x, z, i == 0 ? 0xFF00FF00 : 0xFF0000FF);
            }
        }
        PorkUtil.simpleDisplayImage(true, img);
    }
}
