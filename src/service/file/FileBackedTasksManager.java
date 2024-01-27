package service.file;

import model.*;
import service.history.HistoryManager;
import service.InMemoryTaskManager;
import service.ManagerSaveException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path filePath;

    public FileBackedTasksManager() {
        filePath = Paths.get("src/tasks.csv");
    }
    public FileBackedTasksManager(Path path) {
        this.filePath = path;
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask, int epicId) {
        int id = super.addSubtask(subtask, epicId);
        save();
        return id;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> tasks = super.getTasks();
        save();
        return tasks;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> epics = super.getEpics();
        save();
        return epics;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> subtasks = super.getSubtasks();
        save();
        return subtasks;
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
       Subtask subtask = super.getSubtaskById(id);
       save();
       return subtask;
    }


    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public List<Subtask> getEpicSubtasksById(int epicId) {
        List<Subtask> subtasks = super.getEpicSubtasksById(epicId);
        save();
        return subtasks;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = super.getHistory();
        save();
        return history;
    }

    public void load() {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toString()))) {
            br.readLine();
            int id = 0;
            while (br.ready()) {
                String line = br.readLine();
                if (line.isEmpty()) {
                    break;
                }
                Task task = getTaskFromString(line);
                addTaskToTaskList(task);
                id = Math.max(id, task.getId());
            }
            taskId = ++id;
            String historyLine = br.readLine();
            if (historyLine != null) {
                List<Integer> historyIds = getHistoryFromString(historyLine);
                for (Integer hId : historyIds) {
                    addTaskIdToHistory(hId);
                }
            }
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка чтения", e);
        }
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath.toString()))) {
            bw.write("id,type,name,status,description,duration,start_time,epic\n");
            for (Task task : tasks.values()) {
                bw.write(task.toString() + "\n");
            }
            for (Epic epic : epics.values()) {
                bw.write(epic.toString() + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                bw.write(subtask.toString() + "\n");
            }
            bw.write("\n");
            bw.write(convertHistoryToString(historyManager));
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка сохранения", e);
        }
    }

    protected void addTaskToTaskList(Task task) {
        if (task instanceof Subtask) {
            subtasks.put(task.getId(), (Subtask) task);
            prioritizedTasks.add(task);
        } else if (task instanceof Epic) {
            epics.put(task.getId(), (Epic) task);
        } else {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    protected void addTaskIdToHistory(Integer taskId) {
        if (tasks.get(taskId) != null) {
            historyManager.add(tasks.get(taskId));
        } else if (epics.get(taskId) != null) {
            historyManager.add(epics.get(taskId));
        } else {
            historyManager.add(subtasks.get(taskId));
        }
    }

    private Task getTaskFromString(String value) {
        String[] values = value.split(",");
        TaskType type = TaskType.valueOf(values[1]);
        int id = Integer.parseInt(values[0]);
        String name = values[2];
        State state = State.valueOf(values[3]);
        String description = values[4];
        Duration duration = Duration.parse(values[5]);
        LocalDateTime startTime = LocalDateTime.parse(values[6], DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        switch (type) {
            case TASK:
                return new Task(id, name, description, state, duration, startTime);
            case EPIC:
                Epic epic = new Epic(id, name, description);
                epic.setState(state);
                epic.setSubtasksIds(new ArrayList<>());
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                for (Subtask subtask : subtasks.values()) {
                    if (subtask.getEpicId() == epic.getId()) {
                        epic.addSubtaskId(subtask.getId());
                    }
                }
                return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(id, name, description, state, duration, startTime);
                int epicId = Integer.parseInt(values[7]);
                subtask.setEpicId(epicId);
                Epic subEpic = epics.get(epicId);
                if (subEpic != null) {
                    subEpic.addSubtaskId(id);
                }
                return subtask;
            default:
                return null;
        }
    }

    private static String convertHistoryToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory()) {
            sb.append(task.getId());
            sb.append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private List<Integer> getHistoryFromString(String value) {
        String[] historyIds = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String id : historyIds) {
            history.add(Integer.parseInt(id));
        }
        return history;
    }
}