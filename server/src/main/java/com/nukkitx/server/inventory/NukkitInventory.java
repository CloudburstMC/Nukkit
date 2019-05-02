package com.nukkitx.server.inventory;

import com.nukkitx.api.inventory.Inventory;
import com.nukkitx.server.container.ContainerType;
import com.nukkitx.server.container.NukkitFillingContainer;
import com.nukkitx.server.network.bedrock.session.PlayerSession;

public class NukkitInventory extends NukkitFillingContainer implements Inventory {

    public NukkitInventory(PlayerSession session) {
        super(session, 36, ContainerType.INVENTORY);
    }
}
