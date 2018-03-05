package cn.nukkit.api.entity.component;

import cn.nukkit.api.entity.Entity;

import javax.annotation.Nullable;
import java.util.Optional;

public interface Ownable extends EntityComponent {

    Optional<Entity> getOwner();

    void setOwner(@Nullable Entity entity);
}
