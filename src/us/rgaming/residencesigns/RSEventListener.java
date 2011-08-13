/**
 * 
 */
package us.rgaming.residencesigns;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

import com.bekvon.bukkit.residence.event.ResidenceDeleteEvent;
import com.bekvon.bukkit.residence.event.ResidenceOwnerChangeEvent;
import com.bekvon.bukkit.residence.event.ResidenceRentEvent;

/**
 * @author KarnEdge
 *
 */

public class RSEventListener extends CustomEventListener {
	public static ResidenceSigns plugin;

	@Override
	public void onCustomEvent(Event event) {
		if(event instanceof ResidenceRentEvent) {
			ResidenceRentEvent e = (ResidenceRentEvent) event;
			plugin.log.info("Res: " + e.getResidence().getName());
		} else if(event instanceof ResidenceOwnerChangeEvent) {
			ResidenceOwnerChangeEvent e = (ResidenceOwnerChangeEvent) event;
			plugin.log.info("New owner: " + e.getNewOwner() + " of Res: " + e.getResidence().getName());
		} else if(event instanceof ResidenceDeleteEvent) {
			ResidenceDeleteEvent e = (ResidenceDeleteEvent) event;
			plugin.log.info("Deleted Res: " + e.getResidence().getName());
		}
	}
}