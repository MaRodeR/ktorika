package org.bp.labs.dtomapper

@DslMarker
annotation class HtmlTagDslMarker

@HtmlTagDslMarker
open class Tag {
    private val children = mutableListOf<Tag>()

    protected fun <T : Tag> element(element: T, init: T.() -> Unit): T {
        element.init()
        children.add(element)
        return element
    }
}

class Html : Tag() {

    fun head(init: Head.() -> Unit): Head = element(Head(), init)

    fun body(init: Body.() -> Unit): Body = element(Body(), init)
}

class Head : Tag() {

    fun title(init: TagWithText.() -> Unit): TagWithText = element(TagWithText(), init)
}

class Body : Tag()

open class TagWithText : Tag() {

    private var text: String? = null

    fun unaryPlus(text: String) = this.text + text

    operator fun String.unaryPlus() {
        element(TagWithText()) {}
    }
}

fun html(init: Html.() -> Unit): Html {
    val html = Html()
    html.init()
    return html
}

val htmlObject =
    html {
        head {
            title {
                +"rrr"
            }
        }
        body {
        }
    }