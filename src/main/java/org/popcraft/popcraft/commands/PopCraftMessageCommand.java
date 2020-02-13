package org.popcraft.popcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public abstract class PopCraftMessageCommand extends PopCraftCommand {

    public PopCraftMessageCommand(String name) {
        super(name);
    }

    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(plugin.getMessage(name));
        return Result.SUCCESS;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

}
