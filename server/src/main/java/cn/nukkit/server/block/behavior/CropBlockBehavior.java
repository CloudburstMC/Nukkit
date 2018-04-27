package cn.nukkit.server.block.behavior;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.server.network.minecraft.session.PlayerSession;

public class CropBlockBehavior extends SimpleBlockBehavior {

    @Override
    public boolean onPlace(PlayerSession session, Block against, ItemInstance withItem) {
        return super.onPlace(session, against, withItem);
    }
}
