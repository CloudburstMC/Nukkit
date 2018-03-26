package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Physics;

public class PhysicsComponent implements Physics {
    private float gravity;
    private float drag;

    public PhysicsComponent(float gravity, float drag) {
        this.gravity = gravity;
        this.drag = drag;
    }

    @Override
    public float getDrag() {
        return drag;
    }

    @Override
    public void setDrag(float drag) {
        this.drag = drag;
    }

    @Override
    public float getGravity() {
        return gravity;
    }

    @Override
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
}
