package io.github.ultimatedillon.multispawnplus.commands;

import org.bukkit.ChatColor;
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
	
	public void saveConfig() {
		plugin.saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (player.hasPermission("multispawnplus.add.spawn")) {
				if (args.length < 2) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a name for this "
							+ "spawnpoint, whether it can be used as a random spawn, and the spawn group it should "
							+ "be added to."));
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eLeave &f<spawn group> "
							+ "&eblank to set the spawn group to 'default')"));
					player.sendMessage("Usage: /msp add <name> [true|false] <spawn group>");
				} else if (args.length > 3) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
					player.sendMessage("Usage: /msp add <name> [true|false] <spawn group>");
				} else {
					if (args[0] == "random") {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSpawnpoints must not be "
								+ "named 'random'!"));
					} else if (getConfig().contains("spawns." + args[0])) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cA spawnpoint with that name "
								+ "already exists!"));
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bUse &f/msp delete <name> &bto "
								+ "delete existing spawnpoints."));
					} else {
						if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
							getConfig().set("spawns." + args[1] + ".world", player.getWorld().getName());
							getConfig().set("spawns." + args[1] + ".allow-random-spawn", new Boolean(args[2]));
							
							if (args.length > 2) {
								getConfig().set("spawns." + args[0] + ".spawn-group", args[2]);
							} else {
								getConfig().set("spawns." + args[0] + ".spawn-group", "default");
							}
							
							getConfig().set("spawns." + args[0] + ".X", player.getLocation().getBlockX());
							getConfig().set("spawns." + args[0] + ".Y", player.getLocation().getBlockY());
							getConfig().set("spawns." + args[0] + ".Z", player.getLocation().getBlockZ());
							getConfig().set("spawns." + args[0] + ".yaw", player.getLocation().getYaw());
							getConfig().set("spawns." + args[0] + ".pitch", player.getLocation().getPitch());
							
							plugin.saveConfig();
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSpawnpoint &f" + args[1]
									+ " &bhas been created!"));
							
							plugin.reloadPlugin();
							plugin.reloadPortals();
							
							return true;
						} else if (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSyntax Error!"));
							player.sendMessage("Usage: /msp add <name> [true|false] <spawn group>");
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDerp. "
									+ "Something went wrong!"));
						}
					}
				}
			} else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
			}
		} else {
			sender.sendMessage("MultiSpawnPlus: This command can only be run by a player");
		}
		
		return false;
	}
}
