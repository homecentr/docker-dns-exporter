import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class ExporterContainerShould {
    private static final ContainerController _controller = new ContainerController();

    @BeforeClass
    public static void before() {
        _controller.start();
    }

    @AfterClass
    public static void after() {
        _controller.stopIfExists();
    }

    @Test
    public void listenOnMetricsPort() throws IOException {
        URL root = new URL(String.format("http://%s:%d",
                _controller.getContainer().getContainerIpAddress(),
                _controller.getContainer().getMappedPort(9119)));

        HttpURLConnection connection = (HttpURLConnection)root.openConnection();
        connection.connect();

        assertEquals(200,  connection.getResponseCode());
    }
}