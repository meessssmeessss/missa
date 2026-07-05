package nl.reallife.core.commands;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import nl.reallife.core.models.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SportCommand implements CommandExecutor {

    private final RealLifeCore plugin;
    private final Set<UUID> bezig = new HashSet<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public SportCommand(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }
        Player player = (Player) sender;

        if (!plugin.getFitnessManager().isZoneIngesteld()) {
            player.sendMessage(ChatColor.GRAY + "Er is nog geen sportzone ingesteld door een beheerder.");
            return true;
        }
        if (!plugin.getFitnessManager().isInSportzone(player.getLocation())) {
            player.sendMessage(ChatColor.GRAY + "Je moet in de sportzone staan om te sporten.");
            return true;
        }
        if (bezig.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.GRAY + "Je bent al aan het sporten.");
            return true;
        }

        long cooldownSeconden = plugin.getConfig().getInt("fitheid.sport-cooldown", 60);
        long nu = System.currentTimeMillis() / 1000L;
        Long laatst = cooldowns.get(player.getUniqueId());
        if (laatst != null && nu - laatst < cooldownSeconden) {
            long resterend = cooldownSeconden - (nu - laatst);
            player.sendMessage(ChatColor.GRAY + "Je moet nog " + resterend + " seconden wachten voor je opnieuw kan sporten.");
            return true;
        }

        bezig.add(player.getUniqueId());
        player.sendMessage(ChatColor.GRAY + "Je begint met sporten... blijf in de sportzone staan.");

        new BukkitRunnable() {
            int verstreken = 0;

            @Override
            public void run() {
                if (!player.isOnline() || !plugin.getFitnessManager().isInSportzone(player.getLocation())) {
                    bezig.remove(player.getUniqueId());
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.GRAY + "Je bent gestopt met sporten omdat je de sportzone verliet.");
                    }
                    cancel();
                    return;
                }

                verstreken++;
                if (verstreken >= 15) {
                    bezig.remove(player.getUniqueId());
                    cooldowns.put(player.getUniqueId(), System.currentTimeMillis() / 1000L);

                    int max = plugin.getConfig().getInt("fitheid.max", 200);
                    int punten = plugin.getConfig().getInt("fitheid.punten-per-sport-sessie", 2);
                    PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
                    data.setFitheid(data.getFitheid() + punten, max);
                    plugin.getFitnessManager().updateEffecten(player, data.getFitheid(), max);

                    player.sendMessage(ChatColor.GRAY + "Goed gesport! Je fitheid is met " + punten + " punten gestegen. (" + data.getFitheid() + "/" + max + ")");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);

        return true;
    }
}
