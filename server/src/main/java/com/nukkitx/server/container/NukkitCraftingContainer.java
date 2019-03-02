package com.nukkitx.server.container;

import com.nukkitx.api.container.CraftingContainer;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;

import static com.nukkitx.server.container.ContainerType.INVENTORY;
import static com.nukkitx.server.container.ContainerType.WORKBENCH;

public class NukkitCraftingContainer extends NukkitContainer implements CraftingContainer {
    private final int width;

    public NukkitCraftingContainer(int height, int width) {
        super(height != 2 ? INVENTORY : WORKBENCH);

        this.width = width;
        setContainerSize(height * width);
    }

    @Override
    public ItemStack getItem(int x, int y) {
        return super.getSlot(getSlot(x, y));
    }

    private int getSlot(int x, int y) {
        return x + (width * y);
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

    @Override
    protected final void setContainerSize(int size) {
        throw new UnsupportedOperationException();
    }
}
