package ru.dargen.obfuscator.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.ClassWriter;

@UtilityClass
public class Asm {

    public ClassNode readClass(byte[] bytecode) {
        val reader = new ClassReader(bytecode);
        val classNode = new ClassNode();
        reader.accept(classNode, 0);

        return classNode;
    }

    public byte[] toByteCode(ClassNode classNode) {
        val writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);

        return writer.toByteArray();
    }


}
