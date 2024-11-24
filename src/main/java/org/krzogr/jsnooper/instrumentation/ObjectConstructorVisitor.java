/*
 * Copyright (C) 2025 krzogr (krzogr@gmail.com)
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

/**
 * Visitor which performs instrumentation for the constructor of the Object class.
 */
public class ObjectConstructorVisitor extends MethodVisitor {
  public ObjectConstructorVisitor(final int api, final MethodVisitor methodVisitor) {
    super(api, methodVisitor);
  }

  @Override
  public void visitCode() {
    visitVarInsn(Opcodes.ALOAD, 0);
    visitMethodInsn(Opcodes.INVOKESTATIC, "org/krzogr/jsnooper/tracking/ObjectTracker", "trackObject", "(Ljava/lang/Object;)V", false);
    super.visitCode();
  }
}
