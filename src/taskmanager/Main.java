package taskmanager;

import taskmanager.manager.InMemoryTaskManager;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;
import taskmanager.tasks.Status;

public class Main {
    public static void main(String[] args) {

        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();


        Task task1 = new Task("Купить хлеб", "Описание", Status.NEW);
        Task task2 = new Task("Купить cамолёт", "Описание", Status.NEW);
        long task1Id = inMemoryTaskManager.addNewTask(task1);
        long task2Id = inMemoryTaskManager.addNewTask(task2);

        Epic epic1 = new Epic("Купить машину", "Описание");
        Epic epic2 = new Epic("Купить книгу", "Описание");
        long epic1Id = inMemoryTaskManager.addNewEpic(epic1);
        long epic2Id = inMemoryTaskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Выбрать машину", "Описание",Status.NEW, epic1Id);
        Subtask subtask2 = new Subtask("Взять креди на машину", "Описание",Status.IN_PROGRESS, epic1Id);
        Subtask subtask3 = new Subtask("Выбрать книгу", "Описание", Status.DONE, epic2Id);
        Long subtaskId1 = inMemoryTaskManager.addNewSubtask(subtask1);
        Long subtaskId2 = inMemoryTaskManager.addNewSubtask(subtask2);
        Long subtaskID3 = inMemoryTaskManager.addNewSubtask(subtask3);

        System.out.println(inMemoryTaskManager.getTaskById(task1Id).toString());
        System.out.println(inMemoryTaskManager.getEpicById(epic1Id).toString());
        System.out.println(inMemoryTaskManager.getSubtaskById(subtaskId1).toString());

        System.out.println(inMemoryTaskManager.getTaskList().toString());
        System.out.println(inMemoryTaskManager.getEpicList().toString());
        System.out.println(inMemoryTaskManager.getSubtaskList().toString());

        inMemoryTaskManager.getTaskById(task1Id);
        inMemoryTaskManager.getEpicById(epic2Id);
        inMemoryTaskManager.getSubtaskById(subtaskId2);

        inMemoryTaskManager.getSubtasksOfEpic(epic1);

        inMemoryTaskManager.removeTaskById(task2Id);
        inMemoryTaskManager.removeSubtaskById(subtaskId1);
        System.out.println(inMemoryTaskManager.getTaskList().toString());

        inMemoryTaskManager.removeEpicById(epic1Id);
        System.out.println(inMemoryTaskManager.getHistory());

    }
}
