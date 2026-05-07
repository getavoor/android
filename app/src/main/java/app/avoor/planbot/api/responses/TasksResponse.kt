package app.avoor.planbot.api.responses

import app.avoor.planbot.api.models.Task

/**
 * A response containing a list of tasks.
 */
data class TasksResponse(
    /**
     * The list of tasks.
     */
    val tasks: List<Task>
)
