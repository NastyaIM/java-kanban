package service;

import model.Epic;
import model.State;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Task> tasks = new HashMap<>();

    protected HistoryManager historyManager = Managers.getHistoryDefault();

    protected static int taskId = 1;

    @Override
    public void addTask(Task task) {
        if (tasks.containsValue(task)) return;
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epics.containsValue(epic)) return;
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicState(epic);
    }

    @Override
    public void addSubtask(Subtask subtask, int epicId) {
        if (subtasks.containsValue(subtask)) return;
        Epic epic = epics.get(epicId);
        if (epic == null) return;
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicState(epic);
    }

    //Методы для каждого из типов задач
    //2a - Получение списка всех задач
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    //2b - Удаление всех задач
    @Override
    public void removeTasks() {
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeSubtasksId();
        }
    }

    //2c - Получение задачи по идентификатору
    @Override
    public Task getTaskById(int id) {
        if (tasks.get(id) == null) return null;
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.get(id) == null) return null;
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.get(id) == null) return null;
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    //2e - Обновление
    @Override
    public void updateTask(Task task) {
        if (tasks.containsValue(task)) return;
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsValue(epic)) return;
        removeAllEpicSubtasksId(epic.getId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsValue(subtask)) return;
        subtasks.put(subtask.getId(), subtask);
        updateEpicState(epics.get(subtask.getEpicId()));
    }

    //2f - Удаление задачи по идентификатору
    @Override
    public void removeTaskById(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        removeAllEpicSubtasksId(id);
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        historyManager.remove(id);
        if (subtasks.get(id) == null) {
            return;
        }
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getEpicSubtasksId().remove((Integer) id);
        subtasks.remove(id);
    }

    //3a - Получение списка всех подзадач определённого эпика
    @Override
    public List<Subtask> getEpicSubtasksById(int epicId) {
        if (epics.get(epicId) == null) {
            return null;
        }
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    private void removeAllEpicSubtasksId(int epicId) {
        List<Subtask> epicSubtasksId = getEpicSubtasksById(epicId);
        if (epicSubtasksId == null) {
            return;
        }
        for (Subtask subtask : epicSubtasksId) {
            historyManager.remove(subtask.getId());
            subtasks.remove(subtask.getId());
        }
    }

    private int generateId() {
        return taskId++;
    }

    private void updateEpicState(Epic epic) {
        if (epic.getEpicSubtasksId().isEmpty()) {
            epic.setState(State.NEW);
            return;
        }
        List<Subtask> epicSubtasks = getEpicSubtasksById(epic.getId());
        int counter = 0;
        State state = State.DONE;
        for (Subtask epicSubtask : epicSubtasks) {
            if (epicSubtask.getState() == State.DONE) {
                continue;
            }
            if (epicSubtask.getState() == State.NEW) {
                counter++;
            }
            state = State.IN_PROGRESS;
        }
        if (counter == epicSubtasks.size()) {
            epic.setState(State.NEW);
        } else {
            epic.setState(state);
        }
    }
}