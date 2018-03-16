package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerAnimationEvent;

/**
 * @author Nukkit Project Team
 */
public class AnimatePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ANIMATE_PACKET;

    public long eid;
    public int action;
    public float unknown;

    @Override
    public void decode() {
        this.action = this.getVarInt();
        this.eid = getEntityRuntimeId();
        if ((this.action & 0x80) != 0) {
            this.unknown = this.getLFloat();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.action);
        this.putEntityRuntimeId(this.eid);
        if ((this.action & 0x80) != 0) {
            this.putLFloat(this.unknown);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void handle(Player player) {
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        PlayerAnimationEvent animationEvent = new PlayerAnimationEvent(player, this.action);
        player.server.getPluginManager().callEvent(animationEvent);
        if (animationEvent.isCancelled()) {
            return;
        }

        AnimatePacket animatePacket = new AnimatePacket();
        animatePacket.eid = player.getId();
        animatePacket.action = animationEvent.getAnimationType();
        Server.broadcastPacket(player.getViewers().values(), animatePacket);
    }
}
