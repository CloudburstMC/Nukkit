package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.generation.noise.*;
import lombok.*;

/**
 * Registry for {@link NoiseGenerator}.
 *
 * @author DaPorkchop_
 * @see StandardGeneratorRegistries#noiseGenerator()
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class NoiseGeneratorRegistry extends AbstractGeneratorRegistry<NoiseGenerator> {
    @Override
    protected void registerDefault() {
        this.register(OpenSimplexDefaultNoiseGenerator.ID, OpenSimplexDefaultNoiseGenerator.class);
        this.register(PerlinDefaultNoiseGenerator.ID, PerlinDefaultNoiseGenerator.class);
        this.register(PorkianDefaultNoiseGenerator.ID, PorkianDefaultNoiseGenerator.class);
        this.register(SimplexDefaultNoiseGenerator.ID, SimplexDefaultNoiseGenerator.class);
    }

    @Override
    protected Event constructionEvent() {
        return new ConstructionEvent(this);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ConstructionEvent extends Event {
        @Getter
        private static final HandlerList handlers = new HandlerList();

        @NonNull
        private final NoiseGeneratorRegistry registry;

        public NoiseGeneratorRegistry getRegistry() {
            return this.registry;
        }
    }
}
