package cn.nukkit.entity.impl.vehicle;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.vehicle.CommandBlockMinecart;
import cn.nukkit.level.Location;

public class EntityCommandBlockMinecart extends EntityVehicle implements CommandBlockMinecart {

    public EntityCommandBlockMinecart(EntityType<CommandBlockMinecart> type, Location location) {
        super(type, location);
    }

    @Override
    public boolean mount(Entity entity) {
        return false;
    }

    @Override
    public boolean dismount(Entity vehicle) {
        return false;
    }
}
