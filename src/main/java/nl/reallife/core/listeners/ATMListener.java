package nl.reallife.core.listeners;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import nl.reallife.core.gui.ATMGui;
import nl.reallife.core.managers.EconomyManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ATMListener implements Listener {

    private final RealLifeCore plugin;

    public ATMListener(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKlikBlok(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.RED_SANDSTONE_STAIRS) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        EconomyManager economy = plugin.getEconomyManager();
        int cash = economy.getCashInInventory(player);
        double saldo = economy.getSaldo(player.getUniqueId());
        player.openInventory(ATMGui.build(player, cash, saldo));
    }

    @EventHandler
    public void onKlikMenu(InventoryClickEvent event) {
        if (event.getView().getTitle() == null) return;
        if (!event.getView().getTitle().equals(ATMGui.TITEL)) return;

        event.setCancelled(true);
        if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String naam = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
        if (naam == null) return;

        EconomyManager economy = plugin.getEconomyManager();

        if (naam.equalsIgnoreCase("Storten")) {
            InputListener.vraagInvoer(player, "Typ het bedrag dat je wilt storten:", tekst -> {
                Integer bedrag = parseBedrag(tekst);
                if (bedrag == null) {
                    player.sendMessage(ChatColor.GRAY + "Ongeldig bedrag.");
                    return;
                }
                if (economy.stort(player, bedrag)) {
                    player.sendMessage(ChatColor.GRAY + "Je hebt €" + bedrag + " gestort.");
                } else {
                    player.sendMessage(ChatColor.GRAY + "Je hebt niet genoeg (of niet de juiste) cash bij je om dit bedrag exact te storten.");
                }
            });
        } else if (naam.equalsIgnoreCase("Opnemen")) {
            InputListener.vraagInvoer(player, "Typ het bedrag dat je wilt opnemen:", tekst -> {
                Integer bedrag = parseBedrag(tekst);
                if (bedrag == null) {
                    player.sendMessage(ChatColor.GRAY + "Ongeldig bedrag.");
                    return;
                }
                if (economy.opnemen(player, bedrag)) {
                    player.sendMessage(ChatColor.GRAY + "Je hebt €" + bedrag + " opgenomen.");
                } else {
                    player.sendMessage(ChatColor.GRAY + "Opnemen mislukt: onvoldoende saldo, ongeldig bedrag (moet een combinatie van €50, €500, €1000, €2500 of €5000 zijn), of te weinig ruimte in je inventory.");
                }
            });
        }
    }

    private Integer parseBedrag(String tekst) {
        try {
            int bedrag = Integer.parseInt(tekst.trim());
            if (bedrag <= 0) return null;
            return bedrag;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
