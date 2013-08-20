/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.beans;

/**
 * Property descriptor instance with common methods to access meta data of an
 * Descriptor.
 *
 * @author Serdar
 */
public interface PropertyDescriptor {

    /**
     * Return an unique id.
     *
     * @return String instance, null not permitted.
     */
    public String getId();

    public void setId(String id);

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    /**
     * Returns the fully qualified class name of an Descriptor.
     *
     * @return
     */
    public String getJavaType();

    public void setJavaType(String javaType);

    public String getValue();

    public void setValue(String value);
}
