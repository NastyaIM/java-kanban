package service;

import model.*;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path filePath;

    public FileBackedTasksManager(Path path) {
        this.filePath = path;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask, int epicId) {
        super.addSubtask(subtask, epicId);
        save();
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
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
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
    public List<Task> getHistory() {
        List<Task> history = super.getHistory();
        save();
        return history;
    }

    public void loadFromFile(Path filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toString()))){
            br.readLine();
            int id = 0;
            while(br.ready()) {
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
            if (historyLine != null){
                List<Integer> historyIds = getHistoryFromString(historyLine);
                for (Integer hId : historyIds) {
                    addTaskIdToHistory(hId);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи");
        }
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath.toString()))) {
            bw.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                bw.write(task.toString() + "\n");
            }
            for (Epic epic : getEpics()) {
                bw.write(epic.toString() + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                bw.write(subtask.toString() + "\n");
            }
            bw.write("\n");
            bw.write(convertHistoryToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения");
        }
    }

    private void addTaskToTaskList(Task task) {
        if (task instanceof Subtask) {
            subtasks.put(task.getId(), (Subtask) task);
        } else if (task instanceof Epic) {
            epics.put(task.getId(), (Epic) task);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    private void addTaskIdToHistory(Integer taskId) {
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
        switch (type) {
            case TASK:
                return new Task(id, name, description, state);
            case EPIC:
                Epic epic = new Epic(id, name, description);
                epic.setState(state);
                epic.setEpicSubtasksId(new ArrayList<>());
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(values[5]);
                Epic subEpic = epics.get(epicId);
                subEpic.addSubtaskId(id);
                return new Subtask(id, name, description, state, epicId);
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