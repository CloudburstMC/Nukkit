package cn.nukkit.command.data;

import java.util.ArrayList;

public class CommandEnum {

    //Name of the Command Enum
    protected String name;
    public String getName(){
        return this.name;
    }

    //Indexes for enum values
    protected ArrayList<Integer> enumIndexes;
    public ArrayList<Integer> getEnumIndexes() {
        return this.enumIndexes;
    }

    public CommandEnum (String name, ArrayList<Integer> enumIndexes){
        this.name = name;
        this.enumIndexes = enumIndexes;
    }

    //MC does not support multiple enums with same name
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CommandEnum)) return false;
        CommandEnum commandEnum = (CommandEnum) obj;
        if (commandEnum == null) return false;
        return name.equals(commandEnum.getName());
    }

}
