package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.gen.replacer.BlockReplacer;
import cn.nukkit.level.generator.standard.gen.replacer.GroundReplacer;
import cn.nukkit.level.generator.standard.gen.replacer.SeaReplacer;
import cn.nukkit.utils.Identifier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Registry for {@link BlockReplacer}.
 *
 * @author DaPorkchop_
 * @see StandardGeneratorRegistries#blockReplacer()
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class BlockReplacerRegistry extends AbstractGeneratorRegistry<BlockReplacer> {
    @Override
    protected void registerDefault() {
        this.register(Identifier.fromString("nukkitx:ground"), GroundReplacer.class);
        this.register(Identifier.fromString("nukkitx:sea"), SeaReplacer.class);
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
        private final BlockReplacerRegistry registry;

        public BlockReplacerRegistry getRegistry() {
            return this.registry;
        }
    }
}
