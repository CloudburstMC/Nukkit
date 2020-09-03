package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.value.DirtType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author MagicDroidX (Nukkit Project), kvetinac97
 */

public class BlockDirt extends BlockSolidMeta {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<DirtType> DIRT_TYPE = new ArrayBlockProperty<>("dirt_type", true, DirtType.class);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(DIRT_TYPE);

    public BlockDirt() {
        this(0);
    }

    public BlockDirt(int meta){
        super(meta);
    }

    @Override
    public int getId() {
        return DIRT;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Optional<DirtType> getDirtType() {
        return Optional.of(getPropertyValue(DIRT_TYPE));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setDirtType(@Nullable DirtType dirtType) {
        setPropertyValue(DIRT_TYPE, dirtType);
    }
    
    @Override
    public boolean canBeActivated() {
        return true;
    }
    
    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return this.getDamage() == 0 ? "Dirt" : "Coarse Dirt";
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (item.isHoe()) {
            item.useOn(this);
            this.getLevel().setBlock(this, this.getDamage() == 0 ? get(FARMLAND) : get(DIRT), true);
            if(player != null){
                player.getLevel().addSound(player, Sound.USE_GRASS);
            }
            return true;
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{new ItemBlock(Block.get(BlockID.DIRT))};
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
