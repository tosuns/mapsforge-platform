/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.graph;

import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.maps.project.ui.component.WidgetPropertySheet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Serdar
 */
public class ProcessWidget extends Widget {

    private static final Logger LOG = Logger.getLogger(ProcessWidget.class.getName());
    private final AbstractAggregationProcess process;
    private JButton settingsButton;
    private JLabel nameLabel = new JLabel();
    private final ProcessGraph processGraph;

    public ProcessWidget(ProcessGraph scene, AbstractAggregationProcess proc) {
        super(scene);
        assert proc != null;
        this.process = proc;
        this.processGraph = scene;
        init();
    }

    private void init() {
        getActions().addAction(ActionFactory.createPopupMenuAction(new ProcessPopupProvider()));
        setOpaque(true);
        setToolTipText(process.getDescription());
        setPreferredSize(new Dimension(150, 180));
        setBorder(BorderFactory.createRoundedBorder(10, 10, 2, 2, new Color(0, 0, 0, 255), new Color(0, 0, 0, 0)));
        setBackground(new LinearGradientPaint(0, 0, 0, 180,
                new float[]{0, 0.03f, 0.44f, 0.85f, 0.88f, 1},
                new Color[]{Color.decode("0xffffff"),
            Color.decode("0xe8e8e8"),
            Color.decode("0xb9b9b9"),
            Color.decode("0x9e9e9e"),
            Color.decode("0x9e9e9e"),
            Color.decode("0x696969")}));

        initSettingsButton();
        initLabel();
    }

    private void initLabel() {
        ComponentWidget labelWidget = new ComponentWidget(getScene(), nameLabel);
        labelWidget.setPreferredLocation(new Point(8, 8));
        labelWidget.setPreferredSize(new Dimension(getPreferredSize().width - 24, 14));
        nameLabel.setText(process.getName());
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        addChild(labelWidget);
    }

    private void initSettingsButton() {
        if (!process.getProcessDescriptor().getProperties().getSections().isEmpty()) {
            settingsButton = new JButton();
            settingsButton.setBackground(new Color(0, 0, 0, 0));
            settingsButton.setText("Settings");
            settingsButton.addActionListener(new SettingAction());

            Widget buttonWidget = new ComponentWidget(getScene(), settingsButton);
            buttonWidget.setPreferredLocation(new Point(10, getPreferredSize().height - 32));
            buttonWidget.setPreferredSize(new Dimension(getPreferredSize().width - 24, 20));
            addChild(buttonWidget);
        }
    }

    @Override
    protected Rectangle calculateClientArea() {
        Insets insets = getBorder().getInsets();
        return new Rectangle(
                getLocation().x,
                getLocation().y,
                getPreferredSize().width - (insets.left + insets.right),
                getPreferredSize().height - (insets.bottom + insets.top));
    }

    @Override
    protected void paintWidget() {
        super.paintWidget();
        Rectangle bounds = calculateClientArea();
        Graphics2D graphics = getGraphics();
        Color color = graphics.getColor();
        graphics.setPaint(getBackground());
        graphics.fillRoundRect(0, 0, bounds.width, bounds.height, 10, 10);
        graphics.setColor(Color.black);
        graphics.fillRoundRect(10, 28, getPreferredSize().width - 24, getPreferredSize().height - 68, 10, 10);
        graphics.drawImage(
                process.getIcon(),
                (getPreferredSize().width - process.getIcon().getWidth(null)) / 2 - 2,
                (getPreferredSize().height - process.getIcon().getHeight(null)) / 2 - 5,
                null);
        graphics.setColor(color);
    }

    private class ProcessPopupProvider implements PopupMenuProvider {

        private JPopupMenu popupMenu = null;

        public ProcessPopupProvider() {
            popupMenu = new JPopupMenu();

            popupMenu.add(new AbstractAction("Remove") {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {

                    Collection<String> findNodeEdges = processGraph.findNodeEdges(process, true, true);

                    for (String string : findNodeEdges) {
                        processGraph.removeEdge(string);
                    }
                    Set<?> objects = processGraph.getObjects();
                    if (objects.contains(process)) {
                        processGraph.removeNode(process);
                    } else if (objects.isEmpty()) {
                        processGraph.clearGraph();
                    }
                    processGraph.updateAggregatorPipeline();
                    getScene().validate();
                }
            });
        }

        @Override
        public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
            return popupMenu;
        }
    }

    private class SettingAction implements ActionListener {

        public SettingAction() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            WidgetPropertySheet.createWidgetPropertySheet(process).setVisible(true);
        }
    }
}
