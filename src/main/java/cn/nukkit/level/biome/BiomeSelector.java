package cn.nukkit.level.biome;

import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.math.NukkitRandom;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

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

    public BiomeSelector(NukkitRandom random) {
        this.temperature = new Simplex(random, 2F, 1F / 8F, 1F / 512F);
        this.rainfall = new Simplex(random, 2F, 1F / 8F, 1F / 512F);
        this.river = new Simplex(random, 6d, 2 / 4d, 1 / 256D);
        this.ocean = new Simplex(random, 6d, 2 / 4d, 1 / 512D);
    }

    public Biome pickBiome(int x, int z) {
        /*double noiseOcean = ocean.noise2D(x, z, true);
        double noiseTemp = temperature.noise2D(x, z, true);
        double noiseRain = rainfall.noise2D(x, z, true);
        if (noiseOcean < -0.15) {
            if (noiseOcean < -0.08) {
                return EnumBiome.MUSHROOM_ISLAND.biome;
            } else {
                return EnumBiome.OCEAN.biome;
            }
        }
        double noiseRiver = Math.abs(river.noise2D(x, z, true));
        if (noiseRiver < 0.04) {
            return EnumBiome.RIVER.biome;
        }
        return EnumBiome.OCEAN.biome;*/

        // > using actual biome selectors in 2018
        x >>= 6;
        z >>= 6;
        //return Biome.unorderedBiomes.get(Math.abs(((int) x >> 5) ^ 6457109 * ((int) z >> 5) ^ 9800471) % Biome.unorderedBiomes.size());
        boolean doPlateau = (((x * z) ^ 6457109) & 0x1) == 0;
        boolean doM = (((x * z) ^ 9800471) & 0x1) == 0;
        boolean doF = (((x * z) ^ 7003231) & 0x8) == 0;
        if (doPlateau)  {
            /*if (doM && doF)    {
                return EnumBiome.MESA_PLATEAU_F_M.biome;
            } else if (doM) {
                return EnumBiome.MESA_PLATEAU_M.biome;
            } else if (doF) {
                return EnumBiome.MESA_PLATEAU_F.biome;
            } else {
                return EnumBiome.MESA_PLATEAU.biome;
            }*/
            return doM ? EnumBiome.MESA_PLATEAU_M.biome : EnumBiome.MESA_PLATEAU.biome;
        } else {
            return doF ? EnumBiome.MESA_BRYCE.biome : EnumBiome.MESA.biome;
        }
    }
}

/**
 * Testing how I can best implement a noise generator for rivers. Nothing to see here!
 *
 * @author DaPorkchop_
 */
final class DoNotTouch_RiverNoiseTests {
    public static void main(String... args) {
        Simplex river = new Simplex(new NukkitRandom(System.currentTimeMillis()), 4d, 1 / 4d, 1 / 64D);
        Simplex ocean = new Simplex(new NukkitRandom(System.currentTimeMillis() + 1), 4d, 1 / 4d, 1 / 128D);

        BufferedImage test_min = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 512; x++) {
            for (int z = 0; z < 512; z++) {
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
