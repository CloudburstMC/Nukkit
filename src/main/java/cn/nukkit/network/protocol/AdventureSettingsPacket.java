package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class AdventureSettingsPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.ADVENTURE_SETTINGS_PACKET;

    public boolean worldImmutable;
	public boolean noPvp;
	public boolean noPvm;
	public boolean noMvp;

    public boolean autoJump;
	public boolean allowFlight;
	public boolean noClip;
	public boolean isFlying;

    /*
	 bit mask | flag name
	0x00000001 world_immutable
	0x00000002 no_pvp
	0x00000004 no_pvm
	0x00000008 no_mvp
	0x00000010 ?
	0x00000020 auto_jump
	0x00000040 allow_fly
	0x00000080 noclip
	0x00000100 ?
	0x00000200 is_flying
	*/

    public int flags = 0;
    public int userPermission;

    @Override
    public void decode() {
        this.flags = (int) this.getUnsignedVarInt();
        this.userPermission = (int) this.getUnsignedVarInt();
        this.worldImmutable = (this.flags & 1) != 0;
        this.noPvp = (this.flags & (1 << 1)) != 0;
        this.noPvm = (this.flags & (1 << 2)) != 0;
        this.noMvp = (this.flags & (1 << 3)) != 0;

        this.autoJump = (this.flags & (1 << 5)) != 0;
        this.allowFlight = (this.flags & (1 << 6)) != 0;
        this.noClip = (this.flags & (1 << 7)) != 0;
        this.isFlying = (this.flags & (1 << 9)) != 0;
    }

    @Override
    public void encode() {
        reset();
        this.flags |= this.worldImmutable ? 1 : 0;
        this.flags |= (this.noPvp ? 1 : 0) << 1;
        this.flags |= (this.noPvm ? 1 : 0) << 2;
        this.flags |= (this.noMvp ? 1 : 0) << 3;
        
        this.flags |= (this.autoJump ? 1 : 0) << 5;
        this.flags |= (this.allowFlight ? 1 : 0) << 6;
        this.flags |= (this.noClip ? 1 : 0) << 7;
        this.flags |= (this.isFlying ? 1 : 0) << 9;
        putUnsignedVarInt(flags);
        putUnsignedVarInt(userPermission);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
