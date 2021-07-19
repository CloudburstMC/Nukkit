package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.StructureEditorData;
import cn.nukkit.network.protocol.types.StructureSettings;
import cn.nukkit.math.BlockVector3;
import lombok.ToString;

@ToString
public class StructureBlockUpdatePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.STRUCTURE_BLOCK_UPDATE_PACKET;

    public int x;
    public int y;
    public int z;
    public StructureEditorData structureEditorData;
    public boolean isPowered;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        BlockVector3 blockPosition = this.getBlockVector3();
        this.x = blockPosition.getX();
        this.y = blockPosition.getY();
        this.z = blockPosition.getZ();
        this.structureEditorData = this.getStructureEditorData();
        this.isPowered = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBlockVector3(this.x, this.y, this.z);
        this.putStructureEditorData(this.structureEditorData);
        this.putBoolean(this.isPowered);
    }

    private StructureSettings getStructureSettings() {
        StructureSettings structureSettings = new StructureSettings();
        structureSettings.paletteName = this.getString();
        structureSettings.ignoreEntities = this.getBoolean();
        structureSettings.ignoreBlocks = this.getBoolean();

        BlockVector3 structureSize = this.getBlockVector3();
        structureSettings.structureSizeX = structureSize.getX();
        structureSettings.structureSizeY = structureSize.getY();
        structureSettings.structureSizeZ = structureSize.getZ();

        BlockVector3 structureOffset = this.getBlockVector3();
        structureSettings.structureOffsetX = structureOffset.getX();
        structureSettings.structureOffsetY = structureOffset.getY();
        structureSettings.structureOffsetZ = structureOffset.getZ();

        structureSettings.lastTouchedByPlayerID = this.getEntityUniqueId();
        structureSettings.rotation = (byte) this.getByte();
        structureSettings.mirror = (byte) this.getByte();
        structureSettings.integrityValue = this.getFloat();
        structureSettings.integritySeed = this.getInt();
        structureSettings.pivot = this.getVector3f();
        return structureSettings;
    }

    private void putStructureSettings(StructureSettings structureSettings) {
        this.putString(structureSettings.paletteName);
        this.putBoolean(structureSettings.ignoreEntities);
        this.putBoolean(structureSettings.ignoreBlocks);

        this.putBlockVector3(structureSettings.structureSizeX, structureSettings.structureSizeY, structureSettings.structureSizeZ);
        this.putBlockVector3(structureSettings.structureOffsetX, structureSettings.structureOffsetY, structureSettings.structureOffsetZ);

        this.putEntityUniqueId(structureSettings.lastTouchedByPlayerID);
        this.putByte(structureSettings.rotation);
        this.putByte(structureSettings.mirror);
        this.putFloat(structureSettings.integrityValue);
        this.putInt(structureSettings.integritySeed);
        this.putVector3f(structureSettings.pivot);
    }

    private StructureEditorData getStructureEditorData() {
        StructureEditorData structureEditorData = new StructureEditorData();
        structureEditorData.structureName = this.getString();
        structureEditorData.structureDataField = this.getString();
        structureEditorData.includePlayers = this.getBoolean();
        structureEditorData.showBoundingBox = this.getBoolean();
        structureEditorData.structureBlockType = this.getVarInt();
        structureEditorData.structureSettings = this.getStructureSettings();
        structureEditorData.structureRedstoneSaveMove = this.getVarInt();
        return structureEditorData;
    }

    private void putStructureEditorData(StructureEditorData structureEditorData) {
        this.putString(structureEditorData.structureName);
        this.putString(structureEditorData.structureDataField);
        this.putBoolean(structureEditorData.includePlayers);
        this.putBoolean(structureEditorData.showBoundingBox);
        this.putVarInt(structureEditorData.structureBlockType);
        this.putStructureSettings(structureEditorData.structureSettings);
        this.putVarInt(structureEditorData.structureRedstoneSaveMove);
    }
}
