package cn.nukkit.command.data;

import com.nukkitx.protocol.bedrock.data.CommandEnumData;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CreeperFace
 */
@ToString
public class CommandEnum {

    private final String name;
    private final List<String> values;

    public CommandEnum(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public int hashCode() {
        return name.hashCode();
    }

    protected CommandEnumData toNetwork() {
        String[] aliases;
        if (values.size() > 0) {
            List<String> aliasList = new ArrayList<>(values);
            aliasList.add(this.name);
            aliases = aliasList.toArray(new String[0]);
        } else {
            aliases = new String[]{this.name};
        }
        return new CommandEnumData(this.name + "Aliases", aliases, false);
    }
}
