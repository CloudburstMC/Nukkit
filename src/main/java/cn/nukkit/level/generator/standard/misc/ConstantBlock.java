package cn.nukkit.level.generator.standard.misc;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.random.PRandom;

import java.util.regex.Matcher;

/**
 * Represents a constant block configuration option.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class ConstantBlock implements BlockFilter, BlockSelector {
    private static final Ref<Matcher> BLOCK_MATCHER_CACHE = ThreadRef.regex("^((?:[a-zA-Z0-9_]+:)?[a-zA-Z0-9_]+)(?:#([0-9]+))?$");

    private final Block block;
    private final int runtimeId;

    public ConstantBlock(int runtimeId) {
        this.block = BlockRegistry.get().getBlock(runtimeId);
        this.runtimeId = runtimeId;
    }

    @JsonCreator
    public ConstantBlock(
            @JsonProperty(value = "id", required = true) Identifier id,
            @JsonProperty(value = "meta") @JsonAlias({
                    "damage",
                    "metadata"
            }) int meta) {
        this.block = BlockRegistry.get().getBlock(id, meta);
        this.runtimeId = BlockRegistry.get().getRuntimeId(id, meta);
    }

    @JsonCreator
    public ConstantBlock(String value) {
        Matcher matcher = BLOCK_MATCHER_CACHE.get().reset(value);
        Preconditions.checkArgument(matcher.find(), "Cannot parse block: \"%s\"", value);

        Identifier id = Identifier.fromString(matcher.group(1));
        int meta = matcher.group(2) == null ? 0 : Integer.parseUnsignedInt(matcher.group(2));

        this.block = BlockRegistry.get().getBlock(id, meta);
        this.runtimeId = BlockRegistry.get().getRuntimeId(id, meta);
    }

    public Block block() {
        return this.block;
    }

    public int runtimeId() {
        return this.runtimeId;
    }

    //block filter methods
    @Override
    public boolean test(Block block) {
        return this.block == block || (this.block.getId() == block.getId() && this.block.getMeta() == block.getMeta());
    }

    @Override
    public boolean test(int runtimeId) {
        return this.runtimeId == runtimeId;
    }

    //block selector methods
    @Override
    public Block select(PRandom random) {
        return this.block;
    }

    @Override
    public int selectRuntimeId(PRandom random) {
        return this.runtimeId;
    }
}
