package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Long, Task> taskHashMap = new HashMap<>();
    private final HashMap<Long, Epic> epicHashMap = new HashMap<>();
    private final HashMap<Long, Subtask> subtaskHashMap = new HashMap<>();

    private long generateId = 0;

    @Override
    public long addNewTask(Task task) {
        task.setId(generateId());
        taskHashMap.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public long addNewEpic(Epic epic) {
        epic.setId(generateId());
        epicHashMap.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public Long addNewSubtask(Subtask subtask) {
        Epic epic = epicHashMap.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }

        subtask.setId(generateId());
        subtaskHashMap.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(subtask.getEpicId());

        return subtask.getId();
    }

    public ArrayList<Task> getTaskList() {
        return new ArrayList(taskHashMap.values());
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList(epicHashMap.values());
    }

    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList(subtaskHashMap.values());
    }

    @Override
    public void removeTaskById(long taskId) {
        Task removeTask = taskHashMap.get(taskId);
        taskHashMap.remove(removeTask.getId(),removeTask);
    }

    @Override
    public void removeEpicById(long epicId) {
        Epic removeEpic = epicHashMap.get(epicId);
        ArrayList<Long> subtaskIds = removeEpic.getSubtaskIds();

        for (long subtaskId : subtaskIds) {
            Subtask subtask = subtaskHashMap.get(subtaskId);
            subtaskHashMap.remove(subtask.getId(),subtask);
        }
        epicHashMap.remove(removeEpic.getId(),removeEpic);
    }

    @Override
    public void removeSubtaskById(long subtaskId) {
        Subtask removeSubtask = subtaskHashMap.get(subtaskId);
        Epic epicOfRemoveSubtask = epicHashMap.get(removeSubtask.getEpicId());

        ArrayList<Long> subtaskIds = epicOfRemoveSubtask.getSubtaskIds();
        subtaskIds.remove(removeSubtask.getId());

        epicOfRemoveSubtask.setSubtaskIds(subtaskIds);
        epicHashMap.put(epicOfRemoveSubtask.getId(),epicOfRemoveSubtask);

        subtaskHashMap.remove(removeSubtask.getId(),removeSubtask);
        updateEpicStatus(epicOfRemoveSubtask.getId());
    }

    @Override
    public void getSubtasksOfEpic(Epic epic) {
        if (epic != null) {
            ArrayList<Long> subtaskIDs = epic.getSubtaskIds();

            for (long subtaskId : subtaskIDs) {
                Subtask subtask = subtaskHashMap.get(subtaskId);
                System.out.println(subtask.toString());
            }
        }

    }

    @Override
    public Task getTaskById(long taskId) {
        Managers.getDefaultHistory().add(taskHashMap.get(taskId));
        return taskHashMap.get(taskId);
    }

    @Override
    public Epic getEpicById(long epicId) {
        Managers.getDefaultHistory().add(epicHashMap.get(epicId));
        return epicHashMap.get(epicId);
    }
    @Override
    public Subtask getSubtaskById(long subtaskId) {
        Managers.getDefaultHistory().add(subtaskHashMap.get(subtaskId));
        return subtaskHashMap.get(subtaskId);
    }

    @Override
    public void updateTask(Task task) {
        Task saveTask = taskHashMap.get(task.getId());
        if (saveTask == null) {
            return;
        }
        taskHashMap.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saveEpic = epicHashMap.get(epic.getId());
        if(saveEpic == null) {
            return;
        }
        epicHashMap.put(epic.getId(),epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask saveSubtask = subtaskHashMap.get(subtask.getId());
        if (saveSubtask == null) {
            return;
        }
        subtaskHashMap.put(subtask.getId(),subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public List<Task> getHistory() {
        return Managers.getDefaultHistory().getHistory();
    }

    private long generateId() {
        return generateId++;
    }

    private void updateEpicStatus(long epicId) {
        Epic epic = epicHashMap.get(epicId);
        ArrayList<Long> subtaskIds = epic.getSubtaskIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        Status status = null;
        for (long subtaskId : subtaskIds) {
            Subtask subtask = subtaskHashMap.get(subtaskId);

            if (status == null) {
                status = subtask.getStatus();
                continue;
            }

            if (status.equals(subtask.getStatus())
                    && !status.equals(Status.IN_PROGRESS)) {
                continue;
            }

            epic.setStatus(Status.IN_PROGRESS);
            return;
        }

        epic.setStatus(status);
    }
}
