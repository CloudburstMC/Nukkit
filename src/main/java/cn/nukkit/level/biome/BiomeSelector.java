package cn.nukkit.level.biome;

import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.math.NukkitRandom;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
//WIP
//do not touch lol
public class BiomeSelector {
    private final Simplex temperature;
    private final Simplex rainfall;
    private final Simplex river;
    private final Simplex ocean;

    private Biome[] map = new Biome[64 * 64];

    public BiomeSelector(NukkitRandom random) {
        this.temperature = new Simplex(random, 2F, 1F / 8F, 1F / 512F);
        this.rainfall = new Simplex(random, 2F, 1F / 8F, 1F / 512F);
        this.river = new Simplex(random, 6d, 2 / 4d, 1 / 256D);
        this.ocean = new Simplex(random, 6d, 2 / 4d, 1 / 512D);

        for (int i = 0; i < 64; ++i) {
            for (int j = 0; j < 64; ++j) {
                this.map[i + (j << 6)] = this.lookup(i / 63d, j / 63d);
            }
        }
    }

    public Biome lookup(double temperature, double rainfall) {
        return Biome.getBiome(Biome.EXTREME_HILLS);
    }

    public double getTemperature(double x, double z) {
        return (this.temperature.noise2D(x, z, true) + 1) / 2;
    }

    public double getRainfall(double x, double z) {
        return (this.rainfall.noise2D(x, z, true) + 1) / 2;
    }

    public Biome pickBiome(double x, double z) {
        double noiseOcean = ocean.noise2D(x, z, true);
        if (noiseOcean < -0.15)  {
            if (noiseOcean < -0.8) {
                return Biome.getBiome(Biome.MUSHROOM_ISLAND);
            } else {
                return Biome.getBiome(Biome.OCEAN);
            }
        }
        double noiseRiver = Math.abs(river.noise2D(x, z, true));
        if (noiseRiver < 0.04)  {
            return Biome.getBiome(Biome.RIVER);
        }

        int temperature = (int) (this.getTemperature(x, z) * 63);
        int rainfall = (int) (this.getRainfall(x, z) * 63);
        return this.map[temperature + (rainfall << 6)];
    }
}

/**
 * Testing how I can best implement a noise generator for rivers. Nothing to see here!
 *
 * @author DaPorkchop_
 */
final class DoNotTouch_RiverNoiseTests   {
    public static void main(String... args) {
        Simplex river = new Simplex(new NukkitRandom(System.currentTimeMillis()), 4d, 1 / 4d, 1 / 64D);
        Simplex ocean = new Simplex(new NukkitRandom(System.currentTimeMillis() + 1), 4d, 1 / 4d, 1 / 128D);

        BufferedImage test_min = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 512; x++)   {
            for (int z = 0; z < 512; z++)   {
                double noiseRiver = Math.abs(river.noise2D(x, z, true));
                double noiseOcean = ocean.noise2D(x, z, true);
                test_min.setRGB(x, z, noiseRiver < 0.05 || noiseOcean < -0.25 ? 0xFFFFFF : 0x0);
            }
        }

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(test_min)));
        frame.pack();
        frame.setVisible(true);
    }
}
