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
    public void handle(Player player) {
        Item mapItem = null;

        for (Item item1 : player.inventory.getContents().values()) {
            if (item1 instanceof ItemMap && ((ItemMap) item1).getMapId() == this.mapId) {
                mapItem = item1;
            }
        }

        if (mapItem == null) {
            for (BlockEntity be : player.level.getBlockEntities().values()) {
                if (be instanceof BlockEntityItemFrame) {
                    BlockEntityItemFrame itemFrame1 = (BlockEntityItemFrame) be;

                    if (itemFrame1.getItem() instanceof ItemMap && ((ItemMap) itemFrame1.getItem()).getMapId() == this.mapId) {
                        ((ItemMap) itemFrame1.getItem()).sendImage(player);
                        break;
                    }
                }
            }
        }

        if (mapItem != null) {
            PlayerMapInfoRequestEvent event;
            player.server.getPluginManager().callEvent(event = new PlayerMapInfoRequestEvent(player, mapItem));

            if (!event.isCancelled()) {
                ((ItemMap) mapItem).sendImage(player);
            }
        }
    }
}
