package org.ja13.eau.i18n

import java.io.File
import java.util.*
import java.util.regex.Pattern

internal object SourceCodeParser {
    private val JAVA_TR_PATTERN = Pattern.compile("(?:tr|TR)\\s*\\(\\s*\"(.*?)\"\\s*[,)]")
    private val JAVA_FORGE_PATTERN = Pattern.compile("TR_([A-Z]*)\\s*\\(\\s*(?:I18N.)?Type.(.*?)\\s*,\\s\"(.*?)\"\\s*\\)")
    private const val MULTIPLE_LOCATIONS = "Appearing in multiple source files"

    fun parseSourceFolder(file: File?): Map<String, MutableSet<TranslationItem>> {
        val strings: MutableMap<String, MutableSet<TranslationItem>> = TreeMap()
        strings[MULTIPLE_LOCATIONS] = TreeSet()
        parseSourceFolderRecursive(file, strings)
        return strings
    }

    private fun parseSourceFolderRecursive(folder: File?, strings: MutableMap<String, MutableSet<TranslationItem>>) {
        // Check that arguments are valid.
        if (folder != null && folder.exists()) {

            // Do for each file.
            for (file in folder.listFiles()?: arrayOf()) {

                // If the file is a directory, call the method recursively.
                if (file.isDirectory) {
                    parseSourceFolderRecursive(file, strings)

                    // If it is a file and has the file extension .java, parse the Java file.
                } else if (file.isFile && file.name.endsWith(".java")) {
                    println("Parsing Java source file: " + file.name + "...")
                    parseJavaFile(file, strings)

                    // If it is a file and has the file extension .kt, parse the Kotlin file.
                } else if (file.isFile && file.name.endsWith(".kt")) {
                    println("Parsing Kotlin source file: " + file.name + "...")
                    parseKotlinFile(file, strings)
                }
            }
        }
    }

    private fun parseJavaFile(file: File, strings: MutableMap<String, MutableSet<TranslationItem>>) {
        // Load file into memory.
        val content = Scanner(file).useDelimiter("\\Z").next()
        val textsToTranslate: MutableSet<TranslationItem> = TreeSet()

        // Find all matches for Java style translations.
        val trMatcher = JAVA_TR_PATTERN.matcher(content)
        while (trMatcher.find()) {
            val textToTranslate = TranslationItem(trMatcher.group(1))
            println("  " + textToTranslate.key)
            if (!isStringAlreadyPresent(textToTranslate, strings)) {
                textsToTranslate.add(textToTranslate)
            }
        }
        val forgeMatcher = JAVA_FORGE_PATTERN.matcher(content)
        while (forgeMatcher.find()) {
            val property = forgeMatcher.group(1).toLowerCase()
            val type = I18N.Type.valueOf(forgeMatcher.group(2))
            val text = forgeMatcher.group(3)
            val textToTranslate = TranslationItem(type.prefix +
                I18N.encodeLangKey(text, type.isWhitespacesInFileReplaced) + "." + property, text)
            println("  " + textToTranslate.key)
            if (!isStringAlreadyPresent(textToTranslate, strings)) {
                textsToTranslate.add(textToTranslate)
            }
        }

        // If there were translations for that file, add the list of translations to the map.
        if (textsToTranslate.isNotEmpty()) {
            strings[file.path] = textsToTranslate
        }
    }

    private fun parseKotlinFile(file: File, strings: MutableMap<String, MutableSet<TranslationItem>>) {
        // TODO: This is unlikely to work perfectly. It'll do for now.
        parseJavaFile(file, strings)
    }

    private fun isStringAlreadyPresent(string: TranslationItem, strings: Map<String, MutableSet<TranslationItem>>): Boolean {
        for (fileName in strings.keys) {
            if (MULTIPLE_LOCATIONS == fileName) {
                if (strings[fileName]!!.contains(string)) return true
            } else {
                if (strings[fileName]!!.remove(string)) {
                    strings[MULTIPLE_LOCATIONS]!!.add(string)
                    return true
                }
            }
        }
        return false
    }
}
