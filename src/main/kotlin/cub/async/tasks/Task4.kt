package cub.async.tasks

/*
 * This task is the same as Task3, but now for each submitted post its text should be displayed in UI.
 * The idea is simple: UI has a string buffer, in which you should write the text from the Meta of a post.
 * UI should print contents of its buffer to the console periodically.
 */

abstract class UI {
    protected val buffer = StringBuffer()
}
