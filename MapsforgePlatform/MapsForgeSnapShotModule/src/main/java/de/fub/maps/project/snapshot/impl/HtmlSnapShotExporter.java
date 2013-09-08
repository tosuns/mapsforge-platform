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
package de.fub.maps.project.snapshot.impl;

import de.fub.maps.project.snapshot.api.AbstractComponentSnapShotExporter;
import java.awt.Component;
import java.awt.Image;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_HtmlSnapShotExporter_Description=Exports a compoent as Svg & Html file",
    "CLT_HtmlSnapShotExporter_Name=Html"
})
//@ServiceProvider(service = ComponentSnapShotExporter.class)
public class HtmlSnapShotExporter extends AbstractComponentSnapShotExporter {

    @StaticResource
    private static final String ICON_PATH = "de/fub/maps/project/snapshot/impl/html5Icon.png";

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
