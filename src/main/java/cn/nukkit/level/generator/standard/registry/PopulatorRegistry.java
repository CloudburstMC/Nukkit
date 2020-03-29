package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.misc.NextGenerationPass;
import cn.nukkit.level.generator.standard.pop.OrePopulator;
import cn.nukkit.level.generator.standard.pop.PlantPopulator;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.level.generator.standard.pop.ShrubPopulator;
import cn.nukkit.level.generator.standard.pop.SubmergedOrePopulator;
import cn.nukkit.level.generator.standard.pop.TreePopulator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Registry for {@link Populator}.
 *
 * @author DaPorkchop_
 * @see StandardGeneratorRegistries#populator()
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class PopulatorRegistry extends AbstractGeneratorRegistry<Populator> {
    @Override
    protected void registerDefault() {
        this.register(OrePopulator.ID, OrePopulator.class);
        this.register(PlantPopulator.ID, PlantPopulator.class);
        this.register(ShrubPopulator.ID, ShrubPopulator.class);
        this.register(SubmergedOrePopulator.ID, SubmergedOrePopulator.class);
        this.register(TreePopulator.ID, TreePopulator.class);

        this.register(NextGenerationPass.ID, NextGenerationPass.class);
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
        private final PopulatorRegistry registry;

        public PopulatorRegistry getRegistry() {
            return this.registry;
        }
    }
}
