package cn.nukkit.api.block.entity;

import cn.nukkit.api.Player;

import java.util.UUID;

public interface ChalkboardBlockEntity extends BlockEntity {

    String getText();

    void setText(String text);

    boolean getLocked();

    void setLocked(boolean locked);

    UUID getOwner();

    void setOwner(UUID uuid);

    boolean playerMayEdit(Player player);
}
