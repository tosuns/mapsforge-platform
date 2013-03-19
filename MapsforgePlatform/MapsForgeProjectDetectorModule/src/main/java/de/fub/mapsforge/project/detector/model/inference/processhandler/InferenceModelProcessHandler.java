/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.InferenceMode;
import de.fub.mapsforge.project.detector.model.xmls.InferenceModelDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlerDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlers;
import de.fub.mapsforge.project.detector.utils.DetectorUtils;
import de.fub.utilsmodule.node.CustomAbstractnode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public abstract class InferenceModelProcessHandler {

    private final AbstractInferenceModel inferenceModel;
    private Node nodeDelegate;
    private ProcessHandlerDescriptor descriptor = null;

    public InferenceModelProcessHandler(AbstractInferenceModel inferenceModel) {
        this.inferenceModel = inferenceModel;
    }

    /**
     *
     * @return
     */
    protected AbstractInferenceModel getInferenceModel() {
        return inferenceModel;
    }

    /**
     *
     */
    public void start() {
        handle();
    }

    /**
     *
     * @return
     */
    public JComponent getVisualRepresentation() {
        return new DefaultRepresenter();
    }

    public Node getNodeDelegate() {
        if (nodeDelegate == null) {
            nodeDelegate = createNodeDelegate();
        }
        return nodeDelegate;
    }

    protected Node createNodeDelegate() {
        return new ProcessHandlerNode(InferenceModelProcessHandler.this);
    }

    public ProcessHandlerDescriptor getDescriptor() {
        if (descriptor == null) {
            AbstractInferenceModel inferenceMdl = getInferenceModel();
            if (inferenceMdl != null) {
                InferenceModelDescriptor inferenceModelDescriptor = inferenceMdl.getInferenceModelDescriptor();
                ProcessHandlers inferenceModelProcessHandlers = inferenceModelDescriptor.getInferenceModelProcessHandlers();
                List<ProcessHandlerDescriptor> processHandlerList = inferenceModelProcessHandlers.getProcessHandlerList();
                for (ProcessHandlerDescriptor desc : processHandlerList) {
                    if (desc.getJavaType().equals(getClass().getName())) {
                        descriptor = desc;
                        break;
                    }
                }
            } else {
                try {
                    descriptor = DetectorUtils.getXmlDescriptor(ProcessHandlerDescriptor.class, getClass());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return descriptor;
    }

    protected abstract void handle();

    private static class ProcessHandlerNode extends CustomAbstractnode {

        private final InferenceModelProcessHandler processHandler;

        private ProcessHandlerNode(InferenceModelProcessHandler processHandler) {
            super(Children.LEAF, Lookups.fixed(processHandler));
            this.processHandler = processHandler;
            updateNode();
        }

        private void updateNode() {
            if (processHandler != null && processHandler.getDescriptor() != null) {
                String name = processHandler.getDescriptor().getName();
                setDisplayName(name);
            }
        }
    }

    /**
     *
     */
    private static class DefaultRepresenter extends JPanel {

        private static final long serialVersionUID = 1L;

        public DefaultRepresenter() {
            super(new BorderLayout());
            setBackground(Color.white);
            setMinimumSize(new Dimension(0, 300));
            setPreferredSize(new Dimension(400, 300));
            JLabel jLabel = new JLabel("<Visual representation not supported>");
            add(jLabel, BorderLayout.CENTER);
            jLabel.setForeground(Color.gray);
        }
    }

    /**
     *
     */
    public static class InferenceModelClassifyException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public InferenceModelClassifyException() {
        }

        public InferenceModelClassifyException(String message) {
            super(message);
        }

        public InferenceModelClassifyException(String message, Throwable cause) {
            super(message, cause);
        }

        public InferenceModelClassifyException(Throwable cause) {
            super(cause);
        }

        public InferenceModelClassifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
