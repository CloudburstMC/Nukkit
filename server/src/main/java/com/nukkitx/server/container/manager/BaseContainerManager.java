package com.nukkitx.server.container.manager;

import com.nukkitx.api.item.ItemStack;
import com.nukkitx.protocol.bedrock.data.ContainerId;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;
import lombok.Getter;

@Getter
public abstract class BaseContainerManager implements ContainerManager {
    private final NukkitPlayerSession player;
    private final ContainerId containerId;

    public BaseContainerManager(NukkitPlayerSession player, ContainerId containerId) {
        this.player = player;
        this.containerId = containerId;
        this.init();
    }

    @Override
    public ItemStack getSlot(int slot) {
        return null;
    }

    @Override
    public ItemStack getItems() {
        return null;
    }

    @Override
    public void setData(int type, int data) {

    }

    public abstract void init();
}
