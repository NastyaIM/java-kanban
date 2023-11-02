package model;

import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> epicSubtasks;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public HashMap<Integer, Subtask> getEpicSubtasks() {
        return epicSubtasks;
    }

    public void setEpicSubtasks(HashMap<Integer, Subtask> epicSubtasks) {
        this.epicSubtasks = epicSubtasks;
    }

    public void addSubtask(Subtask subtask) {
        this.epicSubtasks.put(subtask.getId(), subtask);
    }

    public void removeSubtasks() {
        epicSubtasks.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + this.getName() + '\'' +
                ", state=" + this.getState() +
                ", subtasks.length=" + epicSubtasks.size() +
                '}';
    }
}