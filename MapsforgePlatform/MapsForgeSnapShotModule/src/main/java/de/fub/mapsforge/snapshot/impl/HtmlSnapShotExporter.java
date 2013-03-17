/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.snapshot.impl;

import de.fub.mapsforge.snapshot.api.AbstractComponentSnapShotExporter;
import de.fub.mapsforge.snapshot.api.ComponentSnapShotExporter;
import java.awt.Component;
import java.awt.Image;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_HtmlSnapShotExporter_Description=Exports a compoent as Svg & Html file",
    "CLT_HtmlSnapShotExporter_Name=Html"
})
@ServiceProvider(service = ComponentSnapShotExporter.class)
public class HtmlSnapShotExporter extends AbstractComponentSnapShotExporter {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/snapshot/impl/html5Icon.png";

    @Override
    public Image getIconImage() {
        return ImageUtilities.loadImage(ICON_PATH);
    }

    @Override
    public String getName() {
        return Bundle.CLT_HtmlSnapShotExporter_Name();
    }

    @Override
    public String getShortDescription() {
        return Bundle.CLT_HtmlSnapShotExporter_Description();
    }

    @Override
    public void export(Component component) {
        // TODO export
    }
}
