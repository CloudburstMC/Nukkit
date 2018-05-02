package com.nukkitx.api.event.vehicle;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.event.Event;

public interface VehicleEvent extends Event {

    Entity getVehicle();
}
