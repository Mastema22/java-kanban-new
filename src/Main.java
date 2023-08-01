import manager.TaskTracker;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        TaskTracker taskTracker = new TaskTracker();

        Task task1 = new Task("Купить хлеб", "Описание", "NEW");
        Task task2 = new Task("Купить cамолёт", "Описание", "NEW");
        long task1Id = taskTracker.addNewTask(task1);
        long task2Id = taskTracker.addNewTask(task2);

        Epic epic1 = new Epic("Купить машину", "Описание");
        Epic epic2 = new Epic("Купить книгу", "Описание");
        long epic1Id = taskTracker.addNewEpic(epic1);
        long epic2Id = taskTracker.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Выбрать машину", "Описание","NEW", epic1Id);
        Subtask subtask2 = new Subtask("Взять креди на машину", "Описание","IN_PROGRESS", epic1Id);
        Subtask subtask3 = new Subtask("Выбрать книгу", "Описание", "DONE", epic2Id);
        Long subtaskId1 = taskTracker.addNewSubtask(subtask1);
        Long subtaskId2 = taskTracker.addNewSubtask(subtask2);
        Long subtaskID3 = taskTracker.addNewSubtask(subtask3);

        System.out.println(taskTracker.getTaskHashMap().toString());
        System.out.println(taskTracker.getEpicHashMap().toString());
        System.out.println(taskTracker.getSubtaskHashMap().toString());

        taskTracker.getSubtasksOfEpic(epic1);

        taskTracker.removeTaskUseId(task2Id);
        System.out.println(taskTracker.getTaskHashMap().toString());

        taskTracker.removeEpicUseId(epic1Id);
        System.out.println(taskTracker.getEpicHashMap().toString());
        System.out.println(taskTracker.getSubtaskHashMap().toString());
    }
}
