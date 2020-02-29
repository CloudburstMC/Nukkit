package cn.nukkit.level.generator.standard.gen;

import cn.nukkit.Nukkit;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.gen.decorator.BedrockDecorator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;

import java.io.IOException;

/**
 * Allows individual modification of blocks in a chunk after surfaces have been built.
 * <p>
 * Similar to a populator, but only operates on a single chunk.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize(using = Decorator.Deserializer.class)
public interface Decorator {
    /**
     * Decorates a given chunk.
     *
     * @param chunk  the chunk to be decorated
     * @param random an instance of {@link PRandom} for generating random numbers, initialized with a seed based on chunk's position
     */
    void decorate(IChunk chunk, PRandom random, int x, int z);

    final class Deserializer extends JsonDeserializer<Decorator> {
        @Override
        public Decorator deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String nextName = p.nextFieldName();
            Preconditions.checkState("id".equals(nextName), "first field must be \"id\", not \"%s\"", nextName);
            Identifier id = Identifier.fromString(p.nextTextValue());
            p.nextToken();

            Decorator decorator = null;
            if (id == Identifier.fromString("nukkitx:bedrock")) {
                decorator = Nukkit.YAML_MAPPER.readValue(p, BedrockDecorator.class);
            }
            return decorator;
        }
    }
}
