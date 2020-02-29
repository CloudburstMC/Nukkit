package cn.nukkit.level.generator.standard.misc;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.common.cache.Cache;
import net.daporkchop.lib.common.cache.ThreadCache;
import net.daporkchop.lib.random.PRandom;

import java.util.regex.Matcher;

/**
 * Represents a vertical range, given in block coordinates.
 * <p>
 * Note that {@link #maxY} is always an exclusive bound.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class HeightRange {
    private static final Cache<Matcher> RANGE_MATCHER_CACHE = ThreadCache.regex("^([0-9]+)-([0-9]+)$");

    public static final HeightRange EMPTY_RANGE = new HeightRange(0, 0, true);

    public final int minY;
    public final int maxY;

    private HeightRange(int minY, int maxY, boolean overloadFlag) {
        this.minY = minY;
        this.maxY = maxY;
    }

    @JsonCreator
    public HeightRange(
            @JsonProperty(value = "minY", required = true) @JsonAlias({"min"}) int minY,
            @JsonProperty(value = "maxY", required = true) @JsonAlias({"max"}) int maxY) {
        this.minY = minY;
        this.maxY = maxY + 1; //add 1 to make maxY exclusive

        this.validate();
    }

    @JsonCreator
    public HeightRange(String value) {
        Matcher matcher = RANGE_MATCHER_CACHE.get().reset(value);
        Preconditions.checkArgument(matcher.find(), "Cannot parse range: \"%s\"", value);
        this.minY = Integer.parseUnsignedInt(matcher.group(1));
        this.maxY = Integer.parseUnsignedInt(matcher.group(2)) + 1;

        this.validate();
    }

    private void validate() {
        Preconditions.checkArgument(this.minY >= 0 && this.minY < 256, "minY (%d) must be in range 0-255!", this.minY);
        Preconditions.checkArgument(this.maxY >= 1 && this.maxY < 257, "maxY (%d) must be in range 0-255!", this.maxY - 1);
        Preconditions.checkArgument(this.minY < this.maxY, "minY (%d) may not be greater than maxY (%d)!", this.minY, this.maxY - 1);
    }

    /**
     * Gets a random value within this {@link HeightRange}.
     *
     * @param random an instance of {@link PRandom} to use for generating random numbers
     * @return a random value within this {@link HeightRange}
     */
    public int rand(@NonNull PRandom random) {
        return this.empty() ? this.minY : random.nextInt(this.minY, this.maxY);
    }

    /**
     * @return whether or not this {@link HeightRange} is empty
     */
    public boolean empty() {
        return this == EMPTY_RANGE;
    }

    /**
     * @return the size of this {@link HeightRange}
     */
    public int size() {
        return this.maxY - this.minY;
    }
}
