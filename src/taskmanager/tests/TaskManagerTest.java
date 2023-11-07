package taskmanager.tests;

import taskmanager.manager.FileBackedTasksManager;
import taskmanager.manager.InMemoryTaskManager;
import taskmanager.manager.Managers;
import taskmanager.manager.TaskManager;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Status;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;

import java.time.LocalDateTime;


abstract class TaskManagerTest<T extends TaskManager> {
    public T taskManager = (T) new InMemoryTaskManager();
    public T fileBackTaskManager = (T) new FileBackedTasksManager(Managers.getDefaultHistory(), "manager.cvs");



    public final Task task = new Task("Test addNewTask1", "Test addNewTask description1", Status.NEW, 10, LocalDateTime.of(2023, 10, 14, 12, 0));
    public final Task task2 = new Task("Test addNewTask2", "Test addNewTask description2", Status.NEW, 10, LocalDateTime.of(2023,10,14,12,40));
    public final Task task3 = new Task("Test addNewTask3", "Test addNewTask description3", Status.DONE, 10, LocalDateTime.of(2023,10,14,12,50));
    public final Epic epic = new Epic("Test addNewEpic", "Test addNewTask description", 50, LocalDateTime.of(2023, 10, 14, 12, 30));
    public final Epic epic2 = new Epic("Test addNewEpic2", "Test addNewTask description", 60, LocalDateTime.of(2023, 11, 15, 10, 25));
    public final Subtask subtask = new Subtask("Test addNewSubTask", "Test addNewTask description", Status.NEW,
            70, LocalDateTime.of(2023, 10, 14, 12, 35), 1);
    public final Subtask subtask2 = new Subtask("Test addNewSubTask", "Test addNewTask description", Status.NEW,
            70, LocalDateTime.of(2023, 12, 14, 10, 45), 2);


}
