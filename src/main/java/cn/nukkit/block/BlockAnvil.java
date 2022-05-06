package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created by Pub4Game on 27.12.2015.
 */
public class BlockAnvil extends BlockFallable implements Faceable {

    private static final String[] NAMES = new String[]{
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

    private int meta;

    public BlockAnvil() {
        this(0);
    }

    public BlockAnvil(int meta) {
        this.meta = meta;
    }

    @Override
    public int getFullId() {
        return (getId() << 4) + getDamage();
    }

    @Override
    public final int getDamage() {
        return this.meta;
    }

    @Override
    public final void setDamage(int meta) {
        this.meta = meta;
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
        return NAMES[this.getDamage() > 11 ? 0 : this.getDamage()];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!target.isTransparent() || target.getId() == Block.SNOW_LAYER) {
            int damage = this.getDamage();
            int[] faces = {1, 2, 3, 0};
            this.setDamage(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);
            if (damage >= 4 && damage <= 7) {
                this.setDamage(this.getDamage() | 0x04);
            } else if (damage >= 8 && damage <= 11) {
                this.setDamage(this.getDamage() | 0x08);
            }
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            player.addWindow(new AnvilInventory(player.getUIInventory(), this), Player.ANVIL_WINDOW_ID);
        }
        return true;
    }

    @Override
    public Item toItem() {
        int damage = this.getDamage();
        if (damage >= 4 && damage <= 7) {
            return new ItemBlock(this, this.getDamage() & 0x04);
        } else if (damage >= 8 && damage <= 11) {
            return new ItemBlock(this, this.getDamage() & 0x08);
        } else {
            return new ItemBlock(this);
        }
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    this.toItem()
            };
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

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x3);
    }

    @Override
    public double getMinX() {
        return this.x + (this.getBlockFace().getAxis() == BlockFace.Axis.X ? 0 : 2 / 16.0);
    }

    @Override
    public double getMinZ() {
        return this.z + (this.getBlockFace().getAxis() == BlockFace.Axis.Z ? 0 : 2 / 16.0);
    }

    @Override
    public double getMaxX() {
        return this.x + (this.getBlockFace().getAxis() == BlockFace.Axis.X ? 1 : 1 - 2 / 16.0);
    }

    @Override
    public double getMaxZ() {
        return this.z + (this.getBlockFace().getAxis() == BlockFace.Axis.Z ? 1 : 1 - 2 / 16.0);
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
