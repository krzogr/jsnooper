/*
 * Copyright (C) 2024 krzogr (krzogr@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.krzogr.jsnooper.instrumentation;

import org.krzogr.jsnooper.config.TrackingConfig;

import java.lang.instrument.Instrumentation;

import static org.krzogr.jsnooper.config.TrackingInitializer.initTracking;

public class InstrumentationAgent {
    public static void premain(final String agentArgs, final Instrumentation inst) {
        TrackingConfig config = initTracking(agentArgs);
        inst.addTransformer(new ObjectClassTransformer(), true);
        inst.addTransformer(new GlobalClassTransformer(config::canInstrumentClass), true);
        transformJavaLangObjectClass(inst);
    }

    public static void agentmain(final String agentArgs, final Instrumentation inst) {
        TrackingConfig config = initTracking(agentArgs);
        inst.addTransformer(new ObjectClassTransformer(), true);
        inst.addTransformer(new GlobalClassTransformer(config::canInstrumentClass), true);
        transformJavaLangObjectClass(inst);
    }

    private static void transformJavaLangObjectClass(final Instrumentation inst) {
        try {
            inst.retransformClasses(Class.forName("java.lang.Object"));
        } catch (Exception e) {
            throw new RuntimeException("Fatal error while transforming class java.lang.Object: " + e.getMessage(), e);
        }
    }
}