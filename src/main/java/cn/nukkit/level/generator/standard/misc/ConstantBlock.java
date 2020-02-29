package cn.nukkit.level.generator.standard.misc;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.NoArgsConstructor;
import net.daporkchop.lib.common.cache.Cache;
import net.daporkchop.lib.common.cache.ThreadCache;

import java.util.regex.Matcher;

/**
 * Represents a constant block configuration option.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class ConstantBlock implements BlockFilter {
    private static final Cache<Matcher> BLOCK_MATCHER_CACHE = ThreadCache.regex("^((?:[a-zA-Z0-9_]+:)?[a-zA-Z0-9_]+)(?:#([0-9]+))?$");

    private final Block block;
    private final int   runtimeId;

    public ConstantBlock(int runtimeId) {
        this.block = BlockRegistry.get().getBlock(runtimeId);
        this.runtimeId = runtimeId;
    }

    @JsonCreator
    public ConstantBlock(
            @JsonProperty(value = "id", required = true) Identifier id,
            @JsonProperty(value = "meta") @JsonAlias({"damage", "metadata"}) int meta) {
        this.block = BlockRegistry.get().getBlock(id, meta);
        this.runtimeId = BlockRegistry.get().getRuntimeId(id, meta);
    }

    @JsonCreator
    public ConstantBlock(String value)  {
        Matcher matcher = BLOCK_MATCHER_CACHE.get().reset(value);
        Preconditions.checkArgument(matcher.find(), "Cannot parse block: \"%s\"", value);

        Identifier id = Identifier.fromString(matcher.group(1));
        int meta = matcher.group(2) == null ? 0 : Integer.parseUnsignedInt(matcher.group(2));

        this.block = BlockRegistry.get().getBlock(id, meta);
        this.runtimeId = BlockRegistry.get().getRuntimeId(id, meta);
    }

    @Override
    public boolean test(Block block) {
        return this.block == block || (this.block.getId() == block.getId() && this.block.getDamage() == block.getDamage());
    }

    @Override
    public boolean test(int runtimeId) {
        return this.runtimeId == runtimeId;
    }

    public Block block() {
        return this.block;
    }

    public int runtimeId() {
        return this.runtimeId;
    }
}
