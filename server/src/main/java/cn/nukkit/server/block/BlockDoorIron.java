package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemDoorIron;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.util.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockDoorIron extends BlockDoor {

    public BlockDoorIron() {
        this(0);
    }

    public BlockDoorIron(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Iron Door BlockType";
    }

    @Override
    public int getId() {
        return IRON_DOOR_BLOCK;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 25;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public Item toItem() {
        return new ItemDoorIron();
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
