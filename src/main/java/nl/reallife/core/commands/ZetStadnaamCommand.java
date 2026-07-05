package nl.reallife.core.commands;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ZetStadnaamCommand implements CommandExecutor {

    private final RealLifeCore plugin;

    public ZetStadnaamCommand(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.GRAY + "Je hebt geen toestemming voor dit commando.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.GRAY + "Gebruik: /zetstadnaam <naam>");
            return true;
        }

        String naam = String.join(" ", args);
        plugin.getScoreboardManager().zetStadnaam(naam);
        sender.sendMessage(ChatColor.GRAY + "De stadsnaam is aangepast naar: " + naam);
        return true;
    }
}
