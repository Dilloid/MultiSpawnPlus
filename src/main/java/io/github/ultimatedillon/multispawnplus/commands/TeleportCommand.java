package io.github.ultimatedillon.multispawnplus.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import io.github.ultimatedillon.multispawnplus.MultiSpawnPlus;

public class TeleportCommand implements CommandExecutor {
	private MultiSpawnPlus plugin;
	
	public TeleportCommand(MultiSpawnPlus plugin) {
		this.plugin = plugin;
	}
	
	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}
	
	public void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public boolean teleport(CommandSender sender, String[] args) {
		Player target = null;
		
		boolean randomSet = false;
		boolean groupSet = false;
		boolean destSet = false;
		boolean playerSet = false;
		boolean canTeleport = true;
		
		String group = "";
		String dest = "default";
		String player = "";
		
		if (args.length > 2) {
			for (int i = 0; i < args.length; i++) {
				if (!randomSet && args[i].equalsIgnoreCase("random")) {
					randomSet = true;
				} else if (!groupSet && args[i].equalsIgnoreCase("group")) {
					group = args[i + 1];
					groupSet = true;
				} else if (!destSet && args[i].equalsIgnoreCase("dest")) {
					dest = args[i + 1];
					destSet = true;
				} else if (!playerSet && args[i].equalsIgnoreCase("player")) {
					player = args[i + 1];
					playerSet = true;
				}
			}
		}
		
		if (randomSet) {
			String[] groups = plugin.getLocations(groupSet, group);
			
			try {
				Random rand = new Random();
				dest = groups[rand.nextInt(groups.length)];
			} catch (IllegalArgumentException err) {
				sendMessage(sender, "&cThere are no spawn points in group &f" + group + "&c!");
				canTeleport = false;
			}
		}
		
		if (player == "") {
			try {
				target = (Player) sender;
			} catch (Exception ex) {
				sendMessage(sender, "&cThis command must be run by a player, or define a player to teleport!");
				canTeleport = false;
			}
		} else {
			try {
				target = plugin.getPlayerFromName(player);
			} catch (NullPointerException ex) {
				sendMessage(sender, "&cCould not find player &f" + player);
				canTeleport = false;
			}
		}
		
		if (canTeleport) {
			if (getConfig().contains("spawns." + dest)) {
				World world = Bukkit.getWorld(getConfig().get("spawns." + dest + ".world").toString());
				String locString = getConfig().getString("spawns." + dest + ".location");
				String[] locArray = locString.replace(" ", "").split(",");
				
				if (world == null) {
					sendMessage(sender, "&cThat world does not exist!");
					return false;
				} else {
					Bukkit.getLogger().info("MultiSpawnPlus: - Teleporting " 
							+ target.getName()
							+ " to " + dest
							+ "(" + world + ", "
							+ locArray[0] + ", "
							+ locArray[1] + ", "
							+ locArray[2] + ", "
							+ locArray[3] + ", "
							+ locArray[4] + ")");
					
					Location loc = new Location(world, 
							Double.valueOf(locArray[0]) + 0.5, 
							Double.valueOf(locArray[1]), 
							Double.valueOf(locArray[2]) + 0.5, 
							Integer.valueOf(locArray[3]), 
							Integer.valueOf(locArray[4]));
					
					target.teleport(loc);
					return true;
				}
			} else {
				sendMessage(sender, "&cThat spawn point doesn't exist!");
				return false;
			}
		}
		
		return false;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (player.hasPermission("multispawnplus.add.spawn")) {
				if (teleport(player, args)) {
					return true;
				}
			} else {
				sendMessage(player, "&4You do not have permission to do this");
			}
		} else {
			if (teleport(sender, args)) {
				return true;
			}
		}
		
		return false;
	}
}
