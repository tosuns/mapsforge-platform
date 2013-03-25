/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.beans;

/**
 *
 * @author Serdar
 */
public interface PropertyDescriptor {

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public String getJavaType();

    public void setJavaType(String javaType);

    public String getValue();

    public void setValue(String value);
}
