package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.event.player.PlayerFishEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookEnchanted;
import cn.nukkit.item.ItemFishingRod;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.randomitem.Fishing;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.BubbleParticle;
import cn.nukkit.level.particle.WaterParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.Utils;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;


public class EntityFishingHook extends EntityProjectile {

	public static final int NETWORK_ID = 77;

	public int waitChance = 120;
	public int waitTimer = 240;
	public boolean attracted;
	public int attractTimer;
	public boolean caught;
	public int caughtTimer;
	public boolean canCollide = true;
	public Entity caughtEntity;
	private long target;
	public Vector3 fish;
	public Item rod;

	public EntityFishingHook(FullChunk chunk, CompoundTag nbt) {
		this(chunk, nbt, null);
	}

	public EntityFishingHook(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
		super(chunk, nbt, shootingEntity);
	}

	@Override
	protected void initEntity() {
		super.initEntity();
		if (this.age > 0) {
			this.close();
		}
	}

	@Override
	public int getNetworkId() {
		return NETWORK_ID;
	}

	@Override
	public float getWidth() {
		return 0.2f;
	}

	@Override
	public float getLength() {
		return 0.2f;
	}

	@Override
	public float getHeight() {
		return 0.2f;
	}

	@Override
	public float getGravity() {
		return 0.07f;
	}

	@Override
	public float getDrag() {
		return 0.05f;
	}

	@Override
	public boolean onUpdate(int currentTick) {
		if (this.target != 0) {
			Entity ent = this.level.getEntity(this.target);
			if (ent == null || !ent.isAlive()) {
				caughtEntity = null;
				this.setTarget(0);
			} else {
				this.setPosition(ent.add(0, getHeight() * 0.75f, 0));
			}
			return false;
		}

		boolean hasUpdate = super.onUpdate(currentTick);

		boolean inWater = this.isInsideOfWater();
		if (inWater) {
			this.motionX = 0;
			this.motionY -= getGravity() * -0.04;
			this.motionZ = 0;
			hasUpdate = true;
		}

		if (inWater) {
			if (this.waitTimer == 240) {
				this.waitTimer = this.waitChance << 1;
			} else if (this.waitTimer == 360) {
				this.waitTimer = this.waitChance * 3;
			}
			if (!this.attracted) {
				if (this.waitTimer > 0) {
					--this.waitTimer;
				}
				if (this.waitTimer == 0) {
					if (ThreadLocalRandom.current().nextInt(100) < 90) {
						this.attractTimer = (ThreadLocalRandom.current().nextInt(40) + 20);
						this.spawnFish();
						this.caught = false;
						this.attracted = true;
					} else {
						this.waitTimer = this.waitChance;
					}
				}
			} else if (!this.caught) {
				if (this.attractFish()) {
					this.caughtTimer = (ThreadLocalRandom.current().nextInt(20) + 30);
					this.fishBites();
					this.caught = true;
				}
			} else {
				if (this.caughtTimer > 0) {
					--this.caughtTimer;
				}
				if (this.caughtTimer == 0) {
					this.attracted = false;
					this.caught = false;
					this.waitTimer = this.waitChance * 3;
				}
			}
		}

		return hasUpdate;
	}

	public int getWaterHeight() {
		for (int y = this.getFloorY(); y < level.getMaxBlockY(); y++) {
			int id = this.level.getBlockIdAt(chunk, this.getFloorX(), y, this.getFloorZ());
			if (id == Block.AIR) {
				return y;
			}
		}
		return this.getFloorY();
	}

	public void fishBites() {
		Collection<Player> viewers = this.getViewers().values();

		EntityEventPacket pk = new EntityEventPacket();
		pk.eid = this.getId();
		pk.event = EntityEventPacket.FISH_HOOK_HOOK;
		Server.broadcastPacket(viewers, pk);

		EntityEventPacket bubblePk = new EntityEventPacket();
		bubblePk.eid = this.getId();
		bubblePk.event = EntityEventPacket.FISH_HOOK_BUBBLE;
		Server.broadcastPacket(viewers, bubblePk);

		EntityEventPacket teasePk = new EntityEventPacket();
		teasePk.eid = this.getId();
		teasePk.event = EntityEventPacket.FISH_HOOK_TEASE;
		Server.broadcastPacket(viewers, teasePk);

		this.level.addParticle(new BubbleParticle(this.setComponents(
				this.x + ThreadLocalRandom.current().nextDouble() * 0.5 - 0.25,
				this.getWaterHeight(),
				this.z + ThreadLocalRandom.current().nextDouble() * 0.5 - 0.25
		)), null, 5);
	}

	public void spawnFish() {
		this.fish = new Vector3(
				this.x + (ThreadLocalRandom.current().nextDouble() * 1.2 + 1) * (Utils.rand() ? -1 : 1),
				this.getWaterHeight(),
				this.z + (ThreadLocalRandom.current().nextDouble() * 1.2 + 1) * (Utils.rand() ? -1 : 1)
		);
	}

	public boolean attractFish() {
		double multiply = 0.1;
		this.fish.setComponents(
				this.fish.x + (this.x - this.fish.x) * multiply,
				this.fish.y,
				this.fish.z + (this.z - this.fish.z) * multiply
		);
		if (ThreadLocalRandom.current().nextInt(100) < 85) {
			this.level.addParticle(new WaterParticle(this.fish));
		}
		double dist = Math.abs(Math.sqrt(this.x * this.x + this.z * this.z) - Math.sqrt(this.fish.x * this.fish.x + this.fish.z * this.fish.z));
		return dist < 0.15;
	}

	private static final int[] ROD_ENCHANTMENTS = {Enchantment.ID_LURE, Enchantment.ID_FORTUNE_FISHING, Enchantment.ID_DURABILITY, Enchantment.ID_MENDING, Enchantment.ID_VANISHING_CURSE};

	public void reelLine() {
		if (this.shootingEntity instanceof Player && !this.closed) {
			if (this.caught) {
				Player player = (Player) this.shootingEntity;
				Item item = Fishing.getFishingResult(this.rod);
				if (item instanceof ItemTool) {
					item.setDamage(Utils.rand(1, item.getMaxDurability() - 2));
				}
				// TODO: improve loot selectors so this hack won't be needed
				if (item instanceof ItemBookEnchanted) {
					if (!item.hasEnchantments()) {
						item = item.clone();
						Enchantment enchantment = Enchantment.getEnchantment(Utils.rand(0, 35));
						if (ThreadLocalRandom.current().nextDouble() < 0.3) {
							enchantment.setLevel(Utils.rand(1, enchantment.getMaxLevel()));
						}
						item.addEnchantment(enchantment);
					}
				} else if (item instanceof ItemFishingRod) {
					if (Utils.rand(1, 3) == 2 && !item.hasEnchantments()) {
						item = item.clone();
						Enchantment enchantment = Enchantment.getEnchantment(ROD_ENCHANTMENTS[Utils.rand(0, 4)]);
						if (ThreadLocalRandom.current().nextDouble() < 0.3) {
							enchantment.setLevel(Utils.rand(1, enchantment.getMaxLevel()));
						}
						item.addEnchantment(enchantment);
					}
				}
				int experience = ThreadLocalRandom.current().nextInt(3) + 1;
				Vector3 motion = player.subtract(this).multiply(0.1);
				motion.y += Math.sqrt(player.distance(this)) * 0.05;

				PlayerFishEvent event = new PlayerFishEvent(player, this, item, experience, motion);
				this.getServer().getPluginManager().callEvent(event);

				if (!event.isCancelled()) {
					EntityItem itemEntity = (EntityItem) Entity.createEntity(EntityItem.NETWORK_ID,
							this.level.getChunk(this.getChunkX(), this.getChunkZ(), true),
							Entity.getDefaultNBT(new Vector3(this.x, this.getWaterHeight(), this.z), event.getMotion(), ThreadLocalRandom.current().nextFloat() * 360, 0).putShort("Health", 5).putCompound("Item", NBTIO.putItemHelper(event.getLoot())).putShort("PickupDelay", 1));

					itemEntity.setOwner(player.getName());
					itemEntity.spawnToAll();

					player.getLevel().dropExpOrb(player, event.getExperience());
				}
			}
			if (this.caughtEntity != null) {
				Vector3 motion = this.shootingEntity.subtract(this).multiply(0.1);
				motion.y += Math.sqrt(this.shootingEntity.distance(this)) * 0.08;
				this.caughtEntity.setMotion(motion);
			}
		}
		this.close();
	}

	@Override
	protected DataPacket createAddEntityPacket() {
		AddEntityPacket pk = new AddEntityPacket();
		pk.entityRuntimeId = this.getId();
		pk.entityUniqueId = this.getId();
		pk.type = NETWORK_ID;
		pk.x = (float) this.x;
		pk.y = (float) this.y;
		pk.z = (float) this.z;
		pk.speedX = (float) this.motionX;
		pk.speedY = (float) this.motionY;
		pk.speedZ = (float) this.motionZ;
		pk.yaw = (float) this.yaw;
		pk.pitch = (float) this.pitch;

		long ownerId = -1;
		if (this.shootingEntity != null) {
			ownerId = this.shootingEntity.getId();
		}
		pk.metadata = this.dataProperties.putLong(DATA_OWNER_EID, ownerId).clone();
		return pk;
	}

	@Override
	public boolean canCollide() {
		return this.canCollide;
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (entity == this.shootingEntity) {
			return;
		}

		this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
		float damage = this.getResultDamage();

		EntityDamageEvent ev;
		if (this.shootingEntity == null) {
			ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage, knockBack);
		} else {
			ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage, knockBack);
		}

		if (entity.attack(ev)) {
			this.caughtEntity = entity;
			this.setTarget(entity.getId());
		}
	}

	public void checkLure() {
		if (rod != null) {
			Enchantment ench = rod.getEnchantment(Enchantment.ID_LURE);
			if (ench != null) {
				this.waitChance = 120 - (25 * ench.getLevel());
			}
		}
	}

	public void setTarget(long eid) {
		this.target = eid;
		this.setDataProperty(new LongEntityData(DATA_TARGET_EID, eid));
		this.canCollide = eid == 0;
	}

	public long getTarget() {
		return this.target;
	}

	@Override
	public boolean canSaveToStorage() {
		return false;
	}
}
