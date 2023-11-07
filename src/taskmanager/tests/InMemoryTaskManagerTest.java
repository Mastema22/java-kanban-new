package taskmanager.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import taskmanager.manager.CSVFormatter;
import taskmanager.manager.TaskManager;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Status;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {


    @AfterEach
    public void afterEach() {
        taskManager.getTaskList().clear();
        taskManager.getEpicList().clear();
        taskManager.getSubtaskList().clear();
        taskManager.getPrioritizedTasks().clear();
        taskManager.getHistory().clear();
    }

    /*Все тесты по Task*/
    @Test
    public void addNewTask_shouldMakeATask() {
        Task savedTask = taskManager.addNewTask(task);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = new ArrayList<>(taskManager.getTaskList().values());

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addNewTask_shouldNotMakeATaskEmpty() {
        Task task1 = taskManager.addNewTask(null);

        assertNull(task1);
    }

    @Test
    public void getTaskList_ShouldReturnHashMapTasks() {
        taskManager.addNewTask(task);
        HashMap<Integer, Task> listTasks = taskManager.getTaskList();

        assertNotNull(listTasks);
        assertEquals(1, listTasks.size());
    }


    @Test
    public void updateTask_shouldUpdateTaskStatusInProgress() {
        Task task1 = taskManager.addNewTask(task);
        task1.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(task1.getId()).getStatus());
    }

    @Test
    public void updateTask_shouldUpdateTaskStatusDone() {
        Task task1 = taskManager.addNewTask(task);
        task1.setStatus(Status.DONE);

        assertEquals(Status.DONE, taskManager.getTaskById(task1.getId()).getStatus());
    }

    @Test
    public void removeTaskById_shouldRemoveTaskById() {
        taskManager.addNewTask(task);
        taskManager.removeTaskById(task.getId());
        HashMap<Integer, Task> taskHashMap = taskManager.getTaskList();
        List<Task> taskList = new ArrayList<>(taskHashMap.values());

        assertEquals(0, taskHashMap.size());
        assertEquals(0, taskList.size());
    }

    @Test
    public void removeTaskById_shouldRemoveTaskByIdIfNull() {
        taskManager.addNewTask(null);
        taskManager.removeTaskById(task.getId());
        HashMap<Integer, Task> taskHashMap = taskManager.getTaskList();
        List<Task> taskList = new ArrayList<>(taskHashMap.values());

        assertEquals(0, taskHashMap.size());
        assertEquals(0, taskList.size());

    }

    @Test
    public void removeTaskById_shouldRemoveTaskByIdIfIncorrect() {
        taskManager.addNewTask(task);
        taskManager.removeTaskById(99);
        HashMap<Integer, Task> taskHashMap = taskManager.getTaskList();
        List<Task> taskList = new ArrayList<>(taskHashMap.values());

        assertEquals(List.of(task), taskList);

    }

    @Test
    public void removeAllTask_shouldRemoveAllTaskIfNull() {
        taskManager.addNewTask(null);
        taskManager.removeAllTask();

        assertEquals(Collections.EMPTY_MAP, taskManager.getTaskList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getPrioritizedTasks());
    }

    @Test
    public void removeAllTask_shouldRemoveAllTaskIfIncorrect() {
        taskManager.addNewTask(null);
        taskManager.removeTaskById(99);
        HashMap<Integer, Task> taskHashMap = taskManager.getTaskList();

        assertEquals(Collections.EMPTY_MAP, taskHashMap);
    }

    @Test
    public void removeAllTask_shouldRemoveAllTask() {
        taskManager.addNewTask(task);
        taskManager.removeAllTask();

        assertEquals(Collections.EMPTY_MAP, taskManager.getTaskList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getPrioritizedTasks());
    }


    @Test
    public void addNewEpic_shouldMakeAEpic() {
        taskManager.addNewEpic(epic);
        HashMap<Integer, Epic> epicHashMap = taskManager.getEpicList();
        List<Task> epicList = new ArrayList<>(epicHashMap.values());

        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(List.of(epic), epicList);
        assertEquals(Collections.EMPTY_LIST, epic.getSubtaskIds());
    }

    @Test
    public void getEpicList_ShouldReturnHashMapEpics() {
        taskManager.addNewEpic(epic);
        HashMap<Integer, Epic> listEpics = taskManager.getEpicList();

        assertNotNull(listEpics);
        assertEquals(1, listEpics.size());
    }

    @Test
    public void getEpicById() {
        taskManager.addNewEpic(epic);
        Epic epic1 = taskManager.getEpicById(epic.getId());
        HashMap<Integer, Epic> epicHashMap = taskManager.getEpicList();
        List<Epic> epicList = new ArrayList<>(epicHashMap.values());

        assertNotNull(epic1.getStatus());
        assertEquals(1, epic1.getId());
        assertEquals(List.of(epic), epicList);
    }

    @Test
    public void updateEpic_shouldUpdateEpicStatusInProgress() {
        Epic epic1 = taskManager.addNewEpic(epic);
        epic1.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus());
    }

    @Test
    public void updateEpic_shouldUpdateEpicStatusDone() {
        Epic epic1 = taskManager.addNewEpic(epic);
        epic1.setStatus(Status.DONE);

        assertEquals(Status.DONE, taskManager.getEpicById(epic1.getId()).getStatus());
    }

    @Test
    public void removeEpicById_shouldRemoveEpicById() {
        taskManager.addNewEpic(epic);
        taskManager.removeEpicById(epic.getId());
        HashMap<Integer, Epic> epicHashMap = taskManager.getEpicList();
        List<Task> taskList = new ArrayList<>(epicHashMap.values());

        assertEquals(0, epicHashMap.size());
        assertEquals(0, taskList.size());
    }

    @Test
    public void removeEpicById_shouldRemoveEpicByIdIfNull() {
        taskManager.addNewEpic(null);
        taskManager.removeEpicById(epic.getId());
        HashMap<Integer, Epic> epicHashMap = taskManager.getEpicList();
        List<Task> taskList = new ArrayList<>(epicHashMap.values());

        assertEquals(0, epicHashMap.size());
        assertEquals(0, taskList.size());
    }

    @Test
    public void removeEpicById_shouldRemoveEpicByIdIfIncorrect() {
        taskManager.addNewEpic(epic);
        taskManager.removeEpicById(99);
        HashMap<Integer, Epic> epicHashMap = taskManager.getEpicList();
        List<Task> epicList = new ArrayList<>(epicHashMap.values());

        assertEquals(List.of(epic), epicList);

    }

    @Test
    public void removeAllEpics_shouldRemoveAllEpics() {
        taskManager.addNewEpic(epic);
        taskManager.removeAllEpics();

        assertEquals(Collections.EMPTY_MAP, taskManager.getEpicList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getPrioritizedTasks());
    }

    @Test
    public void removeAllEpics_shouldRemoveAllEpicsIfNull() {
        taskManager.addNewEpic(null);
        taskManager.removeAllEpics();

        assertEquals(Collections.EMPTY_MAP, taskManager.getEpicList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getPrioritizedTasks());
    }

    @Test
    public void removeAllEpic_shouldRemoveAllEpicsIfIncorrect() {
        taskManager.addNewEpic(null);
        taskManager.removeEpicById(99);
        HashMap<Integer, Epic> epicHashMap = taskManager.getEpicList();

        assertEquals(Collections.EMPTY_MAP, epicHashMap);
    }

    /*Все тесты по Subtask*/
    @Test
    public void addNewSubtask() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);


        HashMap<Integer, Subtask> subtaskHashMap = taskManager.getSubtaskList();
        List<Task> subtaskList = new ArrayList<>(subtaskHashMap.values());

        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(List.of(subtask), subtaskList);
        assertEquals(List.of(subtask.getId()), epic.getSubtaskIds());
        assertEquals(CSVFormatter.toString(subtask), CSVFormatter.toString(taskManager.getSubtaskById(subtask.getId())));
    }

    @Test
    public void getSubtaskList_ShouldReturnHashMapSubtasks() {
        taskManager.addNewSubtask(subtask);
        HashMap<Integer, Subtask> listSubtasks = taskManager.getSubtaskList();

        assertNotNull(listSubtasks);
    }

    @Test
    public void getSubtaskById() {
        taskManager.addNewEpic(epic);
        Subtask subtask1 = taskManager.addNewSubtask(subtask);
        HashMap<Integer, Subtask> subtaskHashMap = taskManager.getSubtaskList();
        List<Subtask> subtaskList = new ArrayList<>(subtaskHashMap.values());

        assertNotNull(subtask1.getStatus());
        assertEquals(1, subtask1.getEpicId());
        assertEquals(List.of(subtask1), subtaskList);
    }

    @Test
    public void updateSubtask_shouldUpdateSubtaskStatusInProgress() {
        taskManager.addNewEpic(epic);
        Subtask subtask1 = taskManager.addNewSubtask(subtask);
        subtask1.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, taskManager.getSubtaskById(subtask1.getId()).getStatus());
    }

    @Test
    public void updateSubtask_shouldUpdateSubtaskStatusDone() {
        taskManager.addNewEpic(epic);
        Subtask subtask1 = taskManager.addNewSubtask(subtask);
        subtask1.setStatus(Status.DONE);

        assertEquals(Status.DONE, taskManager.getSubtaskById(subtask1.getId()).getStatus());
    }


    @Test
    public void removeSubtaskById_shouldRemoveSubtaskById() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);
        epic.setId(subtask.getEpicId());
        taskManager.removeSubtaskById(subtask.getId());
        HashMap<Integer, Subtask> subtaskHashMap = taskManager.getSubtaskList();
        List<Task> subtaskList = new ArrayList<>(subtaskHashMap.values());
        assertEquals(0, subtaskHashMap.size());
        assertEquals(0, subtaskList.size());

    }

    @Test
    public void removeSubtaskById_shouldRemoveSubtaskByIdIfNull() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(null);
        taskManager.removeTaskById(subtask.getId());
        HashMap<Integer, Subtask> subtaskHashMap = taskManager.getSubtaskList();
        List<Subtask> subtaskList = new ArrayList<>(subtaskHashMap.values());

        assertEquals(0, subtaskHashMap.size());
        assertEquals(0, subtaskList.size());

    }

    @Test
    public void removeSubtaskById_shouldRemoveSubtaskByIdIfIncorrect() {
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);
        taskManager.removeSubtaskById(99);
        HashMap<Integer, Subtask> subtaskHashMap = taskManager.getSubtaskList();
        List<Subtask> subtaskList = new ArrayList<>(subtaskHashMap.values());

        assertEquals(List.of(subtask), subtaskList);
    }

    @Test
    public void removeAllSubtasks_shouldRemoveAllSubtasks() {
        taskManager.addNewSubtask(subtask);
        taskManager.removeAllSubtasks();

        assertEquals(Collections.EMPTY_MAP, taskManager.getSubtaskList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getPrioritizedTasks());
    }

    @Test
    public void removeAllSubtasks_shouldRemoveAllSubtasksIfNull() {
        taskManager.addNewSubtask(null);
        taskManager.removeAllSubtasks();

        assertEquals(Collections.EMPTY_MAP, taskManager.getSubtaskList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getPrioritizedTasks());
    }

    @Test
    public void removeAllSubtask_shouldRemoveAllSubtasksIfIncorrect() {
        taskManager.addNewSubtask(null);
        taskManager.removeSubtaskById(99);
        HashMap<Integer, Subtask> subtaskHashMap = taskManager.getSubtaskList();

        assertEquals(Collections.EMPTY_MAP, subtaskHashMap);
    }

    /*Общие методы*/
    @Test
    public void removeAll_shouldDeleteAllTask() {
        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);
        taskManager.removeAll();

        assertEquals(Collections.EMPTY_MAP, taskManager.getTaskList());
        assertEquals(Collections.EMPTY_MAP, taskManager.getEpicList());
        assertEquals(Collections.EMPTY_MAP, taskManager.getSubtaskList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getPrioritizedTasks());
    }

    @Test
    public void removeAll_shouldDeleteAllTasksIfListEmpty() {
        taskManager.addNewTask(null);
        taskManager.addNewEpic(null);
        taskManager.addNewSubtask(null);
        taskManager.removeAll();

        assertEquals(Collections.EMPTY_MAP, taskManager.getTaskList());
        assertEquals(Collections.EMPTY_MAP, taskManager.getEpicList());
        assertEquals(Collections.EMPTY_MAP, taskManager.getSubtaskList());
        assertEquals(Collections.EMPTY_LIST, taskManager.getPrioritizedTasks());
    }


    @Test
    public void getPrioritizedTasks() {
        Task task1 = taskManager.addNewTask(task);
        taskManager.getTaskById(task1.getId());
        List<Task> list = List.of(task1);
        List<Task> taskListPrioritized = taskManager.getPrioritizedTasks();

        assertEquals(list, taskListPrioritized);
        assertFalse(taskListPrioritized.isEmpty());
    }

    @Test
    public void getHistory_shouldReturnHistoryList() {
        Epic epic1 = taskManager.addNewEpic(epic);
        taskManager.getEpicById(epic1.getId());
        List<Task> listOfHistory = taskManager.getHistory();

        assertEquals(1, listOfHistory.size());
    }

    @Test
    public void getHistory_shouldReturnEmptyHistoryList() {
        taskManager.getTaskById(0);
        taskManager.getEpicById(0);
        taskManager.getSubtaskById(0);

        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    public void addTaskToPrioritizedList() {
        Task task1 = taskManager.addNewTask(task);
        taskManager.addTaskToPrioritizedList(task1);
        List<Task> list = List.of(task1);
        List<Task> taskListPrioritized = taskManager.getPrioritizedTasks();

        assertEquals(task1, taskManager.getPrioritizedTasks().get(0));
        assertEquals(list, taskListPrioritized);
        assertFalse(taskListPrioritized.isEmpty());
    }

}
