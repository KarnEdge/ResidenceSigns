/**
 * 
 */
package us.rgaming.residencesigns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Flex
 *
 */
public class Downloader {
    protected static int count;
    protected static int total;
    protected static int itemCount;
    protected static int itemTotal;
    protected static String error;
    protected static boolean cancelled;
    
    public static void install(String location, String filename) {
        try {
            // Reset all values
            cancelled = false;
            count = 0;
            total = 0;
            itemCount = 0;
            itemTotal = 0;
            
            if (cancelled) {
                return;
            }
            
            // Inform the console and start downloading
            ResidenceSigns.log.info(ResidenceSigns.name + "Downloading " + filename + "...");
            ResidenceSigns.log.info(ResidenceSigns.name + "Server restart may be necessary");
            download(location, filename);
        }
        catch(IOException ex) {
            // Something went wrong, inform the console
            ResidenceSigns.log.warning(ResidenceSigns.name + "Error Downloading File: " + ex);
        }
    }
    
    protected static synchronized void download(String location, String filename) throws IOException {
        // Set up a connection with the server we are downloading the file from
        URLConnection connection = new URL(location).openConnection();
        connection.setUseCaches(false);
        // int filesize = connection.getContentLength();
        
        // Figure out where to save the file and create folders if needed
        String destination = "lib" + File.separator + filename;
        File parentDirectory = new File(destination).getParentFile();

        if (parentDirectory != null) {
            parentDirectory.mkdirs();
        }
        
        // The stream where we are getting the data from the server
        InputStream input = connection.getInputStream();
        // The stream where we are writing the data to the disk
        OutputStream output = new FileOutputStream(destination);

        byte[] buffer = new byte[65536];
        
        @SuppressWarnings("unused")
		int currentCount = 0;

        //A s long as the download isn't cancelled read from the input and write to the output
        while (!cancelled) {
            int count = input.read(buffer);

            if (count < 0) {
                // When there's no more to be read break the loop
                break;
            }
            output.write(buffer, 0, count);
            currentCount += count;
        }
        
        //And close both streams
        input.close();
        output.close();
    }
}
