package cn.nukkit.command.args.registry;

import cn.nukkit.command.args.*;

import static cn.nukkit.network.protocol.AvailableCommandsPacket.*;

import static cn.nukkit.command.data.CommandParamType.*;
import cn.nukkit.network.protocol.AvailableCommandsPacket;
import com.mojang.brigadier.arguments.*;

import java.util.HashMap;
import java.util.Map;

public class ArgumentRegistry {
    private final Map<Class<? extends ArgumentType>, ArgumentData> arguments = new HashMap<>();

    public ArgumentRegistry() {
        // Brigadier
        register(StringArgumentType.class, new ArgumentData("string", STRING));
        register(LongArgumentType.class, new ArgumentData("long", INT)); // TODO: int??
        register(FloatArgumentType.class, new ArgumentData("float", FLOAT));
        register(IntegerArgumentType.class, new ArgumentData("int", INT));
        register(DoubleArgumentType.class, new ArgumentData("double", FLOAT));
        register(BoolArgumentType.class, new ArgumentData("bool", STRING)); // TODO: theres no bool type? really?

        // Nukkit
        register(EffectArgument.class, new EnumArgumentData("Effect", STRING));
        register(CommandArgument.class, new EnumArgumentData("CommandName", STRING));
        register(ParticleArgument.class, new EnumArgumentData("Particle", STRING));
        register(ItemArgument.class, new EnumArgumentData("Item", STRING));
        register(PlayerArgument.class, new ArgumentData("target", TARGET));
    }

    public void register(Class<? extends ArgumentType> argumentClass, ArgumentData data) {
        arguments.put(argumentClass, data);
    }

    public ArgumentData getArgumentData(Class<? extends ArgumentType> argumentClass) {
        return arguments.get(argumentClass);
    }
}
