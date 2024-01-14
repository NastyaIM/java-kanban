package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubtask(Subtask subtask, int epicId);

    //Методы для каждого из типов задач
    //2a - Получение списка всех задач
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    TreeSet<Task> getPrioritizedTasks();

    //2b - Удаление всех задач
    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    //2c - Получение задачи по идентификатору
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    //2e - Обновление
    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    //2f - Удаление задачи по идентификатору
    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    //3a - Получение списка всех подзадач определённого эпика
    List<Subtask> getEpicSubtasksById(int epicId);

    List<Task> getHistory();
}