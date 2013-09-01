/**
 * *****************************************************************************
 * Copyright (c) 2012 Johannes Mitlmeier.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110, USA
 *
 *****************************************************************************
 */
package de.fub.agg2graph.ui.gui.jmv;

import de.fub.agg2graph.input.GPXWriter;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

public class DrawGPX {

    private static final Logger LOG = Logger.getLogger(DrawGPX.class.getName());

    private Rectangle2D.Double area = new Rectangle2D.Double(0, 0, 1, 1);
    private GPSPoint startCenter = new GPSPoint(52.5148476, 13.3973507);
    private List<GPSSegment> segments = new ArrayList<GPSSegment>(5);
    private int counter = 0;

    private JFrame frmGpxGenerator;
    private final JButton btnNext = new JButton("Next");
    private RenderPanel panel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DrawGPX window = new DrawGPX();

                window.frmGpxGenerator.setVisible(true);
            }
        });
    }

    /**
     * Create the application.
     */
    public DrawGPX() {
        segments.add(new GPSSegment());

        initialize();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(frmGpxGenerator);
            frmGpxGenerator.pack();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmGpxGenerator = new JFrame();
        frmGpxGenerator.setTitle("GPX generator");
        frmGpxGenerator.setBounds(100, 100, 450, 300);
        frmGpxGenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new RenderPanel();
        // center the map
//        panel.setZoomContolsVisible(false);
        panel.setCenterPosition(new GeoPosition(startCenter.getLat(), startCenter.getLon()));
        panel.setZoom(5);
//        panel.setMapRectanglesVisible(false);
        updateArea();

        panel.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                updateArea();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                GPSSegment currentSegment = segments.get(segments.size() - 1);
                // make new GPSPoint
                GeoPosition pos = panel.convertPointToGeoPosition(new Point2D.Double(e.getX(), e.getY()));
                GPSPoint point = new GPSPoint(pos.getLatitude(), pos.getLongitude());
                LOG.info(point.toString());
                currentSegment.add(point);
                panel.repaint();
            }
        });
        panel.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                updateArea();
            }
        });

        frmGpxGenerator.getContentPane().add(panel, BorderLayout.CENTER);
        btnNext.setMnemonic('n');
        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new File("test/input/draw-gpx").mkdirs();
                    GPXWriter.writeSegment(
                            new File(String.format(
                                            "test/input/draw-gpx/%04d.gpx", counter++)),
                            segments.get(segments.size() - 1));
                } catch (IOException e1) {
                    LOG.log(Level.SEVERE, "Write error: {0}", e1.getLocalizedMessage());
                    LOG.log(Level.SEVERE, e1.getMessage(), e1);
                }
                segments.add(new GPSSegment());
                panel.repaint();
            }
        });
        frmGpxGenerator.getContentPane().add(btnNext, BorderLayout.SOUTH);
    }

    private void updateArea() {
        GeoPosition topLeft = panel.convertPointToGeoPosition(new Point2D.Double(0, panel.getHeight()));
        GeoPosition bottomRight = panel.convertPointToGeoPosition(new Point2D.Double(panel.getWidth(), 0));
        area.setRect(topLeft.getLatitude(),
                topLeft.getLongitude(),
                bottomRight.getLatitude() - topLeft.getLatitude(),
                bottomRight.getLongitude() - topLeft.getLongitude());
    }

    public class RenderPanel extends JXMapViewer {

        private static final long serialVersionUID = 1858332755567599369L;

        @Override
        public void paint(Graphics g) {
            g.clearRect(0, 0, getWidth(), getHeight());
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            repaintOverlay(g2);
        }

        public void repaintOverlay(Graphics2D g2) {
            // old segments
            g2.setColor(new Color(0f, 0f, 0f, 0.4f));
            g2.setStroke(new BasicStroke(2f));
            for (int s = 0; s < segments.size() - 1; s++) {
                paintSegment(g2, segments.get(s));
            }
            // current segment
            g2.setColor(new Color(0.5f, 0.1f, 1f, 1f));
            g2.setStroke(new BasicStroke(5f));
            paintSegment(g2, segments.get(segments.size() - 1));
        }

        private void paintSegment(Graphics2D g2, GPSSegment segment) {
            for (int i = 0; i < segment.size(); i++) {
                GPSPoint point = segment.get(i);
                Point2D p2 = convertGeoPositionToPoint(new GeoPosition(point.getLat(), point.getLon()));
                g2.drawOval((int) p2.getX() - 2, (int) p2.getY() - 2, 4, 4);
                if (i > 0) {
                    GPSPoint lastPoint = segment.get(i - 1);
                    Point2D p1 = convertGeoPositionToPoint(new GeoPosition(lastPoint.getLat(), lastPoint.getLon()));
                    g2.drawLine((int) p2.getX(), (int) p2.getY(), (int) p1.getX(), (int) p1.getY());
                }
            }
        }
    }

}
