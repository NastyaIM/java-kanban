package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> epicSubtasksId;

    public Epic(String name, String description) {
        super(name, description);
        epicSubtasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        epicSubtasksId = new ArrayList<>();
    }

    public List<Integer> getEpicSubtasksId() {
        return epicSubtasksId;
    }

    public void setEpicSubtasksId(ArrayList<Integer> epicSubtasksId) {
        this.epicSubtasksId = epicSubtasksId;
    }

    public void addSubtaskId(int id) {
        epicSubtasksId.add(id);
    }

    public void removeSubtasksId() {
        epicSubtasksId.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + this.name + '\'' +
                ", state=" + this.state +
                ", subtasks.length=" + epicSubtasksId.size() +
                '}';
    }
}