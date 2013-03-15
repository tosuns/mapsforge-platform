/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.processhandler;

import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Serdar
 */
public abstract class InferenceModelProcessHandler {

    private final AbstractInferenceModel inferenceModel;

    public InferenceModelProcessHandler(AbstractInferenceModel inferenceModel) {
        this.inferenceModel = inferenceModel;
    }

    protected AbstractInferenceModel getInferenceModel() {
        return inferenceModel;
    }

    public void start() {
        handle();
    }

    protected abstract void handle();

    public JComponent getVisualRepresentation() {
        return new DefaultRepresenter();
    }

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
