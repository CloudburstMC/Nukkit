package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.gen.BlockReplacer;
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

/**
 * Registry for {@link BlockReplacer}.
 *
 * @author DaPorkchop_
 * @see StandardGeneratorRegistries#blockReplacerRegistry()
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class BlockReplacerRegistry extends AbstractGeneratorRegistry<BlockReplacer.Factory> implements BlockReplacer.Factory {
    @Override
    public BlockReplacer create(@NonNull ConfigSection config) {
        String id = config.getString("id");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "id must be set!");
        BlockReplacer.Factory factory = this.get(Identifier.fromString(id));
        return factory.create(config);
    }

    @Override
    protected void registerDefault() {
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
