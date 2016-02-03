package cn.nukkit.block;

/**
 * author: Justin
 */

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySkull;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;


public class BlockSkull extends BlockTransparent{
    public static final int SKELETON_SKULL = 0;
    public static final int WITHER_SKELETON_SKULL = 1;
    public static final int ZOMBIE_HEAD = 2;
    public static final int HEAD = 3;
    public static final int CREEPER_HEAD = 4;

    public BlockSkull() {
        this(0);
    }

    public BlockSkull(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SKULL_BLOCK;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public boolean isSolid(){
        return false;
    }

    @Override
    public String getName() {
        switch (meta) {
            case 1:
                return "Wither Skeleton Skull";
            case 2:
                return "Zombie Head";
            case 3:
                return "Head";
            case 4:
                return "Creeper Head";
            case 0:
            default:
                return "Skeleton Skull";
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        switch (face) {
            case Vector3.SIDE_NORTH:
            case Vector3.SIDE_SOUTH:
            case Vector3.SIDE_EAST:
            case Vector3.SIDE_WEST:
            case Vector3.SIDE_UP:
                this.meta = face;
                break;
            case Vector3.SIDE_DOWN:
            default:
                return false;
        }
        this.getLevel().setBlock(block, this, true, true);

        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.SKULL)
                .putByte("SkullType", (byte) item.getDamage())
                .putInt("x", block.getFloorX())
                .putInt("y", block.getFloorY())
                .putInt("z", block.getFloorZ())
                .putByte("Rot", (byte) ((int) Math.floor((player.yaw * 16 / 360) + 0.5) & 0x0f)); // TODO: 2016/2/3 VERIFY
        if (item.hasCustomBlockData()) {
            for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag);
            }
        }
        new BlockEntitySkull(getLevel().getChunk((int) block.x >> 4, (int) block.z >> 4), nbt);

        return true;
    }

    public double getResistance(){
        return 5;
    }

    @Override
    public int[][] getDrops(Item item) {
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        int dropMeta = 0;
        if (blockEntity != null) dropMeta = blockEntity.namedTag.getByte("SkullType");
        return new int[][]{
                {Item.SKULL, dropMeta, 1}
        };
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

}