package app.avoor.planbot.api.responses

import app.avoor.planbot.api.models.Task

/**
 * A response received when creating a task.
 */
data class CreateTaskResponse(
    /**
     * A message returned from the server.
     */
    val msg: String,
    /**
     * The created task.
     */
    val task: Task
)
