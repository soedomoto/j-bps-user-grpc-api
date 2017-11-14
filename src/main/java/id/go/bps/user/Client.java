package id.go.bps.user;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.util.RoundRobinLoadBalancerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.AtomixNameResolverFactory;
import java.util.concurrent.TimeUnit;


public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class.getName());

    private static ManagedChannel atomixChannel(String host, int port, String registryName) {
        ManagedChannel channel = ManagedChannelBuilder
                .forTarget(String.format("atomix://%s:%d/%s", host, port, registryName))
                .nameResolverFactory(new AtomixNameResolverFactory())
                .loadBalancerFactory(RoundRobinLoadBalancerFactory.getInstance())
                .usePlaintext(true)
                .build();
        return channel;
    }

    private static ManagedChannel directChannel(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext(true)
                .build();
        return channel;
    }

    public static void main(String[] args) throws Exception {
        ManagedChannel channel = atomixChannel("localhost", 8701, "service-user");

        for(int i=0; i<100; i++) {
            PositionServiceGrpc.PositionServiceStub stub = PositionServiceGrpc.newStub(channel);
            stub.list(Empty.newBuilder().build(), new StreamObserver<Position>() {
                @Override
                public void onNext(Position position) {
                    logger.info("Position : {}", position);
                }

                @Override
                public void onError(Throwable throwable) {
                    logger.error("Error : {}", throwable);
                }

                @Override
                public void onCompleted() {
                    logger.info("Complete");
                }
            });
        }

        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}