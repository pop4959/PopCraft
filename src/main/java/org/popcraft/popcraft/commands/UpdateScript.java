package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.popcraft.popcraft.PopCommand;
import org.popcraft.popcraft.utils.Message;

@PopCommand("update")
@Slf4j
public class UpdateScript implements CommandExecutor {

    private final FileConfiguration config;

    @Inject
    public UpdateScript(final FileConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        try {
            final int exitCode = new ProcessBuilder(this.config.getString("jonslogger.file")).start().waitFor();
            if (exitCode == 0) {
                Message.normal(commandSender, "Reloaded Successfully.");
            } else {
                Message.error(commandSender, "Reload returned a non-zero exit code.");
            }

        } catch (Exception e) {
            LOGGER.error("Failed to reload!", e);
            Message.error(commandSender, "Failed to reload!");
        }
        return true;
    }

}
