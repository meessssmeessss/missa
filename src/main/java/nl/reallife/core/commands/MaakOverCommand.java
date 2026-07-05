package nl.reallife.core.commands;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MaakOverCommand implements CommandExecutor {

    private final RealLifeCore plugin;

    public MaakOverCommand(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length != 2) {
            player.sendMessage(ChatColor.GRAY + "Gebruik: /maakover <speler> <bedrag>");
            return true;
        }

        Player doel = Bukkit.getPlayer(args[0]);
        if (doel == null) {
            player.sendMessage(ChatColor.GRAY + "Die speler is niet online.");
            return true;
        }
        if (doel.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.GRAY + "Je kan geen geld naar jezelf overmaken.");
            return true;
        }

        int bedrag;
        try {
            bedrag = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.GRAY + "Ongeldig bedrag.");
            return true;
        }

        if (bedrag <= 0) {
            player.sendMessage(ChatColor.GRAY + "Ongeldig bedrag.");
            return true;
        }

        boolean succes = plugin.getEconomyManager().maakOver(player.getUniqueId(), doel.getUniqueId(), bedrag);
        if (succes) {
            player.sendMessage(ChatColor.GRAY + "Je hebt €" + bedrag + " overgemaakt naar " + doel.getName() + ".");
            doel.sendMessage(ChatColor.GRAY + "Je hebt €" + bedrag + " ontvangen van " + player.getName() + ".");
        } else {
            player.sendMessage(ChatColor.GRAY + "Je hebt niet genoeg saldo om dit bedrag over te maken.");
        }
        return true;
    }
}
