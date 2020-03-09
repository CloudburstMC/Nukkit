package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

/**
 * Created on 2015/11/24 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockHayBale extends BlockSolid implements Faceable {
    public BlockHayBale(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public float getResistance() {
        return 2.5f;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        int[] faces = new int[]{
                0,
                0,
                0b1000,
                0b1000,
                0b0100,
                0b0100,
        };
        this.setMeta((this.getMeta() & 0x03) | faces[face.getIndex()]);
        this.getLevel().setBlock(block.getPosition(), this, true, true);

        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }
}
