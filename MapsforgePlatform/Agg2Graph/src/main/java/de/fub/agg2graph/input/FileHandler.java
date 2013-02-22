/*******************************************************************************
   Copyright 2013 Johannes Mitlmeier

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
******************************************************************************/
package de.fub.agg2graph.input;

import java.io.File;
import java.io.FilenameFilter;

/**
 * File-related helper methods.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class FileHandler {
	public static FilenameFilter gpxFilter = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".gpx");
		}
	};

	public static File getFile(String filename) {
		// this might return something different to handle accessing files from
		// inside the jar file we might run from
		return new File(filename);
	}

	/**
	 * Get a writable path even if the code is living on non-writable media. If
	 * so, a folder inside the system's temp path is returned.
	 * 
	 * @param folderName
	 * @return
	 */
	public static File getWritableFolder(String folderName) {
		File folder = new File(folderName);
		folder.mkdirs();
		if (!folder.canWrite()) {
			folder = new File(System.getProperty("java.io.tmpdir")
					+ "/agg2graph/" + folderName);
			folder.mkdirs();
			if (!folder.canWrite()) {
				return null;
			}
		}
		return folder;
	}

	public static boolean removeDirectory(File directory) {
		if (directory.isDirectory()) {
			String[] children = directory.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = removeDirectory(new File(directory,
						children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return directory.delete();
	}

}
