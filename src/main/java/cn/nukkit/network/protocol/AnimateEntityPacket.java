package cn.nukkit.network.protocol;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import lombok.ToString;

/**
 * Used to trigger an entity animation on the specified runtime IDs to the client that receives it.
 * Source: <a href="https://github.com/CloudburstMC/Protocol">...</a>
 * Default values from <a href="https://wiki.vg/Bedrock_Protocol">...</a>
 */
@ToString
public class AnimateEntityPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ANIMATE_ENTITY_PACKET;

    /**
     * Name of the to play on the entities specified in {@link #runtimeEntityIds}
     */
    public String animation;

    /**
     * The entity state to move to when the animation has finished playing.
     */
    public String nextState = "default";

    /**
     * Expression to check if the animation needs to stop.
     */
    public String stopExpression = "query.any_animation_finished";

    /**
     * The molang stop expression version
     */
    public int stopExpressionVersion;

    /**
     * Name of the animation controller to use.
     */
    public String controller = "__runtime_controller";

    /**
     * Time taken to blend out of the specified animation.
     */
    public float blendOutTime = 0f;

    /**
     * Entity runtime IDs to run the animation on when sent to the client.
     */
    public final LongList runtimeEntityIds = new LongArrayList();

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();

        this.putString(this.animation);
        this.putString(this.nextState);
        this.putString(this.stopExpression);

        this.putLInt(this.stopExpressionVersion);

        this.putString(this.controller);
        this.putLFloat(this.blendOutTime);

        this.putUnsignedVarInt(this.runtimeEntityIds.size());
        for (long runtimeId : this.runtimeEntityIds) {
            this.putUnsignedVarLong(runtimeId);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
