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
