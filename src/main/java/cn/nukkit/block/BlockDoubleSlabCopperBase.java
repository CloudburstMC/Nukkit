package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public abstract class BlockDoubleSlabCopperBase extends BlockDoubleSlabBase implements Waxable, Oxidizable {

    public BlockDoubleSlabCopperBase(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        return Waxable.super.onActivate(item, player)
                || Oxidizable.super.onActivate(item, player);
    }

    @Override
    public int onUpdate(int type) {
        return Oxidizable.super.onUpdate(type);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public Block getStateWithOxidizationLevel(OxidizationLevel oxidizationLevel) {
        return Block.get(this.getCopperId(this.isWaxed(), oxidizationLevel), this.getDamage());
    }

    @Override
    public boolean setOxidizationLevel(OxidizationLevel oxidizationLevel) {
        if (this.getOxidizationLevel().equals(oxidizationLevel)) {
            return true;
        }
        return this.level.setBlock(this, Block.get(this.getCopperId(this.isWaxed(), oxidizationLevel)));
    }

    @Override
    public boolean setWaxed(boolean waxed) {
        if (this.isWaxed() == waxed) {
            return true;
        }
        return this.level.setBlock(this, Block.get(getCopperId(waxed, getOxidizationLevel())));
    }

    @Override
    public boolean isWaxed() {
        return false;
    }

    protected abstract int getCopperId(boolean waxed, OxidizationLevel oxidizationLevel);
}
