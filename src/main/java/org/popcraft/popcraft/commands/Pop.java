package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import org.bukkit.Server;
import org.popcraft.popcraft.newCode.PopCommand;

import static java.lang.String.format;
import static org.bukkit.ChatColor.*;

@PopCommand("pop")
public class Pop extends FakeChatCommand {

    @Inject
    public Pop(final Server server) {
        super(server);
    }

    @Override
    public String getHeader() {
        return format("%s%sOwner%s %spop4959%s:", DARK_GREEN, BOLD, RESET, DARK_GREEN, RESET);
    }
}