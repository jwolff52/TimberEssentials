package io.github.jwolff52.timberessentials;

import io.github.jwolff52.timberessentials.commands.CommandParser;
import io.github.jwolff52.timberessentials.minigames.MinigameListener;
import io.github.jwolff52.timberessentials.minigames.arenas.DefaultArena;
import io.github.jwolff52.timberessentials.util.ArenasGUI;
import io.github.jwolff52.timberessentials.util.PlayerInventories;
import io.github.jwolff52.timberessentials.util.SettingsManager;
import io.github.jwolff52.timberessentials.util.TimberListener;
import io.github.jwolff52.timberessentials.util.WarpsGUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class TimberEssentials extends JavaPlugin {
	public final Logger logger = Logger.getLogger("Minecraft");

	public SettingsManager sm;

	private TimberListener tl;
	private MinigameListener ml;

	private PluginDescriptionFile pdf;

	private ArrayList<String> consoleCommands;
	private ArrayList<String> playerCommands;

	private String title;

	private int configNumber = 1;
	private int nickMaxLength;

	private String inGameMotd;
	private String serverMotd;
	private String nickPrefix;

	private boolean useServerMotd;
	private boolean useTVL;
	private boolean tvlUseLiveBroadcast;

	private ItemStack tvlReward;

	private HashMap<String, Location> warps;
	private HashMap<String, DefaultArena> arenas;

	@Override
	public void onEnable() {
		sm = SettingsManager.getInstance();

		warps = new HashMap<>();
		arenas = new HashMap<>();

		WarpsGUI.setup();
		ArenasGUI.setup();

		sm.setup(this);

		PlayerInventories.setup();

		saveDefaultConfig();

		pdf = getDescription();

		consoleCommands = new ArrayList<String>();
		playerCommands = new ArrayList<String>();

		title = ChatColor.GOLD + "[" + ChatColor.DARK_GRAY + "TimberEssentials"
				+ ChatColor.GOLD + "]" + ChatColor.AQUA;

		tl = new TimberListener(this);
		ml = new MinigameListener(this);

		getServer().getPluginManager().registerEvents(tl, this);
		getServer().getPluginManager().registerEvents(ml, this);

		CommandParser.init(this);

		initializeConfigOptions();

		if (useTVL) {
			if (Bukkit.getServer().getPluginManager()
					.isPluginEnabled("Votifier")) {
				getLogger().info("Using TimberVoteListener!");
				if (tvlUseLiveBroadcast
						&& !Bukkit.getServer().getPluginManager()
								.isPluginEnabled("LiveBroadcast")) {
					getLogger()
							.warning(
									"tvlUseLiveBroadcast is set to true in config.yml, however LiveBroadcast is not present on this server!");
				} else if (tvlUseLiveBroadcast
						&& Bukkit.getServer().getPluginManager()
								.isPluginEnabled("LiveBroadcast")) {
					getLogger().info(
							"Using LiveBroadcast with TimberVoteListener!");
				}
			} else {
				getLogger()
						.warning(
								"useTVL is set to true in the config.yml, however Votifier is not present on the server!");
			}

		}

		for (Player p : Bukkit.getOnlinePlayers()) {
			WarpsGUI.initializePlayerGUI(p);
			ArenasGUI.initializePlayerGUI(p);
		}

		this.logger.info("TimberEssentials Version: " + pdf.getVersion()
				+ " has been enabled!");

		super.onEnable();
	}

	public void onDisable() {
		saveDefaultConfig();
		sm.savePlayers();
		sm.saveWarps();
		this.logger.info("WelcomeBook has been disabled!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		return CommandParser.parseCommand(sender, cmd, label, args);
	}

	public String parseColors(String temp) {
		return ChatColor.translateAlternateColorCodes('&', temp);
	}

	public String parseNewLines(String temp) {
		StringBuilder parsed = new StringBuilder();
		for (int i = 0; i < temp.length(); i++) {
			if (temp.charAt(i) != '\\') {
				parsed.append(temp.charAt(i));
			} else if (temp.charAt(i + 1) == 'n') {
				parsed.append('\n');
				i++;
			}
		}
		return parsed.toString();
	}

	public String parseVariables(String temp, CommandSender sender) {
		StringBuilder parsed = new StringBuilder();
		for (int i = 0; i < temp.length(); i++) {
			if (temp.charAt(i) != '%') {
				parsed.append(temp.charAt(i));
			} else {
				StringBuilder variable = new StringBuilder();
				for (i = i + 1; i < temp.length(); i++) {
					if (temp.charAt(i) != '%') {
						variable.append(temp.charAt(i));
					} else {
						i++;
						break;
					}
				}
				if (variable.toString().equalsIgnoreCase("player")) {
					parsed.append(sender.getName());
				} else {
					if (i == temp.length() - 1) {
						return temp;
					}
				}
			}
		}
		return parsed.toString();
	}

	public ItemStack parseItemStack(String input) {
		String material = input.substring(0, input.indexOf(' ')).toUpperCase();
		int ammount = Integer.valueOf(input.substring(input.indexOf(' ') + 1));
		return new ItemStack(Material.getMaterial(material), ammount);
	}

	public ArrayList<String> getConsoleCommands() {
		return consoleCommands;
	}

	public ArrayList<String> getPlayerCommands() {
		return playerCommands;
	}

	public String getTitle() {
		return title;
	}

	public String getInGameMotd() {
		return inGameMotd;
	}

	public String getServerMotd() {
		return serverMotd;
	}

	public String getNickPrefix() {
		return nickPrefix;
	}

	public int getNickMaxLength() {
		return nickMaxLength;
	}

	public HashMap<String, Location> getWarps() {
		return warps;
	}

	public ItemStack getTVLReward() {
		return tvlReward;
	}

	public boolean isUsingServerMotd() {
		return useServerMotd;
	}

	public boolean isUsingTVL() {
		return useTVL;
	}

	public void setInGameMotd(String motd) {
		inGameMotd = motd;
	}

	public void setServerMotd(String motd) {
		serverMotd = motd;
	}

	public void setWarps(HashMap<String, Location> warps) {
		this.warps = warps;
	}

	public void addWarp(String name, Location loc) {
		getWarps().put(name, loc);
	}

	public void delWarp(String name) {
		getWarps().remove(name);
	}

	public void addArena(String name, DefaultArena arena) {
		arenas.put(name, arena);
	}

	public void initializeConfigOptions() {
		while (true) {
			if (sm.getConfig().getString("consoleCommands." + configNumber) != null) {
				consoleCommands.add(sm.getConfig().getString(
						"consoleCommands." + configNumber));
				configNumber++;
			} else {
				configNumber = 1;
				break;
			}
		}

		while (true) {
			if (sm.getConfig().getString("playerCommands." + configNumber) != null) {
				playerCommands.add(sm.getConfig().getString(
						"playerCommands." + configNumber));
				configNumber++;
			} else {
				configNumber = 1;
				break;
			}
		}

		inGameMotd = parseNewLines(parseColors(sm.getConfig().getString(
				"motd.ingame")));

		serverMotd = parseColors(sm.getConfig().getString("motd.server"));

		useServerMotd = sm.getConfig().getBoolean("motd.useServerMotd");

		nickPrefix = sm.getConfig().getString("nick.prefix");

		nickMaxLength = sm.getConfig().getInt("nick.maxLength");

		useTVL = sm.getConfig().getBoolean("tvl.useTVL");

		tvlUseLiveBroadcast = sm.getConfig().getBoolean("tvl.useLiveBroadcast");

		tvlReward = parseItemStack(sm.getConfig().getString("tvl.reward"));
	}
}
