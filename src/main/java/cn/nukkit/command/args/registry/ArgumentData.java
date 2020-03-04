package cn.nukkit.command.args.registry;

import cn.nukkit.command.data.CommandParamType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArgumentData {
    private String enumName;
    private CommandParamType argumentType;
}
