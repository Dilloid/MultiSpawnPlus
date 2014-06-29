<h1>MultiSpawnPlus</h1>
<h3>Spawn players at various defined spawnpoints randomly</h3>
<p>This plugin has been made so you can control (or rather not control) where players spawn, either on first login or by typing a command.</p>

<h4>Command Usage:</h4>
<ul>
	<li><strong>/msp add <name> [true|false]</strong> - Adds a spawn point where you are standing and sets whether or not players can spawn at it randomly.</li>
	<li><strong>/msp delete <name></strong> - Deletes a spawn point from the server.</li>
	<li><strong>/msp spawn <name></strong> - Teleports you to a specific spawn point.</li>
	<li><strong>/msp random</strong> - Teleports you to a random spawn point if its allow-random-spawn setting is set to true.</li>
	<li><strong>/msp list</strong> - Lists all defined spawn points.</li>
	<li><strong>/msp reload</strong> - Reloads the plugin and configuration.</li>
	<li><strong>/msp help</strong> - Shows a list of commands.</li>
</ul>

<h4>Config</h4>
<ul>
	<li><strong>options.random-spawn-on-join: true/false</strong> - Tells the plugin whether to spawn new players at one of the allowed random spawns.</li>
	<li><strong>spawns.<name>.allow-random-spawn: true/false</strong> - Tells the plugin whether this spawnpoint can be teleported to by new players or players typing the /msp random command.</li>
</ul>

<h3>Permissions</h3>
<ul>
	<li><strong>multispawnplus.*</strong> - Gives access to all features of MultiSpawnPlus</li>
	<li><strong>multispawnplus.add</strong> - Allows the user to create spawn points.</li>
	<li><strong>multispawnplus.delete</strong> - Allows the user to remove spawn points.</li>
	<li><strong>multispawnplus.spawn</strong> - Allows the user to teleport to any defined spawn point.</li>
	<li><strong>multispawnplus.random</strong> - Allows the user to teleport to a random spawn point.</li>
	<li><strong>multispawnplus.list</strong> - Allows the user to see a list of all defined spawn points.</li>
	<li><strong>multispawnplus.reload</strong> - Allows the user to reload the plugin and config.</li>
	<li><strong>multispawnplus.help</strong> - Allows the user to see a list of MultiSpawnPlus commands.</li>
</ul>

<h4>Questions?</h4>
<p>Email me bug reports and questions at <a href="mailto:freetheenslaved@gmail.com" alt="Email Me">freetheenslaved@gmail.com</a></p>
