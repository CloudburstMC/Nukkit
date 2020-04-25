package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.biome.map.complex.BiomeFilter;
import cn.nukkit.level.generator.standard.biome.map.complex.filter.*;
import lombok.*;

/**
 * Registry for {@link BiomeFilter}.
 *
 * @author DaPorkchop_
 * @see StandardGeneratorRegistries#biomeFilter()
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class BiomeFilterRegistry extends AbstractGeneratorRegistry<BiomeFilter> {
    @Override
    protected void registerDefault() {
        this.register(BleedIslandBiomeFilter.ID, BleedIslandBiomeFilter.class);
        this.register(ClimateBiomeFilter.ID, ClimateBiomeFilter.class);
        this.register(RandomBiomeFilter.ID, RandomBiomeFilter.class);
        this.register(ReplaceSwathBiomeFilter.ID, ReplaceSwathBiomeFilter.class);
        this.register(ReplaceThresholdBiomeFilter.ID, ReplaceThresholdBiomeFilter.class);
        this.register(RiverBiomeFilter.ID, RiverBiomeFilter.class);
        this.register(RiverCombineBiomeFilter.ID, RiverCombineBiomeFilter.class);
        this.register(ShoreBiomeFilter.ID, ShoreBiomeFilter.class);
        this.register(SmoothBiomeFilter.ID, SmoothBiomeFilter.class);
        this.register(SubstituteRandomBiomeFilter.ID, SubstituteRandomBiomeFilter.class);
        this.register(ZoomBiomeFilter.ID, ZoomBiomeFilter.class);
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
        private final BiomeFilterRegistry registry;

        public BiomeFilterRegistry getRegistry() {
            return this.registry;
        }
    }
}
