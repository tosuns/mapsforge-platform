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
package de.fub.utilsmodule.node;

import de.fub.utilsmodule.text.StringUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 * A custom node which only changes the short description method. The short
 * description will be line wrapped and transformd to a html test,
 *
 * With this changes the tooltip of a node that will be display will not have an
 * infinitive width with long short descriptions.
 *
 * @author Serdar
 */
public abstract class CustomAbstractnode extends AbstractNode {

    public CustomAbstractnode(Children children) {
        super(children);
    }

    public CustomAbstractnode(Children children, Lookup lookup) {
        super(children, lookup);
    }

    @Override
    public void setShortDescription(String s) {
        super.setShortDescription(StringUtils.StringAsHtmlWrapString(s));
    }
}
