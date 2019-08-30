package fi.haltu.harrastuspassi.utils

fun weekDayToId() {

}

fun idToWeekDay(id: Int): String? {
    var weekDays: Map<Int, String> = mapOf(1 to "Maanantai", 2 to "Tiistai", 3 to "Keskiviikko", 4 to "Torstai",
        5 to "Perjantai", 6 to "Lauantai", 7 to "Sunnuntai")
    //TODO there should be translation later
    return weekDays[id]
}