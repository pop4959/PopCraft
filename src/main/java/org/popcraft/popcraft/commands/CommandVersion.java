package org.popcraft.popcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class CommandVersion extends PopCraftMessageCommand {

    public CommandVersion() {
        super("version");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        PluginDescriptionFile descriptionFile = plugin.getDescription();
        sender.sendMessage(plugin.getMessage(name, descriptionFile.getName(), descriptionFile.getVersion(),
                descriptionFile.getDescription(), descriptionFile.getWebsite(),
                descriptionFile.getAuthors().toString().replace("[", "").replace("]", "")));
        return Result.SUCCESS;
    }

}
