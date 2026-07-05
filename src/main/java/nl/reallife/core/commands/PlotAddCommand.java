package nl.reallife.core.commands;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import nl.reallife.core.models.Plot;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlotAddCommand implements CommandExecutor {

    private final RealLifeCore plugin;

    public PlotAddCommand(RealLifeCore plugin) {
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
            player.sendMessage(ChatColor.GRAY + "Gebruik: /plotadd <plotnaam> <speler>");
            return true;
        }

        Plot plot = plugin.getPlotManager().getPlot(args[0]);
        if (plot == null) {
            player.sendMessage(ChatColor.GRAY + "Dat plot bestaat niet.");
            return true;
        }

        if (!plot.getEigenaar().equals(player.getUniqueId()) && !player.isOp()) {
            player.sendMessage(ChatColor.GRAY + "Alleen de eigenaar van het plot kan spelers toevoegen.");
            return true;
        }

        OfflinePlayer doel = Bukkit.getOfflinePlayer(args[1]);
        plot.voegLidToe(doel.getUniqueId());
        plugin.getPlotManager().opslaan();

        player.sendMessage(ChatColor.GRAY + args[1] + " is toegevoegd aan plot '" + plot.getNaam() + "'.");
        return true;
    }
}
