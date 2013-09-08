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
package de.fub.utilsmodule.text;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 *
 * @author Serdar
 */
public class CustomNumberFormat extends NumberFormat {

    private static final long serialVersionUID = 1L;
    private final NumberFormat def = NumberFormat.getPercentInstance();

    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        return new StringBuffer(MessageFormat.format("{0, number, 0.00}", number));
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        return new StringBuffer(MessageFormat.format("{0, number, 0.00}", number));
    }

    @Override
    public Number parse(String source, ParsePosition parsePosition) {
        return def.parse(source, parsePosition);
    }
}
