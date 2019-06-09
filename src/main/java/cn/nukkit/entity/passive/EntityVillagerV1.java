package cn.nukkit.entity.passive;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by Pub4Game on 21.06.2016.
 */
public class EntityVillagerV1 extends EntityCreature implements EntityNPC, EntityAgeable {

    public static final int PROFESSION_FARMER = 0;
    public static final int PROFESSION_LIBRARIAN = 1;
    public static final int PROFESSION_PRIEST = 2;
    public static final int PROFESSION_BLACKSMITH = 3;
    public static final int PROFESSION_BUTCHER = 4;
    public static final int PROFESSION_GENERIC = 5;
    public static final int NETWORK_ID = 15;

    public EntityVillagerV1(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.9f;
        }
        return 1.8f;
    }

    @Override
    public String getName() {
        return "Villager";
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);

        if (!this.namedTag.contains("Profession")) {
            this.setProfession(PROFESSION_GENERIC);
        }
    }

    public int getProfession() {
        return this.namedTag.getInt("Profession");
    }

    public void setProfession(int profession) {
        this.namedTag.putInt("Profession", profession);
    }

    @Override
    public boolean isBaby() {
        return this.getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY);
    }
}
