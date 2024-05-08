package ru.dargen.obfuscator.transformer;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.*;
import ru.dargen.obfuscator.mapping.Mappings;

@RequiredArgsConstructor
public class InstructionTransformer {

    private final Mappings mappings;

    public void transformList(InsnList instructions) {
        for (val instruction : instructions) {
            transform(instruction);
        }
    }

    public void transform(AbstractInsnNode node) {
        if (node instanceof FieldInsnNode) {
            transformField((FieldInsnNode) node);
        } else if (node instanceof MethodInsnNode) {
            transformMethod((MethodInsnNode) node);
        } else if (node instanceof InvokeDynamicInsnNode) {
            transformInvokeDynamic((InvokeDynamicInsnNode) node);
        } else if (node instanceof MultiANewArrayInsnNode) {
            transformNewArray((MultiANewArrayInsnNode) node);
        } else if (node instanceof TypeInsnNode) {
            transformType((TypeInsnNode) node);
        }
    }

    private void transformField(FieldInsnNode node) {
        node.desc = mappings.mapClassNames(node.desc);

        val mapping = mappings.getMapping(node.owner);
        if (mapping != null) {
            node.owner = mappings.mapClassName(node.owner);
            node.name = mapping.mapField(node.name);
        }
    }

    private void transformMethod(MethodInsnNode node) {
        val mapping = mappings.getMapping(node.owner);
        if (mapping != null) {
            node.owner = mappings.mapClassName(node.owner);
            node.name = mapping.mapMethod(node.name, node.desc);
            node.desc = mappings.mapClassNames(node.desc);
        } else node.desc = mappings.mapClassNames(node.desc);
    }

    private void transformInvokeDynamic(InvokeDynamicInsnNode node) {
        node.desc = mappings.mapClassNames(node.desc);

        val bsm = node.bsm;
        val mapping = mappings.getMapping(bsm.getOwner());
        if (mapping != null) {
            node.bsm = new Handle(
                    bsm.getTag(),
                    mapping.getActual(),
                    mapping.mapMethod(bsm.getName(), bsm.getDesc()),
                    mappings.mapClassNames(bsm.getDesc()),
                    bsm.isInterface()
            );
        }
    }

    private void transformNewArray(MultiANewArrayInsnNode node) {
        node.desc = mappings.mapClassNames(node.desc);
    }

    private void transformType(TypeInsnNode node) {
        node.desc = mappings.mapClassName(node.desc);
    }

}
