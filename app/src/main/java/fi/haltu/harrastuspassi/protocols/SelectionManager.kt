package fi.haltu.harrastuspassi.protocols

import fi.haltu.harrastuspassi.models.Category

interface SelectionManager {
    var selectedItems: HashSet<Int>
    fun addSelection(selectedItem: Category)
    fun removeSelection(removedItem:Category)
    fun saveFiltersAndDismiss()
}