package taskmanager.tests;

import org.junit.jupiter.api.Test;
import taskmanager.manager.*;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    HistoryManager historyManager = new InMemoryHistoryManager();


    @Test
    public void add_shouldAddTasksInHistory() {
        task.setId(0);
        task2.setId(1);
        historyManager.add(task);
        historyManager.add(task2);

        assertEquals(List.of(task, task2), historyManager.getHistory());
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

    /*Пустая история задач*/
    @Test
    public void add_shouldReturnNullIfNoHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    /*Добавление пустой задачи*/
    @Test
    public void add_shouldReturnNullTaskIsEmpty() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    /*Дублирование*/
    @Test
    public void add_shouldNotAddExistingTaskToHistoryList() {
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(List.of(task), historyManager.getHistory());
    }

    /* Удаление истории начало*/
    @Test
    public void remove_shouldRemoveFirstTaskTasksHistory() {
        task.setId(0);
        task2.setId(1);
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(0);

        assertEquals(List.of(task2), historyManager.getHistory());
    }

    /* Удаление истории середина*/
    @Test
    public void remove_shouldRemoveMediumTaskTasksHistory() {
        task.setId(0);
        task2.setId(1);
        task3.setId(2);
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1);

        assertEquals(List.of(task, task3), historyManager.getHistory());
    }

    /* Удаление истории конец*/
    @Test
    public void remove_shouldRemoveEndTasksTasksHistory() {
        task.setId(0);
        task2.setId(1);
        task3.setId(2);
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2);

        assertEquals(List.of(task, task2), historyManager.getHistory());
    }
}


