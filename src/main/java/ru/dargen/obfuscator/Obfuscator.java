package ru.dargen.obfuscator;

import lombok.val;
import ru.dargen.obfuscator.mapping.Mappings;
import ru.dargen.obfuscator.transformer.ClassTransformer;
import ru.dargen.obfuscator.transformer.InstructionTransformer;
import ru.dargen.obfuscator.util.Asm;
import ru.dargen.obfuscator.util.File;
import ru.dargen.obfuscator.util.Jars;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.getProperty;

public class Obfuscator {

    public static Mappings mappings;

    public static ClassTransformer transformer;
    public static InstructionTransformer instructionTransformer;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar Obfuscator.jar <path-to-in> [path-to-out]");
            return;
        }

        val mappingsPath = getProperty("mappings", "mappings");

        val inFile = Paths.get(args[0]);
        val outFile = args.length > 1 ? Paths.get(args[1]) : inFile;

        mappings = new Mappings(Paths.get(mappingsPath));
        instructionTransformer = new InstructionTransformer(mappings);
        transformer = new ClassTransformer(mappings, instructionTransformer);

        val entries = Jars.readJar(inFile);

        transformClasses(entries);

        Jars.writeJar(outFile, entries);
    }

    private static void transformClasses(Map<String, byte[]> entries) {
        entries.entrySet().stream()
                .filter(entry -> entry.getKey().endsWith(".class"))
                .forEach(entry -> entries.put(
                        entry.getKey(),
                        transformClass(Jars.getEntryClassName(entry.getKey()), entry.getValue())
                ));
    }

    private static byte[] transformClass(String className, byte[] bytecode) {
        val classNode = Asm.readClass(bytecode);
        transformer.transform(classNode);
        return Asm.toByteCode(classNode);
    }

    private static void addDependencies(List<Path> dependencies) {
        for (Path dependency : dependencies) {
            for (Path file : File.collectFilesTree(dependency)) {
                Jars.addAppClassPath(file);
            }
        }
    }

}
