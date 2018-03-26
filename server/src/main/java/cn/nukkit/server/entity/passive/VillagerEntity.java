package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.component.Ageable;
import cn.nukkit.api.entity.component.Professionable;
import cn.nukkit.api.entity.passive.Villager;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.entity.component.AgeableComponent;
import cn.nukkit.server.entity.component.ProfessionableComponent;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class VillagerEntity extends LivingEntity implements Villager {

    public VillagerEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.VILLAGER, position, level, server, 20);

        registerComponent(Ageable.class, new AgeableComponent(24000));
        registerComponent(Professionable.class, new ProfessionableComponent());
    }
}
