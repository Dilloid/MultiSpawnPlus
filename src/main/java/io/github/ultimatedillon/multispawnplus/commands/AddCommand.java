package io.github.ultimatedillon.multispawnplus.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import io.github.ultimatedillon.multispawnplus.MultiSpawnPlus;

public class AddCommand implements CommandExecutor {
	private MultiSpawnPlus plugin;
	
	public AddCommand(MultiSpawnPlus plugin) {
		this.plugin = plugin;
	}
	
	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}
	
	public void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public void addSpawn(Player player, String[] args) {
		Location loc = player.getLocation();
		
		if (getConfig().contains("spawns." + args[1])) {
			sendMessage(player, "&cSpawn point &f" + args[1] + " &calready exists!");
		} else {
			getConfig().set("spawns." + args[1] + ".world", loc.getWorld().getName());
			
			String yaw = String.valueOf((int) loc.getYaw());
			String pitch = String.valueOf((int) loc.getPitch());
			
			getConfig().set("spawns." + args[1] + ".location", ""
					+ loc.getBlockX() + ", " 
					+ loc.getBlockY() + ", " 
					+ loc.getBlockZ() + ", " 
					+ yaw + ", " + pitch);
			
			boolean randomSet = false;
			boolean groupSet = false;
			
			if (args.length > 2) {
				for (int i = 2; i < args.length; i++) {
					if (!randomSet && args[i].equalsIgnoreCase("random")) {
						getConfig().set("spawns." + args[1] + ".random-target", true);
						randomSet = true;
					} else if (!groupSet && args[i].equalsIgnoreCase("group")) {
						String group = args[i + 1];
						getConfig().set("spawns." + args[1] + ".spawn-group", group);
						groupSet = true;
					} else if (args[i].equalsIgnoreCase("destination")) {
						sendMessage(player, "&cThe &fdestination &ctrait does not apply to spawn points!");
					}
				}
			}
			
			if (!randomSet) {
				getConfig().set("spawns." + args[1] + ".allow-random-spawn", false);
			}
			
			if (!groupSet) {
				getConfig().set("spawns." + args[1] + ".spawn-group", "default");
			}
			
			plugin.saveConfig();
			sendMessage(player, "&bSpawn point &f" + args[1] + " &bhas been created at &f" 
					+ loc.getBlockX() + "&b, &f" 
					+ loc.getBlockY() + "&b, &f" 
					+ loc.getBlockZ() + "&b in &f"
					+ loc.getWorld().getName());
			
			player.getLocation().subtract(0, 1, 0).getBlock().setType(Material.WOOL);
			
			plugin.reloadPlugin();
			plugin.reloadPortals();
		}
	}

	public void addPortal(Player player, String[] args) {
		@SuppressWarnings("deprecation")
		Block targetBlock = player.getTargetBlock(null, 8);
		Location loc = targetBlock.getLocation();
		
		if (getConfig().contains("portals." + args[1])) {
			sendMessage(player, "&cPortal &f" + args[1] + " &calready exists!");
		} else if (targetBlock.getType().equals(Material.AIR)) {
			sendMessage(player, "&cPlease target the block to set as the portal block!");
		} else {
			getConfig().set("portals." + args[1] + ".world", loc.getWorld().getName());
			getConfig().set("portals." + args[1] + ".location", ""
					+ loc.getBlockX() + ", " 
					+ loc.getBlockY() + ", " 
					+ loc.getBlockZ());
			
			boolean randomSet = false;
			boolean groupSet = false;
			boolean destSet = false;
			
			if (args.length > 2) {
				for (int i = 2; i < args.length; i++) {
					if (!randomSet && args[i].equalsIgnoreCase("random")) {
						getConfig().set("portals." + args[1] + ".random-target", true);
						randomSet = true;
					} else if (!groupSet && args[i].equalsIgnoreCase("group")) {
						String group = args[i + 1];
						getConfig().set("portals." + args[1] + ".spawn-group", group);
						groupSet = true;
					} else if (!destSet && args[i].equalsIgnoreCase("destination")) {
						String dest = args[i + 1];
						getConfig().set("portals." + args[1] + ".destination", dest);
						destSet = true;
					}
				}
			}
			
			if (!randomSet) {
				getConfig().set("portals." + args[1] + ".allow-random-spawn", false);
			}
			
			if (!groupSet) {
				getConfig().set("portals." + args[1] + ".spawn-group", "default");
			}
			
			if (!destSet) {
				getConfig().set("portals." + args[1] + ".destination", "default");
			}
			
			plugin.saveConfig();
			sendMessage(player, "&bPortal &f" + args[1] + " &bhas been created at &f" 
					+ loc.getBlockX() + "&b, &f" 
					+ loc.getBlockY() + "&b, &f" 
					+ loc.getBlockZ() + "&b in &f"
					+ loc.getWorld().getName());
			
			plugin.reloadPlugin();
			plugin.reloadPortals();
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (args.length < 1) {
				sendMessage(player, "&cNot enough arguments!");
				return false;
			} else {
				if (args[0].equalsIgnoreCase("spawn")) {
					if (player.hasPermission("multispawnplus.add.spawn")) {
						if (args.length < 2) {
							sendMessage(player, "&cNot enough arguments!");
						} else {
							addSpawn(player, args);
							return true;
						}
					} else {
						sendMessage(player, "&4You do not have permission to do this");
					}
				} else if (args[0].equalsIgnoreCase("portal")) {
					if (player.hasPermission("multispawnplus.add.portal")) {
						if (args.length < 2) {
							sendMessage(player, "&cNot enough arguments!");
						} else {
							addPortal(player, args);
							return true;
						}
					} else {
						sendMessage(player, "&4You do not have permission to do this");
					}
				} else {
					sendMessage(player, "&cInvalid arguments.");
				}
			}
		} else {
			sendMessage(sender, "[MultiSpawnPlus] This command can only be run by a player");
		}
		
		return false;
	}
}
