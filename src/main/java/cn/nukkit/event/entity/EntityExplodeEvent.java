package cn.nukkit.event.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.level.Position;

import java.util.HashMap;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class EntityExplodeEvent extends EntityEvent implements Cancellable{

    protected Position position;
    protected HashMap<String, Block> blocks;
    protected float yield;

    public EntityExplodeEvent(Entity entity, Position position, HashMap<String, Block> blocks, float yield){
        this.entity = entity;
        this.position = position;
        this.blocks = blocks;
        this.yield = yield;
    }

    public Position getPosition() {
        return this.position;
    }

    public HashMap<String, Block> getBlockList() {
        return this.blocks;
    }

    public void setBlockList(HashMap<String, Block> blocks) {
        this.blocks = blocks;
    }

    public float getYield() {
        return this.yield;
    }

    public void setYield(float yield) {
        this.yield = yield;
    }

}
