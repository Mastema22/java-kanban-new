package taskmanager;

import taskmanager.manager.InMemoryTaskManager;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Status;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        Task task1 = new Task("Купить хлеб", "Описание", Status.NEW);
        Task task2 = new Task("Купить cамолёт", "Описание", Status.NEW);

        Epic epic1 = new Epic("Купить машину", "Описание");
        Epic epic2 = new Epic("Купить книгу", "Описание");

        Subtask subtask1 = new Subtask("Выбрать машину", "Описание", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Взять креди на машину", "Описание", Status.IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask("Выбрать книгу", "Описание", Status.DONE, epic2.getId());

        inMemoryTaskManager.addNewTask(task1);
        inMemoryTaskManager.addNewTask(task2);
        inMemoryTaskManager.addNewEpic(epic1);
        inMemoryTaskManager.addNewEpic(epic2);
        inMemoryTaskManager.addNewSubtask(subtask1);
        inMemoryTaskManager.addNewSubtask(subtask2);
        inMemoryTaskManager.addNewSubtask(subtask3);


        System.out.println(inMemoryTaskManager.getTaskList());
        System.out.println(inMemoryTaskManager.getEpicList());
        System.out.println(inMemoryTaskManager.getSubtaskList());
        System.out.println(inMemoryTaskManager.getHistory());
    }

}
