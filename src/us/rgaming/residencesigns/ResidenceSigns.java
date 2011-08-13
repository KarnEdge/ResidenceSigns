/**
 * 
 */
package us.rgaming.residencesigns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.economy.TransactionManager;
import com.bekvon.bukkit.residence.economy.rent.RentManager;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

/**
 * @author KarnEdge
 *
 */
public class ResidenceSigns extends JavaPlugin {
	private RSBlockListener blockListener = new RSBlockListener();
	private RSPlayerListener playerListener = new RSPlayerListener();
	private RSEventListener eventListener = new RSEventListener();
	public final static HashMap<Player, ArrayList<Block>> RSUsers = new HashMap<Player, ArrayList<Block>>();
	public String name = this.getDescription().getName();
	public String version = this.getDescription().getVersion();
	public final Logger log = Logger.getLogger("Minecraft");
	
	// Settings
	public boolean UseColors;
	public String Locale;
	
	// Locales
	public String ForSaleSignFirstLine, RentSignFirstLine, ForSaleSignFormatMessage, RentSignFormatMessage, 
			InvalidPrice, Available, Sold, ResAdminModeEnabled, ResAdminModeDisabled;
	
	@Override
	public void onDisable() {
		log.info(name + " disabled.");
	}
	
	@Override
	public void onEnable() {		
		// Check if Residence is loaded, if not try to load it.
		PluginManager pm = this.getServer().getPluginManager();
		Plugin p = pm.getPlugin("Residence");
		if (p != null) {
			if (!p.isEnabled()) {
				// Residence was found, but not loaded yet.
				log.info(name + " Manually enabling Residence.");
				pm.enablePlugin(p);
			}
		} else {
			// Residence was not found.
			log.info(name +  " Residence was not found, disabling.");
			this.setEnabled(false);
			return;
		}
		
		// Register our events
		pm.registerEvent(Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.CUSTOM_EVENT, eventListener, Priority.Normal, this);
		
		// Initialize the Config.
		RSConfig config = new RSConfig();
		config.configCheck();
		
		// Initialize the locale.
		RSLocale locale = new RSLocale();
		locale.localeCheck();

		// Check if H2 library is installed, if not try to download it.
		// TODO: http://dl.dropbox.com/u/41672/h2.jar
		
		// Initialize our Database backend.
		// TODO
		
		log.info(name + " v" + version + " enabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String commandName = command.getName().toLowerCase();
		if (sender instanceof Player) {
			RentManager rentManager = Residence.getRentManager();
			TransactionManager transManager = Residence.getTransactionManager();
			Player player = (Player) sender;
			
			// By default, we always check the player's location for residence.
			Location loc = player.getLocation();
			ClaimedResidence resName = checkLocation(loc);
			
			if (commandName.equals("rsadmin")) {
				if (Residence.getPermissionManager().isResidenceAdmin(player)) {
					toggleResAdmin(player);
				} else {
					player.sendMessage(ChatColor.RED + Residence.getLanguage().getPhrase("AdminOnly"));
				}
				return true;
			} else if (commandName.equals("rent")) {
				// Check for arguments and assign resName if someone is using residence name instead of location.
				if (args.length > 0) {
					if (!args[0].equalsIgnoreCase("info")) {
						resName = checkName(args[0]);
					} else if (args.length > 1) {
						resName = checkName(args[1]);
					}
				}

				// If any arguments or locations for residences come back invalid, then send error.
				if (resName == null) {
					player.sendMessage(ChatColor.RED + Residence.getLanguage().getPhrase("InvalidResidence"));
					return true;
				} else if ((args.length > 0) && (args[0].equalsIgnoreCase("info"))) {
					rentManager.printRentInfo(player, resName.getName());
					return true;
				} else {
					rentManager.rent(player, resName.getName(), true, enabled(player));
					return true;
				}
			} else if (commandName.equals("unrent")) {
				// Check for arguments and assign resName if someone is using residence name instead of location.
				if (args.length > 0) {
					resName = checkName(args[0]);
				}
				
				// If any arguments or locations for residences come back invalid, then send error.
				if (resName == null) {
					player.sendMessage(ChatColor.RED + Residence.getLanguage().getPhrase("InvalidResidence"));
					return true;
				} else {
					rentManager.removeFromForRent(player, resName.getName(), enabled(player));
					return true;
				}
			} else if (commandName.equals("land")) {
				if (args.length > 0) {
					String option = args[0].toLowerCase();
					// Check for arguments and assign resName if someone is using residence name instead of location.
					if ((args.length > 1) && (!checkNumber(args[1]))) {
						resName = checkName(args[1]);
					}
					
					// If any arguments or locations for residences come back invalid, then send error.
					if (resName == null) {
						player.sendMessage(ChatColor.RED + Residence.getLanguage().getPhrase("InvalidResidence"));
					} else if (option.equals("info")) {
						transManager.viewSaleInfo(resName.getName(), player);
					} else if (option.equals("buy")) {
						transManager.buyPlot(resName.getName(), player, enabled(player));
					} else if (option.equals("sell")) {
						// Check arguments for prices and make sure they are numbers.
						String number = args[args.length-1];
						int price = 0;
						try {
							price = Integer.parseInt(number);
						} catch (Exception e) {
							player.sendMessage(ChatColor.RED + InvalidPrice);
							return true;
						}
						transManager.putForSale(resName.getName(), player, Math.abs(price), enabled(player));
					} else if (option.equals("remove")) {
						transManager.removeFromSale(player, resName.getName(), enabled(player));
					} else {
						return false;
					}
					return true;
				}
				return false;
			}
		}
		return false;
	}	

	public void toggleResAdmin(Player player) {
		if (enabled(player)) {
			RSUsers.remove(player);
			player.sendMessage(ChatColor.YELLOW + ResAdminModeDisabled);
		} else {
			RSUsers.put(player, null);
			player.sendMessage(ChatColor.YELLOW + ResAdminModeEnabled);
		}
	}

	public boolean enabled(Player player) {
		return RSUsers.containsKey(player);
	}
	
	public ClaimedResidence checkLocation(Location loc) {
		ClaimedResidence res = Residence.getResidenceManger().getByLoc(loc);
		return res;
	}
	
	public ClaimedResidence checkName(String resName) {
		ClaimedResidence res = Residence.getResidenceManger().getByName(resName);
		return res;
	}
	
	public boolean checkNumber(String number) {
		if (number.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
			return true;
		} else {
			return false;
		}
	}
}