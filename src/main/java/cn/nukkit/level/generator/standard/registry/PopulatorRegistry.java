package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.gen.decorator.Decorator;
import cn.nukkit.level.generator.standard.gen.decorator.NoiseSelectionDecorator;
import cn.nukkit.level.generator.standard.misc.NextGenerationPass;
import cn.nukkit.level.generator.standard.pop.BlobPopulator;
import cn.nukkit.level.generator.standard.pop.CocoaPopulator;
import cn.nukkit.level.generator.standard.pop.DistanceSelectionPopulator;
import cn.nukkit.level.generator.standard.pop.EndIslandPopulator;
import cn.nukkit.level.generator.standard.pop.GlowstonePopulator;
import cn.nukkit.level.generator.standard.pop.SpringPopulator;
import cn.nukkit.level.generator.standard.pop.plant.DoublePlantPopulator;
import cn.nukkit.level.generator.standard.pop.NoiseSelectionPopulator;
import cn.nukkit.level.generator.standard.pop.cluster.OrePopulator;
import cn.nukkit.level.generator.standard.pop.plant.PlantPopulator;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.level.generator.standard.pop.plant.ShrubPopulator;
import cn.nukkit.level.generator.standard.pop.SpikesPopulator;
import cn.nukkit.level.generator.standard.pop.SubmergedOrePopulator;
import cn.nukkit.level.generator.standard.pop.VinesPopulator;
import cn.nukkit.level.generator.standard.pop.tree.BushPopulator;
import cn.nukkit.level.generator.standard.pop.tree.HugeTreePopulator;
import cn.nukkit.level.generator.standard.pop.tree.TreePopulator;
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
        //register all decorators as populators as well, unless they have the @SkipRegistrationAsPopulator annotation
        StandardGeneratorRegistries.decorator().idToValues.forEach((id, clazz) -> {
            if (clazz.getAnnotation(Decorator.SkipRegistrationAsPopulator.class) == null)   {
                this.register(id, clazz);
            }
        });

        this.register(BlobPopulator.ID, BlobPopulator.class);
        this.register(BushPopulator.ID, BushPopulator.class);
        this.register(CocoaPopulator.ID, CocoaPopulator.class);
        this.register(DistanceSelectionPopulator.ID, DistanceSelectionPopulator.class);
        this.register(DoublePlantPopulator.ID, DoublePlantPopulator.class);
        this.register(EndIslandPopulator.ID, EndIslandPopulator.class);
        this.register(GlowstonePopulator.ID, GlowstonePopulator.class);
        this.register(HugeTreePopulator.ID, HugeTreePopulator.class);
        this.register(NoiseSelectionPopulator.ID, NoiseSelectionPopulator.class);
        this.register(OrePopulator.ID, OrePopulator.class);
        this.register(PlantPopulator.ID, PlantPopulator.class);
        this.register(ShrubPopulator.ID, ShrubPopulator.class);
        this.register(SpikesPopulator.ID, SpikesPopulator.class);
        this.register(SpringPopulator.ID, SpringPopulator.class);
        this.register(SubmergedOrePopulator.ID, SubmergedOrePopulator.class);
        this.register(TreePopulator.ID, TreePopulator.class);
        this.register(VinesPopulator.ID, VinesPopulator.class);

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
