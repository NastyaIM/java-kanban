import model.Epic;
import model.State;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.fromFile(Paths.get("src/tasks.csv"));
        //TaskManager taskManager = Managers.getDefault();
        //System.out.println(taskManager.getEpics().size());
        //System.out.println(taskManager.getTasks().size());
        //System.out.println(taskManager.getSubtasks().size());
        //System.out.println(taskManager.getHistory().size());
        //System.out.println(taskManager.getEpicById(3).getEndTime());


        /*Subtask subtask44 = new Subtask("Sb4", "Dsb4", State.NEW, 8);
        taskManager.addSubtask(subtask44, 8);
        System.out.println(taskManager.getEpicById(8).getEpicSubtasksId().size());*/
        Task task1 = new Task("T1", "Dt1", State.NEW,
                Duration.ofMinutes(5), LocalDateTime.of(2024, 01, 8, 10, 11));
        Task task2 = new Task("T2", "Dt2", State.NEW,
                Duration.ofMinutes(5),  LocalDateTime.of(2024, 01, 9, 10, 13));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Task task3 = new Task("T3", "Dt1", State.NEW,
                Duration.ofMinutes(5), LocalDateTime.of(2024, 01, 8, 10, 14));
        taskManager.addTask(task3);

        Epic epic1 = new Epic("E1", "De1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Sb1", "Dsb1", State.NEW, Duration.ofMinutes(5),
                LocalDateTime.of(2024, 01, 10, 10, 13), epic1.getId());
        Subtask subtask2 = new Subtask("Sb2", "Dsb2", State.NEW, Duration.ofMinutes(5),
                LocalDateTime.of(2024, 01, 10, 11, 13), epic1.getId());
        taskManager.addSubtask(subtask1, epic1.getId());
        taskManager.addSubtask(subtask2, epic1.getId());

        Epic epic2 = new Epic("E2", "De2");
        taskManager.addEpic(epic2);

        Subtask subtask3 = new Subtask("Sb3", "Dsb3", State.DONE, Duration.ofMinutes(5),
                LocalDateTime.of(2024, 01, 11, 10, 22), epic2.getId());
        taskManager.addSubtask(subtask3, 6);

        Subtask subtask4 = new Subtask("Sb333", "Dsb3222", State.NEW,  epic2.getId());

        taskManager.addSubtask(subtask4, 6);

        taskManager.updateSubtask(new Subtask(subtask2.getId(), "Sb2", "Dsb2", State.IN_PROGRESS,
                Duration.ofMinutes(5),  LocalDateTime.of(2024, 01, 12, 11, 13), epic1.getId()));
        System.out.println(taskManager.getSubtasks());

        //Epic epic3 = new Epic("E3", "De3");
        //taskManager.addEpic(epic3);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println();

        //taskManager.removeSubtasks();

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println();

        //taskManager.removeSubtaskById(4);

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println();

        taskManager.getEpicById(3); //epic1
        taskManager.getEpicById(6); //epic2
        System.out.println(taskManager.getHistory()); //epic1 epic2

        taskManager.getTaskById(1); //task1
        System.out.println(taskManager.getHistory()); //epic1 epic2 task1
        System.out.println();
        taskManager.getEpicById(3); //epic1
        System.out.println(taskManager.getHistory()); //epic2 task1 epic1
        taskManager.getEpicById(6); //epic2
        System.out.println(taskManager.getHistory()); //task1 epic1 epic2
        taskManager.getEpicById(3); //epic1
        System.out.println(taskManager.getHistory()); //task1 epic2 epic1
        taskManager.getSubtaskById(5); //epic2
        System.out.println(taskManager.getHistory()); //task1 epic2 epic1 subtask2

        System.out.println();
        taskManager.removeEpicById(3);
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(1); //task1
        System.out.println(taskManager.getHistory()); //epic2(6) task1(1)

        taskManager.getSubtaskById(7); //task1
        System.out.println(taskManager.getTasks());

        taskManager.getTaskById(99); //такого нет
        System.out.println(taskManager.getHistory()); //epic2(6) task1(1)
        System.out.println(taskManager.getPrioritizedTasks());

        System.out.println("Поехали!");
        TaskManager taskManager1 = Managers.fromFile(Paths.get("src/tasks.csv"));
        System.out.println(taskManager1.getHistory());
    }
}