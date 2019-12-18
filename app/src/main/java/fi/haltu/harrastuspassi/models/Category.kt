package fi.haltu.harrastuspassi.models

import java.io.Serializable

class Category : Serializable {
    var id: Int? = 0
    var name: String = ""
    var treeId: Int? = 0
    var level: Int? = 0
    var parent: Int? = 0
    var childCategories: ArrayList<Category>? = ArrayList()
    override fun toString(): String {
        return this.name
    }
}