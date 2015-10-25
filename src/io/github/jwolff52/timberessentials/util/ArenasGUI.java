package io.github.jwolff52.timberessentials.util;

import io.github.jwolff52.timberessentials.minigames.OneInTheQuiver;
import io.github.jwolff52.timberessentials.minigames.arenas.DefaultArena.Arenas;
import io.github.jwolff52.timberessentials.minigames.arenas.OIQArena;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArenasGUI {
	
	private static HashMap<Player,Inventory> invs;
	private static HashMap<String,ItemStack> arenas;
	
	public static void setup(){
		invs=new HashMap<>();
		arenas=new HashMap<>();
	}
	
	public static void addArena(Material mat, String name){
		ItemStack arena=new ItemStack(Material.DIAMOND_SWORD,1);
		if(mat != null){
			arena=new ItemStack(mat,1);
		}
		ItemMeta arenaMeta=arena.getItemMeta();
		arenaMeta.setDisplayName(name);
		arena.setItemMeta(arenaMeta);
		arenas.put(name, arena);
	}
	
	public static Inventory getInventory(HumanEntity humanEntity){
		return invs.get(humanEntity);
	}
	
	public static void show(Player player){
		initializePlayerGUI(player);
		player.openInventory(invs.get(player));
	}
	
	public static void initializePlayerGUI(Player p){
		invs.put(p, Bukkit.getServer().createInventory(p, 27, p.getName()+"\'s Arenas"));
		for(ItemStack i:arenas.values()){
			if(p.hasPermission("te.arenas.allow."+i.getItemMeta().getDisplayName().toLowerCase())){
				ItemMeta arenaMeta=i.getItemMeta();
				arenaMeta.setLore(Arrays.asList("Current Players: ",((OIQArena)Arenas.getArenaFromName(i.getItemMeta().getDisplayName())).getOIQ().getReadyPlayers().size()+"/"+OneInTheQuiver.MAX_SCORE));
				i.setItemMeta(arenaMeta);
				invs.get(p).addItem(i);
			}
		}
	}
}
