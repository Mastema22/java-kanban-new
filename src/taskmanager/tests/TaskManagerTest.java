package taskmanager.tests;

import org.junit.jupiter.api.BeforeEach;
import taskmanager.manager.*;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Status;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;

import java.time.LocalDateTime;


abstract class TaskManagerTest<T extends TaskManager, S extends HistoryManager, E extends FileBackedTasksManager> {
    public T taskManager;
    public S historyManager;
    public E fileBackTaskManager;

    public final Task task = new Task("Test addNewTask1", "Test addNewTask description1", Status.NEW, 10, LocalDateTime.of(2023, 10, 14, 12, 0));
    public final Task task2 = new Task("Test addNewTask2", "Test addNewTask description2", Status.NEW, 10, LocalDateTime.of(2023,10,14,12,40));
    public final Task task3 = new Task("Test addNewTask3", "Test addNewTask description3", Status.DONE, 10, LocalDateTime.of(2023,10,14,12,50));
    public final Epic epic = new Epic("Test addNewEpic", "Test addNewTask description", 50, LocalDateTime.of(2023, 10, 14, 12, 30));
    public final Subtask subtask = new Subtask("Test addNewSubTask", "Test addNewTask description", Status.NEW,
            70, LocalDateTime.of(2023, 10, 14, 12, 35), 1);

    @BeforeEach
    public void beforeEach() {
        taskManager = (T) new InMemoryTaskManager();
        historyManager = (S) new InMemoryHistoryManager();
        fileBackTaskManager = (E) new FileBackedTasksManager(Managers.getDefaultHistory(),"manager.cvs");
    }


}
