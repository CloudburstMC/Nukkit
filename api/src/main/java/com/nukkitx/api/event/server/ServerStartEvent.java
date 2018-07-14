package com.nukkitx.api.event.server;

import com.nukkitx.api.event.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerStartEvent implements Event {
    public static final ServerStartEvent INSTANCE = new ServerStartEvent();
}
