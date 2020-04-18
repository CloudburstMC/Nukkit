package cn.nukkit.level.generator.standard.misc.layer;

import cn.nukkit.Nukkit;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
public final class BlockLayersDeserializer extends JsonDeserializer<BlockLayer[]> {
    @Override
    public BlockLayer[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return Nukkit.YAML_MAPPER.readValue(p, BlockLayers.class).stream()
                .map(TempBlockLayer::toActualLayer)
                .toArray(BlockLayer[]::new);
    }

    @AllArgsConstructor(onConstructor_ = {@JsonCreator})
    @JsonDeserialize
    private static final class BlockLayers {
        @NonNull
        private final TempBlockLayer[] layers;

        @JsonCreator
        public BlockLayers(String value) {
            this.layers = Arrays.stream(value.split(","))
                    .map(TempBlockLayer::new)
                    .toArray(TempBlockLayer[]::new);
        }

        public Stream<TempBlockLayer> stream() {
            return Arrays.stream(this.layers);
        }
    }

    @JsonDeserialize
    private static final class TempBlockLayer {
        private static final Ref<Matcher> LAYER_MATCHER_CACHE = ThreadRef.regex("^(?:([0-9]+(?:-[0-9]+)?)\\*)?((?:[a-zA-Z0-9_]+:)?[a-zA-Z0-9_]+)(?:#([0-9]+))?$");

        private final int blockId;
        private final int minSize;
        private final int maxSize;

        @JsonCreator
        public TempBlockLayer(
                @JsonProperty("block") ConstantBlock block,
                @JsonProperty("count") IntRange range) {
            this.blockId = block.runtimeId();
            this.minSize = range == null ? 1 : range.min;
            this.maxSize = range == null ? 2 : range.max;
        }

        @JsonCreator
        public TempBlockLayer(String value) {
            Matcher matcher = LAYER_MATCHER_CACHE.get().reset(value);
            Preconditions.checkArgument(matcher.find(), "Cannot parse layer: \"%s\"", value);

            Identifier id = Identifier.fromString(matcher.group(2));
            int meta = matcher.group(3) == null ? 0 : Integer.parseUnsignedInt(matcher.group(3));
            this.blockId = BlockRegistry.get().getRuntimeId(id, meta);

            if (matcher.group(1) == null) {
                this.minSize = 1;
                this.maxSize = 2;
            } else {
                IntRange range = new IntRange(matcher.group(1));
                this.minSize = range.min;
                this.maxSize = range.max;
            }
        }

        public BlockLayer toActualLayer() {
            return this.minSize == this.maxSize - 1
                   ? new ConstantSizeBlockLayer(this.blockId, this.minSize)
                   : new VariableSizeBlockLayer(this.blockId, this.minSize, this.maxSize);
        }
    }
}
