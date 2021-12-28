package main.server.comands;

import java.io.Serializable;
import java.util.Arrays;

public enum CommandType implements Serializable {
    AUTH("auth"),
    MKDIR("mkdir"),
    RENAME("rename"),
    TOUCH("touch"),
    DELETE("delete"),
    DOWNLAND("downland"),
    UPLAND("upland"),
    GET_FILES("get_files");

    private final String command;

    CommandType(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static CommandType byCommand(String command) {
        return Arrays.stream(values())
                .filter(cmd -> cmd.getCommand().equals(command))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Command not found"));
    }
}
