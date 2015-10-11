package cn.nukkit.event.entity;

import cn.nukkit.entity.Projectile;
import cn.nukkit.event.Cancellable;

public class ProjectileLaunchEvent extends EntityEvent implements Cancellable{¨

	private static final HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlers() {
    return handlers;
  }
	
	/**
	 * @param Projectile entity
	 */
	public ProjectileHitEvent(Projectile entity){
		this.entity = entity;
	}
	/**
	 * @return Projectile
	 */
	public Entity getEntity(){
		return this->entity;
	}
}
