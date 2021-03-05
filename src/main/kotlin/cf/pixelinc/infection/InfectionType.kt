package cf.pixelinc.infection

enum class InfectionType(val value : Int, val chance : Double) {

    NONE(0, 0.0),
    ZOMBIE(1, 0.4),
    VAMPIRE(2, 0.2),
    AIR(3, 0.5);

    companion object {
        private val map = values().associateBy(InfectionType::value)
        fun fromInt(type : Int) = map[type]
    }
}