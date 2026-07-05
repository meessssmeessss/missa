package nl.reallife.core.models;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Een plot is een kubusvormig gebied (min/max coördinaten) met een eigenaar
 * en een lijst van leden die er ook mogen bouwen/deuren openen/kisten openen.
 */
public class Plot {

    private final String naam;
    private final String wereldNaam;
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;
    private UUID eigenaar;
    private final Set<UUID> leden = new HashSet<>();

    public Plot(String naam, World wereld, int minX, int minY, int minZ,
                int maxX, int maxY, int maxZ, UUID eigenaar) {
        this.naam = naam;
        this.wereldNaam = wereld.getName();
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.eigenaar = eigenaar;
    }

    public boolean bevat(Location loc) {
        if (!loc.getWorld().getName().equals(wereldNaam)) return false;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public boolean magBouwen(UUID speler) {
        return speler.equals(eigenaar) || leden.contains(speler);
    }

    public void voegLidToe(UUID speler) {
        leden.add(speler);
    }

    public void verwijderLid(UUID speler) {
        leden.remove(speler);
    }

    public String getNaam() {
        return naam;
    }

    public String getWereldNaam() {
        return wereldNaam;
    }

    public UUID getEigenaar() {
        return eigenaar;
    }

    public Set<UUID> getLeden() {
        return leden;
    }

    public int getMinX() { return minX; }
    public int getMinY() { return minY; }
    public int getMinZ() { return minZ; }
    public int getMaxX() { return maxX; }
    public int getMaxY() { return maxY; }
    public int getMaxZ() { return maxZ; }
}
