package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockCopperDoor extends BlockDoor implements Oxidizable, Waxable {

    public BlockCopperDoor() {
        this(0);
    }

    public BlockCopperDoor(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Copper Door";
    }

    @Override
    public int getId() {
        return COPPER_DOOR_BLOCK;
    }

    @Override
    public double getHardness() {
        return 3;
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
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId()), 0, 1);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
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
    public boolean canHarvestWithHand() {
        return false;
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
                return waxed ? WAXED_COPPER_DOOR_BLOCK : COPPER_DOOR_BLOCK;
            case EXPOSED:
                return waxed ? WAXED_EXPOSED_COPPER_DOOR_BLOCK : EXPOSED_COPPER_DOOR_BLOCK;
            case WEATHERED:
                return waxed ? WAXED_WEATHERED_COPPER_DOOR_BLOCK : WEATHERED_COPPER_DOOR_BLOCK;
            case OXIDIZED:
                return waxed ? WAXED_OXIDIZED_COPPER_DOOR_BLOCK : OXIDIZED_COPPER_DOOR_BLOCK;
            default:
                return this.getId();
        }
    }

    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
