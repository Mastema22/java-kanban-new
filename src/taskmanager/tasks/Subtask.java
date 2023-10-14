package taskmanager.tasks;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;
    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, long duration, LocalDateTime startTime, int epicId) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", start time='" + getStartTime() +'\'' +
                ", duration='" + getDuration() +'\'' +
                ", end time'" + getEndTime() +'\'' +
                ", epicId'" + getEpicId() +'\'' +
                '}';

    }
}
