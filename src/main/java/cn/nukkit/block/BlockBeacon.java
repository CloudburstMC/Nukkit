package cn.nukkit.block;

import cn.nukkit.blockentity.Beacon;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.inventory.BeaconInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.blockentity.BlockEntityTypes.BEACON;

/**
 * author: Angelic47 Nukkit Project
 */
public class BlockBeacon extends BlockTransparent {

    public BlockBeacon(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this.getPosition());
            if (!(t instanceof Beacon)) {
                t.close();
                BlockEntityRegistry.get().newEntity(BEACON, this.getChunk(), this.getPosition());
            }

            player.addWindow(new BeaconInventory(player.getUIInventory(), this), ContainerIds.BEACON);
        }
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        boolean blockSuccess = super.place(item, block, target, face, clickPos, player);

        if (blockSuccess) {
            BlockEntityRegistry.get().newEntity(BEACON, this.getChunk(), this.getPosition());
        }

        return blockSuccess;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
