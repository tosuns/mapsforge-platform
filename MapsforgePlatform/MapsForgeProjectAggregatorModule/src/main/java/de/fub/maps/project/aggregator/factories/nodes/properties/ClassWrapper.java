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
package de.fub.maps.project.aggregator.factories.nodes.properties;

/**
 *
 * @author Serdar
 */
public class ClassWrapper {

    private Class<?> clazz;

    public ClassWrapper(String qualifiedClassName) {
        try {
            this.clazz = Class.forName(qualifiedClassName);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Couldn't find class with the name: " + qualifiedClassName);
        }
    }

    public ClassWrapper(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getName() {
        return clazz.getSimpleName();
    }

    public String getQualifiedName() {
        return clazz.getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.clazz != null ? this.clazz.getName().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClassWrapper other = (ClassWrapper) obj;
        if ((this.clazz == null || !this.clazz.getName().equals(other.clazz.getName()))) {
            return false;
        }
        return true;
    }
}
