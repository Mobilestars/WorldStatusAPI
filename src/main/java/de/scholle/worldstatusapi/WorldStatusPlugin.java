package de.scholle.worldstatusapi;

import de.scholle.worldstatusapi.placeholder.BungeeServerExpansion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class WorldStatusPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration cfg = getConfig();

        int updateInterval = cfg.getInt("update-interval-seconds", 10); // Default 10 Sekunden

        Map<String, String> hosts = new HashMap<>();
        Map<String, Integer> ports = new HashMap<>();

        if (cfg.contains("servers")) {
            for (String serverName : cfg.getConfigurationSection("servers").getKeys(false)) {
                hosts.put(serverName, cfg.getString("servers." + serverName + ".host"));
                ports.put(serverName, cfg.getInt("servers." + serverName + ".port"));
            }
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            BungeeServerExpansion expansion = new BungeeServerExpansion(this, hosts, ports, updateInterval);
            expansion.register();
            getLogger().info("WorldStatusAPI geladen.");
        } else {
            getLogger().warning("PlaceholderAPI nicht gefunden. Plugin deaktiviert.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("WorldStatusAPI deaktiviert.");
    }
}
