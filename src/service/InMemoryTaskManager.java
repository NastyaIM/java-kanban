package service;

import model.Const;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>();
    protected HistoryManager historyManager = Managers.getHistoryDefault();
    protected int taskId = 1;


    @Override
    public int addTask(Task task) {
        if (tasks.containsValue(task)) return -1;
        if (!checkFreeTime(task)) return -1;
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        if (epics.containsValue(epic)) return -1;
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        epic.update(getEpicSubtasksById(epic.getId()));
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask, int epicId) {
        if (subtasks.containsValue(subtask)) return -1;
        if (!checkFreeTime(subtask)) return -1;
        Epic epic = epics.get(epicId);
        if (epic == null) return -1;
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        epic.update(getEpicSubtasksById(epic.getId()));
        prioritizedTasks.add(subtask);
        return subtask.getId();
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

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    //2b - Удаление всех задач
    @Override
    public void removeTasks() {
        for (Task task : getTasks()) {
            removeTaskFromPrioritizedTasks(task);
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Subtask subtask : getSubtasks()) {
            removeTaskFromPrioritizedTasks(subtask);
            historyManager.remove(subtask.getId());
        }
        for (Epic epic : getEpics()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Subtask subtask : getSubtasks()) {
            removeTaskFromPrioritizedTasks(subtask);
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeSubtasksIds();
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
    public Task updateTask(Task task) {
        if (tasks.containsValue(task)) return null;
        removeTaskFromPrioritizedTasks(tasks.get(task.getId()));
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsValue(epic)) return null;
        removeAllEpicSubtasksId(epic.getId());
        epics.put(epic.getId(), epic);
        prioritizedTasks.addAll(getEpicSubtasksById(epic.getId()));
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtasks.containsValue(subtask)) return null;
        removeTaskFromPrioritizedTasks(subtasks.get(subtask.getId()));
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.update(getEpicSubtasksById(epic.getId()));
        prioritizedTasks.add(subtask);
        return subtask;
    }

    //2f - Удаление задачи по идентификатору
    @Override
    public void removeTaskById(int id) {
        historyManager.remove(id);
        removeTaskFromPrioritizedTasks(tasks.get(id));
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
        if (subtasks.get(id) == null) return;
        removeTaskFromPrioritizedTasks(subtasks.get(id));
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getSubtasksIds().remove((Integer) id);
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

    private int generateId() {
        return taskId++;
    }

    private void removeAllEpicSubtasksId(int epicId) {
        List<Subtask> epicSubtasksId = getEpicSubtasksById(epicId);
        if (epicSubtasksId == null) {
            return;
        }
        for (Subtask subtask : epicSubtasksId) {
            removeTaskFromPrioritizedTasks(subtask);
            historyManager.remove(subtask.getId());
            subtasks.remove(subtask.getId());
        }
    }

    private boolean isFreeTime(LocalDateTime startTime, LocalDateTime endTime) {
        for (Task task : prioritizedTasks) {
            if (task.getStartTime().isEqual(startTime) || task.getEndTime().isEqual(endTime)) {
                return false;
            }
            if (task.getStartTime().isBefore(startTime) && (task.getEndTime().isAfter(endTime))) {
                return false;
            }
            if (task.getStartTime().isAfter(startTime) && (task.getEndTime().isBefore(endTime))) {
                return false;
            }
            if (task.getStartTime().isAfter(startTime) && (task.getEndTime().isAfter(endTime))
                    && task.getStartTime().isBefore(endTime)) {
                return false;
            }
            if (task.getStartTime().isBefore(startTime) && (task.getEndTime().isBefore(endTime))
                    && task.getEndTime().isAfter(startTime)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkFreeTime(Task task) {
        if (!task.getStartTime().equals(Const.DEFAULT_START_TIME)) {
            boolean isFreeTime = isFreeTime(task.getStartTime(), task.getEndTime());
            if (!isFreeTime) {
                System.out.println("Время занято");
                return false;
            }
        }
        return true;
    }

    private void removeTaskFromPrioritizedTasks(Task task) {
        prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.equals(task));
    }
}