package nl.reallife.core.commands;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckSaldoCommand implements CommandExecutor {

    private final RealLifeCore plugin;

    public CheckSaldoCommand(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }
        Player player = (Player) sender;
        double saldo = plugin.getEconomyManager().getSaldo(player.getUniqueId());
        player.sendMessage(ChatColor.GRAY + "Je banksaldo is: " + ChatColor.WHITE + "€" + (int) saldo);
        return true;
    }
}
