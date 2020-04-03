package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.generator.standard.gen.decorator.NoiseSelectionDecorator;
import cn.nukkit.level.generator.standard.pop.BlobPopulator;
import cn.nukkit.level.generator.standard.pop.CocoaPopulator;
import cn.nukkit.level.generator.standard.pop.DoublePlantPopulator;
import cn.nukkit.level.generator.standard.pop.NoiseSelectionPopulator;
import cn.nukkit.level.generator.standard.pop.OrePopulator;
import cn.nukkit.level.generator.standard.pop.PlantPopulator;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.level.generator.standard.pop.ShrubPopulator;
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
        this.register(BlobPopulator.ID, BlobPopulator.class);
        this.register(BushPopulator.ID, BushPopulator.class);
        this.register(CocoaPopulator.ID, CocoaPopulator.class);
        this.register(DoublePlantPopulator.ID, DoublePlantPopulator.class);
        this.register(HugeTreePopulator.ID, HugeTreePopulator.class);
        this.register(OrePopulator.ID, OrePopulator.class);
        this.register(PlantPopulator.ID, PlantPopulator.class);
        this.register(ShrubPopulator.ID, ShrubPopulator.class);
        this.register(SpikesPopulator.ID, SpikesPopulator.class);
        this.register(SubmergedOrePopulator.ID, SubmergedOrePopulator.class);
        this.register(TreePopulator.ID, TreePopulator.class);
        this.register(VinesPopulator.ID, VinesPopulator.class);

        StandardGeneratorRegistries.decorator().idToValues.forEach(this::register);
        //nukkitx:next is implicitly registered

        this.idToValues.remove(NoiseSelectionDecorator.ID);
        this.register(NoiseSelectionPopulator.ID, NoiseSelectionPopulator.class);
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
