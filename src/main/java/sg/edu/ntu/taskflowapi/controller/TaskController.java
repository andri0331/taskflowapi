package sg.edu.ntu.taskflowapi.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.ntu.taskflowapi.model.Task;
import sg.edu.ntu.taskflowapi.service.TaskService;
import sg.edu.ntu.taskflowapi.service.TaskSummaryService;

/**
 * REST endpoints for the TaskFlow API.
 *
 * The service is brought in with constructor injection. The controller only
 * deals with HTTP: it hands the work to the service and wraps the result in a
 * ResponseEntity with the right status code.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskSummaryService taskSummaryService;

    public TaskController(TaskService taskService, TaskSummaryService taskSummaryService) {
        this.taskService = taskService;
        this.taskSummaryService = taskSummaryService;
    }

    /**
     * GET /api/tasks - get all tasks.
     */
    @GetMapping("")
    public ResponseEntity<List<Task>> getAllTasks() {
        return new ResponseEntity<>(taskService.findAllTasks(), HttpStatus.OK);
    }

    /**
     * GET /api/tasks/{id} - get one task by id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return new ResponseEntity<>(taskService.findTaskById(id), HttpStatus.OK);
    }

    /**
     * POST /api/tasks - create a new task.
     */
    @PostMapping("")
    public ResponseEntity<Task> createTask(@RequestBody @Valid Task task) {
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    /**
     * PUT /api/tasks/{id} - update an existing task.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody @Valid Task task) {
        return new ResponseEntity<>(taskService.updateTask(id, task), HttpStatus.OK);
    }

    /**
     * DELETE /api/tasks/{id} - delete a task.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable Long id) {
        Task deletedTask = taskService.findTaskById(id);
        taskService.deleteTask(id);
        return new ResponseEntity<>(deletedTask, HttpStatus.OK);
    }

    /**
     * PUT /api/tasks/{id}/complete - mark a task as complete.
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> markTaskAsComplete(@PathVariable Long id) {
        return new ResponseEntity<>(taskService.markTaskAsComplete(id), HttpStatus.OK);
    }

    /**
     * GET /api/tasks/summary - plain-English summary of what is pending and what
     * is done, produced with Spring AI.
     */
    @GetMapping("/summary")
    public ResponseEntity<String> getTaskSummary() {
        String summary = taskSummaryService.summarise(taskService.findAllTasks());
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }
}
