package com.nukkitx.api.metadata.blockentity;

public interface MusicBlockEntity extends BlockEntity {

    void tune();

    void setPitch(int pitch);

    int getPitch();

    void playNote();
}
