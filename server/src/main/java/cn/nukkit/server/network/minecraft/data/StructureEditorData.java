package cn.nukkit.server.network.minecraft.data;

import com.flowpowered.math.vector.Vector3i;
import lombok.Value;

@Value
public class StructureEditorData {
    private String unknown0;
    private String metadata;
    private Vector3i structureOffset;
    private Vector3i structureSize;
    private boolean includingEntities;
    private boolean ignoringBlocks;
    private boolean includingPlayers;
    private boolean showingAir;
    private StructureSettings structureSettings;
}
