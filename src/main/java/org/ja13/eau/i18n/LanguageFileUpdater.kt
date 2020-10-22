package org.ja13.eau.i18n

import org.ja13.eau.i18n.LanguageFileGenerator.updateFile
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import kotlin.system.exitProcess

internal object LanguageFileUpdater {
    private fun updateFile(languageFile: File, stringsToTranslate: Map<String, Set<TranslationItem>>) {
        // Parse the existing translations from the language file.
        val existingTranslations = Properties()
        existingTranslations.load(FileInputStream(languageFile))

        // Update the existing language file.
        updateFile(languageFile, stringsToTranslate, existingTranslations)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            if (args.size != 2) System.exit(1)
            val srcFolder = File(args[0])
            val languageFileOrFolder = File(args[1])

            // Check if the source folder is present.
            if (!srcFolder.isDirectory) exitProcess(1)

            // Get the strings to translate from the actual source code.
            val stringsToTranslate = SourceCodeParser.parseSourceFolder(srcFolder)

            // If a single file is passed to the main method, we just update that particular file.
            if (languageFileOrFolder.isFile) {
                updateFile(languageFileOrFolder, stringsToTranslate)
            } else if (languageFileOrFolder.isDirectory) {
                for (file in languageFileOrFolder.listFiles()?: arrayOf()) {
                    if (file.name.endsWith(".lang") && !file.name.startsWith("_")) {
                        updateFile(file, stringsToTranslate)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Runtime.getRuntime().exit(1)
        }
    }
}
