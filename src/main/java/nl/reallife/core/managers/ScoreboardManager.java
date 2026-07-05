package nl.reallife.core.managers;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import nl.reallife.core.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Bouwt en onderhoudt het scoreboard rechts in beeld: stadsnaam, level,
 * fitheid, saldo, en de actuele tijd/datum in Nederland.
 * Stadsnaam en scoreboardkleur zijn in-game aanpasbaar en worden opgeslagen
 * in config.yml.
 */
public class ScoreboardManager implements Listener {

    private final RealLifeCore plugin;
    private static final DateTimeFormatter TIJD_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATUM_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final ZoneId NL_ZONE = ZoneId.of("Europe/Amsterdam");

    public ScoreboardManager(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    public void startUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                update(player);
            }
        }, 20L, 20L);
    }

    public void update(Player player) {
        String infoKleur = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("scoreboard.info-kleur", "&7"));
        String stadKleur = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("scoreboard.stadnaam-kleur", "&8&l"));
        String stadNaam = plugin.getConfig().getString("scoreboard.stadnaam", "MijnStad");

        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective doel = board.registerNewObjective("reallife", "dummy", stadKleur + stadNaam);
        doel.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

        int regel = 10;
        String tijd = java.time.ZonedDateTime.now(NL_ZONE).format(TIJD_FORMAT);
        String datum = java.time.ZonedDateTime.now(NL_ZONE).format(DATUM_FORMAT);

        zetRegel(doel, infoKleur + "Tijd: " + ChatColor.WHITE + tijd, regel--);
        zetRegel(doel, infoKleur + "Datum: " + ChatColor.WHITE + datum, regel--);
        zetRegel(doel, " ", regel--);
        zetRegel(doel, infoKleur + "Level: " + ChatColor.WHITE + data.getLevel(), regel--);
        zetRegel(doel, infoKleur + "Fitheid: " + ChatColor.WHITE + data.getFitheid() + "/200", regel--);
        zetRegel(doel, infoKleur + "Saldo: " + ChatColor.WHITE + "€" + (int) data.getSaldo(), regel--);

        player.setScoreboard(board);
    }

    private void zetRegel(Objective doel, String tekst, int score) {
        // Scoreboard-regels moeten uniek zijn; voeg onzichtbare kleurcodes toe bij duplicaten indien nodig
        Score s = doel.getScore(tekst.length() > 40 ? tekst.substring(0, 40) : tekst);
        s.setScore(score);
    }

    public void zetStadnaam(String naam) {
        plugin.getConfig().set("scoreboard.stadnaam", naam);
        plugin.saveConfig();
        for (Player player : Bukkit.getOnlinePlayers()) update(player);
    }

    public void zetKleur(String kleurcode) {
        plugin.getConfig().set("scoreboard.stadnaam-kleur", kleurcode);
        plugin.saveConfig();
        for (Player player : Bukkit.getOnlinePlayers()) update(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        update(event.getPlayer());
    }
}
