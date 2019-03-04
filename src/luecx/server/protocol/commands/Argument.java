package luecx.server.protocol.commands;

import java.util.Arrays;

/**
 * Created by finne on 27.01.2018.
 */
public class Argument {

    private final String name;
    private final String description;
    private String[] aliases = new String[256];

    private boolean required;
    private String[] values;

    public Argument(String name,boolean required, String... aliases) {
        this.name = name.toLowerCase();
        this.aliases = aliases;
        this.description = "N/A";
        this.required = required;
    }

    public Argument(String name, String description, boolean required, String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
        this.required = required;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public String[] getValues() {
        return values;
    }

    public String getValue() {
        if(values != null) return values[0];
        return null;
    }

    public void setValues(String... values) {
        this.values = values;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasAlias(String s) {
        if(s.equals(name)) return true;
        for(String string:aliases){
            if(string.toLowerCase().equals(s.toLowerCase())) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Argument{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", aliases=" + Arrays.toString(aliases) +
                ", required=" + required +
                ", values=" + Arrays.toString(values) +
                '}';
    }
}
