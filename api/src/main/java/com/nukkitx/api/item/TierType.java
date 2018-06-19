package com.nukkitx.api.item;

public interface TierType extends Comparable<TierType> {

    int getLevel();

    float getMiningEfficiency();

    @Override
    default int compareTo(TierType that) {
        return Integer.compare(this.getLevel(), that.getLevel());
    }
}
