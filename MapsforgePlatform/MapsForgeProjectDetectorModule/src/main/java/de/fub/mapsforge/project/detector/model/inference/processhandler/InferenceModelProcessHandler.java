/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlerDescriptor;
import de.fub.utilsmodule.node.CustomAbstractnode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public abstract class InferenceModelProcessHandler {

    private AbstractInferenceModel inferenceMode;
    private Node nodeDelegate;
    private ProcessHandlerDescriptor descriptor = null;

    public InferenceModelProcessHandler(AbstractInferenceModel inferenceModel) {
        this.inferenceMode = inferenceModel;
    }

    /**
     *
     * @return
     */
    protected AbstractInferenceModel getInferenceModel() {
        return inferenceMode;
    }

    protected void setInferenceModel(AbstractInferenceModel inferenceModel) {
        this.inferenceMode = inferenceModel;
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
            descriptor = createDefaultDescriptor();
        }
        return descriptor;
    }

    protected void setDescriptor(ProcessHandlerDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    protected abstract void handle();

    protected abstract ProcessHandlerDescriptor createDefaultDescriptor();

    public static synchronized Collection<? extends InferenceModelProcessHandler> findAll() {
        Collection<? extends InferenceModelProcessHandler> allInstances = Lookup.getDefault().lookupResult(InferenceModelProcessHandler.class).allInstances();
        List<InferenceModelProcessHandler> resultList = new ArrayList<InferenceModelProcessHandler>();

        for (InferenceModelProcessHandler instance : allInstances) {
            if (instance != null) {
                try {
                    InferenceModelProcessHandler handler = instance.getClass().newInstance();
                    resultList.add(handler);
                } catch (InstantiationException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IllegalAccessException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return resultList;
    }

    public static synchronized InferenceModelProcessHandler find(ProcessHandlerDescriptor descriptor, AbstractInferenceModel inferenceModel) throws InstantiationException {
        assert descriptor != null && descriptor.getJavaType() != null;
        InferenceModelProcessHandler handler = find(descriptor.getJavaType(), inferenceModel);
        if (handler != null) {
            handler.setDescriptor(descriptor);
        } else {
            throw new InstantiationException(MessageFormat.format("Couldn't instantiate class {0}. Make sure class is annotated with '@ServiceProvider'", descriptor.getJavaType()));
        }
        return handler;
    }

    public static synchronized InferenceModelProcessHandler find(String qualifiedName, AbstractInferenceModel inferenceModel) throws InstantiationException {
        assert qualifiedName != null;
        InferenceModelProcessHandler handler = null;
        Collection<? extends InferenceModelProcessHandler> findAll = Lookup.getDefault().lookupResult(InferenceModelProcessHandler.class).allInstances();

        for (InferenceModelProcessHandler instance : findAll) {
            if (instance != null && instance.getClass().getName().equals(qualifiedName)) {
                try {
                    handler = instance.getClass().newInstance();
                    handler.setInferenceModel(inferenceModel);
                } catch (IllegalAccessException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        if (handler == null) {
            throw new InstantiationException(MessageFormat.format("Couldn't instantiate class {0}. Make sure class is annotated with '@ServiceProvider'", qualifiedName));
        }
        return handler;
    }

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
