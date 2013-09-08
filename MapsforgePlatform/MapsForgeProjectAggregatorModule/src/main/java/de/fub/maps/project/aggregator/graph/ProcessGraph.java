/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.maps.project.aggregator.graph;

import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.utilsmodule.Collections.ObservableArrayList;
import de.fub.utilsmodule.Collections.ObservableList;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;

/**
 * TODO besseren layouter implementieren.
 *
 * @author Serdar
 */
public class ProcessGraph extends GraphScene<AbstractAggregationProcess<?, ?>, String> implements ExplorerManager.Provider {

    private static long edgeCount = 0;
    private LayerWidget backgroundLayer = null;
    private LayerWidget mainLayer = null;
    private LayerWidget connectionLayer = null;
    private LayerWidget interactionLayer = null;
    private final ChangeSupport pcs = new ChangeSupport(this);
    private final HashMap<String, Integer> javaTypeCounterMap = new HashMap<String, Integer>();
    private final ExplorerManager explorerManager = new ExplorerManager();
    private final Lookup lookup = ExplorerUtils.createLookup(explorerManager, new ActionMap());
    private final ObservableList<AbstractAggregationProcess<?, ?>> processList = new ObservableArrayList<AbstractAggregationProcess<?, ?>>();
    private final NodeFactory nodeFactory;

    public ProcessGraph() {
        nodeFactory = new NodeFactory();
        explorerManager.setRootContext(new AbstractNode(Children.create(nodeFactory, true)));
        getActions().addAction(ActionFactory.createZoomAction(1.5, true));
        backgroundLayer = new LayerWidget(ProcessGraph.this);
        mainLayer = new LayerWidget(ProcessGraph.this);
        connectionLayer = new LayerWidget(ProcessGraph.this);
        interactionLayer = new LayerWidget(ProcessGraph.this);

        addChild(backgroundLayer);
        addChild(mainLayer);
        addChild(connectionLayer);
        addChild(interactionLayer);
        getActions().addAction(ActionFactory.createAcceptAction(new AcceptProviderImpl()));
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    public synchronized String createEdge() {
        return MessageFormat.format("edge-{0}", edgeCount++);
    }

    public void addChangeListener(ChangeListener listener) {
        pcs.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        pcs.removeChangeListener(listener);
    }

    public void clearGraph() {
        nodeFactory.objectToNodeMap.clear();
        processList.clear();
        javaTypeCounterMap.clear();
        Collection<AbstractAggregationProcess<?, ?>> nodes = getNodes();
        ArrayList<AbstractAggregationProcess<?, ?>> list = new ArrayList<AbstractAggregationProcess<?, ?>>(nodes);
        for (AbstractAggregationProcess<?, ?> process : list) {
            removeNodeWithEdges(process);
            validate();
        }
        mainLayer.removeChildren();
        validate();
        connectionLayer.removeChildren();
        validate();
        interactionLayer.removeChildren();
        validate();
    }

    private Integer getTypeCounter(AbstractAggregationProcess<?, ?> process) {
        if (!javaTypeCounterMap.containsKey(process.getProcessDescriptor().getJavaType())) {
            javaTypeCounterMap.put(process.getProcessDescriptor().getJavaType(), new Integer(1));
        }
        return javaTypeCounterMap.get(process.getProcessDescriptor().getJavaType());
    }

    private void updateTypeCounter(AbstractAggregationProcess<?, ?> process) {
        Integer typeCounter = getTypeCounter(process);
        javaTypeCounterMap.put(process.getProcessDescriptor().getJavaType(), ++typeCounter);
        Logger.getLogger(ProcessGraph.class.getName()).log(Level.INFO, MessageFormat.format("{0} count: {1}", process.getProcessDescriptor().getJavaType(), typeCounter));
    }

    @Override
    protected Widget attachNodeWidget(AbstractAggregationProcess<?, ?> process) {
        updateAggregatorPipeline();
        processList.add(process);
        ProcessWidget processWidget = new ProcessWidget(this, process);
        processWidget.setPreferredLocation(new Point(0, 0));
        Widget widget = new Widget(getScene());
        widget.setBackground(Color.BLACK);
        widget.getActions().addAction(ActionFactory.createMoveAction());
        processWidget.getActions().addAction(ActionFactory.createExtendedConnectAction(interactionLayer, new SceneConnectProvider()));
        widget.getActions().addAction(ActionFactory.createSelectAction(new ProcessSelectProvider()));
        widget.addChild(processWidget);
        widget.setPreferredSize(processWidget.getPreferredSize());
        mainLayer.addChild(widget);
        mainLayer.revalidate();
        validate();
        return widget;
    }

    @Override
    protected Widget attachEdgeWidget(String edge) {
        ConnectionWidget connectionWidget = new ConnectionWidget(this);
        connectionWidget.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        connectionWidget.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
        connectionWidget.setRouter(RouterFactory.createOrthogonalSearchRouter(mainLayer));
        connectionWidget.getActions().addAction(ActionFactory.createReconnectAction(new SceneReconnectProvider()));
        connectionWidget.getActions().addAction(createObjectHoverAction());
        connectionWidget.getActions().addAction(createSelectAction());
        connectionWidget.getActions().addAction(ActionFactory.createPopupMenuAction(new ConnectionPopUp()));
        connectionLayer.addChild(connectionWidget);
        connectionLayer.revalidate();
        validate();
        return connectionWidget;
    }

    @Override
    protected void attachEdgeSourceAnchor(String edge, AbstractAggregationProcess<?, ?> oldSourceNode, AbstractAggregationProcess<?, ?> sourceNode) {
        Widget sourceNodeWidget = findWidget(sourceNode);
        Anchor sourceAnchor = AnchorFactory.createRectangularAnchor(sourceNodeWidget);
        ConnectionWidget edgeWidget = (ConnectionWidget) findWidget(edge);
        edgeWidget.setSourceAnchor(sourceAnchor);
        validate();
    }

    @Override
    protected void attachEdgeTargetAnchor(String edge, AbstractAggregationProcess<?, ?> oldTargetNode, AbstractAggregationProcess<?, ?> targetNode) {
        Widget targetNodeWidget = findWidget(targetNode);
        Anchor targetAnchor = AnchorFactory.createRectangularAnchor(targetNodeWidget);
        ConnectionWidget edgeWidget = (ConnectionWidget) findWidget(edge);
        edgeWidget.setTargetAnchor(targetAnchor);
        validate();
    }

    private Class<?> getInputType(AbstractAggregationProcess target) {
        Class<?> targetInputType = null;
        List<Class<?>> targetInputTypes = getInputTypes(target);

        if (!targetInputTypes.isEmpty()) {
            if (targetInputTypes.size() == 2) {
                for (Class<?> c : targetInputTypes) {
                    if (!c.equals(Object.class)) {
                        targetInputType = c;
                        break;
                    }
                }
            } else {
                targetInputType = targetInputTypes.iterator().next();
            }
        }

        return targetInputType;
    }

    private List<Class<?>> getInputTypes(AbstractAggregationProcess target) {
        ArrayList<Class<?>> resultList = new ArrayList<Class<?>>();
        Class<? extends AbstractAggregationProcess> aClass = target.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if ("setInput".equals(method.getName())) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1) {
                    resultList.add(parameterTypes[0]);
                }
            }
        }
        return resultList;
    }

    private Class<?> getOutputType(AbstractAggregationProcess source) {
        Class<?> sourceOutputType = null;
        List<Class<?>> sourceOutputTypes = getOutputTypes(source);

        if (!sourceOutputTypes.isEmpty()) {
            if (sourceOutputTypes.size() == 2) {
                for (Class<?> c : sourceOutputTypes) {
                    if (!c.equals(Object.class)) {
                        sourceOutputType = c;
                        break;
                    }
                }
            } else {
                sourceOutputType = sourceOutputTypes.iterator().next();
            }
        }

        return sourceOutputType;
    }

    private List<Class<?>> getOutputTypes(AbstractAggregationProcess source) {
        ArrayList<Class<?>> resultList = new ArrayList<Class<?>>();
        Class<? extends AbstractAggregationProcess> aClass = source.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if ("getResult".equals(method.getName())) {
                resultList.add(method.getReturnType());
            }
        }
        return resultList;
    }

    void updateAggregatorPipeline() {
        pcs.fireChange();
    }

    public List<AbstractAggregationProcess<?, ?>> collectPipeline() {
        List<AbstractAggregationProcess<?, ?>> pipeLine = new ArrayList<AbstractAggregationProcess<?, ?>>();
        Collection<AbstractAggregationProcess<?, ?>> nodes = getNodes();
        for (AbstractAggregationProcess<?, ?> process : nodes) {
            Class<?> inputType = getInputType(process);
            if (inputType != null && inputType.isAssignableFrom(Void.class)) {
                pipeLine = collectPipelineUnits(process);
                break;
            }
        }
        return pipeLine;
    }

    private List<AbstractAggregationProcess<?, ?>> collectPipelineUnits(AbstractAggregationProcess<?, ?> process) {
        return collectPipelineUnits(process, new ArrayList<AbstractAggregationProcess<?, ?>>());
    }

    private List<AbstractAggregationProcess<?, ?>> collectPipelineUnits(AbstractAggregationProcess<?, ?> process, List<AbstractAggregationProcess<?, ?>> list) {
        list.add(process);
        Collection<String> findNodeEdges = findNodeEdges(process, true, false);
        if (findNodeEdges.size() == 1) {
            String edge = findNodeEdges.iterator().next();
            AbstractAggregationProcess<?, ?> edgeTarget = getEdgeTarget(edge);
            if (edgeTarget != null) {
                collectPipelineUnits(edgeTarget, list);
            }
        }
        return list;
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    private class ProcessSelectProvider implements SelectProvider {

        @Override
        public boolean isAimingAllowed(Widget widget, Point localLocation, boolean invertSelection) {
            return true;
        }

        @Override
        public boolean isSelectionAllowed(Widget widget, Point localLocation, boolean invertSelection) {
            return true;
        }

        @Override
        public void select(Widget widget, Point localLocation, boolean invertSelection) {
            widget.bringToFront();
            widget.setState(widget.getState().deriveSelected(!widget.getState().isSelected()));
            widget.getScene().validate();
            Node[] nodes = new Node[0];
            if (widget.getState().isSelected()) {
                Object object = ProcessGraph.this.findObject(widget);

                if (object instanceof AbstractAggregationProcess) {
                    AbstractAggregationProcess process = (AbstractAggregationProcess) object;
                    if (nodeFactory.objectToNodeMap.containsKey(process)) {
                        nodes = new Node[]{nodeFactory.objectToNodeMap.get(process)};
                    }
                }
            }
            try {
                getExplorerManager().setSelectedNodes(nodes);
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private class SceneConnectProvider implements ConnectProvider {

        @Override
        public boolean isSourceWidget(Widget sourceWidget) {
            Object object = ProcessGraph.this.findObject(sourceWidget);
            AbstractAggregationProcess source = ProcessGraph.this.isNode(object) ? (AbstractAggregationProcess) object : null;
            return source != null && ProcessGraph.this.findNodeEdges(source, true, false).isEmpty();
        }

        @Override
        public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
            Object sourceObject = ProcessGraph.this.findObject(sourceWidget);
            Object targetObject = ProcessGraph.this.findObject(targetWidget);

            AbstractAggregationProcess target = ProcessGraph.this.isNode(targetObject) ? (AbstractAggregationProcess) targetObject : null;
            AbstractAggregationProcess source = ProcessGraph.this.isNode(sourceObject) ? (AbstractAggregationProcess) sourceObject : null;

            // exclude the case source node connects to itself
            if (target != null
                    && source != null
                    && findNodeEdges(target, false, true).isEmpty()
                    && !source.equals(target)) {

                Class<?> targetInputType = getInputType(target);

                if (targetInputType != null && !targetInputType.equals(Void.TYPE)) {

                    Class<?> sourceOutputType = getOutputType(source);
                    // check whether source output type and
                    // target input type are assignable
                    if (sourceOutputType != null
                            && targetInputType.isAssignableFrom(sourceOutputType)) {
                        return ConnectorState.ACCEPT;
                    }
                }
            }
            // TODO show message dialog why not accepted.
            return ConnectorState.REJECT;
        }

        @Override
        public boolean hasCustomTargetWidgetResolver(Scene scene) {
            return false;
        }

        @Override
        public Widget resolveTargetWidget(Scene scene, Point sceneLocation) {
            return null;
        }

        @Override
        public void createConnection(Widget sourceWidget, Widget targetWidget) {
            String edgeId = MessageFormat.format("edge + {0}", edgeCount++);
            ProcessGraph.this.addEdge(edgeId);
            Object sourceObject = ProcessGraph.this.findObject(sourceWidget);
            Object targetObject = ProcessGraph.this.findObject(targetWidget);
            if (ProcessGraph.this.isNode(targetObject)
                    && ProcessGraph.this.isNode(sourceObject)
                    && sourceObject instanceof AbstractAggregationProcess
                    && targetObject instanceof AbstractAggregationProcess) {
                setEdgeSource(edgeId, (AbstractAggregationProcess) sourceObject);
                setEdgeTarget(edgeId, (AbstractAggregationProcess) targetObject);
                updateAggregatorPipeline();
            }
            validate();
        }
    }

    private class SceneReconnectProvider implements ReconnectProvider {

        String edge;
        AbstractAggregationProcess<?, ?> originalNode;
        AbstractAggregationProcess<?, ?> replacementNode;

        @Override
        public void reconnectingStarted(ConnectionWidget connectionWidget, boolean reconnectingSource) {
        }

        @Override
        public void reconnectingFinished(ConnectionWidget connectionWidget, boolean reconnectingSource) {
            validate();
        }

        @Override
        public boolean isSourceReconnectable(ConnectionWidget connectionWidget) {
            Object object = findObject(connectionWidget);
            edge = isEdge(object) ? (String) object : null;
            originalNode = edge != null ? ProcessGraph.this.getEdgeSource(edge) : null;
            return originalNode != null;
        }

        @Override
        public boolean isTargetReconnectable(ConnectionWidget connectionWidget) {
            // TODO differen logic for the condition to reconnect to given target
            Object object = findObject(connectionWidget);
            edge = isEdge(object) ? (String) object : null;
            originalNode = edge != null ? getEdgeTarget(edge) : null;
            return originalNode != null;
        }

        @Override
        public ConnectorState isReplacementWidget(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
            // TODO differen logic for the condition to reconnect to given target
            Object object = findObject(replacementWidget);
            replacementNode = isNode(object) ? (AbstractAggregationProcess<?, ?>) object : null;
            if (replacementNode != null
                    && findNodeEdges(replacementNode, false, true).isEmpty()) {
                return !originalNode.equals(replacementNode) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
            }
            return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }

        @Override
        public boolean hasCustomReplacementWidgetResolver(Scene scene) {
            return false;
        }

        @Override
        public Widget resolveReplacementWidget(Scene scene, Point sceneLocation) {
            return null;
        }

        @Override
        public void reconnect(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
            // TODO differen logic for the condition to reconnect to given target
            if (replacementWidget == null && isEdge(edge)) {
                removeEdge(edge);
            } else if (reconnectingSource) {
                setEdgeSource(edge, replacementNode);
            } else {
                Object edgeObject = findObject(connectionWidget);
                Object nodeObject = findObject(replacementWidget);

                if (isEdge(edgeObject) && edgeObject instanceof String
                        && isNode(nodeObject) && nodeObject instanceof AbstractAggregationProcess<?, ?>) {

                    setEdgeTarget((String) edgeObject, (AbstractAggregationProcess<?, ?>) nodeObject);
                }
                //
            }
            validate();
            updateAggregatorPipeline();
        }
    }

    private class ConnectionPopUp implements PopupMenuProvider {

        private JPopupMenu popup = new JPopupMenu();
        private Widget owner = null;

        public ConnectionPopUp() {
            init();
        }

        private void init() {
            popup.add(new AbstractAction("Remove") {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (owner != null) {
                        Object findObject = findObject(owner);
                        if (findObject instanceof String) {
                            removeEdge((String) findObject);
                            updateAggregatorPipeline();
                        }
                    }
                }
            });
        }

        @Override
        public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
            owner = widget;
            return popup;
        }
    }

    /**
     * Handles Drop down event to the graph scene.
     */
    private class AcceptProviderImpl implements AcceptProvider {

        public AcceptProviderImpl() {
        }

        @Override
        public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
            Widget child = null;
            try {
                Object transferData = transferable.getTransferData(AbstractAggregationProcess.PROCESS_FLAVOR);
                if (transferData instanceof AbstractAggregationProcess<?, ?>) {
                    AbstractAggregationProcess<?, ?> proc = (AbstractAggregationProcess<?, ?>) transferData;
                    Class<?> inputType = getInputType(proc);
                    if (inputType != null) {
                        if (inputType.isAssignableFrom(Void.class)) {
                            Collection<AbstractAggregationProcess<?, ?>> nodes = getNodes();
                            for (AbstractAggregationProcess<?, ?> p : nodes) {
                                inputType = getInputType(p);
                                if (inputType.isAssignableFrom(Void.class)) {
                                    return ConnectorState.REJECT;
                                }
                            }
                            // TODO message dialog why didn't get accepted!
                            return ConnectorState.ACCEPT;
                        } else {
                            return ConnectorState.ACCEPT;
                        }
                    }
                    child = findWidget(transferData);
                    if (child != null) {
                        return ConnectorState.REJECT;
                    }
                }
            } catch (UnsupportedFlavorException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            return ConnectorState.ACCEPT;
        }

        @Override
        public void accept(Widget widget, Point point, Transferable transferable) {
            try {
                Object transferData = transferable.getTransferData(AbstractAggregationProcess.PROCESS_FLAVOR);
                if (transferData instanceof de.fub.maps.project.api.process.Process) {
                    AbstractAggregationProcess process = (AbstractAggregationProcess) transferData.getClass().newInstance();
                    Integer typeCounter = getTypeCounter(process);
                    process.getProcessDescriptor().setDisplayName(MessageFormat.format("{0} ({1})", process.getProcessDescriptor().getDisplayName(), typeCounter));

                    Widget processWidget = addNode(process);
                    Dimension preferredSize = processWidget.getPreferredSize();
                    Point point1 = new Point(point.x - preferredSize.width / 2, point.y - preferredSize.height / 2);
                    Point convertLocalToScene = widget.convertLocalToScene(point1);
                    processWidget.setPreferredLocation(convertLocalToScene);
                    validate();
                    repaint();
                    updateTypeCounter(process);
                }
            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (UnsupportedFlavorException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private class NodeFactory extends ChildFactory<AbstractAggregationProcess<?, ?>> implements ChangeListener {

        private final HashMap<AbstractAggregationProcess<?, ?>, Node> objectToNodeMap = new HashMap<AbstractAggregationProcess<?, ?>, Node>();

        public NodeFactory() {
            processList.addChangeListener(WeakListeners.change(NodeFactory.this, processList));
        }

        @Override
        protected boolean createKeys(List<AbstractAggregationProcess<?, ?>> toPopulate) {
            toPopulate.addAll(processList);
            return true;
        }

        @Override
        protected Node createNodeForKey(AbstractAggregationProcess<?, ?> process) {
            Node node = new FilterNode(process.getNodeDelegate());
            objectToNodeMap.put(process, node);
            return node;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            refresh(true);
        }
    }
}
