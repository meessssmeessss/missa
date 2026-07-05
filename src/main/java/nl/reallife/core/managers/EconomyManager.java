package nl.reallife.core.managers;

import nl.reallife.core.RealLifeCore;
import nl.reallife.core.models.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Beheert het banksaldo (opgeslagen getal) en de omzetting tussen
 * dat saldo en fysieke "cash-items" (coal, iron_ingot, gold_ingot,
 * diamond, netherite_ingot) in de inventory van de speler.
 */
public class EconomyManager {

    private final RealLifeCore plugin;
    // Van hoogste naar laagste waarde, belangrijk voor het "wisselgeld" algoritme
    private final Map<Material, Integer> denominaties = new LinkedHashMap<>();

    public EconomyManager(RealLifeCore plugin) {
        this.plugin = plugin;
        laadDenominaties();
    }

    private void laadDenominaties() {
        denominaties.put(Material.NETHERITE_INGOT, plugin.getConfig().getInt("economie.waarden.NETHERITE_INGOT", 5000));
        denominaties.put(Material.DIAMOND, plugin.getConfig().getInt("economie.waarden.DIAMOND", 2500));
        denominaties.put(Material.GOLD_INGOT, plugin.getConfig().getInt("economie.waarden.GOLD_INGOT", 1000));
        denominaties.put(Material.IRON_INGOT, plugin.getConfig().getInt("economie.waarden.IRON_INGOT", 500));
        denominaties.put(Material.COAL, plugin.getConfig().getInt("economie.waarden.COAL", 50));
    }

    public double getSaldo(UUID uuid) {
        return plugin.getPlayerDataManager().get(uuid).getSaldo();
    }

    /** Telt de totale waarde van alle cash-items in de inventory van een speler. */
    public int getCashInInventory(Player player) {
        int totaal = 0;
        for (Map.Entry<Material, Integer> entry : denominaties.entrySet()) {
            int aantal = telAantal(player, entry.getKey());
            totaal += aantal * entry.getValue();
        }
        return totaal;
    }

    private int telAantal(Player player, Material materiaal) {
        int aantal = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && stack.getType() == materiaal) {
                aantal += stack.getAmount();
            }
        }
        return aantal;
    }

    /**
     * Probeert cash-items ter waarde van "bedrag" te verwijderen uit de inventory
     * (grootste denominatie eerst). Geeft true terug als het exact gelukt is.
     * Als het niet exact past, wordt er niets verwijderd.
     */
    public boolean stort(Player player, int bedrag) {
        if (bedrag <= 0) return false;

        Map<Material, Integer> teVerwijderen = new LinkedHashMap<>();
        int resterend = bedrag;

        for (Map.Entry<Material, Integer> entry : denominaties.entrySet()) {
            Material mat = entry.getKey();
            int waarde = entry.getValue();
            int beschikbaar = telAantal(player, mat);
            int nodig = Math.min(beschikbaar, resterend / waarde);
            if (nodig > 0) {
                teVerwijderen.put(mat, nodig);
                resterend -= nodig * waarde;
            }
        }

        if (resterend != 0) {
            // Kon het bedrag niet exact samenstellen met de beschikbare items
            return false;
        }

        for (Map.Entry<Material, Integer> entry : teVerwijderen.entrySet()) {
            verwijderItems(player, entry.getKey(), entry.getValue());
        }

        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
        data.addSaldo(bedrag);
        return true;
    }

    /**
     * Probeert cash-items ter waarde van "bedrag" aan de inventory toe te voegen
     * (opnemen van de bank). Geeft true terug bij succes.
     */
    public boolean opnemen(Player player, int bedrag) {
        if (bedrag <= 0) return false;
        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
        if (!data.magAfschrijven(bedrag)) return false;

        Map<Material, Integer> teGeven = new LinkedHashMap<>();
        int resterend = bedrag;
        for (Map.Entry<Material, Integer> entry : denominaties.entrySet()) {
            int waarde = entry.getValue();
            int aantal = resterend / waarde;
            if (aantal > 0) {
                teGeven.put(entry.getKey(), aantal);
                resterend -= aantal * waarde;
            }
        }

        if (resterend != 0) {
            // Bedrag is geen samenstelling van de beschikbare coin-waarden (bv. geen multiple van 50)
            return false;
        }

        // Check of er genoeg ruimte is in de inventory voordat we echt geven
        if (!heeftRuimte(player, teGeven)) {
            return false;
        }

        for (Map.Entry<Material, Integer> entry : teGeven.entrySet()) {
            player.getInventory().addItem(new ItemStack(entry.getKey(), entry.getValue()));
        }

        data.addSaldo(-bedrag);
        return true;
    }

    private boolean heeftRuimte(Player player, Map<Material, Integer> items) {
        // Simpele check: simuleer op een kopie van de inventory
        ItemStack[] kopie = player.getInventory().getContents().clone();
        org.bukkit.inventory.Inventory tijdelijke = org.bukkit.Bukkit.createInventory(null, 36);
        tijdelijke.setContents(kopie.length > 36 ? java.util.Arrays.copyOf(kopie, 36) : kopie);
        for (Map.Entry<Material, Integer> entry : items.entrySet()) {
            ItemStack over = tijdelijke.addItem(new ItemStack(entry.getKey(), entry.getValue())).values().stream()
                    .findFirst().orElse(null);
            if (over != null) return false;
        }
        return true;
    }

    private void verwijderItems(Player player, Material materiaal, int aantal) {
        int resterend = aantal;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && resterend > 0; i++) {
            ItemStack stack = contents[i];
            if (stack != null && stack.getType() == materiaal) {
                int afhalen = Math.min(stack.getAmount(), resterend);
                stack.setAmount(stack.getAmount() - afhalen);
                resterend -= afhalen;
                if (stack.getAmount() <= 0) {
                    player.getInventory().setItem(i, null);
                } else {
                    player.getInventory().setItem(i, stack);
                }
            }
        }
    }

    public boolean maakOver(UUID van, UUID naar, int bedrag) {
        if (bedrag <= 0) return false;
        PlayerData vanData = plugin.getPlayerDataManager().get(van);
        if (!vanData.magAfschrijven(bedrag)) return false;
        PlayerData naarData = plugin.getPlayerDataManager().get(naar);
        vanData.addSaldo(-bedrag);
        naarData.addSaldo(bedrag);
        return true;
    }

    public Map<Material, Integer> getDenominaties() {
        return denominaties;
    }
}
