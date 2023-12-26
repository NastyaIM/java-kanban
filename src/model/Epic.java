package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        return this.id + ","
                + TaskType.EPIC + ","
                + this.name + ","
                + this.state + ","
                + this.description + ",";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(name, epic.name) &&
                Objects.equals(description, epic.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicSubtasksId);
    }
}