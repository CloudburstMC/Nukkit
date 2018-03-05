package cn.nukkit.api.event.vehicle;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.Event;

public interface VehicleEvent extends Event {

    Entity getVehicle();
}
