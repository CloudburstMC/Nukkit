package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

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
        this.setDamage(FACES[face.getIndex()] >> 2);
        return this.getLevel().setBlock(this.asVector3i(), this, true, true);
    }

    @Override
    public Item toItem() {
        return Item.get(id);
    }
}
