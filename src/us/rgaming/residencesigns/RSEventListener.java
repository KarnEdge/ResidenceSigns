/**
 * 
 */
package us.rgaming.residencesigns;

import java.util.logging.Logger;

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
	public final Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public void onCustomEvent(Event event) {
		if(event instanceof ResidenceRentEvent) {
			ResidenceRentEvent e = (ResidenceRentEvent) event;
			log.info("Res: " + e.getResidence().getName());
		} else if(event instanceof ResidenceOwnerChangeEvent) {
			ResidenceOwnerChangeEvent e = (ResidenceOwnerChangeEvent) event;
			log.info("New owner: " + e.getNewOwner() + " of Res: " + e.getResidence().getName());
		} else if(event instanceof ResidenceDeleteEvent) {
			ResidenceDeleteEvent e = (ResidenceDeleteEvent) event;
			log.info("Deleted Res: " + e.getResidence().getName());
		}
	}
}