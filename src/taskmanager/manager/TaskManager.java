package taskmanager.manager;

import taskmanager.tasks.Epic;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    Task addNewTask(Task task);

    Epic addNewEpic(Epic epic);

    Subtask addNewSubtask(Subtask subtask);

    HashMap<Integer, Task> getTaskList();

    HashMap<Integer, Epic> getEpicList();

    HashMap<Integer, Subtask> getSubtaskList();

    void removeTaskById(int taskId);

    void removeEpicById(int epicId);

    void removeSubtaskById(int subtaskId);

    Subtask getSubtasksOfEpic(Epic epic);

    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    Subtask getSubtaskById(int subtaskId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeAllTask();

    void removeAllEpics();

    void removeAllSubtasks();

    void removeAll();

    List<Task> getHistory();

    void addTaskToPrioritizedList(Task task);

    List<Task> getPrioritizedTasks();

}
