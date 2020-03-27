package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.Chest;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.GenericMath;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.blockentity.BlockEntityTypes.CHEST;

public class BlockTrappedChest extends BlockChest {

    public BlockTrappedChest(Identifier id) {
        super(id);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        int[] faces = {2, 5, 3, 4};

        Chest chest = null;
        this.setMeta(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);

        for (BlockFace side : Plane.HORIZONTAL) {
            if ((this.getMeta() == 4 || this.getMeta() == 5) && (side == BlockFace.WEST || side == BlockFace.EAST)) {
                continue;
            } else if ((this.getMeta() == 3 || this.getMeta() == 2) && (side == BlockFace.NORTH || side == BlockFace.SOUTH)) {
                continue;
            }
            Block c = this.getSide(side);
            if (c instanceof BlockTrappedChest && c.getMeta() == this.getMeta()) {
                BlockEntity blockEntity = this.getLevel().getBlockEntity(c.getPosition());
                if (blockEntity instanceof Chest && !((Chest) blockEntity).isPaired()) {
                    chest = (Chest) blockEntity;
                    break;
                }
            }
        }

        this.getLevel().setBlock(block.getPosition(), this, true, true);

        Chest chest1 = BlockEntityRegistry.get().newEntity(CHEST, this.getChunk(), this.getPosition());
        chest1.loadAdditionalData(item.getTag());
        if (item.hasCustomName()) {
            chest1.setCustomName(item.getCustomName());
        }

        if (chest != null) {
            chest1.pairWith(chest);
            chest.pairWith(chest1);
        }

        return true;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        int playerCount = 0;

        BlockEntity blockEntity = this.level.getBlockEntity(this.getPosition());

        if (blockEntity instanceof Chest) {
            playerCount = ((Chest) blockEntity).getInventory().getViewers().size();
        }

        return GenericMath.clamp(playerCount, 0, 15);
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
