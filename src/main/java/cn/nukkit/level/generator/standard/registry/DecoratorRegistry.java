package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.gen.decorator.BedrockDecorator;
import cn.nukkit.level.generator.standard.gen.decorator.Decorator;
import cn.nukkit.level.generator.standard.gen.decorator.DeepSurfaceDecorator;
import cn.nukkit.level.generator.standard.gen.decorator.GroundCoverDecorator;
import cn.nukkit.level.generator.standard.gen.decorator.NoiseSelectionDecorator;
import cn.nukkit.level.generator.standard.gen.decorator.ReplaceTopDecorator;
import cn.nukkit.level.generator.standard.gen.decorator.ScatteredCoverDecorator;
import cn.nukkit.level.generator.standard.gen.decorator.SurfaceDecorator;
import cn.nukkit.level.generator.standard.misc.NextGenerationPass;
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
        this.register(DeepSurfaceDecorator.ID, DeepSurfaceDecorator.class);
        this.register(GroundCoverDecorator.ID, GroundCoverDecorator.class);
        this.register(NoiseSelectionDecorator.ID, NoiseSelectionDecorator.class);
        this.register(ReplaceTopDecorator.ID, ReplaceTopDecorator.class);
        this.register(ScatteredCoverDecorator.ID, ScatteredCoverDecorator.class);
        this.register(SurfaceDecorator.ID, SurfaceDecorator.class);

        this.register(NextGenerationPass.ID, NextGenerationPass.class);
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
