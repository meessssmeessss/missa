package nl.reallife.core.commands;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SportZoneCommand implements CommandExecutor {

    private final RealLifeCore plugin;

    public SportZoneCommand(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }
        Player player = (Player) sender;

        boolean succes = plugin.getFitnessManager().maakZoneVanSelectie(player);
        if (succes) {
            player.sendMessage(ChatColor.GRAY + "De sportzone is ingesteld op je WorldEdit-selectie.");
        } else {
            player.sendMessage(ChatColor.GRAY + "Maak eerst een WorldEdit-selectie (houweel) van het gebied.");
        }
        return true;
    }
}
