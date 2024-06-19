package cn.nukkit.network.protocol.types;

import cn.nukkit.math.BlockVector3;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerBlockActionData {

    private PlayerActionType action;
    private BlockVector3 position;
    private int facing;
}
