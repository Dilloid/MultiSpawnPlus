package io.github.ultimatedillon.multispawnplus.commands;

import java.util.ArrayList;
import java.util.Arrays;

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
	private ArrayList<String> validTraits;
	
	public AddCommand(MultiSpawnPlus plugin) {
		this.plugin = plugin;
		this.validTraits = new ArrayList<String>(Arrays.asList("-r", "-g", "-d"));
	}
	
	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}
	
	private boolean validTrait(String trait) {
		for (String valid : validTraits) {
			if (trait.substring(0, 2).equalsIgnoreCase(valid)) {
				return true;
			}
		}
		return false;
	}
	
	public void sendMessage(Player target, String message) {
		target.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
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
									//region Traits
									ArrayList<String> traits = new ArrayList<String>();
									
									for (int i = 2; i < args.length; i++) {
										traits.add(args[i]);
									}
									
									for (String trait : traits) {
										if (validTrait(trait.toLowerCase())) {
											sendMessage(player, trait);
											
											if (trait.substring(0, 2).equalsIgnoreCase("-r")) {
												if (trait.length() > 2) {
													sendMessage(player, "Warning: Incorrect Usage of the &f-r &ctrait!");
												}
												
												getConfig().set("spawns." + args[1] + ".allow-random-spawn", true);
												randomSet = true;
											} else if (trait.substring(0, 2).equalsIgnoreCase("-g")) {
												if (trait.length() < 4 || 
													!trait.substring(2, 3).equalsIgnoreCase(":")) {
													sendMessage(player, "&cError! &f-g &cmust be followed by '&f:&c' then the name of the spawn group.");
												} else {
													String group = trait.substring(3, trait.length());
													getConfig().set("spawns." + args[1] + ".spawn-group", group);
													groupSet = true;
												}
											} else if (trait.substring(0, 2).equalsIgnoreCase("-d")) {
												sendMessage(player, "&cThe &f-d &ctrait does not apply to spawn points!");
											}
										}
									}
									//endregion
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
								
								return true;
							}
						}
					} else {
						sendMessage(player, "&4You do not have permission to do this");
					}
				} else if (args[0].equalsIgnoreCase("portal")) {
					if (player.hasPermission("multispawnplus.add.portal")) {
						if (args.length < 2) {
							sendMessage(player, "&cNot enough arguments!");
						} else {
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
									//region Traits
									ArrayList<String> traits = new ArrayList<String>();
									
									for (int i = 2; i < args.length; i++) {
										traits.add(args[i]);
									}
									
									for (String trait : traits) {
										if (validTrait(trait.toLowerCase())) {
											sendMessage(player, trait);
											
											if (trait.substring(0, 2).equalsIgnoreCase("-r")) {
												if (trait.length() > 2) {
													sendMessage(player, "Warning: Incorrect Usage of the &f-r &ctrait!");
												}
												
												getConfig().set("portals." + args[1] + ".random-target", true);
												randomSet = true;
											} else if (trait.substring(0, 2).equalsIgnoreCase("-g")) {
												if (trait.length() < 4 || 
													!trait.substring(2, 3).equalsIgnoreCase(":")) {
													sendMessage(player, "&cError! &f-g &cmust be followed by '&f:&c' then the name of the spawn group.");
												} else {
													String group = trait.substring(3, trait.length());
													getConfig().set("portals." + args[1] + ".spawn-group", group);
													groupSet = true;
												}
											} else if (trait.substring(0, 2).equalsIgnoreCase("-d")) {
												if (trait.length() < 4 || 
													!trait.substring(2, 3).equalsIgnoreCase(":")) {
													sendMessage(player, "&cError! &f-f &cmust be followed by '&f:&c' then the name of the destination spawn point.");
												} else {
													String group = trait.substring(3, trait.length());
													getConfig().set("portals." + args[1] + ".destination", group);
													destSet = true;
												}
											}
										}
									}
									//endregion
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
								
								return true;
							}
						}
					} else {
						sendMessage(player, "&4You do not have permission to do this");
					}
				} else {
					sendMessage(player, "&cInvalid arguments.");
				}
			}
		} else {
			sender.sendMessage("[MultiSpawnPlus] This command can only be run by a player");
		}
		
		return false;
	}
}
