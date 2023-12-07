package model;

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
        return "Subtask{" +
                "name='" + this.name +
                //'\'' +
                //", state=" + this.state +
                '}';
    }
}