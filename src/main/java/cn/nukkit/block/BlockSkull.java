package cn.nukkit.block;

/**
 * author: Justin
 */

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySkull;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;


public class BlockSkull extends BlockTransparent {

    public BlockSkull(Identifier id) {
        super(id);
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        switch (face) {
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
            case UP:
                this.setDamage(face.getIndex());
                break;
            case DOWN:
            default:
                return false;
        }
        this.getLevel().setBlock(block, this, true, true);

        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.SKULL)
                .putByte("SkullType", item.getDamage())
                .putInt("x", block.getFloorX())
                .putInt("y", block.getFloorY())
                .putInt("z", block.getFloorZ())
                .putByte("Rot", (int) Math.floor((player.yaw * 16 / 360) + 0.5) & 0x0f);
        if (item.hasCustomBlockData()) {
            for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag);
            }
        }
        new BlockEntitySkull(getLevel().getChunk((int) block.x >> 4, (int) block.z >> 4), nbt);

        // TODO: 2016/2/3 SPAWN WITHER

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        int dropMeta = 0;
        if (blockEntity != null) dropMeta = blockEntity.namedTag.getByte("SkullType");
        return new Item[]{
                Item.get(ItemIds.SKULL, dropMeta)
        };
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

}