/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.installer;

import de.fub.utilsmodule.propertyeditors.BigDecimalEditor;
import java.beans.PropertyEditorManager;
import java.math.BigDecimal;
import org.openide.modules.OnStart;

/**
 * This class will be called, when this module will be loaded. It registers a
 * PropertyEditor for the type BigDecimal.
 *
 * @author Serdar
 */
@OnStart
public class UtilInstaller implements Runnable {

    @Override
    public void run() {
        PropertyEditorManager.registerEditor(BigDecimal.class, BigDecimalEditor.class);
    }
}
