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
package de.fub.agg2graph.osm;

import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.agg2graph.structs.ClassObjectEditor;
import java.io.File;
import java.util.List;

public interface IExporter {

	public void export(RoadNetwork roadNetwork);

	public File getTargetFile();

	public void setTargetFile(File targetFile);

	public List<ClassObjectEditor> getSettings();
}
