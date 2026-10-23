package sg.edu.ntu.taskflowapi.exceptions;

public class TaskSummaryException extends RuntimeException {
    public TaskSummaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
