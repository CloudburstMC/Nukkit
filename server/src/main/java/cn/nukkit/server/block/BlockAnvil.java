package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.inventory.AnvilInventory;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.util.BlockColor;

/**
 * Created by Pub4Game on 27.12.2015.
 */
public class BlockAnvil extends BlockFallable {

    public BlockAnvil() {
        this(0);
    }

    public BlockAnvil(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ANVIL;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 6000;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Anvil",
                "Anvil",
                "Anvil",
                "Anvil",
                "Slighty Damaged Anvil",
                "Slighty Damaged Anvil",
                "Slighty Damaged Anvil",
                "Slighty Damaged Anvil",
                "Very Damaged Anvil",
                "Very Damaged Anvil",
                "Very Damaged Anvil",
                "Very Damaged Anvil"
        };
        return names[this.meta];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!target.isTransparent()) {
            int damage = this.getDamage();
            int[] faces = {1, 2, 3, 0};
            this.meta = faces[player != null ? player.getDirection().getHorizontalIndex() : 0];
            if (damage >= 4 && damage <= 7) {
                this.meta |= 0x04;
            } else if (damage >= 8 && damage <= 11) {
                this.meta |= 0x08;
            }
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            player.addWindow(new AnvilInventory(this), Player.ANVIL_WINDOW_ID);
        }
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        int damage = this.getDamage();
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            Item drop = this.toItem();

            if (damage >= 4 && damage <= 7) { //Slightly Anvil
                drop.setDamage(drop.getDamage() & 0x04);
            } else if (damage >= 8 && damage <= 11) { //Very Damaged Anvil
                drop.setDamage(drop.getDamage() & 0x08);
            }
        }
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
