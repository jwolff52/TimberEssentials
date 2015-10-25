package io.github.jwolff52.timberessentials.util;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WarpsGUI {
	
	private static HashMap<Player,Inventory> invs;
	private static HashMap<String,ItemStack> warps;
	
	public static void setup(){
		invs=new HashMap<>();
		warps=new HashMap<>();
	}
	
	public static void addWarp(Material mat, String name){
		ItemStack warp=new ItemStack(Material.FEATHER,1);
		if(mat != null){
			warp=new ItemStack(mat,1);
		}
		ItemMeta warpMeta=warp.getItemMeta();
		warpMeta.setDisplayName(name);
		warp.setItemMeta(warpMeta);
		warps.put(name, warp);
	}
	
	public static Inventory getInventory(HumanEntity humanEntity){
		return invs.get(humanEntity);
	}
	
	public static void show(Player player){
		initializePlayerGUI(player);
		player.openInventory(invs.get(player));
	}
	
	public static void initializePlayerGUI(Player p){
		invs.put(p, Bukkit.getServer().createInventory(p, 27, p.getName()+"\'s Warps"));
		for(ItemStack i:warps.values()){
			if(p.hasPermission("te.warps.allow."+i.getItemMeta().getDisplayName().toLowerCase())){
				invs.get(p).addItem(i);
			}
		}
	}
}
