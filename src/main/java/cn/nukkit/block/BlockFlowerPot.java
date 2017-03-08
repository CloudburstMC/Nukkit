package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityFlowerPot;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;

/**
 * @author Nukkit Project Team
 */
public class BlockFlowerPot extends BlockFlowable {

    public BlockFlowerPot() {
        this(0);
    }

    public BlockFlowerPot(int meta) {
        super(meta);
    }

    protected static boolean canPlaceIntoFlowerPot(int id) {
        switch (id) {
            case SAPLING:
            case COBWEB:
            case TALL_GRASS:
            case DEAD_BUSH:
            case DANDELION:
            case ROSE:
            case RED_MUSHROOM:
            case BROWN_MUSHROOM:
            case CACTUS:
            case SUGARCANE_BLOCK:
                // TODO: 2016/2/4 case NETHER_WART:
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getName() {
        return "Flower Pot";
    }

    @Override
    public int getId() {
        return FLOWER_POT_BLOCK;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        if (face != Vector3.SIDE_UP) return false;
        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.FLOWER_POT)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("item", 0)
                .putInt("data", 0);
        if (item.hasCustomBlockData()) {
            for (Tag aTag : item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag);
            }
        }
        new BlockEntityFlowerPot(getLevel().getChunk((int) block.x >> 4, (int) block.z >> 4), nbt);

        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        if (!(blockEntity instanceof BlockEntityFlowerPot)) return false;
        if (blockEntity.namedTag.getShort("item") != 0 || blockEntity.namedTag.getInt("mData") != 0) return false;
        int itemID;
        int itemMeta;
        if (!canPlaceIntoFlowerPot(item.getId())) {
            if (!canPlaceIntoFlowerPot(item.getBlock().getId())) {
                return true;
            }
            itemID = item.getBlock().getId();
            itemMeta = item.getDamage();
        } else {
            itemID = item.getId();
            itemMeta = item.getDamage();
        }
        blockEntity.namedTag.putShort("item", itemID);
        blockEntity.namedTag.putInt("data", itemMeta);

        this.meta = 1;
        this.getLevel().setBlock(this, this, true);
        ((BlockEntityFlowerPot) blockEntity).spawnToAll();

        if (player.isSurvival()) {
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item.getCount() > 0 ? item : Item.get(Item.AIR));
        }
        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        boolean dropInside = false;
        int insideID = 0;
        int insideMeta = 0;
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        if (blockEntity instanceof BlockEntityFlowerPot) {
            dropInside = true;
            insideID = blockEntity.namedTag.getShort("item");
            insideMeta = blockEntity.namedTag.getInt("data");
        }

        if (dropInside) {
            return new int[][]{
                    {Item.FLOWER_POT, 0, 1},
                    {insideID, insideMeta, 1}
            };
        } else {
            return new int[][]{
                    {Item.FLOWER_POT, 0, 1}
            };
        }
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(this.x + 0.3125, this.y, this.z + 0.3125, this.x + 0.6875, this.y + 0.375, this.z + 0.6875);
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }
}
