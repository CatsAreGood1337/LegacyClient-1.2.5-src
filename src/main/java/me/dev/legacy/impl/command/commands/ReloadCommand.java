package me.dev.legacy.impl.command.commands;

import me.dev.legacy.Legacy;
import me.dev.legacy.impl.command.Command;

public class ReloadCommand
        extends Command {
    public ReloadCommand() {
        super("reload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        Legacy.reload();
    }
}

