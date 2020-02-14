package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityTypes;
import cn.nukkit.blockentity.FlowerPot;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.*;

/**
 * @author Nukkit Project Team
 */
public class BlockFlowerPot extends FloodableBlock {

    public BlockFlowerPot(Identifier id) {
        super(id);
    }

    protected static boolean canPlaceIntoFlowerPot(Identifier id) {
        return id == SAPLING || id == WEB || id == TALL_GRASS || id == DEADBUSH || id == YELLOW_FLOWER ||
                id == RED_FLOWER || id == RED_MUSHROOM || id == BROWN_MUSHROOM || id == CACTUS || id == REEDS;
        // TODO: 2016/2/4 case NETHER_WART:
    }

    @Override
    public float getHardness() {
        return 0;
    }

    @Override
    public float getResistance() {
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (face != BlockFace.UP) return false;

        FlowerPot flowerPot = BlockEntityRegistry.get().newEntity(BlockEntityTypes.FLOWER_POT, this.getChunk(), this.getPosition());
        flowerPot.loadAdditionalData(item.getTag());

        this.getLevel().setBlock(block.getPosition(), this, true, true);
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
        BlockEntity blockEntity = getLevel().getBlockEntity(this.getPosition());
        if (!(blockEntity instanceof FlowerPot)) return false;
        FlowerPot flowerPot = (FlowerPot) blockEntity;

        Identifier blockId;
        int blockMeta;
        if (!canPlaceIntoFlowerPot(item.getId())) {
            if (!canPlaceIntoFlowerPot(item.getBlock().getId())) {
                return true;
            }
            blockId = item.getBlock().getId();
            blockMeta = item.getMeta();
        } else if (item.getBlock().getId() != AIR) {
            blockId = item.getBlock().getId();
            blockMeta = item.getMeta();
        } else {
            return true;
        }
        flowerPot.setPlant(Block.get(blockId, blockMeta));

        this.setMeta(1);
        this.getLevel().setBlock(this.getPosition(), this, true);
        blockEntity.spawnToAll();

        if (player.isSurvival()) {
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item.getCount() > 0 ? item : Item.get(AIR));
        }
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        boolean dropInside = false;
        Block block = Block.get(AIR);
        BlockEntity blockEntity = getLevel().getBlockEntity(this.getPosition());
        if (blockEntity instanceof FlowerPot) {
            dropInside = true;
            block = ((FlowerPot) blockEntity).getPlant();
        }

        if (dropInside) {
            return new Item[]{
                    Item.get(ItemIds.FLOWER_POT),
                    block.toItem()
            };
        } else {
            return new Item[]{
                    Item.get(ItemIds.FLOWER_POT)
            };
        }
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public float getMinX() {
        return this.getX() + 0.3125f;
    }

    @Override
    public float getMinZ() {
        return this.getZ() + 0.3125f;
    }

    @Override
    public float getMaxX() {
        return this.getX() + 0.6875f;
    }

    @Override
    public float getMaxY() {
        return this.getY() + 0.375f;
    }

    @Override
    public float getMaxZ() {
        return this.getZ() + 0.6875f;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.FLOWER_POT);
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
