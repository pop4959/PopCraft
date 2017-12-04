package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import org.bukkit.Server;
import org.popcraft.popcraft.newCode.PopCommand;

import static java.lang.String.format;
import static org.bukkit.ChatColor.*;

@PopCommand("supersay")
public class SuperSay extends FakeChatCommand {

    @Inject
    public SuperSay(final Server server) {
        super(server);
    }

    @Override
    public String getHeader() {
        return format("%s%sServer%s %sConsole%s:", LIGHT_PURPLE, BOLD, RESET, LIGHT_PURPLE, RESET);
    }
}