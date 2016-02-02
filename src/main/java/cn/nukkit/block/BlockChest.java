package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;

import java.util.Iterator;
import java.util.Map;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockChest extends BlockTransparent {

    public BlockChest() {
        this(0);
    }

    public BlockChest(int meta) {
        super(meta);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getId() {
        return Block.CHEST;
    }

    @Override
    public String getName() {
        return "Chest";
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_AXE;
    }

    @Override
    public AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                this.x + 0.0625,
                this.y,
                this.z + 0.0625,
                this.x + 0.9375,
                this.y + 0.9475,
                this.z + 0.9375
        );
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        int[] faces = {4, 2, 5, 3};

        BlockEntityChest chest = null;
        this.meta = faces[player != null ? player.getDirection() : 0];

        for (int side = 2; side <= 5; ++side) {
            if ((this.meta == 4 || this.meta == 5) && (side == 4 || side == 5)) {
                continue;
            } else if ((this.meta == 3 || this.meta == 2) && (side == 2 || side == 3)) {
                continue;
            }
            Block c = this.getSide(side);
            if (c instanceof BlockChest && c.getDamage() == this.meta) {
                BlockEntity blockEntity = this.getLevel().getTile(c);
                if (blockEntity instanceof BlockEntityChest && !((BlockEntityChest) blockEntity).isPaired()) {
                    chest = (BlockEntityChest) blockEntity;
                    break;
                }
            }
        }

        this.getLevel().setBlock(block, this, true, true);
        CompoundTag nbt = new CompoundTag("")
                .putList(new ListTag<>("Items"))
                .putString("id", BlockEntity.CHEST)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            Iterator iter = customData.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry tag = (Map.Entry) iter.next();
                nbt.put((String) tag.getKey(), (Tag) tag.getValue());
            }
        }

        BlockEntity blockEntity = new BlockEntityChest(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);

        if (chest != null) {
            chest.pairWith(((BlockEntityChest) blockEntity));
            ((BlockEntityChest) blockEntity).pairWith(chest);
        }

        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        BlockEntity t = this.getLevel().getTile(this);
        if (t instanceof BlockEntityChest) {
            ((BlockEntityChest) t).unpair();
        }
        this.getLevel().setBlock(this, new BlockAir(), true, true);

        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            Block top = this.getSide(1);
            if (!top.isTransparent()) {
                return true;
            }

            BlockEntity t = this.getLevel().getTile(this);
            BlockEntityChest chest;
            if (t instanceof BlockEntityChest) {
                chest = (BlockEntityChest) t;
            } else {
                CompoundTag nbt = new CompoundTag("")
                        .putList(new ListTag<>("Items"))
                        .putString("id", BlockEntity.CHEST)
                        .putInt("x", (int) this.x)
                        .putInt("y", (int) this.y)
                        .putInt("z", (int) this.z);
                chest = new BlockEntityChest(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
            }

            if (chest.namedTag.contains("Lock") && chest.namedTag.get("Lock") instanceof StringTag) {
                if (!chest.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return true;
                }
            }

            if (player.isCreative()) {
                return true;
            }
            player.addWindow(chest.getInventory());
        }

        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{new int[]{this.getId(), 0, 1}};
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
