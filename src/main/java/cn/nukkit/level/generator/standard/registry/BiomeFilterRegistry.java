package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.biome.map.filtered.BiomeFilter;
import cn.nukkit.level.generator.standard.biome.map.filtered.RandomBiomeFilter;
import cn.nukkit.level.generator.standard.biome.map.filtered.IslandBiomeFilter;
import cn.nukkit.level.generator.standard.biome.map.filtered.SubstituteRandomBiomeFilter;
import cn.nukkit.level.generator.standard.biome.map.filtered.ZoomBiomeFilter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
        this.register(IslandBiomeFilter.ID, IslandBiomeFilter.class);
        this.register(RandomBiomeFilter.ID, RandomBiomeFilter.class);
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
        private static HandlerList handlers = new HandlerList();

        @NonNull
        private final BiomeFilterRegistry registry;

        public BiomeFilterRegistry getRegistry() {
            return this.registry;
        }
    }
}
