package nl.reallife.core.commands;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import nl.reallife.core.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZetFitheidCommand implements CommandExecutor {

    private final RealLifeCore plugin;

    public ZetFitheidCommand(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.GRAY + "Je hebt geen toestemming voor dit commando.");
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(ChatColor.GRAY + "Gebruik: /zetfitheid <speler> <waarde>");
            return true;
        }

        Player doel = Bukkit.getPlayer(args[0]);
        if (doel == null) {
            sender.sendMessage(ChatColor.GRAY + "Die speler is niet online.");
            return true;
        }

        int waarde;
        try {
            waarde = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.GRAY + "Ongeldige waarde.");
            return true;
        }

        int max = plugin.getConfig().getInt("fitheid.max", 200);
        PlayerData data = plugin.getPlayerDataManager().get(doel.getUniqueId());
        data.setFitheid(waarde, max);
        plugin.getFitnessManager().updateEffecten(doel, data.getFitheid(), max);

        sender.sendMessage(ChatColor.GRAY + "Fitheid van " + doel.getName() + " is ingesteld op " + data.getFitheid() + "/" + max + ".");
        doel.sendMessage(ChatColor.GRAY + "Je fitheid is aangepast naar " + data.getFitheid() + "/" + max + ".");
        return true;
    }
}
