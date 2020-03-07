package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.gen.decorator.BedrockDecorator;
import cn.nukkit.level.generator.standard.gen.decorator.Decorator;
import cn.nukkit.level.generator.standard.gen.decorator.DoubleScatteredCoverDecorator;
import cn.nukkit.level.generator.standard.gen.decorator.ScatteredCoverDecorator;
import cn.nukkit.level.generator.standard.gen.decorator.SurfaceDecorator;
import cn.nukkit.level.generator.standard.misc.BiomeGenerationPass;
import cn.nukkit.registry.RegistryException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Registry for {@link Decorator}.
 *
 * @author DaPorkchop_
 * @see StandardGeneratorRegistries#decorator()
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class DecoratorRegistry extends AbstractGeneratorRegistry<Decorator> {
    @Override
    protected void registerDefault() {
        this.register(BedrockDecorator.ID, BedrockDecorator.class);
        this.register(DoubleScatteredCoverDecorator.ID, DoubleScatteredCoverDecorator.class);
        this.register(ScatteredCoverDecorator.ID, ScatteredCoverDecorator.class);
        this.register(SurfaceDecorator.ID, SurfaceDecorator.class);

        this.register(BiomeGenerationPass.ID, BiomeGenerationPass.class);
    }

    @Override
    public void close() throws RegistryException {
        super.close();
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
        private final DecoratorRegistry registry;

        public DecoratorRegistry getRegistry() {
            return this.registry;
        }
    }
}
