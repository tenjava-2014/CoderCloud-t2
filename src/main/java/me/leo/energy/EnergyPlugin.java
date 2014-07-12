package me.leo.energy;

import java.util.HashMap;

import me.leo.energy.item.ColdEnergy;
import me.leo.energy.item.NaturalEnergy;
import me.leo.energy.item.NewItem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class EnergyPlugin extends JavaPlugin implements Runnable {
	
	private HashMap<Integer, NewItem> items = new HashMap<Integer, NewItem>();
	
	@Override
	public void onEnable() {
		addItem(new NaturalEnergy());
		addItem(new ColdEnergy());
		getServer().getScheduler().runTaskTimer(this, this, 1, 1);
	}
	
	@Override
	public void onDisable() {
		items.clear();
		HandlerList.unregisterAll(this);
	}
	
	private void addItem(NewItem i) {
		if(items.containsKey(i.getId()))
			throw new IllegalArgumentException("Can't have two items with the same id");
		items.put(i.getId(), i);
		getServer().getPluginManager().registerEvents(i, this);
	}
	
	public NewItem getItem(int id) {
		return items.get(id);
	}
	
	public NewItem getItem(String name) {
		return getItem(NewItem.toId(name));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			p.getInventory().addItem(getItem("ColdEnergy").getItem());
			
			
		}
		return true;
	}

	
	private int i = 0;
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		i++;
		for(NewItem i : items.values())
			i.tick(this.i);
	}
	
}
