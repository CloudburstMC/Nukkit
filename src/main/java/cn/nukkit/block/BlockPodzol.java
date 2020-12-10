package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import cn.nukkit.blockproperty.value.DirtType;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author xtypr
 * @since 2015/11/22
 */
public class BlockPodzol extends BlockDirt {

    public BlockPodzol() {
        this(0);
    }

    public BlockPodzol(int meta) {
        // Podzol can't have meta.
        super(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @Override
    public int getId() {
        return PODZOL;
    }

    @Override
    public String getName() {
        return "Podzol";
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Optional<DirtType> getDirtType() {
        return Optional.empty();
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setDirtType(@Nullable DirtType dirtType) {
        if (dirtType != null) {
            throw new InvalidBlockPropertyValueException(DIRT_TYPE, null, dirtType, getName()+" don't support DirtType");
        }
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (!this.up().canBeReplaced()) {
            return false;
        }
        
        if (item.isShovel()) {
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(BlockID.GRASS_PATH));
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS);
            }
            return true;
        }
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }
}
