package taskmanager.manager;

import taskmanager.exceptions.ManagerException;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Status;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int id;
    protected static HashMap<Integer, Task> taskHashMap = new HashMap<>();
    protected static HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    protected static HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    protected static HistoryManager historyManager;
    private final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime);
    private final Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    public InMemoryTaskManager() {
        id = 0;
        historyManager = Managers.getDefaultHistory();
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public Task addNewTask(Task task) {
        if (task != null && !taskHashMap.containsKey(task.getId())) {
            int newId = task.getId();
            if (newId == 0) {
                newId = generateId();
                task.setId(newId);
            }
            taskHashMap.put(newId, task);
            getTaskById(task.getId());
            addTaskToPrioritizedList(task);

        } else {
            return null;
        }
        return task;
    }

    @Override
    public Epic addNewEpic(Epic epic) {
        if (epic != null && !epicHashMap.containsKey(epic.getId())) {
            int newId = epic.getId();
            if (newId == 0) {
                newId = generateId();
                epic.setId(newId);
            }
            epicHashMap.put(newId, epic);
            getEpicById(epic.getId());
        } else {
            return null;
        }
        return epic;
    }

    @Override
    public Subtask addNewSubtask(Subtask subtask) {
        if (subtask != null && !subtaskHashMap.containsKey(subtask.getId())) {
           int newId = subtask.getId();
            if (newId == 0) {
                newId = generateId();
                subtask.setId(newId);
            }
            Epic epic = epicHashMap.get(subtask.getEpicId());
            if (epic != null) {
                addTaskToPrioritizedList(subtask);
                epic.addSubtaskId(newId);
                subtaskHashMap.put(newId, subtask);
                updateEpicStatus(epic.getId());
                getSubtaskById(subtask.getId());
                updateEpicTime(epic);
            }

        } else {
            return null;
        }
        return subtask;
    }


    public HashMap<Integer, Task> getTaskList() {
        if (!taskHashMap.isEmpty()) {
            return taskHashMap;
        } else {
            return new HashMap<>();
        }
    }

    public HashMap<Integer, Epic> getEpicList() {
        if (!epicHashMap.isEmpty()) {
            return epicHashMap;
        } else {
            return new HashMap<>();
        }
    }

    public HashMap<Integer, Subtask> getSubtaskList() {
        if (!subtaskHashMap.isEmpty()) {
            return subtaskHashMap;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public void removeTaskById(int taskId) {
        if (taskHashMap.containsKey(taskId)) {
            Task removeTask = taskHashMap.get(taskId);
            taskHashMap.remove(removeTask.getId(), removeTask);
            historyManager.remove(removeTask.getId());
            prioritizedTasks.remove(removeTask);
        } else {
            System.out.println("Данной задачи нет!");
            return;
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        if (epicHashMap.containsKey(epicId)) {
            Epic removeEpic = epicHashMap.get(epicId);
            ArrayList<Integer> subtaskIds = removeEpic.getSubtaskIds();

            for (int subtaskId : subtaskIds) {
                Subtask subtask = subtaskHashMap.get(subtaskId);
                subtaskHashMap.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            epicHashMap.remove(removeEpic.getId());
            historyManager.remove(removeEpic.getId());
        } else {
            System.out.println("Данного эпика нет!");
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        if (subtaskHashMap.containsKey(subtaskId)) {
            Subtask removeSubtask = subtaskHashMap.get(subtaskId);
            Epic epicOfRemoveSubtask = epicHashMap.get(removeSubtask.getEpicId());
            subtaskHashMap.remove(subtaskId);
            epicOfRemoveSubtask.getSubtaskIds().remove(subtaskId);
            historyManager.remove(subtaskId);
            updateEpicStatus(epicOfRemoveSubtask.getId());
            updateEpicTime(epicOfRemoveSubtask);
            prioritizedTasks.remove(epicOfRemoveSubtask);

        } else {
            System.out.println("Данной подзадачи нет!");
        }
    }

    @Override
    public void removeAllTask() {
        if (!taskHashMap.isEmpty()) {
            taskHashMap.values().forEach(task -> historyManager.remove(task.getId()));
            taskHashMap.values().forEach(prioritizedTasks::remove);
            taskHashMap.clear();

        } else
            System.out.println("Список задач пуст!");
    }

    @Override
    public void removeAllEpics() {
        if (!epicHashMap.isEmpty()) {
            epicHashMap.values().forEach(epic -> historyManager.remove(epic.getId()));
            epicHashMap.values().forEach(prioritizedTasks::remove);
            epicHashMap.clear();
            removeAllSubtasks();
        } else {
            System.out.println("Список эпиков пуст!");
        }
    }

    @Override
    public void removeAllSubtasks() {
        if (!subtaskHashMap.isEmpty()) {
            subtaskHashMap.values().forEach(subtask -> historyManager.remove(subtask.getId()));
            subtaskHashMap.values().forEach(prioritizedTasks::remove);
            epicHashMap.values().forEach(epic -> {
                epic.setSubtaskIds(null);
                epic.setStatus(Status.NEW);
            });
            subtaskHashMap.clear();
        } else {
            System.out.println("Список подзадач пуст!");
        }
    }

    @Override
    public void removeAll() {
        if (!taskHashMap.isEmpty() || !epicHashMap.isEmpty() || !subtaskHashMap.isEmpty()) {
            removeAllEpics();
            removeAllTask();
            prioritizedTasks.clear();
            System.out.println("Удалено всё!");

        } else {
            System.out.println("Списки задач, эпиков и подзадач пусты!");
        }

    }

    @Override
    public Subtask getSubtasksOfEpic(Epic epic) {
        Subtask subtask;
        if (epic != null) {
            ArrayList<Integer> subtaskIDs = epic.getSubtaskIds();
            for (int subtaskId : subtaskIDs) {
                subtask = subtaskHashMap.get(subtaskId);
                return subtask;
            }
        }
        throw new ManagerException("У данного эпика нет сабтаска");

    }

    @Override
    public Task getTaskById(int taskId) {
        historyManager.add(taskHashMap.get(taskId));
        return taskHashMap.get(taskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        historyManager.add(epicHashMap.get(epicId));
        return epicHashMap.get(epicId);
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        historyManager.add(subtaskHashMap.get(subtaskId));
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
        if (saveEpic == null) {
            return;
        }
        epicHashMap.put(epic.getId(), epic);
        updateEpicStatus(saveEpic.getId());
        updateEpicTime(saveEpic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if( subtask != null && subtaskHashMap.containsKey(subtask.getId())) {
            Epic epic1 = getEpicById(subtask.getId());
            subtaskHashMap.put(subtask.getId(), subtask);
            updateEpicStatus(epic1.getId());
            addTaskToPrioritizedList(subtask);
            updateEpicTime(epic1);
        }
    }

    @Override
    public void addTaskToPrioritizedList(Task task) {
        boolean isValidated = validation(task);
        if (!isValidated) {
            prioritizedTasks.add(task);
        } else {
            throw new ManagerException("There is a problem caused by similar tasks time");
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int generateId() {
        return ++id;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epicHashMap.get(epicId);
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        Status status = null;
        for (int subtaskId : subtaskIds) {
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

    private void updateEpicTime(Epic epic) {
        List<Subtask> subtasks = Collections.singletonList(getSubtasksOfEpic(epic));
        LocalDateTime startTime = subtasks.get(0).getStartTime();
        LocalDateTime endTime = subtasks.get(0).getEndTime();

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            } else if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        long duration = (endTime.getSecond() - startTime.getSecond());
        epic.setDuration(duration);
    }

    private boolean validation(Task task) {
        boolean isOverlapping = false;
        LocalDateTime startOfTask = task.getStartTime();
        LocalDateTime endOfTask = task.getEndTime();
        for (Task taskValue : prioritizedTasks) {
            if (taskValue.getStartTime() == null) {
                continue;
            }
            LocalDateTime startTime = taskValue.getStartTime();
            LocalDateTime endTime = taskValue.getEndTime();
            boolean isCovering = startTime.isBefore(startOfTask) && endTime.isAfter(endOfTask);
            boolean isOverlappingByEnd = startTime.isBefore(startOfTask) && endTime.isAfter(startOfTask);
            boolean isOverlappingByStart = startTime.isBefore(endOfTask) && endTime.isAfter(endOfTask);
            boolean isWithin = startTime.isAfter(startOfTask) && endTime.isBefore(endOfTask);
            isOverlapping = isCovering || isOverlappingByEnd || isOverlappingByStart || isWithin;
        }
        return isOverlapping;
    }
}
