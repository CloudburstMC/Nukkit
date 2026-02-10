package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockCopperTrapdoor extends BlockTrapdoor implements Oxidizable, Waxable {

    public BlockCopperTrapdoor() {
        this(0);
    }

    public BlockCopperTrapdoor(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COPPER_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Copper Trapdoor";
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        return Waxable.super.onActivate(item, player)
                || Oxidizable.super.onActivate(item, player) || super.onActivate(item, player);
    }

    @Override
    public int onUpdate(int type) {
        return Oxidizable.super.onUpdate(type) == 0 ? super.onUpdate(type) : 0;
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

    protected int getCopperId(boolean waxed, OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return this.getId();
        }
        switch (oxidizationLevel) {
            case UNAFFECTED:
                return waxed ? WAXED_COPPER_TRAPDOOR : COPPER_TRAPDOOR;
            case EXPOSED:
                return waxed ? WAXED_EXPOSED_COPPER_TRAPDOOR : EXPOSED_COPPER_TRAPDOOR;
            case WEATHERED:
                return waxed ? WAXED_WEATHERED_COPPER_TRAPDOOR : WEATHERED_COPPER_TRAPDOOR;
            case OXIDIZED:
                return waxed ? WAXED_OXIDIZED_COPPER_TRAPDOOR : OXIDIZED_COPPER_TRAPDOOR;
            default:
                return this.getId();
        }
    }

    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
