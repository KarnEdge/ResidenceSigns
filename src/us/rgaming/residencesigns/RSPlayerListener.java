/**
 * 
 */
package us.rgaming.residencesigns;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.economy.TransactionManager;
import com.bekvon.bukkit.residence.economy.rent.RentManager;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

/**
 * @author KarnEdge
 *
 */
public class RSPlayerListener extends PlayerListener {
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block block = event.getClickedBlock();
			if (block.getState() instanceof Sign) {
				Player player = event.getPlayer();
				Sign s = (Sign)block.getState();
				String signType = ChatColor.stripColor(s.getLine(0).toLowerCase());
				if (signType.equals(ResidenceSigns.ForSaleSignFirstLine) || signType.equals(ResidenceSigns.RentSignFirstLine)) {
					// By default, we always check the sign's location for residence.
					Location loc = s.getBlock().getLocation();
					ClaimedResidence res = ResidenceSigns.checkLocation(loc);		
					if (ResidenceSigns.checkName(s.getLine(2)) != null) {
						res = ResidenceSigns.checkName(s.getLine(2));
					} else if (res == null) {
						player.sendMessage(ChatColor.RED + Residence.getLanguage().getPhrase("InvalidResidence"));
						s.setLine(0, ChatColor.RED + s.getLine(0));
						return;
					}
					
					// Residence is there; so now we'll try to rent or buy residence.
					String status = ChatColor.GREEN + "Available";
					String resName = res.getName();
					if (signType.equals(ChatColor.DARK_BLUE + ResidenceSigns.RentSignFirstLine)) {
						RentManager rentManager = Residence.getRentManager();
						rentManager.rent(player, resName, true, ResidenceSigns.enabled(player));
						if (rentManager.isRented(resName))
							status = ChatColor.RED + rentManager.getRentingPlayer(resName);
					} else if (signType.equals(ChatColor.DARK_BLUE + ResidenceSigns.ForSaleSignFirstLine)) {
						TransactionManager transManager = Residence.getTransactionManager();
						transManager.buyPlot(resName, player, ResidenceSigns.enabled(player));
						if (!transManager.isForSale(resName))
							status = ChatColor.RED + ResidenceSigns.Sold;
					}
					s.setLine(3, status);
					s.update(true);
				}
			}
		}
	}
}
