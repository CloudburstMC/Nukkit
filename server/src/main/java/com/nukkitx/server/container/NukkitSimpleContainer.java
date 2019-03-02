package com.nukkitx.server.container;

import com.nukkitx.api.container.SimpleContainer;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;

public class NukkitSimpleContainer extends NukkitContainer implements SimpleContainer {

    public NukkitSimpleContainer(ContainerType type, String name, int size) {
        super(type, name);
        setContainerSize(size);
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void onOpen(NukkitPlayerSession session) {

    }

    @Override
    public void onClose(NukkitPlayerSession session) {

    }
}
