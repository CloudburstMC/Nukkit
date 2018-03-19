package cn.nukkit.api.command;

import cn.nukkit.api.message.Message;

public interface MessageRecipient {

    void sendMessage(String text);

    void sendMessage(Message text);
}
