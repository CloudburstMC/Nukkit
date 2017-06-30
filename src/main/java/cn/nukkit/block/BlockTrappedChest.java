package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;

import java.util.Map;

public class BlockTrappedChest extends BlockChest {

    public BlockTrappedChest() {
        this(0);
    }

    public BlockTrappedChest(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TRAPPED_CHEST;
    }

    @Override
    public String getName() {
        return "Trapped Chest";
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        int[] faces = {2, 5, 3, 4};

        BlockEntityChest chest = null;
        this.meta = faces[player != null ? player.getDirection().getHorizontalIndex() : 0];

        for (BlockFace side : Plane.HORIZONTAL) {
            if ((this.meta == 4 || this.meta == 5) && (side == BlockFace.WEST || side == BlockFace.EAST)) {
                continue;
            } else if ((this.meta == 3 || this.meta == 2) && (side == BlockFace.NORTH || side == BlockFace.SOUTH)) {
                continue;
            }
            Block c = this.getSide(side);
            if (c instanceof BlockTrappedChest && c.getDamage() == this.meta) {
                BlockEntity blockEntity = this.getLevel().getBlockEntity(c);
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
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
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
    public int getWeakPower(BlockFace face) {
        int playerCount = 0;

        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityChest) {
            playerCount = ((BlockEntityChest) blockEntity).getInventory().getViewers().size();
        }

        return playerCount < 0 ? 0 : playerCount > 15 ? 15 : playerCount;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return side == BlockFace.UP ? this.getWeakPower(side) : 0;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }
}
