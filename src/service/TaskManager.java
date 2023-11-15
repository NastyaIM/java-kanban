package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask, int epicId);

    //Методы для каждого из типов задач
    //2a - Получение списка всех задач
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    //2b - Удаление всех задач
    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    //2c - Получение задачи по идентификатору
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    //2e - Обновление
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    //2f - Удаление задачи по идентификатору
    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    //3a - Получение списка всех подзадач определённого эпика
    List<Subtask> getEpicSubtasksById(int epicId);

    List<Task> getHistory();
}