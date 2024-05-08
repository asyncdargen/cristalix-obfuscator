package ru.dargen.obfuscator.transformer;

import lombok.RequiredArgsConstructor;
import org.objectweb.asm.tree.*;
import ru.dargen.obfuscator.mapping.Mappings;

import java.util.List;

@RequiredArgsConstructor
public class ClassTransformer {

    private final Mappings mappings;
    private final InstructionTransformer instructionTransformer;

    public void transform(ClassNode classNode) {
        transformClass(classNode);
        transformFields(classNode.fields);
        transformMethods(classNode.methods);
    }

    private void transformClass(ClassNode classNode) {
        classNode.superName = mappings.mapClassName(classNode.superName);
        classNode.interfaces = mappings.mapClassesNames(classNode.interfaces);
        if (classNode.signature != null) {
            classNode.signature = mappings.mapClassNames(classNode.signature);
        }
    }

    private void transformFields(List<FieldNode> fields) {
        fields.forEach(field -> field.desc = mappings.mapClassNames(field.desc));
    }

    private void transformMethods(List<MethodNode> methods) {
        methods.forEach(this::transformMethod);
    }

    private void transformMethod(MethodNode method) {
        method.desc = mappings.mapClassNames(method.desc);
        if (method.signature != null) {
            method.signature = mappings.mapClassNames(method.signature);
        }

        instructionTransformer.transformList(method.instructions);
    }

}
