package cn.nukkit.network.protocol.types;

import cn.nukkit.network.protocol.types.StructureSettings;
import lombok.ToString;

@ToString
public class StructureEditorData {

    public String structureName;
    public String structureDataField;
    public boolean includePlayers;
    public boolean showBoundingBox;
    public int structureBlockType;
    public StructureSettings structureSettings;
    public int structureRedstoneSaveMove;

    public enum Type {

        DATA,
        SAVE,
        LOAD,
        CORNER,
        INVALID,
        EXPORT
    }
}
