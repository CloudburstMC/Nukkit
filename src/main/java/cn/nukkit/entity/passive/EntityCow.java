package cn.nukkit.entity.passive;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Author: BeYkeRYkt 
 * Nukkit Project
 */
public class EntityCow extends EntityAnimal {

	public static final int NETWORK_ID = 11;

	public EntityCow(FullChunk chunk, CompoundTag nbt) {
		super(chunk, nbt);
	}

	@Override
	public float getWidth() {
		return 0.9f;
	}

	@Override
	public float getHeight() {
		if (isBaby()) {
			return 0.65f;
		}
		return 1.3f;
	}

	@Override
	public float getEyeHeight() {
		if (isBaby()) {
			return 0.65f;
		}
		return 1.2f;
	}

	@Override
	public String getName() {
		return this.getNameTag();
	}

	@Override
	public Item[] getDrops() {
		return new Item[] { Item.get(Item.LEATHER), Item.get(Item.RAW_BEEF) };
	}

	@Override
	public int getNetworkId() {
		return NETWORK_ID;
	}
	
	@Override
	protected void initEntity() {
		super.initEntity();
		setMaxHealth(10);
	}
}
