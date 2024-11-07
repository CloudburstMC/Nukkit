package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class NpcDialoguePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.NPC_DIALOGUE_PACKET;

    public static final int ACTION_OPEN = 0;
    public static final int ACTION_CLOSE = 1;

    public long entityId;
    public int actionType;
    public String dialogue;
    public String sceneName;
    public String npcName;
    public String actionJson;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {
        this.reset();
        this.putLLong(this.entityId);
        this.putVarInt(this.actionType);
        this.putString(this.dialogue);
        this.putString(this.sceneName);
        this.putString(this.npcName);
        this.putString(this.actionJson);
    }

    @Override
    public void decode() {
        // Noop
    }
}
