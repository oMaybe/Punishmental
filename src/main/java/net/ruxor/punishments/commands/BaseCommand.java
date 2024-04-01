package net.ruxor.punishments.commands;

import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.data.Profile;
import net.ruxor.punishments.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class BaseCommand extends Command {

    private String name;
    private String permission;

    public BaseCommand(String name) {
        super(name);
        this.permission = "";
    }

    public BaseCommand(String name, String permission) {
        super(name);
        this.permission = permission;
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.NOT_A_PLAYER);
            return true;
        }

        Player player = (Player) sender;
        Optional<Profile> profile = Punishmental.getInstance().getProfileHandler().getProfile(player.getUniqueId());
        if (!profile.isPresent()){
            player.sendMessage(Messages.INVALID_PROFILE);
            return true;
        }

        if (!permission.isEmpty() && !player.hasPermission(permission)){
            player.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        execute(profile.get(), args);
        return true;
    }

    protected final void setUsage(String... uses) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < uses.length; i++) {
            String use = uses[i];
            builder.append(use);
            if (i + 1 != uses.length)
                builder.append(System.lineSeparator());
        }
        setUsage(builder.toString());
    }

    protected final void setAliases(String... aliases) {
        if (aliases.length > 0) {
            setAliases(aliases.length == 1 ? Collections.singletonList(aliases[0]) : Arrays.asList(aliases));
        }
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, args);
    }

    protected abstract void execute(Profile profile, String[] args);
}
