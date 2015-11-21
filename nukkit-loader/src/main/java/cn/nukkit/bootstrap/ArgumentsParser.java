package cn.nukkit.bootstrap;

import java.util.Objects;

public class ArgumentsParser{
    private Options options = new Options();

    private int index;

    private String[] arguments;

    public ArgumentsParser(String[] args){
        this.arguments = args;
        this.rewind();
    }

    public void parseArguments(){
        this.rewind();
        while (this.hasNext()){
            if(this.handleOption(this.currentValue(), this.peekNextValue())){
                this.next();
            }
            this.next();
        }
    }

    public Options getOptions(){
        return options;
    }

    private boolean handleOption(String key, String value){
        switch (key) {
            case "--disable-ansi":
                this.getOptions().ANSI = false;
                break;
            case "--enable-ansi":
                this.getOptions().ANSI = true;
                break;
            case "--data-path":
                this.getOptions().DATA_PATH = Objects.requireNonNull(value);
                return true;
            case "--plugins-path":
                this.getOptions().PLUGIN_PATH = Objects.requireNonNull(value);
                return true;
            case "--ui":
            case "--show-user-interface":
                this.getOptions().SHOW_USER_INTERFACE = true;
                break;
            case "--no-ui":
            case "--hide-user-interface":
                this.getOptions().SHOW_USER_INTERFACE = false;
                break;
        }
        return false;
    }

    private void next(){
        ++this.index;
    }

    private String currentValue(){
        return this.arguments[this.index];
    }

    private String peekNextValue(){
        return this.index + 1 < this.arguments.length ? this.arguments[this.index + 1].isEmpty() ? null : this.arguments[this.index + 1] : null;
    }

    private boolean hasNext(){
        return this.index < this.arguments.length;
    }

    private void rewind(){
        this.index = 0;
    }
}
