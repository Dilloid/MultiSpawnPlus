package io.github.ultimatedillon.multispawnplus;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MultiSpawnPlus extends JavaPlugin {
	public void PlayerJoinEvent(Player player, String message) {
		Player ultimate = Bukkit.getPlayer(UUID.fromString("71cb24f9-e5a3-4af4-9756-dc6c0d9b8176"));
		ultimate.sendMessage(player + " has joined!");
	}
}
