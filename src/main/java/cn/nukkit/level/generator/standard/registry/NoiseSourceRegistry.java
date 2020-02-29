package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.noise.NoiseSource;

/**
 * Registry for {@link NoiseSource}.
 *
 * @author DaPorkchop_
 * @see StandardGeneratorRegistries#noiseSource()
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class NoiseSourceRegistry extends AbstractGeneratorRegistry<NoiseSource> {
    @Override
    protected void registerDefault() {
        //porktodo:
        /*this.register(NoiseSources.ID_OPENSIMPLEX, NoiseSources.OPENSIMPLEX);
        this.register(NoiseSources.ID_PERLIN, NoiseSources.PERLIN);
        this.register(NoiseSources.ID_PORKIAN, NoiseSources.PORKIAN);
        this.register(NoiseSources.ID_SIMPLEX, NoiseSources.SIMPLEX);*/
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
