package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class SetSpawnPositionPacket extends DataPacket {

    public static final int TYPE_PLAYER_SPAWN = 0;
    public static final int TYPE_WORLD_SPAWN = 1;

    public int spawnType;
    public BlockVector3 position;
    public BlockVector3 position2;
    public int dimensionId = 0;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_SPAWN_POSITION_PACKET;
    }

    @Override
    public void decode() {
    	this.spawnType = this.getVarInt();
		this.position = this.getBlockVector3();
		this.dimensionId = this.getVarInt();
		this.position2 = this.getBlockVector3();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.spawnType);
		this.putBlockVector3(this.position);
		this.putVarInt(this.dimensionId);
		this.putBlockVector3(this.position2);
    }
}
