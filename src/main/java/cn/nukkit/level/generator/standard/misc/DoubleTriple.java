package cn.nukkit.level.generator.standard.misc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.common.cache.Cache;
import net.daporkchop.lib.common.cache.ThreadCache;

import java.util.regex.Matcher;

/**
 * A triple consisting of 3 {@code double}s.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class DoubleTriple {
    private static final Cache<Matcher> TRIPLE_MATCHER_CACHE = ThreadCache.regex("^(-?(?:[0-9]+|[0-9]*\\.[0-9]+)),(-?(?:[0-9]+|[0-9]*\\.[0-9]+)),(-?(?:[0-9]+|[0-9]*\\.[0-9]+))$");

    public static final DoubleTriple ONE = new DoubleTriple(1.0d, 1.0d, 1.0d);

    private final double x;
    private final double y;
    private final double z;

    @JsonCreator
    public DoubleTriple(
            @JsonProperty(value = "x", required = true) double x,
            @JsonProperty(value = "y", required = true) double y,
            @JsonProperty(value = "z", required = true) double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @JsonCreator
    public DoubleTriple(String value) {
        Matcher matcher = TRIPLE_MATCHER_CACHE.get().reset(value);
        Preconditions.checkArgument(matcher.find(), "Cannot parse triple: \"%s\"", value);

        this.x = Double.parseDouble(matcher.group(1));
        this.y = Double.parseDouble(matcher.group(2));
        this.z = Double.parseDouble(matcher.group(3));
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
}
