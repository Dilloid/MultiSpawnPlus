<h1>MultiSpawnPlus</h1>
<h3>Spawn players at various defined points randomly</h3>
<p>This plugin has been created to provide various new ways to spawn and teleport players on your server, ranging from automatic spawning on first join, to portals set up with just a few commands.</p>
<h4><a href="http://dev.bukkit.org/bukkit-plugins/multi-spawn-plus/" alt="MultiSpawnPlus @ BukkitDev">Plugin Page @ BukkitDev</a></h4>

<h4>Command Usage</h4>
<ul>
	<li><strong>/msp add [name] [true|false] [spawn group]</strong> - Adds a spawn point where you are standing and sets whether or not players can spawn at it randomly.</li>
	<li><strong>/msp addportal [name] [destination] [spawn group]</strong> - Adds a portal on the block you are targeting</li>
	<li><strong>/msp delete [name]</strong> - Deletes a spawn point from the server.</li>
	<li><strong>/msp delportal [name]</strong> - Deletes a portal block from the server.</li>
	<li><strong>/msp spawn [name]</strong> - Teleports you to a specific spawn point.</li>
	<li><strong>/msp random [spawn group]</strong> - Teleports you to a random spawn point if its allow-random-spawn setting is set to true.</li>
	<li><strong>/msp list [spawns|portals] [traits]</strong> - Lists all defined spawn points.</li>
	<li><strong>/msp reload</strong> - Reloads the plugin and configuration.</li>
	<li><strong>/msp help</strong> - Shows a list of commands.</li>
</ul>

<h4>Config</h4>
<ul>
	<li><strong>options.random-spawn-on-join: true/false</strong> - Tells the plugin whether to spawn new players at one of the allowed random spawns.</li>
	<li><strong>spawns.[name].allow-random-spawn: true/false</strong> - Tells the plugin whether this spawnpoint can be teleported to by new players or players typing the /msp random command.</li>
</ul>

<h3>Permissions</h3>
<ul>
	<li><strong>multispawnplus.*</strong> - Gives access to all features of MultiSpawnPlus.</li>
	<li><strong>multispawnplus.add.*</strong> - Allows the user to create portal blocks and spawn points.</li>
	<li><strong>multispawnplus.add.spawn</strong> - Allows the user to create spawn points.</li>
	<li><strong>multispawnplus.add.portal</strong> - Allows the user to create portal blocks.</li>
	<li><strong>multispawnplus.delete.*</strong> - Allows the user to remove portal blocks and spawn points.</li>
	<li><strong>multispawnplus.delete.spawn</strong> - Allows the user to remove spawn points.</li>
	<li><strong>multispawnplus.delete.portal</strong> - Allows the user to remove portal blocks.</li>
	<li><strong>multispawnplus.spawn</strong> - Allows the user to teleport to any defined spawn point.</li>
	<li><strong>multispawnplus.random</strong> - Allows the user to teleport to a random spawn point.</li>
	<li><strong>multispawnplus.list</strong> - Allows the user to see a list of all defined spawn points.</li>
	<li><strong>multispawnplus.reload</strong> - Allows the user to reload the plugin and config.</li>
	<li><strong>multispawnplus.help</strong> - Allows the user to see a list of MultiSpawnPlus commands.</li>
</ul>

<h4>MCStats / Plugin Metrics</h4>
This plugin is sending server statistics to MCStats.org as of v1.2.24. To opt out, set <strong>allow-plugin-metrics</strong> to 'false' in the MultiSpawnPlus config.

<h4>Updater</h4>
If <strong>auto-update</strong> is set to 'true', this plugin will automatically download updates using Updater (as of v1.2.27).
You may also set it to 'false' to only receive a message via the console if Updater detects a new version.

<h4>Questions or Suggestions?</h4>
<p>Email me bug reports and questions at <a href="mailto:freetheenslaved@gmail.com" alt="Email Me">freetheenslaved@gmail.com</a></p>
