package me.leo.energy.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class NewItem implements Listener {
	
	private final String name;
	private final int id;
	private final String tag;
	private int lastTick = 0;
	
	
	public NewItem(String name) {
		id = toId(name);
		tag = idToTag(id);
		this.name = name;
	}
	
	public abstract ItemStack getStack();
	public void update() {}
	public int getTick() {return 0;}
	
	@Deprecated
	public void tick(int t) {
		int tt = getTick();
		if(tt<=0)
			return;
		if(t-lastTick >= tt)
			update();
	}
	
	public final int getId() {
		return id;
	}
	
	public final String getName() {
		return name;
	}
	
	public final String getTag() {
		return tag;
	}
	
	/**
	 * Returns a tagged itemstack
	 */
	public final ItemStack getItem() {
		ItemStack i = getStack();
		
		addTag(i, id);
		return i;
	}
	
	/**
	 * If the item is tagged with this classes tag
	 */
	public final boolean isItem(ItemStack i) {
		return hasTag(i, tag);
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity> ArrayList<T> getNearbyEntitysByType(Location l, float radius, Class<T> type) {

		float r2 = radius * radius;
		int x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();

		int chr = radius < 16 ? 1 : (int) ((radius - (radius % 16)) / 16);

		ArrayList<T> ret = new ArrayList<T>();

		for (int cX = 0 - chr; cX <= chr; cX++)
			for (int cZ = 0 - chr; cZ <= chr; cZ++)
				for (Entity e : new Location(l.getWorld(), x + (cX * 16), y, z
						+ (cZ * 16)).getChunk().getEntities())
					if (type.isAssignableFrom(e.getClass())) {
						Location l1 = e.getLocation();
						double i;
						double d2 = ((i = l1.getX() - x) * i
								+ (i = l1.getY() - y) * i + (i = l1.getZ() - z)
								* i);
						if (d2 < r2)
							ret.add((T) e);
					}

		return ret;
	}
	
	public static ItemStack setNameAndLore(ItemStack i, String name, String ... lore) {
		if(i == null)
			return null;
		ItemMeta m = i.getItemMeta();
		if(m == null)
			return i;
		
		List<String> ll = m.getLore();
		if(ll == null)
			ll = new ArrayList<String>();
		ll.clear();
		
		for(String s : lore)
			ll.add(s);
		
		m.setDisplayName(name);
		m.setLore(ll);
		i.setItemMeta(m);
		return i;
	}
	
	/**
	 * Checks if the item is tagged
	 */
	public static boolean hasTag(ItemStack i, int id) {
		return hasTag(i, idToTag(id));
	}
	
	/**
	 * Checks if the item is tagged
	 */
	public static boolean hasTag(ItemStack i, String tag) {
		if(i == null)
			return false;
		ItemMeta m = i.getItemMeta();
		if(m == null)
			return false;
		
		List<String> lore = m.getLore();
		
		if(lore == null || lore.size() == 0)
			return false;
		
		String txt = lore.get(0);
		
		
		return txt.startsWith(tag);
	}
	
	/**
	 * Adds the tagId to the lore
	 * @param i
	 * @param tag
	 */
	public static void addTag(ItemStack i, int id) {
		ItemMeta m = i.getItemMeta();
		
		if(m == null)
			throw new NullPointerException("The item has no lore");
		
		List<String> lore = m.getLore();
		
		if(lore == null)
			lore = new ArrayList<String>();
		
		String tag = idToTag(id);
		
		if(lore.size() > 0) {
			String s = lore.get(0);
			
			if(s.length()>7) {
				char c = ChatColor.COLOR_CHAR;
				if(s.charAt(0) == c && s.charAt(2) == c && s.charAt(4) == c && s.charAt(6) == c) {
					if(s.length()>8) {
						if(s.charAt(8) == c) {
							s = tag + s.substring(8, s.length());
							System.out.println(s);
						} else {
							s = tag + s.substring(6, s.length());
						}
					} else {
						s = tag;
					}
				} else {
					if(s.length()>0 && s.charAt(0) == ChatColor.COLOR_CHAR) {
						s = tag + s;
					} else {
						s = tag + ChatColor.RESET + s;
					}
				}
			}
			
			lore.set(0, s);
		} else {
			lore.add(tag);
		}
		m.setLore(lore);
		i.setItemMeta(m);
	}
	
	/**
	 * Converts an int id to itemTag
	 * @param id The id to use
	 * @return The phrased tag
	 */
	public static String idToTag(int id) {
		char c = ChatColor.COLOR_CHAR;
		StringBuilder b = new StringBuilder();
		
		for (int i = 3; i >= 0; i--)
			b.append(c).append((char) (id >>> 8 * i & 0xFF));
		
		return b.toString();
	}
	
	/**
	 * Same as String.toLowerCase().hashCode()
	 */
	public static int toId(String name) {
		return name.toLowerCase().hashCode();
	}
	
}
