package io.github.jwolff52.timberessentials.util;

import io.github.jwolff52.timberessentials.TimberEssentials;

import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class TimberListener implements Listener{
	
	private TimberEssentials te;
	
	private ArrayList<Player> que;
	
	private ConsoleCommandSender ccs;
	
	public TimberListener(TimberEssentials p){
		te=p;
		que=new ArrayList<>();
		ccs=Bukkit.getConsoleSender();
	}
	
	@EventHandler
    public void onServerPing(ServerListPingEvent e) {
        if(te.isUsingServerMotd()){
        	e.setMotd(te.getServerMotd());
        }
    }
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerVote(VotifierEvent e){
		if(te.isUsingTVL()){
			Vote v=e.getVote();
			te.getServer().dispatchCommand(ccs, te.getTitle()+ChatColor.GOLD+v.getUsername() + " voted on "+ v.getServiceName() + "!");
			Player p=Bukkit.getServer().getPlayer(v.getUsername());
			if(p!=null){
				p.getInventory().addItem(te.getTVLReward());
			}else{
				que.add(p);
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player p=e.getPlayer();
//		te.getMotd(p);
		te.sm.addPlayer(p);
		for(String cmd:te.getConsoleCommands()){
			Bukkit.getServer().dispatchCommand(ccs, cmd);
		}
		for(String cmd:te.getPlayerCommands()){
			p.chat("/"+cmd);
		}
		for(Player target:que){
			if(target.getUniqueId().equals(p.getUniqueId())){
				target.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
				que.remove(target);
				break;
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		te.sm.getPlayerData(e.getPlayer()).set("timestamps.logout", Calendar.getInstance().getTimeInMillis());
		Location loc=e.getPlayer().getLocation();
		te.sm.getPlayerData(e.getPlayer()).set("logoutlocation.world", loc.getWorld().getName());
		te.sm.getPlayerData(e.getPlayer()).set("logoutlocation.x", loc.getX());
		te.sm.getPlayerData(e.getPlayer()).set("logoutlocation.y", loc.getY());
		te.sm.getPlayerData(e.getPlayer()).set("logoutlocation.z", loc.getZ());
		te.sm.getPlayerData(e.getPlayer()).set("logoutlocation.yaw", loc.getYaw());
		te.sm.getPlayerData(e.getPlayer()).set("logoutlocation.pitch", loc.getPitch());
		te.sm.savePlayers();
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e){
		if(te.sm.getPlayerData(e.getPlayer()).getString("nickname")!=null){
			if(!e.getPlayer().hasPermission("te.nick.color")){
				e.getPlayer().setDisplayName(te.sm.getPlayerData(e.getPlayer()).getString("nickname")+ChatColor.RESET);
			}else{
				e.getPlayer().setDisplayName(te.parseColors(te.sm.getPlayerData(e.getPlayer()).getString("nickname"))+ChatColor.RESET);
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		if(e instanceof InventoryCreativeEvent) return;
		if(e.getInventory() == null) return;
		if(e.getCurrentItem() == null) return;
		if(e.getCurrentItem().getItemMeta() == null) return;
		if(e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
		if(!e.getInventory().getName().equals(WarpsGUI.getInventory(e.getWhoClicked()).getName())){
			if(e.getInventory().getName().equals(ArenasGUI.getInventory(e.getWhoClicked()).getName())){
				Player player=(Player)e.getWhoClicked();
				player.closeInventory();
				player.chat("/join "+ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase()));
				return;
			}else{
				return;
			}
		}
		Player player=(Player)e.getWhoClicked();
		player.closeInventory();
		player.chat("/warp "+ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase()));
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		if(!e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
		if(!e.getMaterial().equals(Material.COMPASS)) return;
		WarpsGUI.show(e.getPlayer());
	}
}
