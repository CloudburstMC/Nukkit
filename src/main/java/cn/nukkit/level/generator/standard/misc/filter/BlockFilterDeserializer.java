package cn.nukkit.level.generator.standard.misc.filter;

import cn.nukkit.Nukkit;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
final class BlockFilterDeserializer extends JsonDeserializer<BlockFilter> {
    @Override
    public BlockFilter deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        AnyOfBlockFilter filter = Nukkit.YAML_MAPPER.readValue(p, AnyOfBlockFilter.class);
        if (filter.runtimeIds.length == 1) {
            return filter.runtimeIds[0] == 0 ? BlockFilter.AIR : new ConstantBlock(filter.runtimeIds[0]);
        }
        return filter;
    }
}
