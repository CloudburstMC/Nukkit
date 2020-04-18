package cn.nukkit.level.generator.standard.biome;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Describes the elevation range of a biome.
 *
 * @author DaPorkchop_
 */
public abstract class BiomeElevation {
    public static final BiomeElevation DEFAULT = new Vanilla(0.1d, 0.2d);

    @JsonProperty
    @JsonAlias({
            "base",
            "baseHeight"
    })
    protected final double height;
    @JsonProperty
    @JsonAlias({
            "volatility",
            "heightVariation"
    })
    protected final double variation;

    protected final double normalizedHeight;
    protected final double normalizedVariation;

    public BiomeElevation(double height, double variation) {
        if (variation < 0.0d) {
            throw new IllegalArgumentException("variation may not be negative: " + variation);
        }

        this.height = height;
        this.variation = variation;

        this.normalizedHeight = this.normalizeHeight(height);
        this.normalizedVariation = this.normalizeVariation(variation);
    }

    /**
     * @return the raw height value
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * @return the raw variation value
     */
    public double getVariation() {
        return this.variation;
    }

    /**
     * @return the normalized height value
     */
    public double getNormalizedHeight() {
        return this.normalizedHeight;
    }

    /**
     * @return the normalized variation value
     */
    public double getNormalizedVariation() {
        return this.normalizedVariation;
    }

    protected abstract double normalizeHeight(double height);

    protected abstract double normalizeVariation(double variation);

    /**
     * Implementation of {@link BiomeElevation} which takes height and variation values given in blocks.
     *
     * @author DaPorkchop_
     */
    @JsonDeserialize
    public static class Absolute extends BiomeElevation {
        @JsonCreator
        public Absolute(@JsonProperty("height") double height, @JsonProperty("variation") double variation) {
            super(height, variation);
        }

        @Override
        protected double normalizeHeight(double height) {
            return height / 64.0d;
        }

        @Override
        protected double normalizeVariation(double variation) {
            return variation / 64.0d;
        }
    }

    /**
     * Implementation of {@link BiomeElevation} which takes height and variation values in the format used by vanilla Minecraft.
     *
     * @author DaPorkchop_
     */
    @JsonDeserialize
    public static class Vanilla extends BiomeElevation {
        @JsonCreator
        public Vanilla(@JsonProperty("height") double height, @JsonProperty("variation") double variation) {
            super(height, variation);
        }

        @Override
        protected double normalizeHeight(double height) {
            return height * 17.0d / 64.0d - 1.0d / 256.0d;
        }

        @Override
        protected double normalizeVariation(double variation) {
            return variation * 1.2d + 2.0d / 15.0d;
        }
    }
}
