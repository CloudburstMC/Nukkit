package cn.nukkit.server.network.minecraft.data;

import lombok.experimental.Value;

import java.util.List;

@Value
public class CommandOutputMessage {
    private boolean internal;
    private String messageId;
    private List<String> parameters;
}
