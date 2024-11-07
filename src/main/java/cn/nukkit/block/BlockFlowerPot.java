package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityFlowerPot;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
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

    private static boolean canPlaceIntoFlowerPot(Item item) {
        switch (item.getId()) {
            case SAPLING:
            case DEAD_BUSH:
            case DANDELION:
            case ROSE:
            case RED_MUSHROOM:
            case BROWN_MUSHROOM:
            case CACTUS:
                return true;
            case TALL_GRASS:
                if (item.getDamage() == 2 || item.getDamage() == 3) {
                    return true;
                }
            default:
                return false;
        }
    }

    private static boolean canPlaceIntoFlowerPot(Block block) {
        if (block == null) {
            return false;
        }
        switch (block.getId()) {
            case SAPLING:
            case DEAD_BUSH:
            case DANDELION:
            case ROSE:
            case RED_MUSHROOM:
            case BROWN_MUSHROOM:
            case CACTUS:
            case BAMBOO:
            case CRIMSON_FUNGUS:
            case WARPED_FUNGUS:
            case CRIMSON_ROOTS:
            case WARPED_ROOTS:
            case WITHER_ROSE:
            case MANGROVE_PROPAGULE:
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

    private static boolean isSupportValid(Block block) {
        return block.isSolid() || block.isNarrowSurface() || Block.canStayOnFullSolid(block);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValid(down())) {
                level.useBreakOn(this);
                return type;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!isSupportValid(down())) return false;
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
        BlockEntity.createBlockEntity(BlockEntity.FLOWER_POT, this.getChunk(), nbt);

        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        if (!(blockEntity instanceof BlockEntityFlowerPot)) return false;
        if (blockEntity.namedTag.getShort("item") != AIR) {
            if (!canPlaceIntoFlowerPot(item) && !canPlaceIntoFlowerPot(item.getBlockUnsafe())) {
                int id = blockEntity.namedTag.getShort("item");
                if (id > 255) {
                    for (Item drop : player.getInventory().addItem(new ItemBlock(Block.get(id)))) {
                        player.dropItem(drop);
                    }
                } else {
                    for (Item drop : player.getInventory().addItem(Item.get(id, blockEntity.namedTag.getInt("data")))) {
                        player.dropItem(drop);
                    }
                }

                blockEntity.namedTag.putShort("item", AIR);
                blockEntity.namedTag.putInt("data", 0);
                blockEntity.setDirty();

                this.setDamage(0);
                this.level.setBlock(this, this, true);
                ((BlockEntityFlowerPot) blockEntity).spawnToAll();
                return true;
            }
            return false;
        }
        int itemID;
        if (!canPlaceIntoFlowerPot(item)) {
            Block block = item.getBlockUnsafe();
            if (!canPlaceIntoFlowerPot(block)) {
                return true;
            }
            itemID = block.getId();
        } else {
            itemID = item.getId();
        }
        blockEntity.namedTag.putShort("item", itemID);
        blockEntity.namedTag.putInt("data", item.getDamage());
        blockEntity.setDirty();

        this.setDamage(1);
        this.getLevel().setBlock(this, this, true);
        ((BlockEntityFlowerPot) blockEntity).spawnToAll();

        if (!player.isCreative()) {
            item.count--;
        }
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
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
            if (insideID > 255) {
                return new Item[]{
                        Item.get(Item.FLOWER_POT),
                        new ItemBlock(Block.get(insideID))
                };
            } else {
                return new Item[]{
                        Item.get(Item.FLOWER_POT),
                        Item.get(insideID, insideMeta, 1)
                };
            }
        } else {
            return new Item[]{
                    Item.get(Item.FLOWER_POT)
            };
        }
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public double getMinX() {
        return this.x + 0.3125;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.3125;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.6875;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.375;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.6875;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.FLOWER_POT);
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false; // prevent item loss issue with pistons until a working implementation
    }
}
