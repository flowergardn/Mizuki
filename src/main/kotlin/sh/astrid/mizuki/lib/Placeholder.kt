package sh.astrid.mizuki.lib

class Placeholder(val string: String, val replacement: String)

fun String.pp(vararg placeholders: Placeholder): String {
    var message = this
    for (p in placeholders) {
        if (message.contains(p.string)) {
            message = message.replace(p.string, p.replacement)
        }
    }
    return message
}