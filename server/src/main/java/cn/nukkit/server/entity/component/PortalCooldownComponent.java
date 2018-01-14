package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.PortalCooldown;

public class PortalCooldownComponent implements PortalCooldown {
    private int cooldown;

    @Override
    public int getPortalCooldown() {
        return cooldown;
    }

    @Override
    public void setPortalCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
}
