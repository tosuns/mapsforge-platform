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
package de.fub.agg2graph.ui.gui;

import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.graph.RamerDouglasPeuckerFilter;
import de.fub.agg2graph.input.CleaningOptions;
import de.fub.agg2graph.input.GPSCleaner;
import de.fub.agg2graph.osm.ExporterFactory;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.ui.StepStorage;
import de.fub.agg2graph.ui.gui.RenderingOptions.RenderingType;
import de.fub.agg2graph.ui.gui.jmv.Layer;
import de.fub.agg2graph.ui.gui.jmv.TestUI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class UIStepStorage extends StepStorage {
	// data
	public List<GPSSegment> inputSegmentList = new ArrayList<GPSSegment>();;
	public List<GPSSegment> cleanSegmentList = new ArrayList<GPSSegment>();

	// layers
	public Layer rawLayer;
	public Layer cleanLayer;
	public Layer aggLayer;
	public Layer intersectionLayer;
	public Layer roadLayer;
	private TestUI ui;
	public int levelReached = 1;

	public UIStepStorage(TestUI ui) {
		setGpsCleaner(new GPSCleaner());
		setCleaningRamerDouglasPeuckerFilter(new RamerDouglasPeuckerFilter(5));
		setExportRamerDouglasPeuckerFilter(new RamerDouglasPeuckerFilter(15));
		setRoadNetwork(new RoadNetwork());
		setExporter(ExporterFactory.getObject());

		this.ui = ui;
		LayerManager lm = ui.getLayerManager();

		// init layers
		RenderingOptions x = new RenderingOptions();
		x.setColor(new Color(97, 123, 228)); // blue
		x.setRenderingType(RenderingType.ALL);
		x.setzIndex(-1);
		x.setOpacity(1);
		rawLayer = new Layer("input", "Raw gps data", x);
		lm.addLayerToPanel(rawLayer, new RenderingPanel(ui));
		lm.addLayerToPanel(rawLayer, ui.getMainPanel());

		x = new RenderingOptions();
		x.setColor(new Color(39, 172, 88)); // green
		x.setRenderingType(RenderingType.ALL);
		x.setzIndex(0);
		x.setOpacity(1);
		cleanLayer = new Layer("clean", "Clean gps data", x);
		lm.addLayerToPanel(cleanLayer, new RenderingPanel(ui));
		lm.addLayerToPanel(cleanLayer, ui.getMainPanel());

		x = new RenderingOptions();
		x.setColor(new Color(232, 23, 79)); // red
		x.setzIndex(1);
		x.setOpacity(0.7);
		Layer matchingLayer = new Layer("matching", "Matching", x);
		lm.addLayerToPanel(matchingLayer, new RenderingPanel(ui));
		lm.addLayerToPanel(matchingLayer, ui.getMainPanel());

		x = new RenderingOptions();
		x.setColor(new Color(240, 225, 17)); // yellow/orange
		x.setzIndex(2);
		x.setOpacity(0.7);
		Layer mergingLayer = new Layer("merging", "Merging", x);
		lm.addLayerToPanel(mergingLayer, new RenderingPanel(ui));
		lm.addLayerToPanel(mergingLayer, ui.getMainPanel());

		x = new RenderingOptions();
		x.setColor(new Color(38, 36, 5)); // black
		x.setRenderingType(RenderingType.ALL);
		x.setzIndex(3);
		x.setOpacity(1);
		aggLayer = new Layer("agg", "Aggregation", x);
		lm.addLayerToPanel(aggLayer, new RenderingPanel(ui));
		lm.addLayerToPanel(aggLayer, ui.getMainPanel());

		x = new RenderingOptions();
		x.setColor(new Color(137, 0, 255)); // dark blue, semi transparent!
		x.setRenderingType(RenderingType.POINTS);
		x.setzIndex(4);
		x.setOpacity(0.5);
		x.setStrokeBaseWidthFactor(25);
		intersectionLayer = new Layer("intersections", "Intersections", x);
		lm.addLayerToPanel(intersectionLayer, new RenderingPanel(ui));
		lm.addLayerToPanel(intersectionLayer, ui.getMainPanel());

		x = new RenderingOptions();
		x.setRenderingType(RenderingType.ALL);
		x.setzIndex(5);
		x.setOpacity(1);
		x.setStrokeBaseWidthFactor(1.5f);
		roadLayer = new Layer("road", "Roads", x);
		lm.addLayerToPanel(roadLayer, new RenderingPanel(ui));
		lm.addLayerToPanel(roadLayer, ui.getMainPanel());

		// some initial values
		CleaningOptions o = getGpsCleaner().getCleaningOptions();
		o.filterBySegmentLength = true;
		o.minSegmentLength = 1;
		o.maxSegmentLength = 100;
		o.filterByEdgeLength = true;
		o.minEdgeLength = 0.3;
		o.maxEdgeLength = 750;
		o.filterZigzag = true;
		o.maxZigzagAngle = 30;
		o.filterFakeCircle = true;
		o.maxFakeCircleAngle = 50;
		o.filterOutliers = false;
		o.maxNumOutliers = 2;
	}

	@Override
	public void setAggContainer(AggContainer aggContainer) {
		this.aggContainer = aggContainer;
		aggLayer.clear();
		aggLayer.addObject(aggContainer);
	}

	public void clear(int level) {
		if (level <= 3 && levelReached >= 3) {
			if (roadNetwork != null) {
				roadNetwork.clear();
				roadLayer.clear();
				intersectionLayer.clear();
			}
		}
		if (level <= 2 && levelReached >= 2) {
			if (aggContainer != null) {
				aggContainer.clear();
			}
			// debug stuff
			ui.getLayerManager().getLayer("matching").clear();
			ui.getLayerManager().getLayer("merging").clear();
		}
		if (level <= 1 && levelReached >= 1) {
			cleanLayer.clear();
		}
		if (level <= 0 && levelReached >= 0) {
			rawLayer.clear();
			ui.dataBoundingBox = null;
		}
	}
}
