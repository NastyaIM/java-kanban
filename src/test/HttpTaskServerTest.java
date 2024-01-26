package test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.State;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import service.http.HttpTaskServer;
import service.http.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTest {
    private final Gson gson = Managers.getGson();
    private HttpTaskServer taskServer;
    private TaskManager taskManager;
    private KVServer kvServer;
    protected Task task;
    protected int taskId;
    protected Epic epic;
    protected int epicId;
    protected Subtask subtask1;
    protected int subtask1Id;
    protected Subtask subtask2;
    protected int subtask2Id;

    @BeforeEach
    public void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = Managers.fromServer();
        taskServer = new HttpTaskServer(taskManager);


        task = new Task("T1", "Dt1", State.NEW);
        taskId = taskManager.addTask(task);
        epic = new Epic("E1", "De1");
        epicId = taskManager.addEpic(epic);
        subtask1 = new Subtask("Sb1", "Dsb1", State.NEW);
        subtask2 = new Subtask("Sb2", "Dsb2", State.NEW);
        subtask1Id = taskManager.addSubtask(subtask1, epicId);
        subtask2Id = taskManager.addSubtask(subtask2, epicId);
    }

    @AfterEach
    public void afterEach() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> actualTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertNotNull(actualTasks);
        assertEquals(1, actualTasks.size());
        assertEquals(task, actualTasks.get(0));
    }

    @Test
    public void getEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Epic> actualEpics = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
        }.getType());
        assertNotNull(actualEpics);
        assertEquals(1, actualEpics.size());
        assertEquals(epic, actualEpics.get(0));
    }

    @Test
    public void getSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Subtask> actualSubtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        assertNotNull(actualSubtasks);
        assertEquals(2, actualSubtasks.size());
        assertEquals(List.of(subtask1, subtask2), actualSubtasks);
    }

    @Test
    public void getTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task actualTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(actualTask);
        assertEquals(task, actualTask);
    }

    @Test
    public void getSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/?id=" + subtask1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Subtask actualSubtask = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(actualSubtask);
        assertEquals(subtask1, actualSubtask);
    }

    @Test
    public void getEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic/?id=" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic actualEpic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(actualEpic);
        assertEquals(epic, actualEpic);
    }

    @Test
    public void getEpicSubtasksById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/epic/?id=" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Subtask> actualSubtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        assertNotNull(actualSubtasks);
        assertEquals(2, actualSubtasks.size());
        assertEquals(List.of(subtask1, subtask2), actualSubtasks);
    }

    @Test
    public void getPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> actualPrioritizedTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        List<Integer> prioritizedTasksIds = actualPrioritizedTasks.stream().map(Task::getId).collect(Collectors.toList());
        assertNotNull(prioritizedTasksIds);
        assertEquals(3, prioritizedTasksIds.size());
        assertEquals(List.of(taskId, subtask1Id, subtask2Id), prioritizedTasksIds);
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        taskManager.getTaskById(taskId);
        taskManager.getSubtaskById(subtask1Id);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> actualHistory = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        List<Integer> historyIds = actualHistory.stream().map(Task::getId).collect(Collectors.toList());
        assertNotNull(historyIds);
        assertEquals(2, historyIds.size());
        assertEquals(List.of(taskId, subtask1Id), historyIds);
    }

    @Test
    public void postTask() throws IOException, InterruptedException {
        Task newTask = new Task("T2", "Dt2");
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newTask)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача добавлена", response.body());
        assertEquals(2, taskManager.getTasks().size());

        Task updatedTask = new Task(taskId, "t2", "D2", State.NEW);
        HttpRequest requestUpdate = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updatedTask)))
                .build();
        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseUpdate.statusCode());
        assertEquals("Задача обновлена", responseUpdate.body());
        assertEquals(2, taskManager.getTasks().size());
    }

    @Test
    public void postSubtask() throws IOException, InterruptedException {
        Subtask newSubtask = new Subtask("T2", "Dt2", State.NEW);
        newSubtask.setEpicId(epicId);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newSubtask)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Подзадача добавлена", response.body());
        assertEquals(3, taskManager.getSubtasks().size());

        Subtask updatedSubtask = new Subtask(subtask1Id, "t2", "Dt2", State.NEW);
        newSubtask.setEpicId(epicId);
        HttpRequest requestUpdate = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updatedSubtask)))
                .build();
        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseUpdate.statusCode());
        assertEquals("Подзадача обновлена", responseUpdate.body());
        assertEquals(3, taskManager.getSubtasks().size());
    }

    @Test
    public void postEpic() throws IOException, InterruptedException {
        Epic newEpic = new Epic("E2", "Dt2");
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newEpic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Эпик добавлен", response.body());
        assertEquals(2, taskManager.getEpics().size());

        Epic updatedEpic = new Epic(epicId, "E2Update", "Dt2");
        HttpRequest requestUpdate = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updatedEpic)))
                .build();
        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseUpdate.statusCode());
        assertEquals("Эпик обновлен", responseUpdate.body());
        assertEquals(2, taskManager.getEpics().size());
    }

    @Test
    public void deleteTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void deleteSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(List.of(), taskManager.getEpicById(epicId).getSubtasksIds());
    }

    @Test
    public void deleteEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void deleteTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void deleteSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/?id=" + subtask1Id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(1, taskManager.getSubtasks().size());
        assertEquals(List.of(subtask2Id), taskManager.getEpicById(epicId).getSubtasksIds());
    }

    @Test
    public void deleteEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic/?id=" + epicId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
    }
}