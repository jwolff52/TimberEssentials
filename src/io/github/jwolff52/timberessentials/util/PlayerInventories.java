package io.github.jwolff52.timberessentials.util;

import io.github.jwolff52.timberessentials.minigames.arenas.ArenaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInventories {

	private static HashMap<UUID,ItemStack[][]> inventories;
	
	private static ItemStack[] setupInventory;
	
	private static HashMap<ArenaType,ArrayList<ItemStack>> defaultInventories=new HashMap<>();
	private static HashMap<ArenaType,ArrayList<ItemStack>> defaultArmors=new HashMap<>();
	
	public static void setup(){
		inventories=new HashMap<>();
		setupDefaultInventories();
	}
	
	private static void setupDefaultInventories(){
		setupSetupInventory();
		setupOIQArmor();
		setupOIQInventory();
	}
	
	private static void setupSetupInventory(){
		setupInventory=new ItemStack[4];
		ItemMeta meta;
		ItemStack tool;
		tool=new ItemStack(Material.GOLD_AXE, 1);
		meta=tool.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Arena Region");
		tool.setItemMeta(meta);
		setupInventory[0]=tool;
		tool=new ItemStack(Material.GOLD_HOE, 1);
		meta=tool.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Warps");
		tool.setItemMeta(meta);
		setupInventory[1]=tool;
		tool=new ItemStack(Material.GOLD_SWORD, 1);
		meta=tool.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD+"Spawnpoints");
		tool.setItemMeta(meta);
		setupInventory[2]=tool;
		tool=new ItemStack(Material.GOLD_AXE, 1);
		meta.setDisplayName(ChatColor.GOLD+"Lobby Region");
		tool.setItemMeta(meta);
		setupInventory[3]=tool;
	}
	
	private static void setupOIQArmor(){
		ArrayList<ItemStack> inventory=new ArrayList<>();
		ItemStack armor;
		armor=new ItemStack(Material.DIAMOND_BOOTS);
		inventory.add(armor);
		armor=new ItemStack(Material.DIAMOND_LEGGINGS);
		inventory.add(armor);
		armor=new ItemStack(Material.DIAMOND_CHESTPLATE);
		inventory.add(armor);
		armor=new ItemStack(Material.DIAMOND_HELMET);
		inventory.add(armor);
		defaultArmors.put(ArenaType.OIQ, inventory);
	}
	
	private static void setupOIQInventory(){
		ArrayList<ItemStack> inventory=new ArrayList<>();
		ItemStack item;
		item=new ItemStack(Material.BOW, 1);
		inventory.add(item);
		item=new ItemStack(Material.ARROW, 1);
		inventory.add(item);
		defaultInventories.put(ArenaType.OIQ, inventory);
	}
	
	public static ItemStack[] getSetupInventory(){
		return setupInventory;
	}
	
	public static ItemStack[] getArenaArmor(ArenaType type){
		return (ItemStack[]) defaultArmors.get(type).toArray(new ItemStack[defaultArmors.get(type).size()]);
	}
	
	public static ItemStack[] getArenaInventory(ArenaType type){
		return (ItemStack[]) defaultInventories.get(type).toArray(new ItemStack[defaultInventories.get(type).size()]);
	}
	
	public static void addInventory(Player p){
		inventories.put(p.getUniqueId(), new ItemStack[][] {p.getInventory().getArmorContents(),p.getInventory().getContents()});
	}
	
	public static ItemStack[][] getInventory(Player p){
		return inventories.get(p.getUniqueId());
	}
}
