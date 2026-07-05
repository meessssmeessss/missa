package nl.reallife.core.listeners;

import nl.reallife.core.RealLifeCore;
import nl.reallife.core.models.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Formaat: &7&l| &8&lLevel (1)&7&l |&8&l (Prefix)&7&l | &7&l(Spelernaam) : (Bericht)
 */
public class ChatListener implements Listener {

    private final RealLifeCore plugin;

    public ChatListener(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        // Als de speler op dit moment een bedrag/tekst invoert voor een GUI (bv. ATM),
        // wordt dit al afgehandeld en geannuleerd door InputListener.
        if (event.isCancelled()) return;

        PlayerData data = plugin.getPlayerDataManager().get(event.getPlayer().getUniqueId());

        String format = ChatColor.GRAY.toString() + ChatColor.BOLD + "| "
                + ChatColor.DARK_GRAY + ChatColor.BOLD + "Level (" + data.getLevel() + ")"
                + ChatColor.GRAY + ChatColor.BOLD + " | "
                + ChatColor.DARK_GRAY + ChatColor.BOLD + "(" + data.getPrefix() + ")"
                + ChatColor.GRAY + ChatColor.BOLD + " | "
                + ChatColor.GRAY + ChatColor.BOLD + "%1$s"
                + ChatColor.RESET + " : %2$s";

        event.setFormat(format);
    }
}
