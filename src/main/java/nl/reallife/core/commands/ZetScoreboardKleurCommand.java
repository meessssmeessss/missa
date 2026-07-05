package nl.reallife.core.commands;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ZetScoreboardKleurCommand implements CommandExecutor {

    private final RealLifeCore plugin;

    public ZetScoreboardKleurCommand(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.GRAY + "Je hebt geen toestemming voor dit commando.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatColor.GRAY + "Gebruik: /zetscoreboardkleur <kleurcode> (bv. &8&l)");
            return true;
        }

        String kleurcode = args[0];
        plugin.getScoreboardManager().zetKleur(kleurcode);
        sender.sendMessage(ChatColor.GRAY + "De scoreboardkleur is aangepast naar: " + kleurcode);
        return true;
    }
}
