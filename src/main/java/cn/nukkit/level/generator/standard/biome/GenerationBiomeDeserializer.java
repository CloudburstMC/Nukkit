package cn.nukkit.level.generator.standard.biome;

import cn.nukkit.Nukkit;
import cn.nukkit.level.generator.standard.store.StandardGeneratorStores;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
final class GenerationBiomeDeserializer extends JsonDeserializer<GenerationBiome> {
    @Override
    public GenerationBiome deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return StandardGeneratorStores.generationBiome().find(Nukkit.YAML_MAPPER.readValue(p, Identifier.class));
    }
}
