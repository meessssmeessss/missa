package nl.reallife.core.listeners;

import nl.reallife.core.RealLifeCore;
import nl.reallife.core.models.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class MovementListener implements Listener {

    private final RealLifeCore plugin;
    private final int maxFitheid;

    public MovementListener(RealLifeCore plugin) {
        this.plugin = plugin;
        this.maxFitheid = plugin.getConfig().getInt("fitheid.max", 200);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Zorg dat snelheid/sprong meteen kloppen bij het inloggen
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
                plugin.getFitnessManager().updateEffecten(player, data.getFitheid(), maxFitheid);
            }
        }.runTaskLater(plugin, 20L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        // Alleen tellen bij daadwerkelijke horizontale verplaatsing (voorkomt tellen bij enkel rondkijken)
        double dx = event.getFrom().getX() - event.getTo().getX();
        double dz = event.getFrom().getZ() - event.getTo().getZ();
        double afstand = Math.sqrt(dx * dx + dz * dz);
        if (afstand <= 0.01) return;

        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
        if (data.getFitheid() >= maxFitheid) return;

        data.addLoopAfstand(afstand);

        boolean inZone = plugin.getFitnessManager().isInSportzone(player.getLocation());
        double drempel = inZone
                ? plugin.getConfig().getInt("fitheid.blokken-per-punt-sportzone", 15)
                : plugin.getConfig().getInt("fitheid.blokken-per-punt-lopen", 40);

        if (data.getLoopAfstandTeller() >= drempel) {
            data.resetLoopAfstandTeller();
            data.setFitheid(data.getFitheid() + 1, maxFitheid);
            plugin.getFitnessManager().updateEffecten(player, data.getFitheid(), maxFitheid);
        }
    }
}
