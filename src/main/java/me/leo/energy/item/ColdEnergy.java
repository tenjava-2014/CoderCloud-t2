package me.leo.energy.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ColdEnergy extends NewItem {

	public ColdEnergy() {
		super("ColdEnergy");
		drops.put(Material.SNOW, 0.05f);
		drops.put(Material.ICE, 0.2f);
		drops.put(Material.SNOW_BLOCK, 0.5f);
	}

	@EventHandler
	public void interactEvent(final PlayerInteractEvent e) {
		if(isItem(e.getPlayer().getItemInHand()))
			e.setUseItemInHand(Result.DENY);
	}

	private HashMap<Material, Float> drops = new HashMap<Material, Float>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void breakEvent(BlockBreakEvent e) {
		Material t = e.getBlock().getType();
		if (drops.containsKey(t)) {
			float f = drops.get(t);
			double d = Math.random();
			if (d < f) {
				Location l = e.getBlock().getLocation();
				l.getWorld().dropItem(l, getItem());
			}
		}
	}

	private HashMap<UUID, Freeze> f = new HashMap<UUID, Freeze>();
	private HashSet<Block> freeze = new HashSet<Block>();
	private HashMap<UUID, Integer> waterwalktime = new HashMap<UUID, Integer>();
	
	@Override
	public void update() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			{
				for (Block b : freeze)
					b.setType(Material.WATER);
				freeze.clear();
			}
			if (isItem(p.getItemInHand()) && !p.isSneaking() && p.isSprinting()) {
				Location block = p.getLocation().add(0, -1, 0);
				
				int t = 0;
				if(waterwalktime.containsKey(p.getUniqueId()))
					t = waterwalktime.get(p.getUniqueId());
				Material ma = block.getWorld().getBlockAt(block).getType();
				if(ma == Material.WATER || ma == Material.STATIONARY_WATER) {
					t++;
					waterwalktime.put(p.getUniqueId(), t);
					if((t-1)%10 == 0) {
						ItemStack s = p.getItemInHand();
						if(p.getGameMode() != GameMode.CREATIVE) {
							if(s.getAmount() == 1) {
								p.setItemInHand(new ItemStack(Material.AIR));
							} else {
								s.setAmount(s.getAmount()-1);
							}
						}
					}
				}
				
				for (int x = -1; x <= 1; x++)
					for (int y = -1; y <= 1; y++) {
						Location l = new Location(block.getWorld(),
								block.getX() + x, block.getY(), block.getZ()
										+ y);
						Block b = l.getWorld().getBlockAt(l);
						if (b.getType() == Material.WATER
								|| b.getType() == Material.STATIONARY_WATER) {
							b.setType(Material.ICE);
							freeze.add(b);
						}
					}
			}
			if (isItem(p.getItemInHand()) && p.isSneaking()) {
				int t = 0;
				if(waterwalktime.containsKey(p.getUniqueId()))
					t = waterwalktime.get(p.getUniqueId());
				waterwalktime.put(p.getUniqueId(), ++
						t);
				if((t-1)%10 == 0) {
					ItemStack s = p.getItemInHand();
					if(p.getGameMode() != GameMode.CREATIVE) {
						if(s.getAmount() == 1) {
							p.setItemInHand(new ItemStack(Material.AIR));
						} else {
							s.setAmount(s.getAmount()-1);
						}
					}
				}
				if (!f.containsKey(p.getUniqueId())) {
					f.put(p.getUniqueId(), new Freeze(p.getLocation(), 4));
				}
			} else {
				if (f.containsKey(p.getUniqueId()))
					f.remove(p.getUniqueId()).cancel();
			}
		}
	}

	private class Freeze {

		HashSet<Block> locs = new HashSet<Block>();
		HashSet<Block> waterlocs = new HashSet<Block>();
		
		public Freeze(Location l, int rad) {
			for (int x = -rad; x <= rad; x++)
				for (int y = -rad; y <= rad; y++)
					for (int z = -rad; z <= rad; z++) {
						float r2 = rad * rad;
						float d2 = x * x + y * y + z * z;
						if (d2 < r2) {
							Location l2 = new Location(l.getWorld(), l.getX()
									+ x, l.getY() + y, l.getZ() + z);
							Block b = l.getWorld().getBlockAt(l2);
							if (b.getType() == Material.AIR) {
								locs.add(b);
								b.setType(Material.ICE);
							}
							if (b.getType() == Material.WATER
									|| b.getType() == Material.STATIONARY_WATER) {
								waterlocs.add(b);
								b.setType(Material.ICE);
							}
						}
					}
		}

		private void cancel() {
			for (Block b : locs) {
				b.setType(Material.AIR);
			}
			for (Block b : waterlocs) {
				b.setType(Material.WATER);
			}
		}

	}

	@Override
	public int getTick() {
		return 1;
	}

	@Override
	public ItemStack getStack() {
		ItemStack i = new ItemStack(Material.SNOW_BALL);

		return setNameAndLore(i, ChatColor.AQUA + "ColdEnergy",
				ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC
						+ "A great source of cold power");
	}

}
