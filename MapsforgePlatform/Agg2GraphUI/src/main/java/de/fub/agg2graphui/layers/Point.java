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
package de.fub.agg2graphui.layers;

import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.ui.gui.RenderingOptions;

/**
 *
 * @author Serdar
 */
public class Point implements Drawable {

    private ILocation at;
    private RenderingOptions renderingOptions;

    public Point(ILocation at, RenderingOptions ro) {
        this.at = at;
        this.renderingOptions = ro;
    }

    @Override
    public RenderingOptions getRenderingOptions() {
        return renderingOptions;
    }

    /**
     * @return the at
     */
    public ILocation getAt() {
        return at;
    }

    /**
     * @param at the at to set
     */
    public void setAt(ILocation at) {
        this.at = at;
    }

    /**
     * @param renderingOptions the renderingOptions to set
     */
    public void setRenderingOptions(RenderingOptions renderingOptions) {
        this.renderingOptions = renderingOptions;
    }
}
