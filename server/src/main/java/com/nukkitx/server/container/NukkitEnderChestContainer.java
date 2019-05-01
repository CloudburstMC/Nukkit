package com.nukkitx.server.container;

import com.nukkitx.api.metadata.blockentity.EnderChestBlockEntity;
import com.nukkitx.server.network.bedrock.session.PlayerSession;

import javax.annotation.Nonnull;
import java.util.Optional;

public class NukkitEnderChestContainer extends NukkitFillingContainer {
    private EnderChestBlockEntity enderChest;

    public NukkitEnderChestContainer(PlayerSession session) {
        super(session, 27, ContainerType.CONTAINER);
    }

    public Optional<EnderChestBlockEntity> getActiveEnderChest() {
        return Optional.ofNullable(enderChest);
    }

    public void setActiveEnderChest(EnderChestBlockEntity enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public void onOpen(@Nonnull PlayerSession session) {
        if (enderChest != null) {
            // TODO: 18/12/2018 enderchest open code
        }
        super.onOpen(session);
    }

    @Override
    public void onClose(@Nonnull PlayerSession session) {
        if (enderChest != null) {
            // TODO: 18/12/2018 enderchest close code
        }
        super.onClose(session);
    }
}
