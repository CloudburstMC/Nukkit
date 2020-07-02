package cn.nukkit.entity;

import com.nukkitx.protocol.bedrock.data.entity.EntityLinkData;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Rideable extends Entity {

    /**
     * Mount or Dismounts an Entity from a rideable entity
     *
     * @param entity The target Entity
     * @return {@code true} if the mounting successful
     */
    boolean mount(Entity entity, EntityLinkData.Type mode);

    boolean dismount(Entity entity);
}
