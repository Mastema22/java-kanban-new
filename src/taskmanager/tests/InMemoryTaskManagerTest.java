package taskmanager.manager.tests;

import org.junit.jupiter.api.Test;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.manager.ManagerException;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Status;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    public static final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    private Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW, 1, Instant.ofEpochSecond(1685998800000L));
    private Epic epic = new Epic("Test addNewTask", "Test addNewTask description", 5, Instant.ofEpochSecond(1685998800000L));
    private Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description", Status.NEW,
            7, Instant.ofEpochSecond(1685998800000L), 1);

    @Test
    public void addNewTask() {
        final Task savedTask = inMemoryTaskManager.addNewTask(task);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = new ArrayList<>(inMemoryTaskManager.getTaskList().values());

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addNewEpic() {
        inMemoryTaskManager.addNewEpic(epic);
        HashMap<Integer, Epic> epicHashMap = inMemoryTaskManager.getEpicList();
        List<Task> epicList = new ArrayList<>(epicHashMap.values());

        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(List.of(epic), epicList);
        assertEquals(Collections.EMPTY_LIST, epic.getSubtaskIds());

    }

    @Test
    public void addNewSubtask() {
        inMemoryTaskManager.addNewEpic(epic);
        inMemoryTaskManager.addNewSubtask(subtask);

        HashMap<Integer, Subtask> subtaskHashMap = inMemoryTaskManager.getSubtaskList();
        List<Task> subtaskList = new ArrayList<>(subtaskHashMap.values());

        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(List.of(subtask), subtaskList);
        assertEquals(List.of(subtask.getId()), epic.getSubtaskIds());

    }

    @Test
    public void getTaskList_ShouldReturnHashMapTasks() {
        inMemoryTaskManager.addNewTask(task);
        HashMap<Integer, Task> listTasks = inMemoryTaskManager.getTaskList();

        assertNotNull(listTasks);
        assertEquals(1, listTasks.size());

    }

    @Test
    public void getEpicList_ShouldReturnHashMapEpics() {
        inMemoryTaskManager.addNewEpic(epic);
        HashMap<Integer, Epic> listEpics = inMemoryTaskManager.getEpicList();

        assertNotNull(listEpics);
        assertEquals(1, listEpics.size());

    }

    @Test
    public void getSubtaskList_ShouldReturnHashMapSubtasks() {
        inMemoryTaskManager.addNewSubtask(subtask);
        HashMap<Integer, Subtask> listSubtasks = inMemoryTaskManager.getSubtaskList();

        assertNotNull(listSubtasks);
        assertEquals(1, listSubtasks.size());
    }

    @Test
    public void removeTaskById() {
        inMemoryTaskManager.addNewTask(task);
        inMemoryTaskManager.removeTaskById(task.getId());
        HashMap<Integer, Task> taskHashMap = inMemoryTaskManager.getTaskList();
        List<Task> taskList = inMemoryTaskManager.getPrioritizedTasks();

        assertEquals(0, taskHashMap.size());
        assertEquals(0, taskList.size());

    }

    @Test
    public void removeEpicById() {
        inMemoryTaskManager.addNewEpic(epic);
        inMemoryTaskManager.removeEpicById(epic.getId());
        HashMap<Integer, Epic> epicHashMap = inMemoryTaskManager.getEpicList();
        List<Task> taskList = inMemoryTaskManager.getPrioritizedTasks();

        assertEquals(0, epicHashMap.size());
        assertEquals(0, taskList.size());
    }

    @Test
    public void removeSubtaskById() {
        inMemoryTaskManager.addNewEpic(epic);
        Subtask subtask1 = inMemoryTaskManager.addNewSubtask(subtask);
        inMemoryTaskManager.removeSubtaskById(subtask1.getId());
        HashMap<Integer,Subtask> subtaskHashMap = inMemoryTaskManager.getSubtaskList();
        List<Task> subtaskList = inMemoryTaskManager.getPrioritizedTasks();

        assertEquals(0, subtaskHashMap.size());
        assertEquals(0, subtaskList.size());

    }

    @Test
    public void removeAllTask() {
        inMemoryTaskManager.addNewTask(task);
        inMemoryTaskManager.removeAllTask();

        assertEquals(Collections.EMPTY_MAP, inMemoryTaskManager.getTaskList());
        assertEquals(Collections.EMPTY_LIST, inMemoryTaskManager.getPrioritizedTasks());
    }

    @Test
    public void removeAllEpics() {
        inMemoryTaskManager.addNewEpic(epic);
        inMemoryTaskManager.removeAllEpics();

        assertEquals(Collections.EMPTY_MAP, inMemoryTaskManager.getEpicList());
        assertEquals(Collections.EMPTY_LIST, inMemoryTaskManager.getPrioritizedTasks());
    }

    @Test
    public void removeAllSubtasks() {
        inMemoryTaskManager.addNewSubtask(subtask);
        inMemoryTaskManager.removeAllSubtasks();

        assertEquals(Collections.EMPTY_MAP, inMemoryTaskManager.getSubtaskList());
        assertEquals(Collections.EMPTY_LIST, inMemoryTaskManager.getPrioritizedTasks());
    }

    @Test
    public void removeAll() {
        inMemoryTaskManager.addNewTask(task);
        inMemoryTaskManager.addNewEpic(epic);
        inMemoryTaskManager.addNewSubtask(subtask);
        inMemoryTaskManager.removeAll();

        assertEquals(Collections.EMPTY_MAP, inMemoryTaskManager.getTaskList());
        assertEquals(Collections.EMPTY_MAP, inMemoryTaskManager.getEpicList());
        assertEquals(Collections.EMPTY_MAP, inMemoryTaskManager.getSubtaskList());
        assertEquals(Collections.EMPTY_LIST, inMemoryTaskManager.getPrioritizedTasks());

    }

    @Test
    public void getSubtasksOfEpic() {
        Epic epic1 = inMemoryTaskManager.addNewEpic(epic);
        List<Integer> listOfSubtasksIds = epic1.getSubtaskIds();

        assertTrue(listOfSubtasksIds.isEmpty());
    }

    @Test
    public void getTaskById() {
        inMemoryTaskManager.addNewTask(task);
        Task task1 = inMemoryTaskManager.getTaskById(1);
        HashMap<Integer, Task> taskHashMap = inMemoryTaskManager.getTaskList();
        List<Task> taskList = new ArrayList<>(taskHashMap.values());

        assertNotNull(task1.getStatus());
        assertEquals(1,task1.getId());
        assertEquals(List.of(task1), taskList);
    }

    @Test
    public void getEpicById() {
        inMemoryTaskManager.addNewEpic(epic);
        Epic epic1 = inMemoryTaskManager.getEpicById(epic.getId());
        HashMap<Integer, Epic> epicHashMap = inMemoryTaskManager.getEpicList();
        List<Epic> epicList = new ArrayList<>(epicHashMap.values());

        assertNotNull(epic1.getStatus());
        assertEquals(1,epic1.getId());
        assertEquals(List.of(epic), epicList);
    }

    @Test
    public void getSubtaskById() {
        inMemoryTaskManager.addNewEpic(epic);
        Subtask subtask1 =  inMemoryTaskManager.addNewSubtask(subtask);
        HashMap<Integer, Subtask> subtaskHashMap = inMemoryTaskManager.getSubtaskList();
        List<Subtask> subtaskList = new ArrayList<>(subtaskHashMap.values());

        assertNotNull(subtask1.getStatus());
        assertEquals(1,subtask1.getEpicId());
        assertEquals(List.of(subtask1), subtaskList);
    }

    @Test
    public void updateTask_shouldUpdateTaskStatusInProgress() {
        Task task1 = inMemoryTaskManager.addNewTask(task);
        task1.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getTaskById(task1.getId()).getStatus());
    }

    @Test
    public void updateTask_shouldUpdateTaskStatusDone() {
        Task task1 = inMemoryTaskManager.addNewTask(task);
        task1.setStatus(Status.DONE);

        assertEquals(Status.DONE, inMemoryTaskManager.getTaskById(task1.getId()).getStatus());
    }

    @Test
    public void updateEpic_shouldUpdateEpicStatusInProgress() {
        Epic epic1 = inMemoryTaskManager.addNewEpic(epic);
        epic1.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getEpicById(epic1.getId()).getStatus());
    }

    @Test
    public void updateEpic_shouldUpdateEpicStatusDone() {
        Epic epic1 = inMemoryTaskManager.addNewEpic(epic);
        epic1.setStatus(Status.DONE);

        assertEquals(Status.DONE, inMemoryTaskManager.getEpicById(epic1.getId()).getStatus());
    }

    @Test
    public void updateSubtask_shouldUpdateSubtaskStatusInProgress() {
        inMemoryTaskManager.addNewEpic(epic);
        Subtask subtask1 = inMemoryTaskManager.addNewSubtask(subtask);
        subtask1.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getSubtaskById(subtask1.getId()).getStatus());
    }

    @Test
    public void updateSubtask_shouldUpdateSubtaskStatusDone() {
        inMemoryTaskManager.addNewEpic(epic);
        Subtask subtask1 = inMemoryTaskManager.addNewSubtask(subtask);
        subtask1.setStatus(Status.DONE);

        assertEquals(Status.DONE, inMemoryTaskManager.getSubtaskById(subtask1.getId()).getStatus());
    }

    @Test
    public void addTaskToPrioritizedList() {
        Task task1 = inMemoryTaskManager.addNewTask(task);
        inMemoryTaskManager.addTaskToPrioritizedList(task1);
        List<Task> list = List.of(task1);
        List<Task> taskListPrioritized = inMemoryTaskManager.getPrioritizedTasks();

        assertEquals(task1,inMemoryTaskManager.getPrioritizedTasks().get(0));
        assertEquals(list, taskListPrioritized);
        assertFalse(taskListPrioritized.isEmpty());
    }

    @Test
    public void getPrioritizedTasks() {
        Task task1 = inMemoryTaskManager.addNewTask(task);
        inMemoryTaskManager.getTaskById(task1.getId());
        List<Task> list = List.of(task1);
        List<Task> taskListPrioritized = inMemoryTaskManager.getPrioritizedTasks();

        assertEquals(list, taskListPrioritized);
        assertFalse(taskListPrioritized.isEmpty());
    }

    @Test
    public void getHistory_shouldReturnHistoryList() {
        Epic epic1 = inMemoryTaskManager.addNewEpic(epic);
        inMemoryTaskManager.getEpicById(epic1.getId());
        List<Task> listOfHistory = inMemoryTaskManager.getHistory();

        assertEquals(1, listOfHistory.size());
    }

    @Test
    public void getHistory_shouldReturnEmptyHistoryList() {
        inMemoryTaskManager.getTaskById(0);
        inMemoryTaskManager.getEpicById(0);
        inMemoryTaskManager.getSubtaskById(0);

        assertTrue(inMemoryTaskManager.getHistory().isEmpty());
    }

}
