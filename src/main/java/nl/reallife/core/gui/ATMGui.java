package nl.reallife.core.gui;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

/**
 * Bouwt het ATM-menu (pinautomaat), qua thema gebaseerd op de aangeleverde
 * screenshots: links het groene "Storten"-thema, rechts het gele
 * "Opnemen"-thema, met in het midden het saldo-overzicht.
 *
 * Let op: een vanilla inventory-GUI kan geen custom pixel-art achtergrond of
 * lettertype tonen zoals in de screenshots (dat vereist een resourcepack).
 * Dit is de dichtstbijzijnde vanilla-invulling: kleurenthema, opbouw en
 * labels komen overeen, het pixel-font en de munten-graphics niet.
 */
public class ATMGui {

    public static final String TITEL = ChatColor.of("#4A4A4A") + "" + ChatColor.BOLD + "BANK";

    public static Inventory build(Player player, int cashInInventory, double saldo) {
        Inventory inv = Bukkit.createInventory(null, 27, TITEL);

        // Rij 0 (0-8): bovenrand - groen links, geel rechts
        for (int c = 0; c <= 3; c++) inv.setItem(c, vulling(Material.GREEN_STAINED_GLASS_PANE));
        inv.setItem(4, vulling(Material.GRAY_STAINED_GLASS_PANE));
        for (int c = 5; c <= 8; c++) inv.setItem(c, vulling(Material.YELLOW_STAINED_GLASS_PANE));

        // Rij 1 (9-17): saldo-info in het midden
        for (int c = 0; c <= 3; c++) inv.setItem(9 + c, vulling(Material.GREEN_STAINED_GLASS_PANE));
        inv.setItem(13, infoItem(cashInInventory, saldo));
        for (int c = 5; c <= 8; c++) inv.setItem(9 + c, vulling(Material.YELLOW_STAINED_GLASS_PANE));

        // Rij 2 (18-26): grote knoppen "Storten" (groen) en "Opnemen" (geel)
        for (int c = 0; c <= 8; c++) {
            int slot = 18 + c;
            if (c == 2) {
                inv.setItem(slot, stortenKnop());
            } else if (c == 6) {
                inv.setItem(slot, opnemenKnop());
            } else if (c <= 3) {
                inv.setItem(slot, vulling(Material.GREEN_STAINED_GLASS_PANE));
            } else if (c == 4) {
                inv.setItem(slot, vulling(Material.GRAY_STAINED_GLASS_PANE));
            } else {
                inv.setItem(slot, vulling(Material.YELLOW_STAINED_GLASS_PANE));
            }
        }

        return inv;
    }

    private static ItemStack stortenKnop() {
        return maakItem(Material.LIME_CONCRETE,
                "" + ChatColor.of("#3CB043") + ChatColor.BOLD + "Storten",
                ChatColor.GRAY + "Stort cash uit je inventory",
                ChatColor.GRAY + "op je bankrekening.");
    }

    private static ItemStack opnemenKnop() {
        return maakItem(Material.YELLOW_CONCRETE,
                "" + ChatColor.of("#E8B923") + ChatColor.BOLD + "Opnemen",
                ChatColor.GRAY + "Neem geld van je bankrekening",
                ChatColor.GRAY + "op als cash.");
    }

    private static ItemStack infoItem(int cashInInventory, double saldo) {
        return maakItem(Material.PAPER,
                "" + ChatColor.of("#4A4A4A") + ChatColor.BOLD + "$",
                ChatColor.GRAY + "Banksaldo: " + ChatColor.WHITE + "€" + (int) saldo,
                ChatColor.GRAY + "Cash bij je: " + ChatColor.WHITE + "€" + cashInInventory);
    }

    private static ItemStack vulling(Material materiaal) {
        return maakItem(materiaal, " ");
    }

    private static ItemStack maakItem(Material materiaal, String naam, String... lore) {
        ItemStack item = new ItemStack(materiaal);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(naam);
        meta.setLore(lore.length > 0 ? Arrays.asList(lore) : Collections.emptyList());
        item.setItemMeta(meta);
        return item;
    }
}
