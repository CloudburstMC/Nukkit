package com.nukkitx.server.network.minecraft.data;

import lombok.Value;

import java.util.List;

@Value
public class CommandOutputMessage {
    private boolean internal;
    private String messageId;
    private List<String> parameters;
}
