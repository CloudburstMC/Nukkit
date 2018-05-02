package com.nukkitx.api.command.sender;

import com.nukkitx.api.Server;
import com.nukkitx.api.command.MessageRecipient;
import com.nukkitx.api.permission.Permissible;

import java.util.Locale;

public interface CommandSender extends Permissible, MessageRecipient {

    String getName();

    Server getServer();

    Locale getLocale();
}
