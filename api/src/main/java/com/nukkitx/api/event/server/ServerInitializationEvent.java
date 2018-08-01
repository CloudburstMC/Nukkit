package com.nukkitx.api.event.server;

import com.nukkitx.api.event.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerInitializationEvent implements Event {
    public static final ServerInitializationEvent INSTANCE = new ServerInitializationEvent();
}
