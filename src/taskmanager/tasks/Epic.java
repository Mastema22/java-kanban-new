package taskmanager.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds;
    protected static LocalDateTime endTime = LocalDateTime.MAX;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtaskIds = new ArrayList<>();
    }

    public Epic(String name, String description, long duration, LocalDateTime startTime) {
        super(name, description, Status.NEW, duration, startTime);
        this.subtaskIds = new ArrayList<>();
        endTime = super.getEndTime();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public  void setEndTime(LocalDateTime endTime) {
        Epic.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", start time='" + getStartTime() +'\'' +
                ", duration='" + getDuration() +'\'' +
                ", end time'" + getEndTime() +'\'' +
                '}';
    }

}
