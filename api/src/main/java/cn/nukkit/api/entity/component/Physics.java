package cn.nukkit.api.entity.component;

public interface Physics extends EntityComponent {

    float getDrag();

    void setDrag(float drag);

    float getGravity();

    void setGravity(float gravity);

    boolean isPhysicsDisabled();

    void setPhysicsDisabled(boolean physicsDisabled);
}
