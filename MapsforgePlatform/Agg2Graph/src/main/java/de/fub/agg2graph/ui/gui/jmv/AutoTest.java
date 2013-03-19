/*******************************************************************************
    Copyright (c) 2012 Johannes Mitlmeier.
 
    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA

 ******************************************************************************/
package de.fub.agg2graph.ui.gui.jmv;

import de.fub.agg2graph.input.FileHandler;
import de.fub.agg2graph.input.Globals;
import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.DoubleRect;
import de.fub.agg2graph.ui.DSL;
import de.fub.agg2graph.ui.Token;
import de.fub.agg2graph.ui.cli.CLI;
import de.fub.agg2graph.ui.gui.CalcThread;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class AutoTest extends CLI {
	private TestUI testUI;
	private CalcThread ct;
	private static Logger logger = Logger.getLogger("agg2graph.autotest");

	public static void main(String[] args) throws FileNotFoundException,
			IOException, InterruptedException {
		AutoTest autoTest = new AutoTest();
		autoTest.run(args);
		System.exit(0);
	}

	/**
	 * Execute a number of tests and save output as specified in text files
	 * using a simple domain-specific language.
	 * 
	 * @param args
	 *            list of filenames (search is conducted in folder autotest), if
	 *            empty all files in that folder are processed
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	@Override
	public void run(String[] args) throws FileNotFoundException, IOException,
			InterruptedException {
		Map<String, String> keyValueMap;

		// test all files or do we have filenames in args?
		File[] targetFiles = CLI
				.getTargetFiles(args, new File("test/autotest"));

		// read settings
		Properties p = new Properties();
		p.load(new FileInputStream(new File("test/autotest/settings")));
		Dimension size = new Dimension(Integer.parseInt(p.getProperty(
				"savelayer-width", "100")), Integer.parseInt(p.getProperty(
				"savelayer-height", "75")));
		outputFolder = FileHandler.getWritableFolder("test/autotest/output/");

		for (File currentFile : targetFiles) {
			file = currentFile;
			if ("settings".equals(file.getName()) || file.isDirectory()) {
				continue;
			}
			logger.log(Level.INFO, "processing file {0}", file.getName());
			BufferedReader bufferedReader = null;
			try {
				bufferedReader = new BufferedReader(new FileReader(file));
			} catch (IOException ex) {
				System.out.println("File not found. Search path: " + file);
				continue;
			}
			// initialize stuff
			testUI = new TestUI();
			Globals.put("ui", testUI);
			testUI.frmTestui.setTitle("AutoTest");
			testUI.setShowLayers(false);
			testUI.show(false);
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e1) {
			}
			testUI.getMainPanel().setMinimumSize(size);
			testUI.getMainPanel().setPreferredSize(size);
			testUI.getMainPanel().setMaximumSize(size);
			testUI.frmTestui.pack();
			stepStorage = testUI.uiStepStorage;
			stepStorage.setOpenOsmExportFile(false);
			ct = new CalcThread(testUI);

			String line = null;
			// parse line
			while ((line = bufferedReader.readLine()) != null) {
				logger.fine("parsing line \"" + line + "\"");
				testUI.frmTestui.setTitle("AutoTest - " + file.getName()
						+ " - " + line.replaceAll("\\s", " "));
				tokens = DSL.getTokens(line);
				keyValueMap = DSL.getKeyValueMap(tokens);
				if (tokens.size() == 0) {
					continue;
				}

				// loop tokens
				Token t;
				int tokenIndex = 0;
				t = tokens.get(tokenIndex);
				// handle steps that are available in the GUI as well
				if ("input".equals(t.name)) {
					testUI.sourceFolderCombo.setSelectedItem(new File(
							"test/input/" + tokens.get(1).name));
				}
				if (CalcThread.levels.keySet().contains(t.name)) {
					parseParameters(t.name, keyValueMap);
					// osm output path
					if ("osm".equals(t.name)) {
						File outputFile = new File(
								outputFolder.getAbsoluteFile()
										+ File.separator
										+ file.getName()
										+ "-"
										+ (tokens.size() > tokenIndex + 1 ? tokens
												.get(tokenIndex + 1).name
												: "osm") + ".xml");
						stepStorage.getExporter().setTargetFile(outputFile);
					}
					ct = new CalcThread(testUI);
					ct.setTask(t.name);
					ct.start();
					try {
						ct.join(0);
					} catch (InterruptedException e) {
					}
					Thread.sleep(1000);
				} else if ("savelayer".equals(t.name)) {
					// make empty Image
					BufferedImage bImage = new BufferedImage(size.width,
							size.height, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = (Graphics2D) bImage.getGraphics();
					g2.setColor(new Color(233, 233, 233));
					g2.fillRect(0, 0, size.width, size.height);
					String layerNameString = "";
					while (testUI.isPainting()) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
					}
					// loop other tokens (layer names)
					for (int i = 1; i < tokens.size(); i++) {
						if (tokens.get(i).isKeyValue()) {
							continue;
						}
						// get layer
						String layerName = tokens.get(i).name;
						if ("osm".equals(layerName)) {
							testUI.getMainPanel().paint(g2);
						} else {
							Layer layer = testUI.getLayerManager().getLayer(
									layerName);
							if (layer.getImage() != null) {
								g2.drawImage(layer.getImage(), 0, 0, null);
							}
						}
						layerNameString += layerName + "-";
					}
					layerNameString = layerNameString.substring(0,
							layerNameString.length() - 1);
					// save image to disk
					File outputFile = new File(outputFolder.getAbsoluteFile()
							+ File.separator
							+ file.getName()
							+ "-"
							+ layerNameString
							+ (keyValueMap.get("label") == null ? "" : "-"
									+ keyValueMap.get("label")) + ".png");
					ImageIO.write(bImage, "png", outputFile);
					logger.log(Level.INFO, "Writing output file to "
							+ outputFile.getAbsolutePath());
				} else if ("resetview".equals(t.name)) {
					testUI.getMainPanel().showArea(testUI.dataBoundingBox);
				} else if ("showarea".equals(t.name)) {
					// format: showarea 52.1,12 49.1,14.2 (lat,lon)
					DoubleRect area = new DoubleRect();
					area.fromMinMax(
							Math.min(Double.parseDouble(tokens.get(1).name
									.split(",")[0]), Double.parseDouble(tokens
									.get(2).name.split(",")[0])), Math.min(
									Double.parseDouble(tokens.get(1).name
											.split(",")[1]), Double
											.parseDouble(tokens.get(2).name
													.split(",")[1])), Math.max(
									Double.parseDouble(tokens.get(1).name
											.split(",")[0]), Double
											.parseDouble(tokens.get(2).name
													.split(",")[0])), Math.max(
									Double.parseDouble(tokens.get(1).name
											.split(",")[1]), Double
											.parseDouble(tokens.get(2).name
													.split(",")[1])));
					testUI.getMainPanel().showArea(area);
				} else if ("loadagg".equals(t.name)) {
					// TODO implement
				} else if ("statistics".equals(t.name)) {
					printStatistics("statistics.txt");
				}
			}
			bufferedReader.close();
			testUI.frmTestui.setVisible(false);
			testUI.frmTestui.dispose();
		}
	}

	@Override
	public void parseParameters(String tokenName,
			Map<String, String> parameterMap) {
		List<ClassObjectEditor> objectEditors = stepStorage
				.getObjectEditorsForLevel(CalcThread.levels.get(tokenName));
		for (String key : parameterMap.keySet()) {
			for (ClassObjectEditor objectEditor : objectEditors) {
				logger.log(Level.INFO, "setting "
						+ objectEditor.getObject().getClass().getName() + "'s "
						+ key + " to " + parameterMap.get(key));
				objectEditor.setValue(key, parameterMap.get(key));
			}
		}
	}
}
