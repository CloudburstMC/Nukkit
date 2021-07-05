package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import java.util.ArrayList;
import java.util.List;

/**
 * @author IWareQ
 */
 @PowerNukkitOnly
@Since("1.5.1.0-PN")
public class AnimateEntityPacket extends DataPacket {

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public static final byte NETWORK_ID = ProtocolInfo.ANIMATE_ENTITY_PACKET;

    private String animation;
    private String nextState;
    private String stopExpression;
    private String controller;
    private float blendOutTime;
    private List<Long> entityRuntimeIds = new ArrayList<>();

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
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public void setAnimation(String animation) {
        this.animation = animation;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public String getAnimation() {
        return this.animation;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public void setNextState(String nextState) {
        this.nextState = nextState;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public String getNextState() {
        return this.nextState;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public void setStopExpression(String stopExpression) {
        this.stopExpression = stopExpression;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public String getStopExpression() {
        return this.stopExpression;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public void setController(String controller) {
        this.controller = controller;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public String getController() {
        return this.controller;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public void setBlendOutTime(float blendOutTime) {
        this.blendOutTime = blendOutTime;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public float getBlendOutTime() {
        return this.blendOutTime;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public void setEntityRuntimeIds(List<Long> entityRuntimeIds) {
        this.entityRuntimeIds = entityRuntimeIds;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public List<Long> getEntityRuntimeIds() {
        return this.entityRuntimeIds;
    }
}
