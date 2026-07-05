package nl.reallife.core.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import nl.reallife.core.RealLifeCore;
import nl.reallife.core.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;

/**
 * Houdt de sportzone bij (een kubus, ingesteld via WorldEdit) en berekent
 * hoe fitheid de loopsnelheid en spronghoogte van een speler beïnvloedt.
 */
public class FitnessManager {

    private final RealLifeCore plugin;
    private final File bestand;

    private String zoneWereld;
    private int zoneMinX, zoneMinY, zoneMinZ, zoneMaxX, zoneMaxY, zoneMaxZ;
    private boolean zoneIngesteld = false;

    // Vanilla loopsnelheid-attribuut waarde
    private static final double BASIS_SNELHEID = 0.1;
    private static final double MAX_SNELHEID = 0.16; // duidelijk sneller, maar niet extreem

    public FitnessManager(RealLifeCore plugin) {
        this.plugin = plugin;
        this.bestand = new File(plugin.getDataFolder(), "sportzone.yml");
        laden();
    }

    public boolean maakZoneVanSelectie(Player player) {
        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null) return false;
        try {
            com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);
            com.sk89q.worldedit.LocalSession session = worldEdit.getWorldEdit().getSessionManager().get(wePlayer);
            Region region = session.getSelection(session.getSelectionWorld());

            zoneWereld = player.getWorld().getName();
            zoneMinX = region.getMinimumPoint().getX();
            zoneMinY = region.getMinimumPoint().getY();
            zoneMinZ = region.getMinimumPoint().getZ();
            zoneMaxX = region.getMaximumPoint().getX();
            zoneMaxY = region.getMaximumPoint().getY();
            zoneMaxZ = region.getMaximumPoint().getZ();
            zoneIngesteld = true;
            opslaan();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInSportzone(Location loc) {
        if (!zoneIngesteld) return false;
        if (!loc.getWorld().getName().equals(zoneWereld)) return false;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        return x >= zoneMinX && x <= zoneMaxX && y >= zoneMinY && y <= zoneMaxY && z >= zoneMinZ && z <= zoneMaxZ;
    }

    public boolean isZoneIngesteld() {
        return zoneIngesteld;
    }

    /** Past de loopsnelheid en sprongkracht van een speler aan op basis van fitheid. */
    public void updateEffecten(Player player, int fitheid, int maxFitheid) {
        double factor = (double) fitheid / maxFitheid;
        double snelheid = BASIS_SNELHEID + (MAX_SNELHEID - BASIS_SNELHEID) * factor;
        var attribuut = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attribuut != null) {
            attribuut.setBaseValue(snelheid);
        }

        // Sprongkracht: op fitheid 150 bereik je het maximum van 1.5 blok hoog springen.
        int spronggrens = 150;
        int cappedFitheid = Math.min(fitheid, spronggrens);
        double sprongFactor = (double) cappedFitheid / spronggrens;
        // JUMP_BOOST amplifier 0 geeft ongeveer +0.5 blok extra, amplifier 1 ongeveer +1 blok extra.
        // We schalen tussen 0 en amplifier 1 (samen met vanilla sprong ~1.25 blok is dit ongeveer 1.5 blok max).
        int amplifier = sprongFactor >= 0.5 ? 1 : 0;
        if (fitheid > 60) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, amplifier, true, false, false));
        } else {
            player.removePotionEffect(PotionEffectType.JUMP_BOOST);
        }
    }

    private void laden() {
        if (!bestand.exists()) return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(bestand);
        if (!cfg.contains("wereld")) return;
        zoneWereld = cfg.getString("wereld");
        zoneMinX = cfg.getInt("minX");
        zoneMinY = cfg.getInt("minY");
        zoneMinZ = cfg.getInt("minZ");
        zoneMaxX = cfg.getInt("maxX");
        zoneMaxY = cfg.getInt("maxY");
        zoneMaxZ = cfg.getInt("maxZ");
        zoneIngesteld = true;
    }

    private void opslaan() {
        FileConfiguration cfg = new YamlConfiguration();
        cfg.set("wereld", zoneWereld);
        cfg.set("minX", zoneMinX);
        cfg.set("minY", zoneMinY);
        cfg.set("minZ", zoneMinZ);
        cfg.set("maxX", zoneMaxX);
        cfg.set("maxY", zoneMaxY);
        cfg.set("maxZ", zoneMaxZ);
        try {
            plugin.getDataFolder().mkdirs();
            cfg.save(bestand);
        } catch (IOException e) {
            plugin.getLogger().warning("Kon sportzone.yml niet opslaan: " + e.getMessage());
        }
    }
}
