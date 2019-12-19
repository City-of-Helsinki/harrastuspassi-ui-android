package fi.haltu.harrastuspassi.utils

import fi.haltu.harrastuspassi.models.Category
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

fun getOptionalJSONObject(json: JSONObject, key: String): JSONObject? {
    return if (json.isNull(key))
        null
    else
        json.getJSONObject(key)
}

fun getOptionalDouble(json: JSONObject, key: String): Double? {
    return if (json.isNull(key))
        null
    else
        json.getDouble(key)
}

fun getOptionalInt(json: JSONObject, key: String): Int? {
    return if (json.isNull(key))
        null
    else
        json.getInt(key)
}

fun jsonArrayToCategoryList(jsonArray: JSONArray): ArrayList<Category> {
    val categoryList = ArrayList<Category>()

    for (i in 0 until jsonArray.length()) {
        val category = Category()

        val stringObject = jsonArray.get(i).toString()
        val categoryJson = JSONObject(stringObject)
        val id = getOptionalInt(categoryJson, "id")
        val name = categoryJson.getString("name")
        val treeId = getOptionalInt(categoryJson, "tree_id")
        val level = getOptionalInt(categoryJson, "level")
        val parent = getOptionalInt(categoryJson, "parent")

        try {
            val subCategoryJson = categoryJson.getJSONArray("child_categories")
            val subCategories = jsonArrayToCategoryList(subCategoryJson)
            category.childCategories = subCategories
        } catch (e: JSONException) {

        }
        category.apply {
            this.id = id
            this.name = name
            this.treeId = treeId
            this.level = level
            this.parent = parent
        }
        categoryList.add(category)
    }

    return categoryList
}

fun jsonArrayToSingleCategoryList(jsonArray: JSONArray): ArrayList<Category> {
    var categoryList = ArrayList<Category>()
    for (i in 0 until jsonArray.length()) {
        val category = Category()

        val stringObject = jsonArray.get(i).toString()
        val categoryJson = JSONObject(stringObject)
        val id = getOptionalInt(categoryJson, "id")
        val name = categoryJson.getString("name")
        val treeId = getOptionalInt(categoryJson, "tree_id")
        val level = getOptionalInt(categoryJson, "level")
        val parent = getOptionalInt(categoryJson, "parent")

        category.apply {
            this.id = id
            this.name = name
            this.treeId = treeId
            this.level = level
            this.parent = parent
        }
        categoryList.add(category)
    }
    return categoryList
}


