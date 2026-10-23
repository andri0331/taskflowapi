package sg.edu.ntu.taskflowapi.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import sg.edu.ntu.taskflowapi.model.Task;
import sg.edu.ntu.taskflowapi.repository.TaskRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final TaskRepository taskRepository;

    public DataLoader(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        if (taskRepository.count() == 0) {
            List<Task> sampleTasks = List.of(
                    new Task(null, "Review project requirements", "Confirm the task list and API requirements.",
                            LocalDateTime.now().minusDays(20), false, null),
                    new Task(null, "Implement task endpoints", "Create and test the task REST endpoints.",
                            LocalDateTime.now().minusDays(5), false, null),
                    new Task(null, "Set up project structure",
                            "Initialize the application and organize the source folders.",
                            LocalDateTime.now().minusDays(1), true, LocalDateTime.now()));

            sampleTasks.forEach(taskRepository::save);
        }
    }
}
