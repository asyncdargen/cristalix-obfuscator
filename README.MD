# Cristalix obfuscator
___
Utility for code obfuscation based on cristalix client mappings (**maybe not very correct**)
___
### Guide
+ Clone repo (or download zip)
+ Run obfuscator (mappings, original and obfuscator must be in some folder) with command `java -jar obfuscator.jar <path-to-in> [path-to-out]`
___
### Features
+ Flag -Dmappings=<path-to-mappings> - allows to set your own directory with mappings
+ Flag -Ddependencies=<path-to-dependencies> - allows to set your own directories\files with dependencies (must include libraries with used in your `in file` and original client files)