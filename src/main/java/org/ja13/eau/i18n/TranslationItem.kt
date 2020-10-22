package org.ja13.eau.i18n

import org.ja13.eau.i18n.I18N.encodeLangKey
import java.util.*

internal class TranslationItem : Comparable<TranslationItem> {
    val key: String
    var text: String
        private set

    constructor(text: String) {
        key = encodeLangKey(text)
        this.text = text
    }

    constructor(key: String, text: String) {
        this.key = key
        this.text = text
    }

    fun applyExistingTranslationIfPresent(existing: Properties?) {
        if (existing != null) {
            val text = existing.getProperty(key)
            if (text != null) {
                this.text = text
            }
        }
    }

    override fun compareTo(other: TranslationItem): Int {
        return key.compareTo(other.key)
    }

    override fun equals(other: Any?): Boolean {
        return other is TranslationItem && compareTo(other) == 0 ||
            other is String && key.compareTo((other as String?)!!) == 0
    }

    override fun toString(): String {
        return StringBuilder(key).append("=").append(text.replace("\\\"", "\"")).append("\n").toString()
    }
}
