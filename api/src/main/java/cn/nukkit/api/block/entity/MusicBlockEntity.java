package cn.nukkit.api.block.entity;

public interface MusicBlockEntity extends BlockEntity {

    void tune();

    void setPitch(int pitch);

    int getPitch();

    void playNote();
}
