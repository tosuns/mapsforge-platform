/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.utils;

import de.fub.gpxmodule.GPXDataObject;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.xml.Property;
import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class AggregateUtils {

    @StaticResource
    public static final String ICON_PATH_NORMAL = "de/fub/mapsforge/project/aggregator/aggregatorIcon.png";
    @StaticResource
    public static final String ICON_PATH_BUSY = "de/fub/mapsforge/project/aggregator/aggregatorIconBusy.png";
    @StaticResource
    public static final String ICON_PATH_ERROR = "de/fub/mapsforge/project/aggregator/aggregatorIconError.png";
    private static HashMap<String, Class<? extends AbstractAggregationProcess>> hashMap = new HashMap<String, Class<? extends AbstractAggregationProcess>>();

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Class<T> clazz, String className) {
        T instance = null;
        try {
            Class<?> forName = Class.forName(className);
//            if (!clazz.equals(forName)) {
//                throw new IllegalArgumentException();
//            }
            Class<T> cl = (Class<T>) forName;
            instance = cl.newInstance();
        } catch (InstantiationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return instance;
    }

    public static <T> T createValue(Class<T> clazz, List<Property> properties) {
        T instance = null;
        try {
            instance = clazz.newInstance();
            HashMap<String, Property> propertyMap = new HashMap<String, Property>();
            for (Property property : properties) {
                propertyMap.put(property.getName(), property);
            }
            for (Field field : clazz.getFields()) {
                Property property = propertyMap.get(field.getName());
                if (property != null) {
                    try {
                        field.set(instance, getValue(Class.forName(property.getJavaType()), property));
                    } catch (ClassNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

        } catch (InstantiationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
        return instance;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T getValue(Class<T> clazz, Property property) {
        T instance = null;
        if (clazz.getName().equals(Boolean.class.getName())) {
            instance = (T) Boolean.valueOf(property.getValue());
        } else if (clazz.getName().equals(Double.class.getName())) {
            instance = (T) Double.valueOf(property.getValue());
        } else if (clazz.getName().equals(Integer.class.getName())) {
            instance = (T) Integer.valueOf(property.getValue());
        } else if (clazz.getName().equals(String.class.getName())) {
            instance = (T) property.getValue();
        } else if (clazz.getName().equals(Color.class.getName())) {
            instance = (T) new Color(Integer.parseInt(property.getValue(), 16));
        } else if (clazz.getName().equals(Long.class.getName())) {
            instance = (T) Long.valueOf(property.getValue());
        }


        return instance;
    }

    public static Project findProject(FileObject fileObject) {
        Project project = null;

        while (project == null && !fileObject.isRoot()) {
            try {
                project = ProjectManager.getDefault().findProject(fileObject);
            } catch (IllegalArgumentException ex) {
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            fileObject = fileObject.getParent();
        }

        return project;
    }
}
