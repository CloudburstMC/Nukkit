package cn.nukkit.command.args.registry;

import cn.nukkit.command.data.CommandParamType;

public class EnumArgumentData extends ArgumentData {

    public EnumArgumentData(String enumName, CommandParamType argumentType) {
        super(enumName, argumentType);
    }
}
