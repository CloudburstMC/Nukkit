package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockStairs extends BlockTransparent implements Faceable {

    public BlockStairs(Identifier id) {
        super(id);
    }

    @Override
    public float getMinY() {
        // TODO: this seems wrong
        return this.getY() + ((getMeta() & 0x04) > 0 ? 0.5f : 0);
    }

    @Override
    public float getMaxY() {
        // TODO: this seems wrong
        return this.getY() + ((getMeta() & 0x04) > 0 ? 1 : 0.5f);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        int[] faces = new int[]{2, 1, 3, 0};
        this.setMeta(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        if ((clickPos.getY() > 0.5 && face != BlockFace.UP) || face == BlockFace.DOWN) {
            this.setMeta(this.getMeta() | 0x04); //Upside-down stairs
        }
        this.getLevel().setBlock(block.getPosition(), this, true, true);

        return true;
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
        Item item = super.toItem();
        item.setMeta(0);
        return item;
    }

    @Override
    public boolean collidesWithBB(AxisAlignedBB bb) {
        int damage = this.getMeta();
        int side = damage & 0x03;
        float f = 0;
        float f1 = 0.5f;
        float f2 = 0.5f;
        float f3 = 1;
        if ((damage & 0x04) > 0) {
            f = 0.5f;
            f1 = 1;
            f2 = 0;
            f3 = 0.5f;
        }

        if (bb.intersectsWith(new SimpleAxisAlignedBB(
                this.getX(),
                this.getY() + f,
                this.getZ(),
                this.getX() + 1,
                this.getY() + f1,
                this.getZ() + 1
        ))) {
            return true;
        }


        if (side == 0) {
            if (bb.intersectsWith(new SimpleAxisAlignedBB(
                    this.getX() + 0.5f,
                    this.getY() + f2,
                    this.getZ(),
                    this.getX() + 1,
                    this.getY() + f3,
                    this.getZ() + 1
            ))) {
                return true;
            }
        } else if (side == 1) {
            if (bb.intersectsWith(new SimpleAxisAlignedBB(
                    this.getX(),
                    this.getY() + f2,
                    this.getZ(),
                    this.getX() + 0.5f,
                    this.getY() + f3,
                    this.getZ() + 1
            ))) {
                return true;
            }
        } else if (side == 2) {
            if (bb.intersectsWith(new SimpleAxisAlignedBB(
                    this.getX(),
                    this.getY() + f2,
                    this.getZ() + 0.5f,
                    this.getX() + 1,
                    this.getY() + f3,
                    this.getZ() + 1
            ))) {
                return true;
            }
        } else if (side == 3) {
            if (bb.intersectsWith(new SimpleAxisAlignedBB(
                    this.getX(),
                    this.getY() + f2,
                    this.getZ(),
                    this.getX() + 1,
                    this.getY() + f3,
                    this.getZ() + 0.5f
            ))) {
                return true;
            }
        }

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
