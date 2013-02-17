/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.profiling;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Serdar
 */
public class ProfilungUtils {

    private HashMap<String, Profile> profiles = new HashMap<String, Profile>();

    public synchronized void startProfile(String name) {
        profiles.put(name, new Profile(name));
    }

    public synchronized Profile stopProfile(String name) {
        Profile profile = profiles.remove(name);
        if (profile == null) {
            throw new IllegalArgumentException("There is no profile with the given name available.");
        }
        profile.computeProfile();
        return profile;
    }

    public static class Profile {

        private String name;
        private long startTimeStamp = 0;
        private long endTimeStamp = 0;
        private long duration = 0;
        private SimpleDateFormat df = new SimpleDateFormat("dd.mm.yyyy hh.MM.ss.SS");

        Profile(String name) {
            this.name = name;
            startTimeStamp = System.currentTimeMillis();
        }

        void computeProfile() {
            endTimeStamp = System.currentTimeMillis();
            duration = endTimeStamp - startTimeStamp;
        }

        @Override
        public String toString() {
            return "Profile{"
                    + "name=" + name
                    + ", startTimeStamp=" + df.format(new Date(startTimeStamp))
                    + ", endTimeStamp=" + df.format(new Date(endTimeStamp))
                    + ", duration (ms)=" + duration + '}';
        }
    }
}
