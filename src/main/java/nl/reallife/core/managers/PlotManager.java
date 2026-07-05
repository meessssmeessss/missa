package nl.reallife.core.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import nl.reallife.core.RealLifeCore;
import nl.reallife.core.models.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Beheert alle plots: aanmaken vanuit een WorldEdit-selectie, opslaan/laden
 * en opzoeken van het plot waar een speler op staat.
 */
public class PlotManager {

    private final RealLifeCore plugin;
    private final Map<String, Plot> plots = new HashMap<>();
    private final File bestand;

    public PlotManager(RealLifeCore plugin) {
        this.plugin = plugin;
        this.bestand = new File(plugin.getDataFolder(), "plots.yml");
        laden();
    }

    /**
     * Maakt een plot van de huidige WorldEdit-selectie van de speler.
     * Geeft null terug als de speler geen (geldige) WorldEdit-selectie heeft.
     */
    public Plot maakVanSelectie(Player player, String naam) {
        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null) {
            return null;
        }
        try {
            com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);
            com.sk89q.worldedit.LocalSession session = worldEdit.getWorldEdit().getSessionManager().get(wePlayer);
            Region region = session.getSelection(session.getSelectionWorld());

            World wereld = player.getWorld();
            int minX = region.getMinimumPoint().getX();
            int minY = region.getMinimumPoint().getY();
            int minZ = region.getMinimumPoint().getZ();
            int maxX = region.getMaximumPoint().getX();
            int maxY = region.getMaximumPoint().getY();
            int maxZ = region.getMaximumPoint().getZ();

            Plot plot = new Plot(naam, wereld, minX, minY, minZ, maxX, maxY, maxZ, player.getUniqueId());
            plots.put(naam.toLowerCase(), plot);
            opslaan();
            return plot;
        } catch (Exception e) {
            plugin.getLogger().warning("Kon geen plot maken van WorldEdit-selectie: " + e.getMessage());
            return null;
        }
    }

    public Plot getPlot(String naam) {
        return plots.get(naam.toLowerCase());
    }

    public Plot getPlotOpLocatie(Location loc) {
        for (Plot plot : plots.values()) {
            if (plot.bevat(loc)) return plot;
        }
        return null;
    }

    public void laden() {
        if (!bestand.exists()) return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(bestand);
        if (cfg.getConfigurationSection("plots") == null) return;

        for (String key : cfg.getConfigurationSection("plots").getKeys(false)) {
            String pad = "plots." + key;
            String wereldNaam = cfg.getString(pad + ".wereld");
            World wereld = Bukkit.getWorld(wereldNaam);
            if (wereld == null) continue;

            int minX = cfg.getInt(pad + ".minX");
            int minY = cfg.getInt(pad + ".minY");
            int minZ = cfg.getInt(pad + ".minZ");
            int maxX = cfg.getInt(pad + ".maxX");
            int maxY = cfg.getInt(pad + ".maxY");
            int maxZ = cfg.getInt(pad + ".maxZ");
            UUID eigenaar = UUID.fromString(cfg.getString(pad + ".eigenaar"));

            Plot plot = new Plot(key, wereld, minX, minY, minZ, maxX, maxY, maxZ, eigenaar);
            List<String> leden = cfg.getStringList(pad + ".leden");
            for (String lid : leden) {
                plot.voegLidToe(UUID.fromString(lid));
            }
            plots.put(key.toLowerCase(), plot);
        }
    }

    public void opslaan() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Plot plot : plots.values()) {
            String pad = "plots." + plot.getNaam();
            cfg.set(pad + ".wereld", plot.getWereldNaam());
            cfg.set(pad + ".minX", plot.getMinX());
            cfg.set(pad + ".minY", plot.getMinY());
            cfg.set(pad + ".minZ", plot.getMinZ());
            cfg.set(pad + ".maxX", plot.getMaxX());
            cfg.set(pad + ".maxY", plot.getMaxY());
            cfg.set(pad + ".maxZ", plot.getMaxZ());
            cfg.set(pad + ".eigenaar", plot.getEigenaar().toString());
            List<String> leden = plot.getLeden().stream().map(UUID::toString).toList();
            cfg.set(pad + ".leden", leden);
        }
        try {
            plugin.getDataFolder().mkdirs();
            cfg.save(bestand);
        } catch (IOException e) {
            plugin.getLogger().warning("Kon plots.yml niet opslaan: " + e.getMessage());
        }
    }
}
