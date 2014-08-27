package io.github.ultimatedillon.multispawnplus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import io.github.ultimatedillon.multispawnplus.MultiSpawnPlus;

public class DeleteCommand implements CommandExecutor {
	private MultiSpawnPlus plugin;
	
	public DeleteCommand(MultiSpawnPlus plugin) {
		this.plugin = plugin;
	}
	
	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}
	
	public void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public void deleteSpawn(CommandSender sender, String item) {
		if (getConfig().contains("spawns." + item)) {
			getConfig().set("spawns." + item, null);

			plugin.saveConfig();
			plugin.reloadPlugin();
			plugin.reloadPortals();
			
			sendMessage(sender, "&bSpawn point &f" + item + " &bhas been deleted!");
		} else {
			sendMessage(sender, "&cThat spawnpoint doesn't exist.");
		}
	}
	
	public void deletePortal(CommandSender sender, String item) {
		if (getConfig().contains("portals." + item)) {
			getConfig().set("portals." + item, null);

			plugin.saveConfig();
			plugin.reloadPlugin();
			plugin.reloadPortals();
			
			sendMessage(sender, "&bPortal block &f" + item + " &bhas been deleted!");
		} else {
			sendMessage(sender, "&cThat portal doesn't exist.");
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			sendMessage(sender, "&cNot enough arguments!");
			return false;
		} else {
			if (args[0].equalsIgnoreCase("spawn")) {
				if (args.length < 2) {
					sendMessage(sender, "&cNot enough arguments!");
				} else if (args.length > 2) {
					sendMessage(sender, "&cToo many arguments!");
					sendMessage(sender, "Usage: /msp delete spawn <name>");
				} else {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.add.spawn")) {
							deleteSpawn(player, args[1]);
							return true;
						} else {
							sendMessage(player, "&4You do not have permission to do this");
						}
					} else {
						deleteSpawn(sender, args[1]);
						return true;
					}
				}
			} else if (args[0].equalsIgnoreCase("portal")) {
				if (args.length < 2) {
					sendMessage(sender, "&cNot enough arguments!");
				} else if (args.length > 2) {
					sendMessage(sender, "&cToo many arguments!");
					sendMessage(sender, "Usage: /msp delete portal <name>");
				} else {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.add.portal")) {
							deletePortal(player, args[1]);
							return true;
						} else {
							sendMessage(player, "&4You do not have permission to do this");
						}
					} else {
						deletePortal(sender, args[1]);
						return true;
					}
				}
			} else {
				sendMessage(sender, "&cInvalid arguments.");
			}
		}
		
		return false;
	}
}
