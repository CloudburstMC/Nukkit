package cn.nukkit.network.protocol.types;

import lombok.ToString;

@ToString
public class CommandOutputMessage {

    public boolean isInternal;
    public String messageId;
    public String[] parameters;
}
