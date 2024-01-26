package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import service.Managers;
import service.http.HttpTasksManager;
import service.http.KVServer;

import java.io.IOException;

class HttpTasksManagerTest extends TaskManagerTest<HttpTasksManager> {
    private KVServer server;

    @BeforeEach
    public void beforeEach() throws IOException {
        server = new KVServer();
        server.start();
        taskManager = Managers.fromServer();
        super.beforeEach();
    }

    @AfterEach
    public void afterEach() {
        server.stop();
    }
}