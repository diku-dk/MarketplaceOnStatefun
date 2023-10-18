//package Infra;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.testcontainers.Testcontainers;
//import org.testcontainers.containers.BindMode;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.containers.KafkaContainer;
//import org.testcontainers.containers.Network;
//import org.testcontainers.containers.output.Slf4jLogConsumer;
//import org.testcontainers.utility.DockerImageName;
//
///**
// * This class has been inspired by the following source code:
// * https://github.com/apache/flink-statefun-playground/blob/main/java/showcase/src/main/java/org/apache/flink/statefun/playground/java/showcase/part6/serving/GreeterAppServer.java
// * If you are using MAC:
// * https://stackoverflow.com/questions/61108655/test-container-test-cases-are-failing-due-to-could-not-find-a-valid-docker-envi
// */
//public final class StatefunRuntime {
//
//    private static final Logger LOG =
//            LoggerFactory.getLogger(StatefunRuntime.class);
//
//    private static final Network NETWORK = Network.newNetwork();
//
//    private static final KafkaContainer KAFKA = kafkaContainer(NETWORK);
//    private static final GenericContainer<?> STATEFUN_MANAGER = managerContainer(NETWORK);
//    private static final GenericContainer<?> STATEFUN_WORKER =
//            workerContainer(NETWORK).dependsOn(STATEFUN_MANAGER, KAFKA);
//    private static final GenericContainer<?> KAFKA_JSON_PRODUCER =
//            jsonProducerContainer(NETWORK).dependsOn(STATEFUN_WORKER, KAFKA);
//
//    public static void main(String[] args) throws Exception {
//        try {
//            KAFKA.start();
//            STATEFUN_MANAGER.start();
//
//            Testcontainers.exposeHostPorts(TestServer.PORT);
//            STATEFUN_WORKER.start();
//            KAFKA_JSON_PRODUCER.start();
//
//            sleep();
//        } finally {
//            KAFKA_JSON_PRODUCER.stop();
//            STATEFUN_WORKER.stop();
//            STATEFUN_MANAGER.stop();
//            KAFKA.stop();
//        }
//    }
//
//    private static KafkaContainer kafkaContainer(Network network) {
//        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"))
//                .withNetwork(network)
//                .withNetworkAliases("kafka");
//    }
//
//    private static GenericContainer<?> managerContainer(Network network) {
//        return new GenericContainer<>(DockerImageName.parse("apache/flink-statefun:3.3.0"))
//                .withNetwork(network)
//                .withNetworkAliases("statefun-manager")
//                .withEnv("ROLE", "master")
//                .withEnv("MASTER_HOST", "statefun-manager")
//                .withExposedPorts(8081)
//                .withLogConsumer(new Slf4jLogConsumer(LOG))
//                .withClasspathResourceMapping(
//                        "module.yaml", "/opt/statefun/modules/greeter/module.yaml", BindMode.READ_ONLY);
//    }
//
//    private static GenericContainer<?> workerContainer(Network network) {
//        return new GenericContainer<>(DockerImageName.parse("apache/flink-statefun:3.3.0"))
//                .withNetwork(network)
//                .withNetworkAliases("statefun-worker")
//                .withEnv("ROLE", "worker")
//                .withEnv("MASTER_HOST", "statefun-manager")
//                .withClasspathResourceMapping(
//                        "module.yaml", "/opt/statefun/modules/greeter/module.yaml", BindMode.READ_ONLY);
//    }
//
//    private static GenericContainer<?> jsonProducerContainer(Network network) {
//        return new GenericContainer<>(
//                DockerImageName.parse("ververica/statefun-playground-producer:latest"))
//                .withNetwork(network)
//                .withClasspathResourceMapping(
//                        "user-logins.txt", "/opt/statefun/user-logins.txt", BindMode.READ_ONLY)
//                .withEnv("APP_PATH", "/opt/statefun/user-logins.txt")
//                .withEnv("APP_KAFKA_HOST", "kafka:9092")
//                .withEnv("APP_KAFKA_TOPIC", "user-logins")
//                .withEnv("APP_JSON_PATH", "user_id");
//    }
//
//    private static void sleep() throws Exception {
//        while (true) {
//            Thread.sleep(10000);
//        }
//    }
//}
