package de.scholle.worldstatusapi;

import de.scholle.worldstatusapi.placeholder.BungeeServerExpansion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class WorldStatusPlugin extends JavaPlugin {

    private BungeeServerExpansion expansion;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadExpansion();
    }

    private void loadExpansion() {
        FileConfiguration cfg = getConfig();
        int updateInterval = cfg.getInt("update-interval-seconds", 10);

        Map<String, String> hosts = new HashMap<>();
        Map<String, Integer> ports = new HashMap<>();

        if (cfg.contains("servers")) {
            for (String serverName : cfg.getConfigurationSection("servers").getKeys(false)) {
                hosts.put(serverName, cfg.getString("servers." + serverName + ".host"));
                ports.put(serverName, cfg.getInt("servers." + serverName + ".port"));
            }
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            if (expansion != null) expansion.cancelUpdateTask();
            expansion = new BungeeServerExpansion(this, hosts, ports, updateInterval);
            expansion.register();
            getLogger().info("WorldStatusAPI geladen.");
        } else {
            getLogger().warning("PlaceholderAPI nicht gefunden. Plugin deaktiviert.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((command.getName().equalsIgnoreCase("worldstatusapi") || command.getName().equalsIgnoreCase("wsapi"))
                && args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            loadExpansion();
            sender.sendMessage("§aWorldStatusAPI: Config und Placeholders neu geladen.");
            return true;
        }
        return false;
    }
}
