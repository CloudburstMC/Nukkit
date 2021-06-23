package cn.nukkit.network.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * @author IWareQ
 */
public class AnimateEntityPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ANIMATE_ENTITY_PACKET;

    public String animation;
    public String nextState;
    public String stopExpression;
    public String controller;
    public float blendOutTime;
    public List<Long> entityRuntimeIds = new ArrayList<>();

    @Override
    public void decode() {
    	this.animation = this.getString();
		this.nextState = this.getString();
		this.stopExpression = this.getString();
		this.controller = this.getString();
		this.blendOutTime = this.getLFloat();
		for (int i = 0, len = (int) this.getUnsignedVarInt(); i < len; i++) {
			this.entityRuntimeIds.add(this.getEntityRuntimeId());
		}
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.animation);
		this.putString(this.nextState);
		this.putString(this.stopExpression);
		this.putString(this.controller);
		this.putLFloat(this.blendOutTime);
		this.putUnsignedVarInt(this.entityRuntimeIds.size());
		for (long entityRuntimeId : this.entityRuntimeIds){
			this.putEntityRuntimeId(entityRuntimeId);
		}
    }
    
    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
