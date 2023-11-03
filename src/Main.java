import model.Epic;
import model.State;
import model.Subtask;
import model.Task;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("T1", "Dt1", State.NEW);
        Task task2 = new Task("T2", "Dt2", State.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("E1", "De1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Sb1", "Dsb1", State.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Sb2", "Dsb2", State.NEW, epic1.getId());
        taskManager.addSubtask(subtask1, epic1.getId());
        taskManager.addSubtask(subtask2, epic1.getId());

        Epic epic2 = new Epic("E2", "De2");
        taskManager.addEpic(epic2);

        Subtask subtask3 = new Subtask("Sb3", "Dsb3", State.DONE, epic2.getId());
        taskManager.addSubtask(subtask3, epic2.getId());

        taskManager.updateSubtask(new Subtask(subtask2.getId(), "Sb2",
                "Dsb2", State.IN_PROGRESS, epic1.getId()));

        Epic epic3 = new Epic(epic1.getId(), "E3", "De3");

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println();

        taskManager.removeSubtasks();

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println();

        taskManager.removeSubtaskById(4);

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println();

        System.out.println("Поехали!");
    }
}