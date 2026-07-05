package nl.reallife.core.commands;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import nl.reallife.core.models.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZetPrefixCommand implements CommandExecutor {

    private final RealLifeCore plugin;

    public ZetPrefixCommand(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.GRAY + "Gebruik: /zetprefix <prefix>");
            return true;
        }

        String prefix = String.join(" ", args);
        if (prefix.length() > 16) {
            player.sendMessage(ChatColor.GRAY + "Je prefix mag maximaal 16 tekens lang zijn.");
            return true;
        }

        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
        data.setPrefix(prefix);
        player.sendMessage(ChatColor.GRAY + "Je prefix is aangepast naar: " + prefix);
        return true;
    }
}
