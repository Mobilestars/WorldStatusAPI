package de.scholle.worldstatusapi.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class BungeeServerExpansion extends PlaceholderExpansion {

    private final Plugin plugin;
    private final Map<String, Boolean> serverStatus = new HashMap<>();
    private final Map<String, String> serverHost;
    private final Map<String, Integer> serverPort;
    private final int updateIntervalSeconds;

    public BungeeServerExpansion(Plugin plugin, Map<String, String> hosts, Map<String, Integer> ports, int updateIntervalSeconds) {
        this.plugin = plugin;
        this.serverHost = hosts;
        this.serverPort = ports;
        this.updateIntervalSeconds = updateIntervalSeconds;

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (String server : serverHost.keySet()) {
                serverStatus.put(server, isServerOnline(serverHost.get(server), serverPort.get(server)));
            }
        }, 20L, updateIntervalSeconds * 20L);
    }

    private boolean isServerOnline(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String getIdentifier() {
        return "bungeeserver";
    }

    @Override
    public String getAuthor() {
        return "scholle";
    }

    @Override
    public String getVersion() {
        return "1.0.3";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String[] parts = identifier.split("_");
        if (parts.length == 2 && parts[1].equalsIgnoreCase("status")) {
            String server = parts[0];
            return serverStatus.getOrDefault(server, false) ? "Online" : "Offline";
        }
        return null;
    }
}
