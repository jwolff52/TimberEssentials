package io.github.jwolff52.timberessentials.minigames;

import io.github.jwolff52.timberessentials.TimberEssentials;
import io.github.jwolff52.timberessentials.minigames.arenas.ArenaType;
import io.github.jwolff52.timberessentials.minigames.arenas.DefaultArena;
import io.github.jwolff52.timberessentials.minigames.arenas.DefaultArena.Arenas;
import io.github.jwolff52.timberessentials.minigames.arenas.OIQArena;
import io.github.jwolff52.timberessentials.minigames.events.ArenaDeathEvent;
import io.github.jwolff52.timberessentials.minigames.events.ArenaJoinEvent;
import io.github.jwolff52.timberessentials.minigames.events.ArenaLeaveEvent;
import io.github.jwolff52.timberessentials.minigames.events.ArenaSetupEvent;
import io.github.jwolff52.timberessentials.util.PlayerInventories;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

public class MinigameListener implements Listener{
	
	private TimberEssentials te;
	private static HashMap<Player,DefaultArena> inSetupMode;
	
	public MinigameListener(TimberEssentials p){
		te=p;
		inSetupMode=new HashMap<>();
	}
	
	public static void addArena(DefaultArena arena, Player p){
		inSetupMode.put(p,arena);
	}
	
	public static void removeArena(Player player){
		inSetupMode.remove(player);
	}
	
	private void setLocation(FileConfiguration fc, String rootNode, Location loc){
		fc.set(rootNode+"world", loc.getWorld().getName());
		fc.set(rootNode+"x", loc.getX());
		fc.set(rootNode+"y", loc.getY());
		fc.set(rootNode+"z", loc.getZ());
		te.sm.saveArenas();
	}
	
	private void setPreciseLocation(FileConfiguration fc, String rootNode, Location loc){
		fc.set(rootNode+"world", loc.getWorld().getName());
		fc.set(rootNode+"x", loc.getX());
		fc.set(rootNode+"y", loc.getY());
		fc.set(rootNode+"z", loc.getZ());
		fc.set(rootNode+"yaw", loc.getYaw());
		fc.set(rootNode+"pitch", loc.getPitch());
		te.sm.saveArenas();
	}
	
	@EventHandler
	public void onArenaJoinEvent(ArenaJoinEvent e){
		Player p=e.getPlayer();
		DefaultArena a=e.getArena();
		p.teleport(a.getLobbyWarp(), TeleportCause.COMMAND);
		p.sendMessage(te.getTitle()+"You have joined "+ChatColor.GREEN+a.getName()+ChatColor.AQUA+"!");
		PlayerInventories.addInventory(p);
		switch(a.getType()){
		case OIQ:
			((OIQArena)a).getOIQ().addPlayer(p);
			Arenas.addPlayer(p, a);
		default:
			break;
		}
		p.getInventory().setArmorContents(PlayerInventories.getArenaArmor(a.getType()));
		p.getInventory().setContents(PlayerInventories.getArenaInventory(a.getType()));
	}
	
	@EventHandler
	public void onArenaLeaveEvent(ArenaLeaveEvent e){
		Player p=e.getPlayer();
		DefaultArena a=e.getArena();
		p.teleport(te.sm.getLastLocation(p), TeleportCause.COMMAND);
		p.sendMessage(te.getTitle()+"You have left or been kicked from "+ChatColor.GREEN+a.getName()+ChatColor.AQUA+" and teleported to your previous location!");
		p.getInventory().clear();
		p.getInventory().setArmorContents(PlayerInventories.getInventory(p)[0]);
		p.getInventory().setContents(PlayerInventories.getInventory(p)[1]);
		switch(a.getType()){
		case OIQ:
			((OIQArena)a).getOIQ().removePlayer(p);
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	public void onArenaSetupEvent(ArenaSetupEvent e){
		Player p=e.getPlayer();
		DefaultArena a=e.getArena();
		if(a.getSetupMode()){
			PlayerInventories.addInventory(p);
			p.getInventory().setContents(PlayerInventories.getSetupInventory());
		}else{
			p.getInventory().setContents(PlayerInventories.getInventory(p)[1]);
		}
	}
	
	@EventHandler
	public void onArenaDeathEvent(ArenaDeathEvent e){
		DefaultArena arena=e.getArena();
		Player target=e.getTarget();
		Player damager=e.getDamager();
		target.sendMessage(te.getTitle()+"You were killed by "+damager.getName());
		switch(arena.getType()){
		case OIQ:
			target.getInventory().setContents(PlayerInventories.getArenaInventory(ArenaType.OIQ));
			((OIQArena)arena).respawn(target);
			damager.sendMessage(te.getTitle()+"You killed "+target.getName());
			damager.getInventory().addItem(new ItemStack(Material.ARROW, 1));
			((OIQArena)arena).getOIQ().notifyPlayers(te.getTitle()+damager.getName()+" killed "+target.getName(), damager, target);
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		Player p=e.getPlayer();
		if(inSetupMode.containsKey(p)){
			if(e.getMaterial().equals(Material.GOLD_AXE)||e.getMaterial().equals(Material.GOLD_HOE)||e.getMaterial().equals(Material.GOLD_SWORD)){
				DefaultArena a=inSetupMode.get(p);
				if(a instanceof OIQArena){
					OIQArena arena=(OIQArena)a;
					if(e.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD+"Arena Region")){
						if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
							Location loc=e.getClickedBlock().getLocation();
							final Block block=e.getClickedBlock();
							final Material material=e.getClickedBlock().getType();
							arena.setArenaPos1(loc);
							setLocation(te.sm.getArenaData(arena.getName()), "arena.a1.", loc);
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(te, new Runnable(){

								@Override
								public void run() {
									block.setType(material);
								}
								
							}, 10);
							p.sendRawMessage(te.getTitle()+ChatColor.AQUA+" Arena Position 1 set!");
						}else if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
							Location loc=e.getClickedBlock().getLocation();
							arena.setArenaPos2(loc);
							setLocation(te.sm.getArenaData(arena.getName()), "arena.a2.", loc);
							p.sendRawMessage(te.getTitle()+ChatColor.AQUA+" Arena Position 2 set!");
						}
						return;
					}else if(e.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD+"Warps")){
						if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
							Location loc=e.getClickedBlock().getLocation();
							loc.setY(loc.getY()+1);
							final Block block=e.getClickedBlock();
							final Material material=e.getClickedBlock().getType();
							arena.setLobbyWarp(loc);
							setPreciseLocation(te.sm.getArenaData(arena.getName()), "lobby.warp.", loc);
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(te, new Runnable(){

								@Override
								public void run() {
									block.setType(material);
								}
								
							}, 10);
							p.sendRawMessage(te.getTitle()+ChatColor.AQUA+" Lobby Warp set!");
						}else if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)||e.getAction().equals(Action.RIGHT_CLICK_AIR)){
							p.sendRawMessage(te.getTitle()+"Lobby Warp");
						}
						return;
					}else if(e.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD+"Spawnpoints")){
						if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
							Location loc=e.getClickedBlock().getLocation();
							loc.setY(loc.getY()+1);
							final Block block=e.getClickedBlock();
							final Material material=e.getClickedBlock().getType();
							arena.addSpawn(loc);
							setPreciseLocation(te.sm.getArenaData(arena.getName()), "arena.spawnpoints.s"+arena.getPlayerSpawns().size()+".", loc);
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(te, new Runnable(){

								@Override
								public void run() {
									block.setType(material);
								}
								
							}, 10);
							p.sendRawMessage(te.getTitle()+"Added Player Spawn");
						}else if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)||e.getAction().equals(Action.RIGHT_CLICK_AIR)){
							p.sendRawMessage(te.getTitle()+"Player Spawns");
						}
						return;
					}else if(e.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD+"Lobby Region")){
						if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
							Location loc=e.getClickedBlock().getLocation();
							final Block block=e.getClickedBlock();
							final Material material=e.getClickedBlock().getType();
							arena.setLobbyPos1(loc);
							setLocation(te.sm.getArenaData(arena.getName()), "lobby.l1.", loc);
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(te, new Runnable(){

								@Override
								public void run() {
									block.setType(material);
								}
								
							}, 10);
							p.sendRawMessage(te.getTitle()+" Lobby Position 1 set");
						}else if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
							Location loc=e.getClickedBlock().getLocation();
							arena.setLobbyPos2(loc);
							setLocation(te.sm.getArenaData(arena.getName()), "lobby.l2.", loc);
							p.sendRawMessage(te.getTitle()+" Lobby Position 2 set");
						}
						return;
					}
					return;
				}
				return;
			}
			return;
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if(!Arenas.containsPlayer(e.getPlayer())) return;
		e.setCancelled(true);
	}
}
