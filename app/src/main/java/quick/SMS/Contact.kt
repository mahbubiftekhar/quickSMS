package quick.SMS

// nullableImage is inaccessable, image == nullableImage if nullableImage != null
// else image == "NONE", the else part can be changed as appropriate to produce a default image
class Contact(val name: String, private val nullableImage: String?) {
    // Abusing lazy for a neat way of producing a Delegate
    val image by lazy { nullableImage ?: "NONE" } // Generate default image URI here
    override fun toString() : String = "Contact(name=$name, image=$image)"
}