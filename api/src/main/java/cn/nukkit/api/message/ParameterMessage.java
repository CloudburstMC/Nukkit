package cn.nukkit.api.message;

import java.util.Collection;

public interface ParameterMessage extends Message {

    Collection<String> getParameters();
}
