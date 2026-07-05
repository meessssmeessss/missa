package nl.reallife.core.managers;

import nl.reallife.core.RealLifeCore;
import nl.reallife.core.models.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Beheert het laden/opslaan van PlayerData (saldo, fitheid, level, prefix)
 * in playerdata.yml.
 */
public class PlayerDataManager implements Listener {

    private final RealLifeCore plugin;
    private final File bestand;
    private final Map<UUID, PlayerData> cache = new HashMap<>();

    public PlayerDataManager(RealLifeCore plugin) {
        this.plugin = plugin;
        this.bestand = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!bestand.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                bestand.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Kon playerdata.yml niet aanmaken: " + e.getMessage());
            }
        }
    }

    public PlayerData get(UUID uuid) {
        return cache.computeIfAbsent(uuid, id -> laadOfMaak(id));
    }

    private PlayerData laadOfMaak(UUID uuid) {
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(bestand);
        PlayerData data = new PlayerData(uuid);
        String pad = uuid.toString();
        if (cfg.contains(pad)) {
            data.setSaldo(cfg.getDouble(pad + ".saldo", 0.0));
            data.setFitheid(cfg.getInt(pad + ".fitheid", 50), 200);
            data.setLevel(cfg.getInt(pad + ".level", 1));
            data.setPrefix(cfg.getString(pad + ".prefix", "Burger"));
        }
        return data;
    }

    public void opslaan(UUID uuid) {
        PlayerData data = cache.get(uuid);
        if (data == null) return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(bestand);
        String pad = uuid.toString();
        cfg.set(pad + ".saldo", data.getSaldo());
        cfg.set(pad + ".fitheid", data.getFitheid());
        cfg.set(pad + ".level", data.getLevel());
        cfg.set(pad + ".prefix", data.getPrefix());
        try {
            cfg.save(bestand);
        } catch (IOException e) {
            plugin.getLogger().warning("Kon playerdata.yml niet opslaan: " + e.getMessage());
        }
    }

    public void opslaanAlles() {
        for (UUID uuid : cache.keySet()) {
            opslaan(uuid);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        get(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        opslaan(p.getUniqueId());
        cache.remove(p.getUniqueId());
    }
}
