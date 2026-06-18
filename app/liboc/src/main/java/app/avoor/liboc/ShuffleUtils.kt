package app.avoor.liboc

class ShuffleUtils {

    /**
     * Shuffles the provided array.
     */
    external fun shuffle(numbers: Array<Int>, numSize: Int)

    /**
     * Shuffles the provided array.
     */
    fun shuffle(numbers: Array<Int>) {
        shuffle(numbers, numbers.count())
    }

    companion object {
        // Used to load the 'liboc' library on application startup.
        init {
            System.loadLibrary("liboc")
        }
    }
}