package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.event.player.PlayerMapInfoRequestEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMap;

/**
 * Created by CreeperFace on 5.3.2017.
 */
public class MapInfoRequestPacket extends DataPacket {
    public long mapId;

    @Override
    public byte pid() {
        return ProtocolInfo.MAP_INFO_REQUEST_PACKET;
    }

    @Override
    public void decode() {
        mapId = this.getEntityUniqueId();
    }

    @Override
    public void encode() {

    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
