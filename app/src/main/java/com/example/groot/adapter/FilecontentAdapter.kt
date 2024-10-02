package com.example.groot.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.R
import java.util.regex.Pattern

class FileContentAdapter(
    private val context: Context,
    private val lines: List<Pair<Int, String>>
) : RecyclerView.Adapter<FileContentAdapter.FileContentViewHolder>() {

    private val selectedLines = mutableSetOf<Int>()

    companion object {
        private val JAVA_KEYWORDS = listOf(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if",
            "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package",
            "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized",
            "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
        )

        private val PYTHON_KEYWORDS = listOf(
            "False", "None", "True", "and", "as", "assert", "async", "await", "break", "class", "continue", "def",
            "del", "elif", "else", "except", "finally", "for", "from", "global", "if", "import", "in", "is", "lambda",
            "nonlocal", "not", "or", "pass", "raise", "return", "try", "while", "with", "yield"
        )

        private val HTML_KEYWORDS = listOf(
            "html", "head", "title", "base", "link", "meta", "style", "script", "noscript", "body", "section", "nav", "article", "aside", "h1", "h2",
            "h3", "h4", "h5", "h6", "header", "footer", "address", "main", "p", "hr", "pre", "blockquote", "ol", "ul", "li", "dl", "dt", "dd", "figure",
            "figcaption", "div", "a", "em", "strong", "small", "s", "cite", "q", "dfn", "abbr", "data", "time", "code", "var", "samp", "kbd", "sub",
            "sup", "i", "b", "u", "mark", "ruby", "rb", "rt", "rtc", "rp", "bdi", "bdo", "span", "br", "wbr", "ins", "del", "picture", "source", "img",
            "iframe", "embed", "object", "param", "video", "audio", "track", "map", "area", "table", "caption", "colgroup", "col", "tbody", "thead", "tfoot",
            "tr", "td", "th", "form", "fieldset", "legend", "label", "input", "button", "select", "datalist", "optgroup", "option", "textarea",
            "output", "progress", "meter", "details", "summary", "dialog", "menu", "menuitem", "template", "canvas", "svg", "math"
        )

        private val CSS_KEYWORDS = listOf(
            "align-content", "align-items", "align-self", "all", "animation", "animation-delay", "animation-direction", "animation-duration",
            "animation-fill-mode", "animation-iteration-count", "animation-name", "animation-play-state", "animation-timing-function", "backface-visibility",
            "background", "background-attachment", "background-blend-mode", "background-clip", "background-color", "background-image",
            "background-origin", "background-position", "background-repeat", "background-size", "border", "border-bottom", "border-bottom-color",
            "border-bottom-left-radius", "border-bottom-right-radius", "border-bottom-style", "border-bottom-width", "border-collapse", "border-color", "border-image",
            "border-image-outset", "border-image-repeat", "border-image-slice", "border-image-source", "border-image-width", "border-left", "border-left-color",
            "border-left-style", "border-left-width", "border-radius", "border-right", "border-right-color", "border-right-style", "border-right-width", "border-spacing",
            "border-style", "border-top", "border-top-color", "border-top-left-radius", "border-top-right-radius", "border-top-style", "border-top-width", "border-width",
            "bottom", "box-shadow", "box-sizing", "caption-side", "caret-color", "clear", "clip", "color", "column-count", "column-fill", "column-gap", "column-rule",
            "column-rule-color", "column-rule-style", "column-rule-width", "column-span", "column-width", "columns", "content", "counter-increment", "counter-reset",
            "cursor", "direction", "display", "empty-cells", "filter", "flex", "flex-basis", "flex-direction", "flex-flow", "flex-grow", "flex-shrink", "flex-wrap", "float",
            "font", "font-family", "font-feature-settings", "font-kerning", "font-language-override", "font-size", "font-size-adjust", "font-stretch", "font-style", "font-synthesis",
            "font-variant", "font-variant-alternates", "font-variant-caps", "font-variant-east-asian", "font-variant-ligatures", "font-variant-numeric", "font-variant-position", "font-weight",
            "gap", "grid", "grid-area", "grid-auto-columns", "grid-auto-flow", "grid-auto-rows", "grid-column", "grid-column-end", "grid-column-gap", "grid-column-start", "grid-gap",
            "grid-row", "grid-row-end", "grid-row-gap", "grid-row-start", "grid-template", "grid-template-areas", "grid-template-columns", "grid-template-rows", "hanging-punctuation",
            "height", "hyphens", "image-rendering", "isolation", "justify-content", "left", "letter-spacing", "line-break", "line-height", "list-style", "list-style-image", "list-style-position",
            "list-style-type", "margin", "margin-bottom", "margin-left", "margin-right", "margin-top", "mask", "mask-clip", "mask-composite", "mask-image", "mask-mode", "mask-origin", "mask-position",
            "mask-repeat", "mask-size", "mask-type", "max-height", "max-width", "min-height", "min-width", "object-fit", "object-position", "opacity", "order", "orphans", "outline", "outline-color",
            "outline-offset", "outline-style", "outline-width", "overflow", "overflow-wrap", "overflow-x", "overflow-y", "padding", "padding-bottom", "padding-left", "padding-right", "padding-top",
            "page-break-after", "page-break-before", "page-break-inside", "perspective", "perspective-origin", "place-content", "place-items", "place-self", "pointer-events", "position", "quotes", "resize",
            "right", "row-gap", "scroll-behavior", "tab-size", "table-layout", "text-align", "text-align-last", "text-combine-upright", "text-decoration", "text-decoration-color", "text-decoration-line",
            "text-decoration-style", "text-indent", "text-justify", "text-orientation", "text-overflow", "text-rendering", "text-shadow", "text-transform", "top", "transform", "transform-origin",
            "transform-style", "transition", "transition-delay", "transition-duration", "transition-property", "transition-timing-function", "unicode-bidi", "user-select", "vertical-align", "visibility",
            "white-space", "widows", "width", "word-break", "word-spacing", "word-wrap", "writing-mode", "z-index"
        )

        private val C_KEYWORDS = listOf(
            "auto", "break", "case", "char", "const", "continue", "default", "do", "double", "else", "enum", "extern",
            "float", "for", "goto", "if", "inline", "int", "long", "register", "return", "short", "signed", "sizeof",
            "static", "struct", "switch", "typedef", "union", "unsigned", "void", "volatile", "while"
        )

        private val COMMENT_PATTERNS = mapOf(
            "java" to Pattern.compile("//.*|/\\*[\\s\\S]*?\\*/"),
            "python" to Pattern.compile("#.*"),
            "html" to Pattern.compile("<!--[\\s\\S]*?-->"),
            "css" to Pattern.compile("/\\*[\\s\\S]*?\\*/"),
            "c" to Pattern.compile("//.*|/\\*[\\s\\S]*?\\*/")
        )

        private val KEYWORDS = mapOf(
            "java" to JAVA_KEYWORDS,
            "python" to PYTHON_KEYWORDS,
            "html" to HTML_KEYWORDS,
            "css" to CSS_KEYWORDS,
            "c" to C_KEYWORDS
        )

        private val KEYWORD_COLOR = Color.rgb(153, 43, 65)

        private val TRIPLE_QUOTE_PATTERN = Pattern.compile("\"\"\"([^\"]*)\"\"\"")
        private val DOUBLE_QUOTE_PATTERN = Pattern.compile("\"([^\"]*)\"")
        private val SINGLE_QUOTE_PATTERN = Pattern.compile("'([^']*)'")
    }

    private val keywordPatterns: List<Pattern> by lazy {
        KEYWORDS.values.flatten().map { keyword ->
            Pattern.compile("\\b$keyword\\b")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileContentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file_content, parent, false)
        return FileContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileContentViewHolder, position: Int) {
        val (lineNumber, content) = lines[position]
        holder.lineNumber.text = lineNumber.toString()
        holder.lineContent.text = getHighlightedText(content)

        // Update background color based on selection
        holder.itemView.setBackgroundColor(
            if (selectedLines.contains(lineNumber)) context.getColor(R.color.md_theme_inversePrimary_mediumContrast) else Color.TRANSPARENT
        )

        // Set up long click listener to start ActionMode
        /*holder.lineContent.setOnLongClickListener {
            holder.lineContent.startActionMode(object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    val inflater: MenuInflater = mode.menuInflater
                    inflater.inflate(R.menu.copy_menu, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.action_copy -> {
                            // Copy selected lines to clipboard
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val selectedContent = lines.filter { selectedLines.contains(it.first) }
                                .joinToString("\n") { it.second }
                            val clip = ClipData.newPlainText("copied_text", selectedContent)
                            clipboard.setPrimaryClip(clip)
                            mode.finish() // Close ActionMode
                            true
                        }
                        else -> false
                    }
                }

                override fun onDestroyActionMode(mode: ActionMode) {
                    // No-op
                }
            })
            true
        }*/

        // Set up click listener to toggle line selection
        holder.itemView.setOnClickListener {
            if (selectedLines.contains(lineNumber)) {
                selectedLines.remove(lineNumber)
            } else {
                selectedLines.add(lineNumber)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return lines.size
    }

    class FileContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lineNumber: TextView = itemView.findViewById(R.id.lineNumber)
        val lineContent: TextView = itemView.findViewById(R.id.lineContent)
    }

    private fun getHighlightedText(text: String): SpannableStringBuilder {
        val spannable = SpannableStringBuilder(text)

        // Highlight keywords from multiple languages
        for (pattern in keywordPatterns) {
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                spannable.setSpan(
                    ForegroundColorSpan(KEYWORD_COLOR),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        // Highlight text inside quotes
        highlightText(spannable, text, TRIPLE_QUOTE_PATTERN, Color.rgb(122, 149, 193))
        highlightText(spannable, text, DOUBLE_QUOTE_PATTERN, Color.rgb(122, 149, 193))
        highlightText(spannable, text, SINGLE_QUOTE_PATTERN, Color.rgb(122, 149, 193))

        return spannable
    }

    private fun highlightText(spannable: SpannableStringBuilder, text: String, pattern: Pattern, color: Int) {
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            spannable.setSpan(
                ForegroundColorSpan(color),
                matcher.start(),
                matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}