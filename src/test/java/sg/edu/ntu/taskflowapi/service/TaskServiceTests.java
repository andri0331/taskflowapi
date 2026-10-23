package sg.edu.ntu.taskflowapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sg.edu.ntu.taskflowapi.exceptions.TaskNotFoundException;
import sg.edu.ntu.taskflowapi.model.Task;
import sg.edu.ntu.taskflowapi.repository.TaskRepository;

/**
 * Unit tests for the business logic in the service layer.
 */
class TaskServiceTests {

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        // A fresh repository for every test, so the tests stay independent.
        taskService = new TaskServiceImp(new TaskRepository());
    }

    private Task newTask(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("a description");
        return task;
    }

    @Test
    void createTaskGeneratesAnId() {
        Task created = taskService.createTask(newTask("Write the report"));

        assertTrue(created.getId() > 0, "the repository should generate an id");
        assertEquals("Write the report", created.getTitle());
        assertFalse(created.isCompleted(), "a new task starts out not completed");
    }

    @Test
    void findAllTasksReturnsEverySavedTask() {
        taskService.createTask(newTask("First"));
        taskService.createTask(newTask("Second"));

        List<Task> tasks = taskService.findAllTasks();

        assertEquals(2, tasks.size());
    }

    @Test
    void findTaskByIdReturnsTheStoredTask() {
        Task created = taskService.createTask(newTask("Find me"));

        assertEquals("Find me", taskService.findTaskById(created.getId()).getTitle());
    }

    @Test
    void findTaskByIdThrowsWhenTheTaskIsMissing() {
        assertThrows(TaskNotFoundException.class, () -> taskService.findTaskById(999L));
    }

    @Test
    void updateTaskChangesTheStoredTask() {
        Task created = taskService.createTask(newTask("Old title"));

        Task incoming = new Task();
        incoming.setTitle("New title");
        incoming.setDescription("New description");
        incoming.setCompleted(true);

        Task updated = taskService.updateTask(created.getId(), incoming);

        assertEquals("New title", updated.getTitle());
        assertTrue(updated.isCompleted());
        assertEquals("New title", taskService.findTaskById(created.getId()).getTitle());
    }

    @Test
    void deleteTaskRemovesTheTask() {
        Task created = taskService.createTask(newTask("Delete me"));

        taskService.deleteTask(created.getId());

        assertTrue(taskService.findAllTasks().isEmpty());
        assertThrows(TaskNotFoundException.class, () -> taskService.findTaskById(created.getId()));
    }

    @Test
    void markTaskAsCompleteSetsTheCompletedFlag() {
        Task created = taskService.createTask(newTask("Finish me"));

        Task completed = taskService.markTaskAsComplete(created.getId());

        assertTrue(completed.isCompleted());
        assertTrue(taskService.findTaskById(created.getId()).isCompleted());
    }

    @Test
    void markTaskAsCompleteThrowsWhenTheTaskIsMissing() {
        assertThrows(TaskNotFoundException.class, () -> taskService.markTaskAsComplete(999L));
    }
}
