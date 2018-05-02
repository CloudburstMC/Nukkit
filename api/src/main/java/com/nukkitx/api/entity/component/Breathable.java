package com.nukkitx.api.entity.component;

public interface Breathable extends EntityComponent {

    boolean canBreathe();

    int getTotalAirSupply();

    void setTotalAirSupply(int totalAirSupply);

    int getAirSupply();

    void setAirSupply(int airSupply);
}
