package org.ja13.eau.i18n

import java.io.File
import java.io.FileWriter
import java.util.*

internal object LanguageFileGenerator {
    @JvmStatic
    fun updateFile(file: File, strings: Map<String, Set<TranslationItem>>, existingTranslations: Properties) {
        val writer = FileWriter(file)

        // For each source file with translations create the file comment.
        for (sourceFile in strings.keys) {
            // Standardise file paths for every platforms
            val sourcePath = sourceFile.replace("\\", "/")
            writer.append("\n# ").append(sourcePath).append("\n")

            // For each translated string in that source file, add translation text.
            for (text2Translate in strings[sourceFile]!!) {
                text2Translate.applyExistingTranslationIfPresent(existingTranslations)
                writer.append(text2Translate.toString())
            }
        }

        writer.close()
    }
}
