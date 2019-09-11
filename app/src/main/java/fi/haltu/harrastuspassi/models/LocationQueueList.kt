package fi.haltu.harrastuspassi.models

class LocationQueueList: ArrayList<Location>() {
    companion object {
        const val MAX_SIZE = 5
    }

    override fun add(element: Location): Boolean {
        if(this.size >= MAX_SIZE) {
            this.removeAt(0)
        }

        return super.add(element)
    }
}