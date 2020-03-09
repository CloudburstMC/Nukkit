package cn.nukkit.blockentity;

public interface Skull extends BlockEntity {

    int getSkullType();

    void setSkullType(int skullType);

    float getRotation();

    void setRotation(float rotation);
}
