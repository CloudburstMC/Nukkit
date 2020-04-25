package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import cn.nukkit.level.generator.standard.biome.map.ConstantBiomeMap;
import cn.nukkit.level.generator.standard.biome.map.complex.ComplexBiomeMap;
import lombok.*;

/**
 * Registry for {@link BiomeMap}.
 *
 * @author DaPorkchop_
 * @see StandardGeneratorRegistries#biomeMap()
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class BiomeMapRegistry extends AbstractGeneratorRegistry<BiomeMap> {
    @Override
    protected void registerDefault() {
        this.register(ComplexBiomeMap.ID, ComplexBiomeMap.class);
        this.register(ConstantBiomeMap.ID, ConstantBiomeMap.class);
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
        private final BiomeMapRegistry registry;

        public BiomeMapRegistry getRegistry() {
            return this.registry;
        }
    }
}
