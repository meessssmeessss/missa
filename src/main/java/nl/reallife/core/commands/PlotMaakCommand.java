package nl.reallife.core.commands;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import nl.reallife.core.models.Plot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlotMaakCommand implements CommandExecutor {

    private final RealLifeCore plugin;

    public PlotMaakCommand(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage(ChatColor.GRAY + "Je hebt geen toestemming om een plot te maken.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.GRAY + "Gebruik: /plotmaak <naam>");
            return true;
        }

        String naam = args[0];
        if (plugin.getPlotManager().getPlot(naam) != null) {
            player.sendMessage(ChatColor.GRAY + "Er bestaat al een plot met die naam.");
            return true;
        }

        Plot plot = plugin.getPlotManager().maakVanSelectie(player, naam);
        if (plot == null) {
            player.sendMessage(ChatColor.GRAY + "Maak eerst een WorldEdit-selectie (houweel) van het gebied.");
            return true;
        }

        player.sendMessage(ChatColor.GRAY + "Plot '" + naam + "' is aangemaakt van je selectie.");
        return true;
    }
}
