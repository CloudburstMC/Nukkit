package cn.nukkit.command.args.registry;

import com.nukkitx.protocol.bedrock.data.CommandParamData;

public class EnumArgumentData extends ArgumentData {

    public EnumArgumentData(String enumName, CommandParamData.Type argumentType) {
        super(enumName, argumentType);
    }
}
