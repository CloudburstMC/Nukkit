package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.gen.BlockReplacer;
import cn.nukkit.level.generator.standard.gen.NoiseSourceFactory;
import cn.nukkit.level.generator.standard.gen.noise.NoiseEngines;
import cn.nukkit.level.generator.standard.gen.noise.VanillaNoiseSource;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;

/**
 * Registry for {@link BlockReplacer}.
 *
 * @author DaPorkchop_
 * @see StandardGeneratorRegistries#noiseSourceRegistry()
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class NoiseSourceRegistry extends AbstractGeneratorRegistry<NoiseSourceFactory> implements NoiseSourceFactory {
    @Override
    public NoiseSource apply(@NonNull ConfigSection config, @NonNull PRandom random) {
        String id = config.getString("id");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "id must be set!");
        NoiseSourceFactory factory = this.get(Identifier.fromString(id));
        return factory.apply(config, random);
    }

    @Override
    protected void registerDefault() {
        this.register(NoiseEngines.ID_OPENSIMPLEX, NoiseEngines.OPENSIMPLEX);
        this.register(NoiseEngines.ID_PERLIN, NoiseEngines.PERLIN);
        this.register(NoiseEngines.ID_PORKIAN, NoiseEngines.PORKIAN);
        this.register(NoiseEngines.ID_SIMPLEX, NoiseEngines.SIMPLEX);

        this.register(Identifier.fromString("nukkitx:vanilla"), VanillaNoiseSource::new);
    }

    @Override
    protected Event constructionEvent() {
        return new ConstructionEvent(this);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ConstructionEvent extends Event {
        @Getter
        private static HandlerList handlers = new HandlerList();

        @NonNull
        private final NoiseSourceRegistry registry;

        public NoiseSourceRegistry getRegistry() {
            return this.registry;
        }
    }
}
