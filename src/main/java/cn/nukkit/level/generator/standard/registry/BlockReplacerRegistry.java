package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.gen.BlockReplacer;
import cn.nukkit.level.generator.standard.gen.replacer.GroundReplacer;
import cn.nukkit.level.generator.standard.gen.replacer.SeaReplacer;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.random.PRandom;

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
        this.register(Identifier.fromString("nukkitx:ground"), GroundReplacer::new);
        this.register(Identifier.fromString("nukkitx:sea"), SeaReplacer::new);
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
