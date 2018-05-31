package io.github.jwolff52.timberessentials.util;

import io.github.jwolff52.timberessentials.TimberEssentials;
import io.github.jwolff52.timberessentials.minigames.arenas.OIQArena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;


public class SettingsManager {
	
	private TimberEssentials te;
	
	private FileConfiguration config;
	
	private File cfile;
	private File playersDir;
	private File warpsDir;
	private File arenasDir;
	
	private ArrayList<FileConfiguration> playerData;
	private ArrayList<FileConfiguration> warpData;
	private ArrayList<FileConfiguration> arenaData;
	
	private ArrayList<File> playerFiles;
	private ArrayList<File> warpFiles;
	private ArrayList<File> arenaFiles;
	
	private SettingsManager() {}
	
	public static SettingsManager instance=new SettingsManager();
	
	public static SettingsManager getInstance(){
		return instance;
	}
	
	public void setup(TimberEssentials te){
		this.te=te;
		
		initConfig();
		
		initVariables();
		
		getFiles();
		
		getData();
		
		initWarps();
		
		initArenas();
	}
	
	public TimberEssentials getPlugin(){
		return te;
	}
	
	private void initConfig(){
		config=te.getConfig();
		cfile=new File(te.getDataFolder(), "config.yml");
		te.saveDefaultConfig();
	}
	
	private void initVariables(){
		playersDir=new File(te.getDataFolder()+"/players");
		warpsDir=new File(te.getDataFolder()+"/warps");
		arenasDir=new File(te.getDataFolder()+"/arenas");
		
		playerData=new ArrayList<>();
		warpData=new ArrayList<>();
		arenaData=new ArrayList<>();
		
		playerFiles=new ArrayList<>();
		warpFiles=new ArrayList<>();
		arenaFiles=new ArrayList<>();
		
		if(!playersDir.exists()){
			playersDir.mkdir();
		}
		if(!warpsDir.exists()){
			warpsDir.mkdir();
		}
		if(!arenasDir.exists()){
			arenasDir.mkdir();
		}
	}
	
	private void getFiles(){
		for(File f:playersDir.listFiles()){
			playerFiles.add(f);
		}
		for(File f:warpsDir.listFiles()){
			warpFiles.add(f);
		}
		for(File f:arenasDir.listFiles()){
			arenaFiles.add(f);
		}
	}
	
	private void getData(){
		for(File f:playerFiles){
			playerData.add(YamlConfiguration.loadConfiguration(f));
		}
		for(File f:warpFiles){
			warpData.add(YamlConfiguration.loadConfiguration(f));
		}
		for(File f:arenaFiles){
			arenaData.add(YamlConfiguration.loadConfiguration(f));
		}
	}
	
	private void initWarps(){
		Location warp;
		for(FileConfiguration fc:warpData){
			warp=getPreciseLocation(fc, "");
			te.addWarp(fc.getString("name"), warp);
			String name=fc.getString("name");
			String c=getConfig().getString("warps."+name);
			try{
				Material mat=Material.getMaterial(c.toUpperCase());
				WarpsGUI.addWarp(mat, name.toUpperCase());
			}catch(NullPointerException e){
				te.getLogger().severe(te.getTitle()+ChatColor.DARK_RED+"Unable to load warp "+ChatColor.GOLD+name+ChatColor.DARK_RED+"!");
			}
		}
	}
	
	private void initArenas(){
		for(FileConfiguration fc:arenaData){
			switch(fc.getString("type").toLowerCase()){
			case "oiq":
				OIQArena a=new OIQArena(te, fc.getString("name"));
				a.setOnGameStartMsg(fc.getString("onGameStartMsg"));
				a.setArenaPos1(getLocation(fc,"arena.al."));
				a.setArenaPos2(getLocation(fc,"arena.a2."));
				a.setLobbyPos1(getLocation(fc,"lobby.l1."));
				a.setLobbyPos2(getLocation(fc,"lobby.l2."));
				a.setLobbyWarp(getLocation(fc,"lobby.warp."));
				int i=0;
				while(true){
					if(fc.getString("arena.spawnpoints.s"+i+".world")!=null){
						a.addSpawn(getLocation(fc,"arena.spawnpoints.s"+i+"."));
						i++;
						continue;
					}
					break;
				}
				te.addArena(fc.getString("name"), a);
				break;
			}
			try{
				Material mat=Material.getMaterial(getConfig().getString("arenas."+fc.getString("name")).toUpperCase());
				ArenasGUI.addArena(mat, fc.getString("name").toUpperCase());
			}catch(NullPointerException e){
				te.getLogger().severe(te.getTitle()+ChatColor.DARK_RED+"Unable to load arena "+ChatColor.GOLD+fc.getString("name")+ChatColor.DARK_RED+"!");
			}
		}
	}
	
	public FileConfiguration getConfig(){
		return config;
	}
	
	public void reloadConfig(){
		config=YamlConfiguration.loadConfiguration(cfile);
	}
	
	public void saveConfig() {
		try {
			config.save(cfile);
		} catch (IOException e) {
			te.getLogger().severe(ChatColor.RED+"Could not save "+te.getDescription().getName()+" config.yml!");
		}
	}
	
	public FileConfiguration getPlayerData(Player p){
		for(FileConfiguration data:playerData){
			if(p.getUniqueId().toString().equalsIgnoreCase(data.getString("uuid"))){
				return data;
			}
		}
		return null;
	}
	
	public void savePlayers() {
		for(int i=0;i<playerFiles.size();i++){
			try{
				playerData.get(i).save(playerFiles.get(i));
			} catch (IOException e) {
				te.getLogger().severe(ChatColor.RED+"Could not save player file for "+playerFiles.get(i).getName().substring(0, playerFiles.get(i).getName().lastIndexOf('.'))+"!");
			}
		}
	}
	
	public ArrayList<FileConfiguration> getWarpData(){
		return warpData;
	}
	
	public FileConfiguration getWarpData(String warp){
		for(FileConfiguration data:warpData){
			if(warp.equalsIgnoreCase(data.getString("name"))){
				return data;
			}
		}
		return null;
	}
	
	public void saveWarps() {
		for(int i=0;i<warpFiles.size();i++){
			try{
				warpData.get(i).save(warpFiles.get(i));
			} catch (IOException e) {
				te.getLogger().severe(ChatColor.RED+"Could not save warp file for "+warpFiles.get(i).getName().substring(0, warpFiles.get(i).getName().lastIndexOf('.'))+"!");
			}
		}
	}
	
	public FileConfiguration getArenaData(String arena){
		for(FileConfiguration data:arenaData){
			if(arena.equalsIgnoreCase(data.getString("name"))){
				return data;
			}
		}
		return null;
	}
	
	public void saveArenas() {
		for(int i=0;i<arenaFiles.size();i++){
			try{
				arenaData.get(i).save(arenaFiles.get(i));
			} catch (IOException e) {
				te.getLogger().severe(ChatColor.RED+"Could not save warp file for "+arenaFiles.get(i).getName().substring(0, arenaFiles.get(i).getName().lastIndexOf('.'))+"!");
			}
		}
	}
	
	public void addPlayer(Player p){
		File pf=new File(playersDir+"/"+p.getName()+".yml");
		if(pf.exists()) {
			updatePlayer(p);
			return;
		}
		try {
			pf.createNewFile();
		} catch (IOException e1) {
			te.getLogger().severe(ChatColor.RED+"Could not create player file for "+p.getName()+"!");
		}
		FileConfiguration pfc=YamlConfiguration.loadConfiguration(pf);
		pfc.set("uuid", p.getUniqueId().toString());
		pfc.set("timestamps.login", Calendar.getInstance().getTimeInMillis());
		pfc.set("ipAddress", p.getAddress().getAddress().toString().substring(1));
		Location loc=p.getLocation();
		pfc.set("lastlocation.world", loc.getWorld().getName());
		pfc.set("lastlocation.x", loc.getX());
		pfc.set("lastlocation.y", loc.getY());
		pfc.set("lastlocation.z", loc.getZ());
		pfc.set("lastlocation.yaw", loc.getYaw());
		pfc.set("lastlocation.pitch", loc.getPitch());
		try {
			pfc.save(pf);
		} catch (IOException e) {
			te.getLogger().severe(ChatColor.RED+"Could not save player file for "+p.getName()+"!");
		}
		te.getLogger().info(ChatColor.GREEN+"Successfully created player file for "+p.getName()+"!");
		playerData.add(pfc);
		playerFiles.add(pf);
	}
	
	public void updatePlayer(Player p) {
		FileConfiguration pfc = getPlayerData(p);
		pfc.set("timestamps.login", Calendar.getInstance().getTimeInMillis());
		pfc.set("ipAddress", p.getAddress().getAddress().toString().substring(1));
		savePlayers();
	}
	
	public void addWarp(String name, Location loc, String icon){
		name=name.toLowerCase();
		File wf=new File(warpsDir+"/"+name+".yml");
		try {
			wf.createNewFile();
		} catch (IOException e1) {
			te.getLogger().severe(ChatColor.RED+"Could not create warp file for "+name+"!");
		}
		FileConfiguration wfc=YamlConfiguration.loadConfiguration(wf);
		wfc.set("name", name);
		wfc.set("world", loc.getWorld().getName());
		wfc.set("x", loc.getX());
		wfc.set("y", loc.getY());
		wfc.set("z", loc.getZ());
		wfc.set("yaw", loc.getYaw());
		wfc.set("pitch", loc.getPitch());
		getConfig().set("warps."+name, icon.toUpperCase());
		if(te.getServer().getPluginManager().getPermission("te.warps.allow."+name)==null){
			try{
				te.getServer().getPluginManager().addPermission(new Permission("te.warps.allow."+name));
			}catch(IllegalArgumentException e){
				te.getLogger().info("There was an error registering the permission for this warp. This probably means it already existed, if so this message can be ignored!");
			}
		}
		try {
			wfc.save(wf);
		} catch (IOException e) {
			te.getLogger().severe(ChatColor.RED+"Could not save warp file for "+name+"!");
		}
		te.getLogger().info(ChatColor.GREEN+"Successfully created warp file for "+name+"!");
		warpData.add(wfc);
		warpFiles.add(wf);
		saveConfig();
	}
	
	public void delWarp(String name){
		File wf=new File(warpsDir+"/"+name+".yml");
		if(wf.exists()){
			wf.delete();
		}
		warpData.remove(YamlConfiguration.loadConfiguration(wf));
		warpFiles.remove(wf);
		te.delWarp(name);
	}
	
	public void addArena(OIQArena arena, String icon){
		File af=new File(arenasDir+"/"+arena.getName()+".yml");
		try {
			af.createNewFile();
		} catch (IOException e1) {
			te.getLogger().severe(ChatColor.RED+"Could not create arena file for "+arena.getName()+"!");
		}
		FileConfiguration afc=YamlConfiguration.loadConfiguration(af);
		afc.set("name", arena.getName());
		afc.set("type", "OIQ");
		afc.set("onGameStartMsg", arena.getOnGameStartMsg());
		/*
		Location loc=arena.getArenaPos1();
		afc.set("arena.al.world", loc.getWorld().getName());
		afc.set("arena.al.x", loc.getX());
		afc.set("arena.al.y", loc.getY());
		afc.set("arena.al.z", loc.getZ());
		loc=arena.getArenaPos2();
		afc.set("arena.a2.world", loc.getWorld().getName());
		afc.set("arena.a2.x", loc.getX());
		afc.set("arena.a2.y", loc.getY());
		afc.set("arena.a2.z", loc.getZ());
		for(int i=0;i<arena.getPlayerSpawns().size();i++){
			loc=arena.getPlayerSpawns().get(i);
			afc.set("arena.spawnpoints.s"+i+".world", loc.getWorld().getName());
			afc.set("spawnpoints.s"+i+".x", loc.getX());
			afc.set("spawnpoints.s"+i+".y", loc.getY());
			afc.set("spawnpoints.s"+i+".z", loc.getZ());
		}
		loc=arena.getLobbyPos1();
		afc.set("lobby.ll.world", loc.getWorld().getName());
		afc.set("lobby.ll.x", loc.getX());
		afc.set("lobby.ll.y", loc.getY());
		afc.set("lobby.ll.z", loc.getZ());
		loc=arena.getLobbyPos2();
		afc.set("lobby.l2.world", loc.getWorld().getName());
		afc.set("lobby.l2.x", loc.getX());
		afc.set("lobby.l2.y", loc.getY());
		afc.set("lobby.l2.z", loc.getZ());
		loc=arena.getLobbyWarp();
		afc.set("lobby.warp.world", loc.getWorld().getName());
		afc.set("lobby.warp.x", loc.getX());
		afc.set("lobby.warp.y", loc.getY());
		afc.set("lobby.warp.z", loc.getZ());
		 */
		getConfig().set("arenas."+arena.getName(), icon.toUpperCase());
		if(te.getServer().getPluginManager().getPermission("te.arenas.allow."+arena.getName())==null){
			try{
				te.getServer().getPluginManager().addPermission(new Permission("te.arenas.allow."+arena.getName()));
			}catch(IllegalArgumentException e){
				te.getLogger().info("There was an error registering the permission for this arena. This probably means it already existed, if so this message can be ignored!");
			}
		}
		try {
			afc.save(af);
		} catch (IOException e) {
			te.getLogger().severe(ChatColor.RED+"Could not save arena file for "+arena.getName()+"!");
		}
		te.getLogger().info(ChatColor.GREEN+"Successfully created arena file for "+arena.getName()+"!");
		arenaData.add(afc);
		arenaFiles.add(af);
	}
	
	public Location getLocation(FileConfiguration fc, String rootNode){
		try{
			return new Location(te.getServer().getWorld(fc.getString(rootNode+"world")),fc.getDouble(rootNode+"x"),fc.getDouble(rootNode+"y"),fc.getDouble(rootNode+"z"));
		}catch(IllegalArgumentException e){
			return null;
		}
	}
	
	public Location getPreciseLocation(FileConfiguration fc, String rootNode){
		try{
			return new Location(te.getServer().getWorld(fc.getString(rootNode+"world")),fc.getDouble(rootNode+"x"),fc.getDouble(rootNode+"y"),fc.getDouble(rootNode+"z"),(float)fc.getDouble(rootNode+"yaw"),(float)fc.getDouble(rootNode+"pitch"));
		}catch(IllegalArgumentException e){
			return null;
		}
	}
	
	public Location getLastLocation(Player player){
		Location loc=null;
		FileConfiguration pfc=getPlayerData(player);
		try{
			loc=new Location(Bukkit.getServer().getWorld(pfc.getString("lastlocation.world")), pfc.getDouble("lastlocation.x"), pfc.getDouble("lastlocation.y"), pfc.getDouble("lastlocation.z"), (float)pfc.getDouble("lastlocation.yaw"), (float)pfc.getDouble("lastlocation.pitch"));
		}catch(NullPointerException e){
			(te).getLogger().severe((te).getTitle()+ChatColor.DARK_RED+"An error occurred retrieving the players last location!");
		}
		return loc;
	}
}
