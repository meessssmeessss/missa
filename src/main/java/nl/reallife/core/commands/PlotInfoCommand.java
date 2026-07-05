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

import java.util.stream.Collectors;

public class PlotInfoCommand implements CommandExecutor {

    private final RealLifeCore plugin;

    public PlotInfoCommand(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }
        Player player = (Player) sender;

        Plot plot = plugin.getPlotManager().getPlotOpLocatie(player.getLocation());
        if (plot == null) {
            player.sendMessage(ChatColor.GRAY + "Je staat niet op een plot.");
            return true;
        }

        OfflinePlayer eigenaar = Bukkit.getOfflinePlayer(plot.getEigenaar());
        String ledenNamen = plot.getLeden().stream()
                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                .collect(Collectors.joining(", "));

        player.sendMessage(ChatColor.GRAY + "Plotnaam: " + ChatColor.WHITE + plot.getNaam());
        player.sendMessage(ChatColor.GRAY + "Eigenaar: " + ChatColor.WHITE + eigenaar.getName());
        player.sendMessage(ChatColor.GRAY + "Leden: " + ChatColor.WHITE + (ledenNamen.isEmpty() ? "geen" : ledenNamen));
        return true;
    }
}
