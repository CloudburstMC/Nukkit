package cn.nukkit.level.generator.standard.population.cluster;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

import static java.lang.Math.*;
import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * Generates ore veins.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class OrePopulator extends AbstractReplacingPopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:ore");

    @JsonProperty
    protected IntRange height;

    @JsonProperty
    protected BlockSelector block;

    @JsonProperty
    @JsonAlias({"maxSize"})
    protected int size;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.height, "height must be set!");
        Objects.requireNonNull(this.replace, "replace must be set!");
        Objects.requireNonNull(this.block, "block must be set!");
        Preconditions.checkArgument(this.size > 0, "size must be at least 1!");
    }

    @Override
    protected void populate0(PRandom random, ChunkManager level, int x, int z) {
        final int y = this.height.rand(random);

        final int block = this.block.selectRuntimeId(random);
        final int size = this.size;

        double seed = random.nextDouble() * Math.PI;
        double minAngleX = x + 8 - sin(seed) * size * 0.125d;
        double maxAngleX = x + 8 + sin(seed) * size * 0.125d;
        double minAngleY = y + random.nextInt(3) - 2;
        double maxAngleY = y + random.nextInt(3) - 2;
        double minAngleZ = z + 8 - cos(seed) * size * 0.125d;
        double maxAngleZ = z + 8 + cos(seed) * size * 0.125d;

        for (int i = 0; i < size; i++) {
            double scale = (double) i / (double) size;
            double angleX = lerp(minAngleX, maxAngleX, scale);
            double angleY = lerp(minAngleY, maxAngleY, scale);
            double angleZ = lerp(minAngleZ, maxAngleZ, scale);

            double rayLength = (sin(scale * Math.PI) * (random.nextDouble() * size * 0.0625d) + 1.0d) * 0.5d;

            int minX = floorI(angleX - rayLength);
            int minY = floorI(angleY - rayLength);
            int minZ = floorI(angleZ - rayLength);
            int maxX = floorI(angleX + rayLength);
            int maxY = floorI(angleY + rayLength);
            int maxZ = floorI(angleZ + rayLength);

            for (int dx = minX; dx <= maxX; dx++) {
                double sideX = (dx + 0.5d - angleX) / rayLength;
                if (sideX * sideX >= 1.0d) {
                    continue;
                }
                for (int dy = minY; dy <= maxY; dy++) {
                    if (dy < 0 || dy >= 256) {
                        continue;
                    }
                    double sideY = (dy + 0.5d - angleY) / rayLength;
                    if (sideX * sideX + sideY * sideY >= 1.0d) {
                        continue;
                    }
                    for (int dz = minZ; dz <= maxZ; dz++) {
                        double sideZ = (dz + 0.5d - angleZ) / rayLength;
                        if (sideX * sideX + sideY * sideY + sideZ * sideZ >= 1.0d) {
                            continue;
                        }
                        if (this.replace.test(level.getBlockRuntimeIdUnsafe(dx, dy, dz, 0))) {
                            level.setBlockRuntimeIdUnsafe(dx, dy, dz, 0, block);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
