package com.nukkitx.server.container;

import com.nukkitx.api.container.Container;

public interface ContainerSizeListener {
    void onContainerSizeChange(Container container, int size);
}
