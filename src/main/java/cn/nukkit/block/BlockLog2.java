package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockLog2 extends BlockLog {

    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    public BlockLog2(Identifier id) {
        super(id);
    }

    @Override
    public Item toItem() {
        if ((getDamage() & 0b1100) == 0b1100) {
            return Item.get(BlockIds.WOOD, this.getDamage() & 0x3 + 4);
        } else {
            return Item.get(id, this.getDamage() & 0x03);
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        // Convert the old log bark to the new wood block
        if ((this.getDamage() & 0b1100) == 0b1100) {
            Block woodBlock = Block.get(BlockIds.WOOD, (this.getDamage() & 0x01) + 4, this);
            return woodBlock.place(item, block, target, face, clickPos, player);
        }

        return super.place(item, block, target, face, clickPos, player);
    }

    public static void upgradeLegacyBlock(int[] blockState) {
        if ((blockState[1] & 0b1100) == 0b1100) { // old full bark texture
            blockState[0] = BlockRegistry.get().getLegacyId(BlockIds.WOOD);
            blockState[1] = (blockState[1] & 0x03) + 4; // gets only the log type and set pillar to y
        }
    }
}
