/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.factories.nodes.properties;

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
