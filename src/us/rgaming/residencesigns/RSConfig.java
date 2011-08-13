/**
 * 
 */
package us.rgaming.residencesigns;

import java.io.File;
import org.bukkit.util.config.Configuration;

/**
 * @author KarnEdge
 *
 */
public class RSConfig {
	public static ResidenceSigns plugin;

	public String directory = "plugins" + File.separator + plugin.name;
	File file = new File(directory + File.separator + "config.yml");
	Configuration config = null;
	
	private Configuration load() {
		try {
			Configuration config = new Configuration(file);
			config.load();
			return config;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public void configCheck() {
		// Create directory and load/create configuration if not already done.
		new File(directory).mkdir();
		config = load();
		
		// Check if configuration exists. 
		if (!file.exists()) {
			plugin.log.info(plugin.name + " Creating configuration.");
			try {
				file.createNewFile();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		// Check settings for missing properties, add them if missing.
		checkSettings();
		
		// Load settings, we should be good to go.
		loadSettings();
	}

	private void loadSettings() {
		plugin.UseColors = config.getBoolean("UseColors", true);
		plugin.Locale = config.getString("Locale");
	}

	private void checkSettings() {
		plugin.log.info(plugin.name + "Generating default settings.");
		
		if (config.getProperty("UseColors") == null) {
			config.setProperty("UseColors", true);
		}
		if (config.getProperty("Locale") == null) {
			config.setProperty("Locale", "english");
		}
		config.save();
	}
}
