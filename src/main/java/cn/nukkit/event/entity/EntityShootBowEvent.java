package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.Living;
import cn.nukkit.entity.Projectile;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

/**
 * author: Box
 * Nukkit Project
 */
public class EntityShootBowEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Item bow;

    private Projectile projectile;


        this.entity = shooter;
        this.bow = bow;
        this.projectile = projectile;
        this.force = force;
    }

    }


        return this.bow;
    }


        return this.projectile;
    }

                this.projectile.kill();
                this.projectile.close();
            }
        }
    }

        return this.force;
    }

        this.force = force;
    }
}
