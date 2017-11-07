package id.go.bps.user.impl;

import com.google.protobuf.Empty;
import id.go.bps.request.RequestID;
import id.go.bps.request.Response;
import id.go.bps.request.SearchQuery;
import id.go.bps.user.Position;
import id.go.bps.user.PositionServiceGrpc;
import id.go.bps.user.Server;
import id.go.bps.user.model.PositionModel;
import io.grpc.stub.StreamObserver;

import java.sql.SQLException;
import java.util.logging.Logger;

public class PositionServiceGrpcImpl extends PositionServiceGrpc.PositionServiceImplBase {
    private static final Logger logger = Logger.getLogger(PositionServiceGrpcImpl.class.getName());
    private final Server server;

    public PositionServiceGrpcImpl(Server server) {
        this.server = server;
    }

    private PositionModel _add(Position request) throws SQLException {
        PositionModel positionModel = new PositionModel()
                .setId(request.getId())
                .setName(request.getName());
        server.positionDao.createIfNotExists(positionModel);
        return positionModel;
    }

    private PositionModel _update(Position request) throws SQLException {
        PositionModel positionModel = new PositionModel()
                .setId(request.getId())
                .setName(request.getName());
        server.positionDao.createOrUpdate(positionModel);
        return positionModel;
    }

    @Override
    public void list(Empty request, StreamObserver<Position> responseObserver) {
        try {
            for(PositionModel positionModel: server.positionDao.queryForAll()) {
                responseObserver.onNext(_positionModel2Pb(positionModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void get(RequestID request, StreamObserver<Position> responseObserver) {
        try {
            PositionModel positionModel = server.positionDao.queryForId(request.getId());
            responseObserver.onNext(_positionModel2Pb(positionModel));
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void add(Position request, StreamObserver<Response> responseObserver) {
        try {
            if(request != null) {
                _add(request);
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void addGet(Position request, StreamObserver<Position> responseObserver) {
        try {
            if(request != null) {
                PositionModel positionModel = _add(request);
                responseObserver.onNext(_positionModel2Pb(positionModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void update(Position request, StreamObserver<Response> responseObserver) {
        try {
            if(request != null) {
                _update(request);
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void updateGet(Position request, StreamObserver<Position> responseObserver) {
        try {
            if(request != null) {
                PositionModel positionModel = _update(request);
                responseObserver.onNext(_positionModel2Pb(positionModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void delete(RequestID request, StreamObserver<Response> responseObserver) {
        try {
            int deletedRows = server.positionDao.deleteById(request.getId());
            if(deletedRows > 0) {

            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void search(SearchQuery request, StreamObserver<Position> responseObserver) {
        try {
            for(PositionModel positionModel: server.positionDao.queryBuilder().where()
                    .like("name", request.getQuery())
                    .query()) {
                responseObserver.onNext(_positionModel2Pb(positionModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    public static Position _positionModel2Pb(PositionModel positionModel) {
        Position position = Position.newBuilder()
                .setId(positionModel.getId())
                .setName(positionModel.getName())
                .build();
        return position;
    }

    public static PositionModel _pb2PositionModel(Position position) {
        PositionModel positionModel = new PositionModel()
                .setId(position.getId())
                .setName(position.getName());
        return positionModel;
    }
}
