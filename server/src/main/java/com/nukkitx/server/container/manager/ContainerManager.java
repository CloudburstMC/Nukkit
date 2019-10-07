package com.nukkitx.server.container.manager;

import com.nukkitx.api.item.ItemStack;
//import com.nukkitx.protocol.bedrock.data.ContainerId;
import com.nukkitx.server.network.bedrock.session.PlayerSession;

public interface ContainerManager {

    ItemStack getSlot(int slot);

    ItemStack getItems();

    void setData(int type, int data);

    int getContainerId();

    PlayerSession getPlayer();
}
