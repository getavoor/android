package app.avoor.planbot.api.models

/**
 * A task.
 */
data class Task(
    /**
     * The task's ID.
     */
    val id: Int,
    /**
     * The task's title.
     */
    val title: String,
    /**
     * The task's description.
     */
    val description: String?,
    /**
     * Is this task completed?
     */
    val isCompleted: Boolean,
    /**
     * When was this task created?
     */
    val createdAt: String,
    /**
     * When was this task completed?
     */
    val completedAt: String?,
    /**
     * When is this task due?
     */
    val dueDate: String?,
    /**
     * The task's priority (1=low, 2=medium, 3=high).
     */
    val priority: Int
)
