package id.go.bps.user;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.query.In;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import id.go.bps.user.impl.*;
import id.go.bps.user.model.*;
import io.atomix.Atomix;
import io.atomix.AtomixClient;
import io.atomix.AtomixReplica;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.group.DistributedGroup;
import io.grpc.ServerBuilder;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private io.grpc.Server server;
    private int port;

    public Dao<PositionModel, String> positionDao;
    public Dao<RankModel, String> rankDao;
    public Dao<SectionModel, String> sectionDao;
    public Dao<UserModel, String> userDao;
    public Dao<UserTypeModel, String> userTypeDao;

    private Server(int port) {
        this.port = port;

        try {
            String databaseUrl = "jdbc:h2:mem:account";
            ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);

            // instantiate the dao
            positionDao = DaoManager.createDao(connectionSource, PositionModel.class);
            TableUtils.createTableIfNotExists(connectionSource, PositionModel.class);

            rankDao = DaoManager.createDao(connectionSource, RankModel.class);
            TableUtils.createTableIfNotExists(connectionSource, RankModel.class);

            sectionDao = DaoManager.createDao(connectionSource, SectionModel.class);
            TableUtils.createTableIfNotExists(connectionSource, SectionModel.class);

            userDao = DaoManager.createDao(connectionSource, UserModel.class);
            TableUtils.createTableIfNotExists(connectionSource, UserModel.class);

            userTypeDao = DaoManager.createDao(connectionSource, UserTypeModel.class);
            TableUtils.createTableIfNotExists(connectionSource, UserTypeModel.class);
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
    }

    private void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new RankServiceGrpcImpl(Server.this))
                .addService(new PositionServiceGrpcImpl(Server.this))
                .addService(new SectionServiceGrpcImpl(Server.this))
                .addService(new UserServiceGrpcImpl(Server.this))
                .addService(new UserTypeServiceGrpcImpl(Server.this))
                .build()
                .start();
        logger.info("User gRPC API Server is started, listening on " + port);
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        Options options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("p", "ports", true, "Set gRPC server port range with format \"start[-end]\"");
        options.addOption("P", "registry-port", true, "Set service registry server port");
        options.addOption("j", "join", true, "Join to cluster server with format host:port");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);
        HelpFormatter formatter = new HelpFormatter();

        if(cmd.hasOption("help")) {
            formatter.printHelp( "bps-user-grpc-api-server", options);
            return;
        }



        String portRange = cmd.getOptionValue("p", "50051");
        String[] startEnd = portRange.split("-");
        Integer serviceRegistryPort = null, startPort = null, endPort = null;
        try {
            serviceRegistryPort = Integer.valueOf(cmd.getOptionValue("P", "8701"));

            if (startEnd.length == 2) {
                startPort = Integer.valueOf(startEnd[0]);
                endPort = Integer.valueOf(startEnd[1]);
            } else {
                startPort = Integer.valueOf(startEnd[0]);
            }
        } catch (NumberFormatException e) {
            logger.severe(e.getMessage());
            return;
        }

        // Run single atomix server
        if (serviceRegistryPort == null) {
            logger.severe("No service registry server port defined !!!");
            return;
        }
        
        AtomixReplica replica = AtomixReplica.builder(new Address("0.0.0.0", serviceRegistryPort))
                .withTransport(new NettyTransport())
                .withStorage(Storage.builder()
                        .withDirectory(System.getProperty("user.dir") + "/logs/" + UUID.randomUUID().toString())
                        .build())
                .build();
        replica.bootstrap().join();

        // Run multiple GRPC servers
        List<Integer> ports = new ArrayList<Integer>();
        if (startPort != null) {
            if (endPort != null) {
                for(int p=startPort; p<=endPort; p++) {
                    ports.add(p);
                }
            } else {
                ports.add(startPort);
            }
        }

        if (ports.size() == 0) {
            logger.severe("No port(s) defined !!!");
            return;
        }

        List<Server> servers = new ArrayList<Server>();
        for(Integer port : ports) {
            servers.add( new Server(port));
        }

        for(Server server : servers) server.start();

        // Register gRPC service to service registry
        try {
            AtomixClient client = AtomixClient.builder().withTransport(new NettyTransport()).build();
            Atomix atomix = client.connect(new Address("0.0.0.0", serviceRegistryPort)).get();
            DistributedGroup group = atomix.getGroup("service-helloworld").get();
            // Add the address in metadata
            for(Server server : servers) {
                group.join(Collections.singletonMap("address",
                        new InetSocketAddress("0.0.0.0", server.port)));
            }
        } catch (ExecutionException e) {
            logger.severe(e.getMessage());
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                for(Server server : servers) server.stop();
                System.err.println("*** gRPC server shut down");

                System.err.println("*** shutting down service registry server since JVM is shutting down");
                replica.shutdown();
                System.err.println("*** Service registry server shut down");
            }
        });

        for(Server server : servers) server.blockUntilShutdown();
    }
}
