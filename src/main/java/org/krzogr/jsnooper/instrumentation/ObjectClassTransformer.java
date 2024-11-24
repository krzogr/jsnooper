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

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class ObjectClassTransformer implements ClassFileTransformer {
  public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
                          final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {

    if (className.equals("java/lang/Object")) {
      return transformObjectClass(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
    } else {
      return classfileBuffer;
    }
  }

  private byte[] transformObjectClass(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
                                      final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {
    try {
      ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
      ClassVisitor classVisitor = new ObjectClassVisitor(Opcodes.ASM4, classWriter);
      ClassReader classReader = new ClassReader(classfileBuffer);

      classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

      return classWriter.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Fatal error while transforming class java.langObject: " + e.getMessage(), e);
    }
  }
}