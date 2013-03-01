/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.xmls;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Serdar
 */
@XmlType(name = "inferenceset")
@XmlAccessorType(XmlAccessType.FIELD)
public class InferenceSet {

    @XmlElement(name = "dataset")
    private List<DataSet> datasetList = new ArrayList<DataSet>();

    public InferenceSet() {
    }

    public List<DataSet> getDatasetList() {
        return datasetList;
    }
}
