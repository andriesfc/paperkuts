package paperkuts.io

interface Parts<out P> {
    /**
     * Parent may be null if the path's parent is the root of the file system.
     */
    val parent: P?

    /**
     * The name part (excluding the parent)
     */
    val name: String

    /**
     * The extension which may be `null` if there is no extension
     */
    val extension: String?

    /**
     * The base name, that is the part of the name without the extension.
     */
    val baseName: String
}
