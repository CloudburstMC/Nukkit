package com.nukkitx.server.inventory;

import com.nukkitx.api.item.ItemStack;
import com.nukkitx.protocol.bedrock.data.ContainerId;
import com.nukkitx.protocol.bedrock.packet.InventoryContentPacket;
import com.nukkitx.server.item.ItemUtils;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Inventories {
    public static final int UNTRACKED_INTERACTION_SLOT_COUNT = 9;

    public static InventoryContentPacket newInventoryContentPacket(PlayerSession player, ContainerId id) {
        InventoryContentPacket packet = new InventoryContentPacket();
        packet.setContainerId(id);

        ItemStack[] contents;
        switch (id) {
            // TODO: 20/12/2018 Other inventory types
            case INVENTORY:
                contents = player.getInventory().getContents();
                break;
            case CURSOR:
                contents = new ItemStack[]{player.getCursorSelectedItem().orElse(ItemUtils.AIR)};
                break;
            default:
                throw new IllegalArgumentException("Container ID " + id + " is not associated with the player");
        }

        packet.setContents(ItemUtils.toNetwork(contents));
        return packet;
    }
}
