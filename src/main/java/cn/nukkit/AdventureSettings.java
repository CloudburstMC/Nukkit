package cn.nukkit;

import cn.nukkit.network.protocol.AdventureSettingsPacket;

/**
 * Nukkit Project
 * Author: MagicDroidX
 */
public class AdventureSettings implements Cloneable {

    private boolean canDestroyBlock = true;

    private boolean autoJump = true;

    private boolean canFly = false;

    private boolean isFlying = false;

    private Player player;

    private AdventureSettings() {
    }

    public AdventureSettings clone(Player newPlayer) {
        try {
            AdventureSettings settings = (AdventureSettings) super.clone();
            settings.player = newPlayer;
            return settings;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public void setCanDestroyBlock(boolean canDestroyBlock) {
        this.canDestroyBlock = canDestroyBlock;
    }

    public void setAutoJump(boolean autoJump) {
        this.autoJump = autoJump;
    }

    public void setCanFly(boolean canFly) {
        this.canFly = canFly;
    }

    public void setFlying(boolean flying) {
        isFlying = flying;
    }

    public boolean canDestroyBlock() {
        return canDestroyBlock;
    }

    public boolean isAutoJumpEnabled() {
        return autoJump;
    }

    public boolean canFly() {
        return canFly;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public void update() {
        /*
         bit mask | flag name
		0x00000001 world_immutable
		0x00000002 no_pvp
		0x00000004 no_pvm
		0x00000008 no_pve
		0x00000010 static_time
		0x00000020 nametags_visible
		0x00000040 auto_jump
		0x00000080 can_switch_between_walking_and_flying
		0x00000100 is_flying
		*/
        int flags = 0;

        flags |= 0x02; // No PvP (Remove hit markers client-side).
        flags |= 0x04; // No PvM (Remove hit markers client-side).
        flags |= 0x08; // No PvE (Remove hit markers client-side).

        if (this.canDestroyBlock()) {
            flags |= 0x01;
        }

		/*if(!nametags){
            flags |= 0x20; //Show Nametags
		}*/

        if (this.isAutoJumpEnabled()) {
            flags |= 0x40;
        }

        if (this.canFly()) {
            flags |= 0x80;
        }

        if (this.isFlying()) {
            flags |= 0x100;
        }

        AdventureSettingsPacket pk = new AdventureSettingsPacket();
        pk.flags = flags;
        pk.userPermission = 0x2;
        pk.globalPermission = 0x2;
        player.dataPacket(pk);

        player.resetInAirTicks();
    }

    public static class Builder {
        private final AdventureSettings settings = new AdventureSettings();

        public Builder(Player player) {
            if (player == null) {
                throw new IllegalArgumentException("Player can not be null.");
            }

            settings.player = player;
        }

        public Builder canFly(boolean can) {
            settings.canFly = can;
            return this;
        }

        public Builder isFlying(boolean flying) {
            settings.isFlying = flying;
            return this;
        }

        public Builder canDestroyBlock(boolean can) {
            settings.canDestroyBlock = can;
            return this;
        }

        public Builder autoJump(boolean autoJump) {
            settings.autoJump = autoJump;
            return this;
        }

        public AdventureSettings build() {
            return this.settings;
        }
    }
}
