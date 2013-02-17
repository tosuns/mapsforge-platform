/*******************************************************************************
 * Copyright (c) 2012 Johannes Mitlmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Affero Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/agpl-3.0.html
 * 
 * Contributors:
 *     Johannes Mitlmeier - initial API and implementation
 ******************************************************************************/
package de.fub.agg2graph.ui;

import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.graph.RamerDouglasPeuckerFilter;
import de.fub.agg2graph.input.GPSCleaner;
import de.fub.agg2graph.osm.IExporter;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.ui.gui.CalcThread;
import java.util.ArrayList;
import java.util.List;

public class StepStorage {
	protected AggContainer aggContainer;
	private GPSCleaner gpsCleaner;
	private RamerDouglasPeuckerFilter cleaningRamerDouglasPeuckerFilter;
	private RamerDouglasPeuckerFilter exportRamerDouglasPeuckerFilter;
	protected RoadNetwork roadNetwork;
	private IExporter exporter;
	private boolean openOsmExportFile = false;

	public List<ClassObjectEditor> getObjectEditorsForLevel(String level) {
		return getObjectEditorsForLevel(CalcThread.levels.get(level));
	}

	public List<ClassObjectEditor> getObjectEditorsForLevel(int level) {
		List<ClassObjectEditor> result = new ArrayList<ClassObjectEditor>(2);
		switch (level) {
		// input
		case 0:
			break;
		// clean
		case 1:
			result.add(new ClassObjectEditor(getGpsCleaner()
					.getCleaningOptions()));
			result.add(new ClassObjectEditor(
					getCleaningRamerDouglasPeuckerFilter()));
			break;
		// agg
		case 2:
			result.addAll(getAggContainer().getAggregationStrategy()
					.getSettings());
			break;
		// road gen
		case 3:
			result.addAll(getRoadNetwork().getSettings());
			break;
		// osm export
		case 4:
			result.addAll(getExporter().getSettings());
			break;
		}
		return result;
	}

	public GPSCleaner getGpsCleaner() {
		return gpsCleaner;
	}

	public void setGpsCleaner(GPSCleaner gpsCleaner) {
		this.gpsCleaner = gpsCleaner;
	}

	public RamerDouglasPeuckerFilter getCleaningRamerDouglasPeuckerFilter() {
		return cleaningRamerDouglasPeuckerFilter;
	}

	public void setCleaningRamerDouglasPeuckerFilter(
			RamerDouglasPeuckerFilter cleaningRamerDouglasPeuckerFilter) {
		this.cleaningRamerDouglasPeuckerFilter = cleaningRamerDouglasPeuckerFilter;
	}

	public RamerDouglasPeuckerFilter getExportRamerDouglasPeuckerFilter() {
		return exportRamerDouglasPeuckerFilter;
	}

	public void setExportRamerDouglasPeuckerFilter(
			RamerDouglasPeuckerFilter exportRamerDouglasPeuckerFilter) {
		this.exportRamerDouglasPeuckerFilter = exportRamerDouglasPeuckerFilter;
	}

	public AggContainer getAggContainer() {
		return aggContainer;
	}

	public void setAggContainer(AggContainer aggContainer) {
		this.aggContainer = aggContainer;
	}

	public RoadNetwork getRoadNetwork() {
		return roadNetwork;
	}

	public void setRoadNetwork(RoadNetwork roadNetwork) {
		this.roadNetwork = roadNetwork;
	}

	public IExporter getExporter() {
		return exporter;
	}

	public void setExporter(IExporter exporter) {
		this.exporter = exporter;
	}

	public void setOpenOsmExportFile(boolean openOsmExportFile) {
		this.openOsmExportFile = openOsmExportFile;
	}

	public boolean isOpenOsmExportFile() {
		return openOsmExportFile;
	}
}
