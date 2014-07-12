package me.leo.energy.item;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class NaturalEnergy extends NewItem {

	public NaturalEnergy() {
		super("NaturalEnergy");
		drops.put(Material.LEAVES, 0.1f);
		drops.put(Material.LEAVES_2, 0.1f);
		drops.put(Material.GRASS, 0.05f);
		drops.put(Material.WOOD, 0.3f);
	}

	@EventHandler
	public void interactEvent(final PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR
				|| e.getAction() == Action.RIGHT_CLICK_BLOCK) {

		}
	}
	
	private HashMap<Material, Float> drops = new HashMap<Material, Float>();
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void breakEvent(BlockBreakEvent e) {
		Material t = e.getBlock().getType();
		if(drops.containsKey(t)) {
			float f = drops.get(t);
			double d = Math.random();
			if(d<f) {
				Location l = e.getBlock().getLocation();
				l.getWorld().dropItem(l, getItem());
			}
		}
	}
	
	private HashMap<UUID, Integer> ticks = new HashMap<UUID, Integer>();
	
	
	@Override
	public void update() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (isItem(p.getItemInHand()) && p.isSneaking() && p.isOnGround()) {
				int t = 0;
				if(ticks.containsKey(p.getUniqueId()))
					t = ticks.get(p.getUniqueId());
				
				ticks.put(p.getUniqueId(), ++t);
				List<Entity> entities = getNearbyEntitysByType(p.getLocation(),
						4, Entity.class);
				
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
				
				World w = p.getWorld();
				
				for(int i = 0; i<9; i++)
					w.playEffect(p.getLocation(), Effect.SMOKE, i);
				
				for (Entity e : entities) {
					if (e == p)
						continue;
					Vector v = e.getVelocity();

					Location l = e.getLocation().subtract(p.getLocation());

					Vector dir = new Vector(l.getX(), l.getY(), l.getZ());
					dir.add(new Vector(0, 0.03, 0));
					double d = dir.length();

					d = 4 - d;

					dir = dir.normalize();

					v = v.add(dir.multiply(d * 2));
					e.setVelocity(v);
					
				}
			} else {
				int t = 0;
				if(ticks.containsKey(p.getUniqueId()))
					t = ticks.get(p.getUniqueId());
				ticks.put(p.getUniqueId(), 0);
				
				
				World w = p.getWorld();
				
				
				
				if(t>60) {
					for(int i = 0; i<9; i++)
						w.playEffect(p.getLocation().add(0, 0.5, 0), Effect.SMOKE, i);
					for(int i = 0; i<9; i++)
						w.playEffect(p.getLocation().add(0, 1, 0), Effect.SMOKE, i);
					for(int i = 0; i<9; i++)
						w.playEffect(p.getLocation().add(0, 1.5, 0), Effect.SMOKE, i);
					
					List<Entity> entities = getNearbyEntitysByType(p.getLocation(),
							6f, Entity.class);

					for (Entity e : entities) {
						if (e == p)
							continue;
						Vector v = e.getVelocity();

						Location l = e.getLocation().subtract(p.getLocation());

						Vector dir = new Vector(l.getX(), l.getY(), l.getZ());
						dir.add(new Vector(0, 0.3, 0));
						dir = dir.normalize();

						v = v.add(dir.multiply(5));
						e.setVelocity(v);
					}
					
				}
			}
		}
	}

	
	
	
	@Override
	public int getTick() {
		return 1;
	}

	@Override
	public ItemStack getStack() {
		ItemStack i = new ItemStack(Material.INK_SACK);
		i.setDurability((short) 2);

		return setNameAndLore(i, ChatColor.AQUA + "Energy",
				ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC
						+ "A great source of power");
	}

}
