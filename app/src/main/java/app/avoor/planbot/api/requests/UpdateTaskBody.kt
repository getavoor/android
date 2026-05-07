package app.avoor.planbot.api.requests

/**
 * Request body for updating a task.
 */
data class UpdateTaskBody(
    /**
     * The task's title.
     */
    val title: String? = null,
    /**
     * The task's description.
     */
    val description: String? = null,
    /**
     * The task's priority (1=low, 2=medium, 3=high).
     */
    val priority: Int? = null,
    /**
     * Is this task completed?
     */
    val isCompleted: Boolean? = null,
    /**
     * When is this task due?
     */
    val dueDate: String? = null
)
