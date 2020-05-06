import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.nio.file.Paths;

public class ContainerController {
    private static final Logger logger = LoggerFactory.getLogger(ContainerController.class);

    private GenericContainer _container;
    private GenericContainer _dnsContainer;

    public void start() {
        Network network = Network.newNetwork();

        _dnsContainer = startDnsContainer(network);
        _container = startExporterContainer(network, _dnsContainer.getContainerIpAddress());
    }

    public void stopIfExists() {
        if(_container != null) {
            _container.stop();
            _container.close();
        }
    }

    protected GenericContainer getContainer() {
        return _container;
    }

    private GenericContainer startDnsContainer(Network network) {
        GenericContainer result = new GenericContainer<>("homecentr/dns")
                .withFileSystemBind(Paths.get(System.getProperty("user.dir"), "..", "example/bind").normalize().toString(), "/config")
                .withNetwork(network)
                .waitingFor(Wait.forHealthcheck());

        result.start();
        result.followOutput(new Slf4jLogConsumer(logger));

        return result;
    }

    private GenericContainer startExporterContainer(Network network, String dnsIpAddress) {
        String dockerImageTag = System.getProperty("image_tag");

        logger.info("Tested Docker image tag: {}", dockerImageTag);

        GenericContainer result = new GenericContainer<>(dockerImageTag)
                .withNetwork(network)
                .withEnv("EXPORTER_ARGS", String.format("-bind.stats-url http://%s:8888", dnsIpAddress))
                .waitingFor(Wait.forHealthcheck());

        result.start();
        result.followOutput(new Slf4jLogConsumer(logger));

        return result;
    }
}