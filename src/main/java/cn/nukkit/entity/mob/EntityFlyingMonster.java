package cn.nukkit.entity.mob;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.ai.EntityFlying;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;

public abstract class EntityFlyingMonster extends EntityFlying implements EntityMob {

    protected int[] minDamage;
    protected int[] maxDamage;

    protected int attackDelay = 0;

    protected boolean canAttack = true;

    public EntityFlyingMonster(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void setTarget(Entity target) {
        this.setTarget(target, true);
    }

    public void setTarget(Entity target, boolean attack) {
        super.setTarget(target);
        this.canAttack = attack;
    }

    public int getDamage() {
        return getDamage(null);
    }

    public int getDamage(Integer difficulty) {
        return MathHelper.rand(this.getMinDamage(difficulty), this.getMaxDamage(difficulty));
    }

    public int getMinDamage() {
        return getMinDamage(null);
    }

    public int getMinDamage(Integer difficulty) {
        if (difficulty == null || difficulty > 3 || difficulty < 0) {
            difficulty = Server.getInstance().getDifficulty();
        }
        return this.minDamage[difficulty];
    }

    public int getMaxDamage() {
        return getMaxDamage(null);
    }

    public int getMaxDamage(Integer difficulty) {
        if (difficulty == null || difficulty > 3 || difficulty < 0) {
            difficulty = Server.getInstance().getDifficulty();
        }
        return this.maxDamage[difficulty];
    }

    public void setDamage(int damage) {
        this.setDamage(damage, Server.getInstance().getDifficulty());
    }

    public void setDamage(int damage, int difficulty) {
        if (difficulty >= 1 && difficulty <= 3) {
            this.minDamage[difficulty] = damage;
            this.maxDamage[difficulty] = damage;
        }
    }

    public void setDamage(int[] damage) {
        if (damage.length < 4) return;

        if (minDamage == null || minDamage.length < 4) {
            minDamage = new int[]{0, 0, 0, 0};
        }

        if (maxDamage == null || maxDamage.length < 4) {
            maxDamage = new int[]{0, 0, 0, 0};
        }

        for (int i = 0; i < 4; i++) {
            this.minDamage[i] = damage[i];
            this.maxDamage[i] = damage[i];
        }
    }

    public void setMinDamage(int[] damage) {
        if (damage.length < 4) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            this.setDamage(Math.min(damage[i], this.getMaxDamage(i)), i);
        }
    }

    public void setMinDamage(int damage) {
        this.setDamage(damage, Server.getInstance().getDifficulty());
    }

    public void setMinDamage(int damage, int difficulty) {
        if (difficulty >= 1 && difficulty <= 3) {
            this.minDamage[difficulty] = Math.min(damage, this.getMaxDamage(difficulty));
        }
    }

    public void setMaxDamage(int[] damage) {
        if (damage.length < 4) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            this.setMaxDamage(Math.max(damage[i], this.getMinDamage(i)), i);
        }
    }

    public void setMaxDamage(int damage) {
        setMinDamage(damage, Server.getInstance().getDifficulty());
    }

    public void setMaxDamage(int damage, int difficulty) {
        if (difficulty >= 1 && difficulty <= 3) {
            this.maxDamage[difficulty] = Math.max(damage, this.getMinDamage(difficulty));
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.server.getDifficulty() < 1) {
            this.close();
            return false;
        }

        if (!this.isAlive()) {
            if (++this.deadTicks >= 23) {
                this.close();
                return false;
            }
            return true;
        }

        int tickDiff = currentTick - this.lastUpdate;
        this.lastUpdate = currentTick;
        this.entityBaseTick(tickDiff);

        Vector3 target = this.updateMove(tickDiff);
        if (target instanceof Entity) {
            if (target != this.followTarget || this.canAttack) {
                this.attackEntity((Entity) target);
            }
        } else if (
                target != null &&
                        (Math.pow(this.x - target.x, 2) + Math.pow(this.z - target.z, 2)) <= 1
                ) {
            this.moveTime = 0;
        }
        return true;
    }

    protected boolean entityBaseTick(int tickDiff) {
        //Timings.timerEntityBaseTick.startTiming();

        boolean hasUpdate = super.entityBaseTick(tickDiff);

        this.attackDelay += tickDiff;
        if (!this.hasEffect(Effect.WATER_BREATHING) && this.isInsideOfWater()) {
            hasUpdate = true;
            int airTicks = this.getDataPropertyInt(DATA_AIR) - tickDiff;
            if (airTicks <= -20) {
                airTicks = 0;
                this.attack(new EntityDamageEvent(this, EntityDamageEvent.CAUSE_DROWNING, 2));
            }
            this.setDataProperty(new ShortEntityData(DATA_AIR, airTicks));
        } else {
            this.setDataProperty(new ShortEntityData(DATA_AIR, 300));
        }

        //Timings.timerEntityBaseTick.stopTiming();
        return hasUpdate;
    }

}