package nl.reallife.core;

import nl.reallife.core.commands.*;
import nl.reallife.core.listeners.ATMListener;
import nl.reallife.core.listeners.ChatListener;
import nl.reallife.core.listeners.InputListener;
import nl.reallife.core.listeners.MovementListener;
import nl.reallife.core.listeners.PlotProtectionListener;
import nl.reallife.core.managers.EconomyManager;
import nl.reallife.core.managers.FitnessManager;
import nl.reallife.core.managers.PlayerDataManager;
import nl.reallife.core.managers.PlotManager;
import nl.reallife.core.managers.ScoreboardManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RealLifeCore extends JavaPlugin {

    private static RealLifeCore instance;

    private PlayerDataManager playerDataManager;
    private EconomyManager economyManager;
    private PlotManager plotManager;
    private FitnessManager fitnessManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.playerDataManager = new PlayerDataManager(this);
        this.economyManager = new EconomyManager(this);
        this.plotManager = new PlotManager(this);
        this.fitnessManager = new FitnessManager(this);
        this.scoreboardManager = new ScoreboardManager(this);

        // Listeners
        getServer().getPluginManager().registerEvents(new ATMListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new InputListener(this), this);
        getServer().getPluginManager().registerEvents(new MovementListener(this), this);
        getServer().getPluginManager().registerEvents(new PlotProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(playerDataManager, this);
        getServer().getPluginManager().registerEvents(scoreboardManager, this);

        // Commands
        getCommand("checksaldo").setExecutor(new CheckSaldoCommand(this));
        getCommand("maakover").setExecutor(new MaakOverCommand(this));
        getCommand("plotmaak").setExecutor(new PlotMaakCommand(this));
        getCommand("plotadd").setExecutor(new PlotAddCommand(this));
        getCommand("plotinfo").setExecutor(new PlotInfoCommand(this));
        getCommand("sportzonemaken").setExecutor(new SportZoneCommand(this));
        getCommand("sport").setExecutor(new SportCommand(this));
        getCommand("zetfitheid").setExecutor(new ZetFitheidCommand(this));
        getCommand("zetprefix").setExecutor(new ZetPrefixCommand(this));
        getCommand("zetstadnaam").setExecutor(new ZetStadnaamCommand(this));
        getCommand("zetscoreboardkleur").setExecutor(new ZetScoreboardKleurCommand(this));

        // Scoreboard elke seconde bijwerken (tijd/datum + info)
        scoreboardManager.startUpdateTask();

        getLogger().info("ReallifeCore is ingeschakeld.");
    }

    @Override
    public void onDisable() {
        if (plotManager != null) plotManager.opslaan();
        if (playerDataManager != null) playerDataManager.opslaanAlles();
        getLogger().info("ReallifeCore is uitgeschakeld.");
    }

    public static RealLifeCore getInstance() {
        return instance;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public PlotManager getPlotManager() {
        return plotManager;
    }

    public FitnessManager getFitnessManager() {
        return fitnessManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
}
