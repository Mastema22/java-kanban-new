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

        System.out.println(taskTracker.getTaskById(task1Id).toString());
        System.out.println(taskTracker.getEpicById(epic1Id).toString());
        System.out.println(taskTracker.getSubtaskById(subtaskId1).toString());

        System.out.println(taskTracker.getTaskList().toString());
        System.out.println(taskTracker.getEpicList().toString());
        System.out.println(taskTracker.getSubtaskList().toString());

        taskTracker.getSubtaskById(subtaskId2);

        taskTracker.getSubtasksOfEpic(epic1);

        taskTracker.removeTaskById(task2Id);
        taskTracker.removeSubtaskById(subtaskId1);
        System.out.println(taskTracker.getTaskList().toString());

        taskTracker.removeEpicById(epic1Id);

    }
}
