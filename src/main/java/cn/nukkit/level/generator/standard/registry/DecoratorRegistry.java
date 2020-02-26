package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.gen.Decorator;
import cn.nukkit.level.generator.standard.gen.decorator.BedrockDecorator;
import cn.nukkit.utils.Identifier;
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
        this.register(Identifier.fromString("nukkitx:bedrock"), BedrockDecorator::new);
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
