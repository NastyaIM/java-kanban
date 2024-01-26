package test;

import org.junit.jupiter.api.BeforeEach;
import service.InMemoryTaskManager;
import service.Managers;

import java.io.IOException;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() throws IOException {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
        super.beforeEach();
    }
}
