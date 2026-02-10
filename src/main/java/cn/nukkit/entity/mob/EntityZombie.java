package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityMobWithTool;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityZombie extends EntityWalkingMob implements EntitySmite, EntityMobWithTool {

    public static final int NETWORK_ID = 32;

    private Item tool;
    private Item offhand;

    public EntityZombie(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    public int getKillExperience() {
        return this.isBaby() ? 12 : 5;
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);

        this.sendHandItems(player);
    }

    @Override
    public Item getTool() {
        return this.tool;
    }

    @Override
    public void setTool(Item tool) {
        this.tool = tool;
    }

    @Override
    public Item getOffhand() {
        return this.offhand;
    }

    @Override
    public void setOffhand(Item offhand) {
        this.offhand = offhand;
    }
}
