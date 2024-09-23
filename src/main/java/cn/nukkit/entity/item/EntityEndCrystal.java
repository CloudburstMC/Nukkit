package cn.nukkit.entity.item;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityEndCrystal extends Entity implements EntityExplosive {

    public static final int NETWORK_ID = 71;

    private boolean detonated = false;
    private String nameTag;

    @Override
    public float getLength() {
        return 2f;
    }

    @Override
    public float getHeight() {
        return 2f;
    }

    @Override
    public float getWidth() {
        return 2f;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityEndCrystal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("ShowBottom")) {
            this.setShowBase(this.namedTag.getBoolean("ShowBottom"));
        }

        this.fireProof = true;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putBoolean("ShowBottom", this.showBase());
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.closed) {
            return false;
        }

        if (source.getCause() == EntityDamageEvent.DamageCause.FIRE || source.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || source.getCause() == EntityDamageEvent.DamageCause.LAVA ||
                (source.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && source.getFinalDamage() < 0.8)) {
            source.setCancelled(true);
        }

        this.getServer().getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return false;
        }

        this.setLastDamageCause(source);

        this.explode();

        return true;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return true;
    }

    public boolean showBase() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_SHOWBASE);
    }

    public void setShowBase(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHOWBASE, value);
    }

    @Override
    public void explode() {
        this.close();
        if (!this.detonated && this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
            EntityExplosionPrimeEvent ev = new EntityExplosionPrimeEvent(this, 6);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return;
            }

            this.detonated = true;

            Explosion explosion = new Explosion(this, (float) ev.getForce(), this);

            int floor = this.getFloorY();
            int down = this.level.getBlockIdAt(this.chunk, this.getFloorX(), floor - 1, this.getFloorZ());
            if (down == Block.BEDROCK || down == Block.OBSIDIAN) {
                explosion.setMinHeight(floor);
            }

            if (ev.isBlockBreaking()) {
                explosion.explodeA();
            }
            explosion.explodeB();
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "End Crystal";
    }

    @Override
    public void setNameTag(String name) {
        this.nameTag = name;
        if (this.namedTag.contains("CustomNameVisible") || this.namedTag.contains("CustomNameAlwaysVisible")) { // Hack: Vanilla: Disable client side name tag while keeping custom name in nbt
            this.setDataProperty(new StringEntityData(DATA_NAMETAG, name));
        }
    }

    @Override
    public boolean hasCustomName() {
        return this.nameTag != null;
    }

    @Override
    public String getNameTag() {
        return this.nameTag == null ? "" : this.nameTag;
    }

    @Override // Minimal
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (!this.isAlive() || this.y < (this.getLevel().getMinBlockY() - 16)) {
            this.close();
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) {
            return false;
        }

        this.minimalEntityTick(currentTick, tickDiff);
        return false;
    }

    @Override
    public boolean ignoredAsSaveReason() {
        return true;
    }
}
