/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.detector.factories;

import de.fub.maps.project.detector.factories.nodes.datasets.DataSetNode;
import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.xmls.DataSet;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class DataSetNodeFactory extends ChildFactory<DataSet> {

    private final Detector detector;
    private final List<DataSet> datasetList;

    public DataSetNodeFactory(Detector detector, List<DataSet> datasetList) {
        this.detector = detector;
        this.datasetList = datasetList;
    }

    @Override
    protected boolean createKeys(List<DataSet> toPopulate) {
        toPopulate.addAll(datasetList);
        return true;
    }

    @Override
    protected Node createNodeForKey(DataSet deteset) {
        return new DataSetNode(detector, deteset);
    }
}
