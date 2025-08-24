package de.scholle.worldstatusapi.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

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
    private BukkitTask updateTask;

    public BungeeServerExpansion(Plugin plugin, Map<String, String> hosts, Map<String, Integer> ports, int updateIntervalSeconds) {
        this.plugin = plugin;
        this.serverHost = hosts;
        this.serverPort = ports;
        this.updateIntervalSeconds = updateIntervalSeconds;
        startUpdateTask();
    }

    private void startUpdateTask() {
        updateTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (String server : serverHost.keySet()) {
                serverStatus.put(server, isServerOnline(serverHost.get(server), serverPort.get(server)));
            }
        }, 20L, updateIntervalSeconds * 20L);
    }

    public void cancelUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
        }
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
        return "bungeeserver"; // Haupt-Identifier, wird von PAPI erkannt
    }

    @Override
    public String getAuthor() {
        return "scholle";
    }

    @Override
    public String getVersion() {
        return "1.0.5";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String server = null;
        // Unterstützt beide Präfixe: bungeeserver oder wsapi
        if (identifier.startsWith("bungeeserver_") && identifier.endsWith("_status")) {
            server = identifier.substring("bungeeserver_".length(), identifier.length() - "_status".length());
        } else if (identifier.startsWith("wsapi_") && identifier.endsWith("_status")) {
            server = identifier.substring("wsapi_".length(), identifier.length() - "_status".length());
        }

        if (server != null) {
            return serverStatus.getOrDefault(server, false) ? "Online" : "Offline";
        }

        return null;
    }
}
