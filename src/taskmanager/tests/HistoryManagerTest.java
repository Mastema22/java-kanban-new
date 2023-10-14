package taskmanager.manager.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.manager.HistoryManager;
import taskmanager.manager.InMemoryHistoryManager;
import taskmanager.tasks.Status;
import taskmanager.tasks.Task;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    private HistoryManager historyManager;
    private final Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW, 1, Instant.ofEpochSecond(1685998800000L));

    private final Task task2 = new Task("Test addNewTask2", "Test addNewTask description", Status.IN_PROGRESS, 2, Instant.ofEpochSecond(1685998800000L));
    private final Task task3 = new Task("Test addNewTask3", "Test addNewTask description", Status.DONE, 3, Instant.ofEpochSecond(1685998800000L));

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void add_shouldAddTasksInHistory() {
        task.setId(0);
        task2.setId(1);
        historyManager.add(task);
        historyManager.add(task2);

        assertEquals(List.of(task, task2), historyManager.getHistory());
    }

    @Test
    public void add_shouldReturnNullTaskIsEmpty() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void add_shouldReturnNullIfNoHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void add_shouldNotAddExistingTaskToHistoryList() {
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(List.of(task), historyManager.getHistory());
    }

    @Test
    public void remove_shouldRemoveBeginTasksHistory() {
        task.setId(0);
        task2.setId(1);
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(0);

        assertEquals(List.of(task2),historyManager.getHistory());
    }

    @Test
    public void remove_shouldRemoveMediumTasksHistory() {
        task.setId(0);
        task2.setId(1);
        task3.setId(2);
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1);

        assertEquals(List.of(task, task3),historyManager.getHistory());
    }

    @Test
    public void remove_shouldRemoveEndTasksHistory() {
        task.setId(0);
        task2.setId(1);
        task3.setId(2);
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2);

        assertEquals(List.of(task,task2),historyManager.getHistory());
    }

    @Test
    public void getHistory() {
        task.setId(0);
        task2.setId(1);
        historyManager.add(task);
        historyManager.add(task2);

        assertEquals(2, historyManager.getHistory().size());
        assertFalse(historyManager.getHistory().isEmpty());
    }
}
