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
public class RSLocale {
	public static ResidenceSigns plugin;
	
	public String directory = "plugins" + File.separator + plugin.name;
	File file = new File(directory + File.separator + plugin.Locale + ".locale");
	Configuration locale = null;
	
	
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
	
	public void localeCheck() {
		// Create directory and load/create locale if not already done.
		new File(directory).mkdir();
		locale = load();
		
		// Check if configuration exists. 
		if (!file.exists()) {
			plugin.log.info(plugin.name + " Creating " + plugin.Locale + ".locale with defaults from english.locale.");
			try {
				file.createNewFile();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		// Check locales for missing properties, add them if missing.
		checkLocales();

		// Load locales, we should be good to go.
		loadLocales();
	}

	private void loadLocales() {
		plugin.ForSaleSignFirstLine = locale.getString("ForSaleSignFirstLine").toLowerCase();
		plugin.RentSignFirstLine = locale.getString("RentSignFirstLine").toLowerCase();
		plugin.ForSaleSignFormatMessage = locale.getString("ForSaleSignFormatMessage");
		plugin.RentSignFormatMessage = locale.getString("RentSignFormatMessage");
		plugin.InvalidPrice = locale.getString("InvalidPrice");
		plugin.Available = locale.getString("Available");
		plugin.Sold = locale.getString("Sold");
		plugin.ResAdminModeEnabled = locale.getString("ResAdminModeEnabled");
		plugin.ResAdminModeDisabled = locale.getString("ResAdminModeDisabled");
	}

	private void checkLocales() {
		if (locale.getProperty("ForSaleSignFirstLine") == null) {
			locale.setProperty("ForSaleSignFirstLine", "[forsale]");
		}
		if (locale.getProperty("RentSignFirstLine") == null) {
			locale.setProperty("RentSignFirstLine", "[rent]");
		}
		if (locale.getProperty("ForSaleSignFormatMessage") == null) {
			locale.setProperty("ForSaleSignFormatMessage", "Please enter the price of the residence in the second line of the sign.");
		}
		if (locale.getProperty("RentSignFormatMessage") == null) {
			locale.setProperty("RentSignFormatMessage", "Please write the second line of the sign in this format \"<Price>/<Days>/<Auto-Renew>\". For example, \"100/10/t\".");
		}
		if (locale.getProperty("InvalidPrice") == null) {
			locale.setProperty("InvalidPrice", "Invalid Price");
		}
		if (locale.getProperty("Available") == null) {
			locale.setProperty("Available", "Available");
		}
		if (locale.getProperty("Sold") == null) {
			locale.setProperty("Sold", "Sold");
		}
		if (locale.getProperty("ResAdminModeEnabled") == null) {
			locale.setProperty("ResAdminModeEnabled", "ResAdmin mode enabled.");
		}
		if (locale.getProperty("ResAdminModeDisabled") == null) {
			locale.setProperty("ResAdminModeDisabled", "ResAdmin mode disabled.");
		}
		locale.save();
	}
}
