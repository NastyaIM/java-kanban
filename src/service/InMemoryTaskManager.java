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
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Task> tasks = new HashMap<>();

    private HistoryManager historyManager = Managers.getHistoryDefault();

    private static int taskId = 0;

    @Override
    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicState(epic);
    }

    @Override
    public void addSubtask(Subtask subtask, int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }
        Epic epic = epics.get(epicId);
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
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    //2e - Обновление
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        removeAllEpicSubtasksId(epic.getId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicState(epics.get(subtask.getEpicId()));
    }

    //2f - Удаление задачи по идентификатору
    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        removeAllEpicSubtasksId(id);
        epics.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            return;
        }
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getEpicSubtasksId().remove((Integer) id);
        subtasks.remove(id);
    }

    //3a - Получение списка всех подзадач определённого эпика
    @Override
    public List<Subtask> getEpicSubtasksById(int epicId) {
        if (!epics.containsKey(epicId)) {
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
            subtasks.remove(subtask.getId());
        }
    }

    private int generateId() {
        return ++taskId;
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