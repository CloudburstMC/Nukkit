package cn.nukkit.blockentity;

import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BlockEntityCommandFormWindow extends FormWindowCustom {

    private static String COMMAND_FORM_WINDOW_TITLE = "Command Block";

    private long id;

    private ElementInput commandInput;
    private ElementDropdown typeDropdown;
    private ElementDropdown redstoneDropdown;
    private ElementDropdown conditionalDropdown;

    public BlockEntityCommandFormWindow(Long id) {
        super(COMMAND_FORM_WINDOW_TITLE);
        buildForm();
        this.setId(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCommand(String command) {
        commandInput.setDefaultText(command);
    }

    public void setType(int type) {
        typeDropdown.setDefaultOptionIndex(type);
    }

    public void setRedstone(int redstone) {
        redstoneDropdown.setDefaultOptionIndex(redstone);
    }

    public void setConditional(int conditional) {
        conditionalDropdown.setDefaultOptionIndex(conditional);
    }

    private void buildForm() {
        this.commandInput = new ElementInput("Command:", "/say hi");
        this.addElement(commandInput);

        this.typeDropdown = new ElementDropdown("Block Type:", BlockEntityCommandType.getNames(), 0);
        this.addElement(typeDropdown);

        this.redstoneDropdown = new ElementDropdown("Redstone:", BlockEntityCommandRedstoneOption.getNames(), 0);
        this.addElement(redstoneDropdown);

        this.conditionalDropdown = new ElementDropdown("Conditional:", BlockEntityCommandConditionalOption.getNames(), 0);
        this.addElement(conditionalDropdown);
    }
}
