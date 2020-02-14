package cn.nukkit.block;

import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.SNOW_LAYER;

/**
 * Created by Pub4Game on 27.12.2015.
 */
public class BlockAnvil extends BlockFallable implements Faceable {

    public BlockAnvil(Identifier id) {
        super(id);
    }

    @Override
    public final void setMeta(int meta) {
        this.meta = meta;
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
    public float getHardness() {
        return 5;
    }

    @Override
    public float getResistance() {
        return 6000;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (!target.isTransparent() || target.getId() == SNOW_LAYER) {
            int meta = this.getMeta();
            int[] faces = {1, 2, 3, 0};
            this.setMeta(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);
            if (meta >= 4 && meta <= 7) {
                this.setMeta(this.getMeta() | 0x04);
            } else if (meta >= 8 && meta <= 11) {
                this.setMeta(this.getMeta() | 0x08);
            }
            this.getLevel().setBlock(block.getPosition(), this, true);
            this.getLevel().addSound(this.getPosition().toFloat(), Sound.RANDOM_ANVIL_LAND, 1, 0.8F);
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
        int meta = this.getMeta();
        if (meta >= 4 && meta <= 7) {
            return Item.get(id, this.getMeta() & 0x04);
        } else if (meta >= 8 && meta <= 11) {
            return Item.get(id, this.getMeta() & 0x08);
        } else {
            return Item.get(id);
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
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x7);
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
