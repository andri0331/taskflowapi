package sg.edu.ntu.taskflowapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

/**
 * A plain Java bean representing a task in the TaskFlow API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    private LocalDateTime createdDate;

    private boolean completed;

    private LocalDateTime completedDate;
}
