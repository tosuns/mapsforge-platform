/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.beans;

import com.rits.cloning.Cloner;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public class BeanUtil {

    public static <T> T copyBean(T bean) {
        T beanCopy = null;
        Cloner cloner = new Cloner();
        ClassLoader systemClassLoader = Lookup.getDefault().lookup(ClassLoader.class);
        try {
//            cloner.dontClone(Class.forName("java.io.File"),
//                    Class.forName("java.io.InputStream"),
//                    Class.forName("java.io.OutputStream"),
//                    Class.forName("org.openide.filesystems.FileObject"),
//                    Class.forName("org.openide.util.Lookup")//,
            //                    Class.forName("org.openide.explorer.ExplorerManager") //                    ,
            //                    Class.forName("org.openide.loaders.DataObject"),
            //                    Class.forName("org.jfree.chart.title.TextTitle"),
            //                    Class.forName("org.jfree.ui.RectangleEdge")
//                    );
            cloner.dontCloneInstanceOf(Class.forName("java.io.File"),
                    Class.forName("java.io.InputStream"),
                    Class.forName("java.io.OutputStream"),
                    Class.forName("org.openide.filesystems.FileObject"),
                    Class.forName("org.openide.util.Lookup"),
                    Class.forName("org.openide.explorer.ExplorerManager"),
                    Class.forName("org.jfree.chart.title.Title", true, systemClassLoader),
                    Class.forName("org.jfree.ui.RectangleEdge", true, systemClassLoader),
                    Class.forName("org.jfree.chart.axis.AxisLocation", true, systemClassLoader),
                    Class.forName("org.jfree.chart.plot.PlotOrientation", true, systemClassLoader),
                    Class.forName("de.fub.mapsforge.project.detector.model.Detector", true, systemClassLoader),
                    Class.forName("de.fub.mapsforge.project.detector.model.inference.processhandler.InferenceModelProcessHandler", true, systemClassLoader),
                    Class.forName("weka.classifiers.Evaluation", true, systemClassLoader),
                    Class.forName("org.openide.loaders.DataObject", true, systemClassLoader),
                    Class.forName("sun.misc.Unsafe"),
                    Class.forName("org.jfree.chart.JFreeChart", true, systemClassLoader),
                    Class.forName("sun.misc.Launcher$AppClassLoader", true, systemClassLoader),
                    Class.forName("sun.misc.URLClassPath", true, systemClassLoader),
                    Class.forName("sun.misc.URLClassPath$JarLoader", true, systemClassLoader),
                    Class.forName("sun.misc.URLClassPath$Loader", true, systemClassLoader),
                    Class.forName("sun.misc.Launcher$ExtClassLoader", true, systemClassLoader),
                    Class.forName("sun.misc.MetaIndex", true, systemClassLoader),
                    Class.forName("sun.misc.Cleaner", true, systemClassLoader),
                    Class.forName("java.lang.ClassLoader", true, systemClassLoader),
                    Class.forName("sun.misc.FloatingDecimal$1", true, systemClassLoader),
                    Class.forName("de.fub.mapsforge.project.detector.filetype.DetectorDataObject", true, systemClassLoader),
                    Class.forName("de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel.ClassLoader", true, systemClassLoader),
                    Class.forName("sun.misc.FloatingDecimal$1", true, systemClassLoader));

        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        cloner.setDumpClonedClasses(false);
        beanCopy = cloner.deepClone(bean);
        return beanCopy;
    }
}
