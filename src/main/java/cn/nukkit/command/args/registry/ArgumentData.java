package cn.nukkit.command.args.registry;

import com.nukkitx.protocol.bedrock.data.CommandParamData;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArgumentData {
    private String enumName;
    private CommandParamData.Type argumentType;
}
