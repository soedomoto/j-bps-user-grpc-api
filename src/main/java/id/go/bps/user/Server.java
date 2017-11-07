package id.go.bps.user;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import id.go.bps.user.impl.*;
import id.go.bps.user.model.*;
import io.grpc.ServerBuilder;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.sql.SQLException;
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
            // this uses h2 by default but change to match your database
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
            e.printStackTrace();
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
        logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                Server.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        Options options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("p", "port", true, "Set port");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);
        HelpFormatter formatter = new HelpFormatter();

        if(cmd.hasOption("help")) {
            formatter.printHelp( "bps-user", options);
            return;
        }

        Integer port = 50051;
        if(cmd.hasOption("p")) {
            port = Integer.valueOf(cmd.getOptionValue("p"));
        }

        final Server server = new Server(port);
        server.start();
        server.blockUntilShutdown();
    }
}
