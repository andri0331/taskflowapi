package sg.edu.ntu.taskflowapi.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import sg.edu.ntu.taskflowapi.model.Task;

/**
 * Stores tasks in memory.
 * Uses a simple HashMap to store tasks in memory, keyed by their id.
 */
@Repository
public class TaskRepository {

    /** In-memory store of tasks, keyed by id. Initialised inline, so never null. */
    private final Map<Long, Task> tasks = new HashMap<>();

    /** Simple counter used to generate ids for new tasks. */
    private long idGenerator = 0;

    /**
     * Returns every stored task.
     *
     * @return a list of all tasks, empty if nothing has been saved yet
     */
    public synchronized List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Finds a single task by its id.
     *
     * @param id the id of the task to look up
     * @return an Optional holding the task, or empty if no task has that id
     */
    public synchronized Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    /**
     * Saves a task. If the task has no id yet, one is generated for it.
     *
     * @param task the task to store
     * @return the stored task, including its id
     */
    public synchronized Task save(Task task) {
        if (task.getId() == null) {
            task.setId(++idGenerator);
        }
        tasks.put(task.getId(), task);
        return task;
    }

    /**
     * Deletes a task by its id.
     *
     * @param id the id of the task to delete
     * @return true if a task was deleted, false if no task had that id
     */
    public synchronized boolean deleteById(Long id) {
        return tasks.remove(id) != null;
    }

    /**
     * Checks whether a task with the given id exists.
     *
     * @param id the id to check
     * @return true if a task with that id is stored
     */
    public synchronized boolean existsById(Long id) {
        return tasks.containsKey(id);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return the task count
     */
    public synchronized int count() {
        return tasks.size();
    }
}
