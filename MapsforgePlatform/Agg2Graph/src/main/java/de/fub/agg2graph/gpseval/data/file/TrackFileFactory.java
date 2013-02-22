package de.fub.agg2graph.gpseval.data.file;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TrackFileFactory is used to generate TrackFile-instances based on the files
 * extension.
 * 
 * <p>
 * You can register your own TrackFile-class for a file extension (see
 * {@link de.fub.agg2graph.gpseval.data.file.TrackFileFactory#registerGPSDataFileClass(java.lang.String, java.lang.Class)
 * registerGPSDataFileClass}).
 * </p>
 * 
 * @see
 */
public class TrackFileFactory {

	private static TrackFileFactory instance = new TrackFileFactory();
	private Map<String, Class<? extends TrackFile>> mGPSDataFileClasses = new HashMap<>();

	private TrackFileFactory() {
		registerBuiltInGPSDataFiles();
	}

	/**
	 * Register the built-in TrackFile-classes.
	 */
	private void registerBuiltInGPSDataFiles() {
		registerGPSDataFileClass(".csv", MyTracksCSVFile.class);
	}

	/**
	 * Returns the TrackFileFactory-instance.
	 * 
	 * @return
	 */
	public static TrackFileFactory getFactory() {
		return instance;
	}

	/**
	 * Register a TrackFile-class for the specified file extension.
	 * 
	 * @param extension
	 * @param gpsDataFileClass
	 */
	public void registerGPSDataFileClass(String extension,
			Class<? extends TrackFile> gpsDataFileClass) {
		mGPSDataFileClasses.put(extension, gpsDataFileClass);
	}

	/**
	 * Get the TrackFile-class used for the specified file extension.
	 * 
	 * @param extension
	 * @return
	 */
	public Class<? extends TrackFile> getGPSDataFileClass(String extension) {
		return mGPSDataFileClasses.get(extension);
	}

	/**
	 * Create a TrackFile-instance for the file specified by the path.
	 * 
	 * @param file
	 * @return
	 */
	public TrackFile newGPSDataFile(Path file) {
		String filename = file.getFileName().toString();
		String extension = filename.substring(filename.lastIndexOf('.'));

		TrackFile gpsDataFile = null;
		Class<? extends TrackFile> gpsDataFileClass = mGPSDataFileClasses
				.get(extension);

		if (gpsDataFileClass != null) {
			try {
				gpsDataFile = gpsDataFileClass.newInstance();
				gpsDataFile.setDataFile(file);
			} catch (InstantiationException | IllegalAccessException ex) {
				Logger.getLogger(TrackFileFactory.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}

		return gpsDataFile;
	}
}
