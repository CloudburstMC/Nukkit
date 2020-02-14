package cn.nukkit.block;

/**
 * author: Justin
 */

import cn.nukkit.blockentity.Skull;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.blockentity.BlockEntityTypes.SKULL;

public class BlockSkull extends BlockTransparent {

    public BlockSkull(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 1;
    }

    @Override
    public float getResistance() {
        return 5;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
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
        this.getLevel().setBlock(block.getPosition(), this, true, true);

        Skull skull = BlockEntityRegistry.get().newEntity(SKULL, this.getChunk(), this.getPosition());
        skull.loadAdditionalData(item.getTag());
        skull.setSkullType(item.getMeta());
        skull.setRotation((player.getYaw() * 16 / 360) + 0.5f);

        // TODO: 2016/2/3 SPAWN WITHER

        return true;
    }

    @Override
    public Item toItem() {
        Skull blockEntity = (Skull) getLevel().getBlockEntity(this.getPosition());
        int meta = 0;
        if (blockEntity != null) meta = blockEntity.getSkullType();
        return Item.get(ItemIds.SKULL, meta);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }
}