/**
 * 
 */
package us.rgaming.residencesigns;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.economy.TransactionManager;
import com.bekvon.bukkit.residence.economy.rent.RentManager;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

/**
 * @author KarnEdge
 *
 */
public class RSBlockListener extends BlockListener {
	public static ResidenceSigns plugin;
	public final Logger log = Logger.getLogger("Minecraft");
	
	public void onSignChange(SignChangeEvent event) {
		String signType = event.getLine(0).toLowerCase();
		if ((signType.equals("[rent]")) || (signType.equals("[forsale]"))) {
			RentManager rentManager = Residence.getRentManager();
			TransactionManager transManager = Residence.getTransactionManager();
			Player player = event.getPlayer();
			
			// By default, we always check the sign's location for residence.
			Location loc = event.getBlock().getLocation();
			ClaimedResidence res = ResidenceSigns.checkLocation(loc);		
			if (ResidenceSigns.checkName(event.getLine(2)) != null) {
				res = ResidenceSigns.checkName(event.getLine(2));
			} else if (res == null) {
				player.sendMessage(ChatColor.RED + Residence.getLanguage().getPhrase("InvalidResidence"));
				event.setLine(0, ChatColor.RED + event.getLine(0));
				return;
			}
			
			String resName = res.getName();
			String price = "";
			String status = ChatColor.GREEN + "Available";;

			if (signType.equals("[rent]")) {
				int cost = 0;
				int days = 0;
				boolean renew = true;
				if (rentManager.isForRent(resName)) {
					cost = rentManager.getCostOfRent(resName);
					days = rentManager.getRentDays(resName);
					if (rentManager.getRentingPlayer(resName) != null)
						status = ChatColor.RED + rentManager.getRentingPlayer(resName);
				} else if (transManager.isForSale(resName)) {
					player.sendMessage(ChatColor.RED + Residence.getLanguage().getPhrase("SellRentFail"));
					event.setLine(0, ChatColor.RED + event.getLine(0));
					return;
				} else {
					String[] setup = event.getLine(1).split("/");
					try {
						cost = Integer.parseInt(setup[0]);
						days = Integer.parseInt(setup[1]);
					} catch (Exception e) {
						player.sendMessage(ChatColor.RED + "Second line must be in this format:\n" +
								ChatColor.RED + "  1000/7/f ($1000 per 7 days, auto-renew off)");
						event.setLine(0, ChatColor.RED + event.getLine(0));
						return;
					}
					if (setup.length > 1) {
						renew = (!setup[2].equalsIgnoreCase("f"));
					}
					rentManager.setForRent(player, resName, Math.abs(cost), Math.abs(days), renew, ResidenceSigns.enabled(player));
				}
				price = cost + "/" + days + "d";
			} else if (signType.equals("[forsale]")) {
				int cost = 0;
				if (transManager.isForSale(resName)) {
					cost = transManager.getSaleAmount(resName);
				} else if (rentManager.isForRent(resName)) {
					player.sendMessage(ChatColor.RED + Residence.getLanguage().getPhrase("RentSellFail"));
					event.setLine(0, ChatColor.RED + event.getLine(0));
					return;
				} else {
					try {
						cost = Integer.parseInt(event.getLine(1));
					} catch (Exception e) {
						player.sendMessage(ChatColor.RED + "Invalid Price...");
						event.setLine(0, ChatColor.RED + event.getLine(0));
						return;
					}
					transManager.putForSale(resName, player, Math.abs(cost), ResidenceSigns.enabled(player));
				}
				price = cost + "";
			}
			event.setLine(0, ChatColor.DARK_BLUE + event.getLine(0));
			event.setLine(1, ChatColor.WHITE + price);
			event.setLine(2, resName);
			event.setLine(3, status);
		}
	}
}
