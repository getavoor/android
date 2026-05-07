package app.avoor.planbot.api.requests

/**
 * Request body for creating a task.
 */
data class CreateTaskBody(
    /**
     * The task's title.
     */
    val title: String,
    /**
     * The task's description.
     */
    val description: String? = null,
    /**
     * The task's priority (1=low, 2=medium, 3=high).
     */
    val priority: Int? = null,
    /**
     * When is this task due?
     */
    val dueDate: String? = null
)
