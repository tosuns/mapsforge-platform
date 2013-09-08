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
package de.fub.gpxmodule;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Serdar
 */
public class DateAdapter {

    public static Date parseDate(String s) {
        return DatatypeConverter.parseDateTime(s).getTime();
    }

    public static String printDate(Date dt) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTime(dt);
        String printDate = DatatypeConverter.printDateTime(cal);

        return printDate;
    }
}
