package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    long addNewTask(Task task);

    long addNewEpic(Epic epic);

    Long addNewSubtask(Subtask subtask);

    void removeTaskById(long taskId);

    void removeEpicById(long epicId);

    void removeSubtaskById(long subtaskId);

    void getSubtasksOfEpic(Epic epic);
    Task getTaskById(long taskId);

    Epic getEpicById(long epicId);

    Subtask getSubtaskById(long subtaskId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Task> getHistory();

}
