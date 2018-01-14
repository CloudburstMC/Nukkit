package cn.nukkit.api.entity.component;

public interface Physics extends EntityComponent {

    float getDrag();

    void setDrag(float drag);

    double getGravity();

    void setGravity(double gravity);
}
