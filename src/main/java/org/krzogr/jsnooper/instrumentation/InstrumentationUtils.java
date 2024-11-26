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

import org.objectweb.asm.Opcodes;

public class InstrumentationUtils {
    public static String getPrimitiveArrayTypeSignature(int operand) {
        switch (operand) {
            case Opcodes.T_BOOLEAN: return "[Z";
            case Opcodes.T_CHAR: return "[C";
            case Opcodes.T_FLOAT: return "[F";
            case Opcodes.T_DOUBLE: return "[D";
            case Opcodes.T_BYTE: return "[B";
            case Opcodes.T_SHORT: return "[S";
            case Opcodes.T_INT: return "[I";
            case Opcodes.T_LONG: return "[J";
            default: throw new RuntimeException("Unknown primitive array operand: " + operand);
        }
    }

    public static String getObjectArrayTypeSignature(String refType) {
        return refType.startsWith("[") ? "[" + refType : "[L" + refType + ";";
    }
}
