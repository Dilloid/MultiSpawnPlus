package io.github.ultimatedillon.multispawnplus;

import io.github.ultimatedillon.multispawnplus.commands.AddCommand;
import io.github.ultimatedillon.multispawnplus.commands.DeleteCommand;
import io.github.ultimatedillon.multispawnplus.commands.TeleportCommand;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public final class MultiSpawnPlus extends JavaPlugin {
	private String version = "v1.3.73";
	
	private String[] spawns;
	public String[] allowed;
	private String[] portals;
	
	public PlayerJoinListener joinListener;
	public PlayerMoveListener moveListener;
	
	private Metrics metrics;
	private Updater updater;
	
	@Override
	public void onDisable() {
  		getServer().getScheduler().cancelTasks(this);
  	}
	
	@Override
	public void onEnable() {
		reloadPlugin();
		
        joinListener = new PlayerJoinListener(this);
        moveListener = new PlayerMoveListener(this);
        
        getCommand("mspAdd").setExecutor(new AddCommand(this));
        getCommand("mspDelete").setExecutor(new DeleteCommand(this));
        getCommand("mspTP").setExecutor(new TeleportCommand(this));
        
        if (getConfig().getBoolean("options.allow-plugin-metrics", true)) {
	        try {
	            metrics = new Metrics(this);
	            metrics.start();
	            
	            getLogger().info("[MultiSpawnPlus] Succesfully submitting stats to MCStats.org!");
	        } catch (IOException e) {
	            getLogger().info("[MultiSpawnPlus] Failed to submit stats to MCStats.org! Error: " + e);
	        }
        } else {
        	getLogger().info("[MultiSpawnPlus] Plugin Metrics disallowed! Disabling submission of stats to MCStats.org...");
        }
        
        if (getConfig().getBoolean("options.auto-update") == true) {
			updater = new Updater(this, 81894, this.getFile(), Updater.UpdateType.DEFAULT, true);
        } else if (getConfig().getBoolean("options.auto-update") == false) {
        	updater = new Updater(this, 81894, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
        	
        	String latest = updater.getLatestName().substring(15);
        	if (version.equalsIgnoreCase(latest)) {
        		getLogger().info("[MultiSpawnPlus] No updates available.");
        	} else {
        		getLogger().info("[MultiSpawnPlus] Update available! (" + latest + ")");
        		getLogger().info("[MultiSpawnPlus] Download here: " + updater.getLatestFileLink());
        	}
        }
    }
	
	public void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public void reloadPlugin() {
		reloadConfig();
		
		World defaultWorld = Bukkit.getServer().getWorlds().get(0);
		
		int[] coords = {
			defaultWorld.getSpawnLocation().getBlockX(),
			defaultWorld.getSpawnLocation().getBlockY(),
			defaultWorld.getSpawnLocation().getBlockZ()
		};
		
		int yaw = (int) defaultWorld.getSpawnLocation().getYaw();
		int pitch = (int) defaultWorld.getSpawnLocation().getPitch();
		
		if (!new File(getDataFolder(), "config.yml").exists()) {
			getConfig().addDefault("version", version);
			getConfig().addDefault("options.random-spawn-on-join", false);
			getConfig().addDefault("options.first-join-spawn-group", "default");
			getConfig().addDefault("options.auto-update", false);
			getConfig().addDefault("options.allow-plugin-metrics", true);
			getConfig().addDefault("spawns.default.world", defaultWorld.getName());
			getConfig().addDefault("spawns.default.location", coords[0] + ", " + coords[1] 
					+ ", " + coords[2] + ", " + yaw + ", " + pitch);
			getConfig().addDefault("spawns.default.allow-random-spawn", false);
			getConfig().addDefault("spawns.default.spawn-group", "default");
			
			getConfig().addDefault("portals.default.world", defaultWorld.getName());
			getConfig().addDefault("portals.default.location", "0, 0, 0");
			getConfig().addDefault("portals.default.destination", "default");
			getConfig().addDefault("portals.default.spawn-group", "default");
			getConfig().addDefault("portals.default.random-target", false);
			
			getConfig().options().copyDefaults(true);
		}
		
		getConfig().set("version", version);
		
		if (!getConfig().contains("options.random-spawn-on-join")) {
			getConfig().set("options.random-spawn-on-join", false);
		} else if (getConfig().getBoolean("options.random-spawn-on-join") != true) {
			getConfig().set("options.random-spawn-on-join", false);
		}
		
		if (!getConfig().contains("options.first-join-spawn-group")) {
			getConfig().set("options.first-join-spawn-group", "default");
		}
		
		if (!getConfig().contains("options.auto-update")) {
			getConfig().set("options.auto-update", false);
		} else if (getConfig().getBoolean("options.auto-update") != true) {
			getConfig().set("options.auto-update", false);
		}
		
		if (!getConfig().contains("options.allow-plugin-metrics")) {
			getConfig().set("options.allow-plugin-metrics", true);
		} else if (getConfig().getBoolean("options.allow-plugin-metrics") != true) {
			getConfig().set("options.allow-plugin-metrics", false);
		}
		
		if (getConfig().getConfigurationSection("spawns") == null) {
			getConfig().options().copyDefaults(true);
		}
		
		Set<String> spawnList = getConfig().getConfigurationSection("spawns").getKeys(false);
		spawns = spawnList.toArray(new String[spawnList.size()]);
		ArrayList<String> allowedList = new ArrayList<String>();
		
		for (int i = 0; i < spawns.length; i++) {
			if (getConfig().getBoolean("spawns." + spawns[i] + ".allow-random-spawn") == true) {
				if (spawns[i] != null && spawns[i] != "null") {
					allowedList.add(spawns[i]);
				}
			}
			
			if (!getConfig().contains("spawns." + spawns[i] + ".world") ||
				getConfig().getString("spawns." + spawns[i] + ".world") == null) {
				getConfig().set("spawns." + spawns[i] + ".world", defaultWorld.getName());
			}
			
			if (!getConfig().contains("spawns." + spawns[i] + ".location") ||
				getConfig().get("spawns." + spawns[i] + ".location") == null) {
				getConfig().set("spawns.default.location", coords[0] + ", " + coords[1] 
						+ ", " + coords[2] + ", " + yaw + ", " + pitch);
			}
			
			if (!getConfig().contains("spawns." + spawns[i] + ".allow-random-spawn")) {
				getConfig().set("spawns." + spawns[i] + ".allow-random-spawn", new Boolean(false));
			}
			
			if (!getConfig().contains("spawns." + spawns[i] + ".spawn-group") ||
				getConfig().get("spawns." + spawns[i] + ".spawn-group") == null) {
				getConfig().set("spawns." + spawns[i] + ".spawn-group", "default");
			}
		}
		
		allowed = allowedList.toArray(new String[allowedList.size()]);
		
		Set<String> portalList = getConfig().getConfigurationSection("portals").getKeys(false);
		portals = portalList.toArray(new String[portalList.size()]);
		
		if (getConfig().getConfigurationSection("portals") == null) {
			getConfig().options().copyDefaults(true);
		}
		
		for (int i = 0; i < portals.length; i++) {
			if (!getConfig().contains("portals." + portals[i] + ".world") ||
				getConfig().getString("portals." + portals[i] + ".world") == null) {
				getConfig().set("portals." + portals[i] + ".world", defaultWorld.getName());
			}
			
			if (!getConfig().contains("portals." + portals[i] + ".location") ||
					getConfig().get("portals." + portals[i] + ".location") == null) {
					getConfig().set("portals.default.location", "0, 0, 0");
				}
			
			if (!getConfig().contains("portals." + portals[i] + ".destination")) {
				getConfig().set("portals." + portals[i] + ".destination", "default");
			}
			
			if (!getConfig().contains("portals." + portals[i] + ".spawn-group") ||
				getConfig().get("portals." + portals[i] + ".spawn-group") == null) {
				getConfig().set("portals." + portals[i] + ".spawn-group", "default");
			}
			
			if (!getConfig().contains("portals." + portals[i] + ".random-target") ||
				getConfig().get("portals." + portals[i] + ".random-target") == null) {
				getConfig().set("portals." + portals[i] + ".random-target", false);
			}
		}
		
		if (validFirstGroup() == false) {
			if (getConfig().getBoolean("options.random-spawn-on-join") == true) {
				Bukkit.getLogger().info("[MultiSpawnPlus] The spawn group you have chosen under 'first-join-spawn-group' "
						+ "doesn't have any random spawn points to use! Disabling random spawning on first join...");
				getConfig().set("options.random-spawn-on-join", new Boolean(false));
			} else {
				Bukkit.getLogger().info("[MultiSpawnPlus] The spawn group you have chosen under 'first-join-spawn-group' "
						+ "doesn't have any random spawn points to use!");
			}
		}
		
		saveConfig();
	}
	
	public boolean validFirstGroup() {
		String firstGroup = getConfig().getString("options.first-join-spawn-group");
		
		for (int i = 0; i < allowed.length; i++) {
			if (getConfig().getString("spawns." + allowed[i] + ".spawn-group").equalsIgnoreCase(firstGroup)) {
				return true;
			}
		}
		return false;
	}
	
	public void reloadPortals() {
		Set<String> portalList = getConfig().getConfigurationSection("portals").getKeys(false);
		portals = portalList.toArray(new String[portalList.size()]);
	}
	
	public String[] aliasArgs(String[] args) {
		ArrayList<String> argsList = new ArrayList<String>();
		
		for (int i = 1; i < args.length; i++) {
			argsList.add(args[i]);
		}
		
		String[] arr = argsList.toArray(new String[argsList.size()]);
		return arr;
	}
	
	public String[] getLocations(boolean groupDefined, String group) {
		try {
			Set<String> spawns = getConfig().getConfigurationSection("spawns").getKeys(false);
			ArrayList<String> allowed = new ArrayList<String>();
			ArrayList<String> groupList = new ArrayList<String>();
			
			for (String item : spawns) {
				if (getConfig().getBoolean("spawns." + item + ".allow-random-spawn") == true) {
					if (item != null && item != "null") {
						allowed.add(item);
					}
				}
			}
			
			if (groupDefined) {
				for (String item : allowed) {
					if (getConfig().getString("spawns." + item + ".spawn-group").equalsIgnoreCase(group)) {
						groupList.add(item);
					}
				}
				return groupList.toArray(new String[groupList.size()]);
			} else {
				return allowed.toArray(new String[allowed.size()]);
			}
		} catch (Exception ex) {
			return null;
		}
	}
	
	public Player getPlayerFromName(String player) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(player) ||
				p.getDisplayName().equalsIgnoreCase(player) ||
				p.getName().toLowerCase().startsWith(player.toLowerCase())) {
				return p;
			}
		}
		
		return null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("multispawnplus")) {
			if (args.length < 1) {
				sendMessage(sender, "&b#####################");
				sendMessage(sender, "&eMultiSpawnPlus " + version);
				sendMessage(sender, "&eby UltimateDillon");
				sendMessage(sender, "&b#####################");
				sendMessage(sender, "Use &6/msp help &ffor command syntax.");
			} else {
				if (args[0].equalsIgnoreCase("add")) {
					if (args.length > 2) {
						getCommand("mspAdd").execute(sender, label, aliasArgs(args));
					} else {
						sendMessage(sender, "&cNot enough arguments!");
					}
				} else if (args[0].equalsIgnoreCase("delete")
						|| args[0].equalsIgnoreCase("del")) {
					if (args.length > 2) {
						getCommand("mspDelete").execute(sender, label, aliasArgs(args));
					} else {
						sendMessage(sender, "&cNot enough arguments!");
					}
				} else if (args[0].equalsIgnoreCase("teleport")
						|| args[0].equalsIgnoreCase("tp")
						|| args[0].equalsIgnoreCase("spawn")) {
						getCommand("mspTP").execute(sender, label, aliasArgs(args));
				
				//region List
				} else if (args[0].equalsIgnoreCase("list")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.list")) {
							if (args.length < 2) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter something to list!"));
								player.sendMessage("Usage: /msp list [spawns|portals] [traits]");
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bExamples:"));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "/msp list spawns &8- Lists all "
										+ "spawn points."));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "/msp list spawns random &8- Lists "
										+ "all spawn points that can be teleported to randomly."));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "/msp list spawns random default "
										+ "&8- Lists all random spawn points in the 'default' spawn group."));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "/msp list portals &8- Lists all "
										+ "portal blocks."));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "/msp list portals random &8- Lists "
										+ "all portal blocks that teleport the player to a random spawn point."));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "/msp list portals random default "
										+ "&8- Lists all random portal blocks that teleport the player to spawn points in the "
										+ "'default' spawn group."));
							} else if (args.length > 4) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
								player.sendMessage("Usage: /msp list [spawns|portals] [traits]");
							} else {
								if (args[1].equalsIgnoreCase("spawns")) {
									if (args.length > 2) {
										if (args[2].equalsIgnoreCase("random")) {
											if (args.length > 3) {
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All spawn points in the &f"
														+ args[3] + " &6spawn group:"));
												
												for (int i = 0; i < spawns.length; i++) {
													if (getConfig().getString("spawns." + spawns[i] 
															+ ".spawn-group").equalsIgnoreCase(args[3])) {
														player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- &b" + spawns[i]));
													}
												}
											} else {
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All random spawn points:"));
												
												for (int i = 0; i < spawns.length; i++) {
													if (getConfig().getBoolean("spawns." + spawns[i] 
															+ ".allow-random-spawn") == true) {
														player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- &b" + spawns[i]));
													}
												}
											} 
										} else {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid Syntax!"));
										}
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All defined spawn points:"));
										
										for (int i = 0; i < spawns.length; i++) {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- &b" + spawns[i]));
										}
									}
								} else if (args[1].equalsIgnoreCase("portals")) {
									Set<String> portalList = getConfig().getConfigurationSection("portals").getKeys(false);
									portals = portalList.toArray(new String[portalList.size()]);
									
									if (args.length > 2) {
										if (args[2].equalsIgnoreCase("random")) {
											if (args.length > 3) {
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All portals in the &f"
														+ args[3] + " &6spawn group:"));
												
												for (int i = 0; i < portals.length; i++) {
													if (getConfig().getString("portals." + portals[i] 
															+ ".spawn-group").equalsIgnoreCase(args[3])) {
														player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- &b" + portals[i]));
													}
												}
											} else {
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All random portals:"));
												
												for (int i = 0; i < portals.length; i++) {
													if (getConfig().getString("portals." + portals[i] 
															+ ".destination").equalsIgnoreCase("random")) {
														player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- &b" + portals[i]));
													}
												}
											} 
										} else {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid Syntax!"));
										}
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All defined portals:"));
										
										for (int i = 0; i < portals.length; i++) {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- &b" + portals[i]));
										}
									}
								} else {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid Arguments!"));
									player.sendMessage("Usage: /msp list [spawns|portals] [traits]");
								}
							}
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						sender.sendMessage("[MultiSpawnPlus] All spawn points in order of creation:");
						
						for (int i = 0; i < spawns.length; i++) {
							sender.sendMessage("- " + spawns[i]);
						}
					}
				//endregion
					
				//region Reload
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.reload")) {
							reloadPlugin();
							reloadPortals();
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6MultiSpawnPlus config reloaded!"));
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						reloadPlugin();
						reloadPortals();
						sender.sendMessage("[MultiSpawnPlus] Config reloaded!");
					}
				//endregion
				
				//region Help
				} else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.help")) {
							return false;
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						return false;
					}
				//endregion
				
				} else {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid argument!"));
					} else {
						sender.sendMessage("[MultiSpawnPlus] Invalid argument!");
					}
					
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
}
