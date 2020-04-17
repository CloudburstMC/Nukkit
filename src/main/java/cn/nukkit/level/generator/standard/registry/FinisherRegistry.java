package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.generation.decorator.Decorator;
import cn.nukkit.level.generator.standard.misc.NextGenerationPass;
import cn.nukkit.level.generator.standard.population.BlobPopulator;
import cn.nukkit.level.generator.standard.population.CocoaPopulator;
import cn.nukkit.level.generator.standard.population.DistanceSelectionPopulator;
import cn.nukkit.level.generator.standard.population.EndIslandPopulator;
import cn.nukkit.level.generator.standard.population.GlowstonePopulator;
import cn.nukkit.level.generator.standard.population.LakePopulator;
import cn.nukkit.level.generator.standard.population.NoiseSelectionPopulator;
import cn.nukkit.level.generator.standard.population.Populator;
import cn.nukkit.level.generator.standard.population.SpikesPopulator;
import cn.nukkit.level.generator.standard.population.SpringPopulator;
import cn.nukkit.level.generator.standard.population.SubmergedOrePopulator;
import cn.nukkit.level.generator.standard.population.VinesPopulator;
import cn.nukkit.level.generator.standard.population.cluster.OrePopulator;
import cn.nukkit.level.generator.standard.population.plant.DoublePlantPopulator;
import cn.nukkit.level.generator.standard.population.plant.PlantPopulator;
import cn.nukkit.level.generator.standard.population.plant.ShrubPopulator;
import cn.nukkit.level.generator.standard.population.tree.BushPopulator;
import cn.nukkit.level.generator.standard.population.tree.HugeTreePopulator;
import cn.nukkit.level.generator.standard.population.tree.TreePopulator;
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
public final class FinisherRegistry extends AbstractGeneratorRegistry<Populator> {
    @Override
    protected void registerDefault() {
        //register all decorators as populators as well, unless they have the @SkipRegistrationAsPopulator annotation
        StandardGeneratorRegistries.populator().idToValues.forEach((id, clazz) -> {
            if (clazz.getAnnotation(Populator.SkipRegistrationAsFinisher.class) == null || clazz.getAnnotation(Decorator.SkipRegistrationAsPopulator.class) == null)   {
                this.register(id, clazz);
            }
        });

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
        private final FinisherRegistry registry;

        public FinisherRegistry getRegistry() {
            return this.registry;
        }
    }
}
