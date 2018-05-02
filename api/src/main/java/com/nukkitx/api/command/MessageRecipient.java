package com.nukkitx.api.command;

import com.nukkitx.api.message.Message;

public interface MessageRecipient {

    void sendMessage(String text);

    void sendMessage(Message text);
}
