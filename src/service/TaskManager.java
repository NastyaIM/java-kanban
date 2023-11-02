package service;

import model.Epic;
import model.State;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Task> tasks;

    private int taskId = 0;

    public TaskManager() {
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        tasks = new HashMap<>();
    }

    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        epic.setEpicSubtasks(new HashMap<Integer, Subtask>());
        epic.setState(updateEpicState(epic.getEpicSubtasks()));
    }

    public void addSubtask(ArrayList<Subtask> sub, int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }
        Epic epic = epics.get(epicId);
        for (Subtask subtask : sub) {
            subtask.setId(generateId());
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtask(subtask);
        }
        epic.setState(updateEpicState(epic.getEpicSubtasks()));
    }

    //Методы для каждого из типов задач
    //2a - Получение списка всех задач
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    //2b - Удаление всех задач
    public void removeTasks() {
        tasks.clear();
    }

    public void removeEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeSubtasks();
        }
    }

    //2c - Получение задачи по идентификатору
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    //2e - Обновление
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        removeAllEpicSubtasks(epic.getId());
        epics.put(epic.getId(), epic);
        epic.setEpicSubtasks(new HashMap<Integer, Subtask>());
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        HashMap<Integer, Subtask> epicSubtasks = epic.getEpicSubtasks();
        epicSubtasks.put(subtask.getId(), subtask);
        epic.setState(updateEpicState(epicSubtasks));
    }

    //2f - Удаление задачи по идентификатору
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        removeAllEpicSubtasks(id);
        epics.remove(id);
    }

    public void removeSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            return;
        }
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getEpicSubtasks().remove(id);
        subtasks.remove(id);
    }

    //3a - Получение списка всех подзадач определённого эпика
    public HashMap<Integer, Subtask> getEpicSubtasksById(int epicId) {
        if (!epics.containsKey(epicId)) {
            return null;
        }
        Epic epic = epics.get(epicId);
        return epic.getEpicSubtasks();
    }

    private void removeAllEpicSubtasks(int epicId) {
        HashMap<Integer, Subtask> epicSubtasks = getEpicSubtasksById(epicId);
        if (epicSubtasks == null) {
            return;
        }
        for (Subtask subtask : epicSubtasks.values()) {
            subtasks.remove(subtask.getId());
        }
    }

    public int generateId() {
        return ++taskId;
    }

    public State updateEpicState(HashMap<Integer, Subtask> epicSubtasks) {
        if (epicSubtasks.isEmpty()) {
            return State.NEW;
        }
        int counter = 0;
        State state = State.DONE;
        for (Subtask epicSubtask : epicSubtasks.values()) {
            if (epicSubtask.getState() == State.DONE) {
                continue;
            }
            if (epicSubtask.getState() == State.NEW) {
                counter++;
            }
            state = State.IN_PROGRESS;
        }
        if (counter == epicSubtasks.size()) {
            return State.NEW;
        }
        return state;
    }
}