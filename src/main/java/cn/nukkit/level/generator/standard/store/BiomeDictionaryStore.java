package cn.nukkit.level.generator.standard.store;

import cn.nukkit.Nukkit;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.biome.BiomeDictionary;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Identifier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Store for {@link BiomeDictionary}.
 *
 * @author DaPorkchop_
 * @see StandardGeneratorStores#biomeDictionary()
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class BiomeDictionaryStore extends AbstractGeneratorStore<BiomeDictionary> {
    @Override
    protected BiomeDictionary compute(@NonNull Identifier id) throws IOException {
        try (InputStream in = StandardGeneratorUtils.read("biome/dictionary", id))  {
            return Nukkit.YAML_MAPPER.readValue(in, BiomeDictionary.class).setId(id);
        }
    }
}
