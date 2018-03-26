package cn.nukkit.api.util.data;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum DeviceOS {
    @JsonEnumDefaultValue
    UNKNOWN,
    ANDROID,
    IOS,
    OSX,
    FIREOS,
    GEARVR,
    HOLOLENS,
    WIN10,
    WIN32,
    DEDICATED,
    ORBIS,
    NX
}
