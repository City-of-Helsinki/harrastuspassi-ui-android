package fi.haltu.harrastuspassi.models

import java.io.Serializable

class Category : Serializable {
    var id: Int? = 0
    var name: String? = ""
    var treeId : Int? = 0
    var level : Int? = 0
    var parent : Int? = 0
    var childCategories : List<Category>? = ArrayList<Category>()

    /*override fun toString(): String {

        return "{id: ${id.toString()} name: ${name.toString()} \n\t sub: ${childCategories.toString()}"
    } */
}