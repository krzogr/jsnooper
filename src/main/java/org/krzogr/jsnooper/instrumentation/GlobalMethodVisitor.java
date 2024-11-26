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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.krzogr.jsnooper.instrumentation.InstrumentationUtils.getObjectArrayTypeSignature;
import static org.krzogr.jsnooper.instrumentation.InstrumentationUtils.getPrimitiveArrayTypeSignature;

public class GlobalMethodVisitor extends MethodVisitor {
  public GlobalMethodVisitor(final int api, final MethodVisitor methodVisitor) {
    super(api, methodVisitor);
  }

  @Override
  public void visitIntInsn(int opcode, int operand) {
    super.visitIntInsn(opcode, operand);
    if (opcode == Opcodes.NEWARRAY) {
      visitMethodInsn(Opcodes.INVOKESTATIC, "org/krzogr/jsnooper/tracking/ObjectTracker", "trackAndReturnObject", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
      visitTypeInsn(Opcodes.CHECKCAST, getPrimitiveArrayTypeSignature(operand));
    }
  }

  @Override
  public void visitTypeInsn(int opcode, String type) {
    super.visitTypeInsn(opcode, type);
    if (opcode == Opcodes.ANEWARRAY) {
      visitMethodInsn(Opcodes.INVOKESTATIC, "org/krzogr/jsnooper/tracking/ObjectTracker", "trackAndReturnObject", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
      visitTypeInsn(Opcodes.CHECKCAST, getObjectArrayTypeSignature(type));
    }
  }

  @Override
  public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
    super.visitMultiANewArrayInsn(descriptor, numDimensions);
    visitMethodInsn(Opcodes.INVOKESTATIC, "org/krzogr/jsnooper/tracking/ObjectTracker", "trackAndReturnObject", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
    visitTypeInsn(Opcodes.CHECKCAST, descriptor);
  }
}
