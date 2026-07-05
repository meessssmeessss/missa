package nl.reallife.core.listeners;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Generiek systeem om na een GUI-klik een bedrag (of andere tekst) via de
 * chat op te vragen, zonder dat de speler dit als normaal chatbericht ziet.
 * De ATM gebruikt dit voor "Storten" en "Opnemen".
 */
public class InputListener implements Listener {

    private static final Map<UUID, Consumer<String>> WACHTEND = new ConcurrentHashMap<>();
    private final RealLifeCore plugin;

    public InputListener(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    public static void vraagInvoer(Player player, String vraagTekst, Consumer<String> callback) {
        WACHTEND.put(player.getUniqueId(), callback);
        player.closeInventory();
        player.sendMessage(ChatColor.GRAY + vraagTekst);
        player.sendMessage(ChatColor.GRAY + "Typ 'annuleer' om te annuleren.");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Consumer<String> callback = WACHTEND.get(player.getUniqueId());
        if (callback == null) return;

        event.setCancelled(true);
        WACHTEND.remove(player.getUniqueId());
        String bericht = event.getMessage().trim();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (bericht.equalsIgnoreCase("annuleer")) {
                    player.sendMessage(ChatColor.GRAY + "Geannuleerd.");
                    return;
                }
                callback.accept(bericht);
            }
        }.runTask(plugin);
    }
}
