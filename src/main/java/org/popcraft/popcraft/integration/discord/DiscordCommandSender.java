package org.popcraft.popcraft.integration.discord;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DiscordCommandSender implements CommandSender {

    private DiscordIntegration discordIntegration;
    protected final PermissibleBase perm = new PermissibleBase(this);
    private boolean op = false;

    public DiscordCommandSender(DiscordIntegration discordIntegration) {
        this.discordIntegration = discordIntegration;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        TextChannel messageChannel = this.discordIntegration.getJda()
                .getTextChannelById(this.discordIntegration.getChannelId());
        if (messageChannel != null) {
            messageChannel.sendMessage(ChatColor.stripColor(message)).queue();
        }
    }

    @Override
    public void sendMessage(@NotNull String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @NotNull
    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @NotNull
    @Override
    public String getName() {
        User lastSender = this.discordIntegration.getJda().getUserById(this.discordIntegration.getLastSender());
        return lastSender == null ? "DiscordCommandSender" : lastSender.getName();
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return this.perm.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return this.perm.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return "bukkit.broadcast.user".equals(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return this.hasPermission(perm.getName());
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return this.perm.addAttachment(plugin, name, value);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return this.perm.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return this.perm.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return this.perm.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        this.perm.recalculatePermissions();
    }

    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return this.op;
    }

    @Override
    public void setOp(boolean value) {
        this.op = value;
        perm.recalculatePermissions();
    }

}
