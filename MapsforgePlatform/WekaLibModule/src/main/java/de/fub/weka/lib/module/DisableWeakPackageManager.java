/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.weka.lib.module;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.openide.modules.OnStart;
import org.openide.util.Exceptions;
import weka.core.WekaPackageManager;

/**
 * This class responsiblity is to make a hack to the weka lib to avoid an error
 * message, when trying to initialize a PrintComponent. This hack disables the
 * WekaPackage manaager system to avail Exception, which are catched by an
 * internal function and will be displayed. This hack only avoid the display of
 * an error dialog but errors will still be displayed in the system out console.
 *
 * @author Serdar
 */
@OnStart
public class DisableWeakPackageManager implements Runnable {

    @Override
    public void run() {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    Field declaredField = WekaPackageManager.class.getDeclaredField("m_loadPackages");
                    declaredField.setAccessible(true);
                    declaredField.setBoolean(null, false);
                    WekaPackageManager.loadPackages(true);
                } catch (NoSuchFieldException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (SecurityException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IllegalArgumentException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IllegalAccessException ex) {
                    Exceptions.printStackTrace(ex);
                }
                return null;
            }
        });

    }
}
