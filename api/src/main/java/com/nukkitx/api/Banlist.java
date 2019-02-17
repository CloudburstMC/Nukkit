package com.nukkitx.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.Date;
import java.util.UUID;

@ParametersAreNullableByDefault
public interface Banlist {

    boolean pardon(@Nonnull UUID uuid);

    boolean pardon(@Nonnull Player player);

    void ban(@Nonnull Player player, Date expireDate, String reason, String source);

    void ban(@Nonnull UUID uuid, Date expireDate, String reason, String source);
}
