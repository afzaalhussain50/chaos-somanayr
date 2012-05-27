package vacuum.npcs;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import vacuum.npcs.hooks.EntityNPC;

public class Plugin extends JavaPlugin {
	
	public static File pluginDataFolder;

	@Override
	public void onEnable(){
		this.pluginDataFolder = getDataFolder();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(command.getName().equals("spawnnpc")){
			/*Villager v = ((Player)sender).getWorld().spawn(((Player)sender).getLocation(), Villager.class);
			v.setVelocity(new Vector(Math.random() * 20 - 10, Math.random() * 10, Math.random() * 20 - 10));*/
			sender.sendMessage("Spawning NPC");
			new EntityNPC((args.length > 0) ? args[0] : "Player");
			return true;
		}
		return false;
	}

}
