package ac.grim.grimac;

import ac.grim.grimac.manager.InitManager;
import ac.grim.grimac.manager.TickManager;
import ac.grim.grimac.utils.anticheat.PlayerDataManager;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public enum GrimAPI {
    INSTANCE;

    private final PlayerDataManager playerDataManager = new PlayerDataManager();
    private final InitManager initManager = new InitManager();
    private final TickManager tickManager = new TickManager();

    private ConfigurationSection config;
    private JavaPlugin plugin;

    public void load(final JavaPlugin plugin, final ConfigurationSection config) {
        this.plugin = plugin;
        this.config = config;
        initManager.load();
    }

    public void start(final JavaPlugin plugin) {
        this.plugin = plugin;
        initManager.start();
    }

    public void stop(final JavaPlugin plugin) {
        this.plugin = plugin;
        initManager.stop();
    }
}
