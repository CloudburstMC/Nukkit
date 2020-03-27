package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

public class BlockStrippedLog extends BlockLog {

    public BlockStrippedLog(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        this.setMeta(FACES[face.getIndex()] >> 2);
        return this.getLevel().setBlock(this.getPosition(), this, true, true);
    }

    @Override
    public Item toItem() {
        return Item.get(id);
    }
}
