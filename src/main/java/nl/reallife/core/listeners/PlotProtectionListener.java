package nl.reallife.core.listeners;

import net.md_5.bungee.api.ChatColor;
import nl.reallife.core.RealLifeCore;
import nl.reallife.core.models.Plot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlotProtectionListener implements Listener {

    private final RealLifeCore plugin;

    public PlotProtectionListener(RealLifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreek(BlockBreakEvent event) {
        Plot plot = plugin.getPlotManager().getPlotOpLocatie(event.getBlock().getLocation());
        if (plot == null) return;
        if (!plot.magBouwen(event.getPlayer().getUniqueId()) && !event.getPlayer().isOp()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "Je mag hier niet bouwen, dit is plot '" + plot.getNaam() + "'.");
        }
    }

    @EventHandler
    public void onPlaats(BlockPlaceEvent event) {
        Plot plot = plugin.getPlotManager().getPlotOpLocatie(event.getBlock().getLocation());
        if (plot == null) return;
        if (!plot.magBouwen(event.getPlayer().getUniqueId()) && !event.getPlayer().isOp()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "Je mag hier niet bouwen, dit is plot '" + plot.getNaam() + "'.");
        }
    }

    @EventHandler
    public void onInteractie(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null) return;

        BlockState state = block.getState();
        boolean isDeurOfKist = block.getBlockData() instanceof Openable || state instanceof Container;
        if (!isDeurOfKist) return;

        Plot plot = plugin.getPlotManager().getPlotOpLocatie(block.getLocation());
        if (plot == null) return;

        Player player = event.getPlayer();
        if (!plot.magBouwen(player.getUniqueId()) && !player.isOp()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.GRAY + "Je mag dit niet openen, dit is plot '" + plot.getNaam() + "'.");
        }
    }
}
