package model;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, State state, int epicId) {
        super(name, description, state);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, State state, int epicId) {
        super(id, name, description, state);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return this.id + ","
                + TaskType.SUBTASK + ","
                + this.name + ","
                + this.state + ","
                + this.description
                + "," + this.epicId;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals((Subtask) o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}