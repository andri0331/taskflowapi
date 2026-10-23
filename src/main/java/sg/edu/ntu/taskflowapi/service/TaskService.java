package sg.edu.ntu.taskflowapi.service;

import java.util.List;

import sg.edu.ntu.taskflowapi.model.Task;

public interface TaskService {

    List<Task> findAllTasks();

    Task findTaskById(Long id);

    Task createTask(Task task);

    Task updateTask(Long id, Task task);

    void deleteTask(Long id);

    Task markTaskAsComplete(Long id);
}
