package id.go.bps.user.impl;

import com.google.protobuf.Empty;
import id.go.bps.request.RequestID;
import id.go.bps.request.Response;
import id.go.bps.request.SearchQuery;
import id.go.bps.user.UserType;
import id.go.bps.user.UserTypeServiceGrpc;
import id.go.bps.user.Server;
import id.go.bps.user.model.UserTypeModel;
import io.grpc.stub.StreamObserver;

import java.sql.SQLException;
import java.util.logging.Logger;

public class UserTypeServiceGrpcImpl extends UserTypeServiceGrpc.UserTypeServiceImplBase {
    private static final Logger logger = Logger.getLogger(UserTypeServiceGrpcImpl.class.getName());
    private final Server server;

    public UserTypeServiceGrpcImpl(Server server) {
        this.server = server;
    }

    private UserTypeModel _add(UserType request) throws SQLException {
        UserTypeModel userTypeModel = new UserTypeModel()
                .setId(request.getId())
                .setCode(request.getCode())
                .setName(request.getName());
        server.userTypeDao.createIfNotExists(userTypeModel);
        return userTypeModel;
    }

    private UserTypeModel _update(UserType request) throws SQLException {
        UserTypeModel userTypeModel = new UserTypeModel()
                .setId(request.getId())
                .setCode(request.getCode())
                .setName(request.getName());
        server.userTypeDao.createOrUpdate(userTypeModel);
        return userTypeModel;
    }

    @Override
    public void list(Empty request, StreamObserver<UserType> responseObserver) {
        try {
            for(UserTypeModel userTypeModel: server.userTypeDao.queryForAll()) {
                responseObserver.onNext(_userTypeModel2Pb(userTypeModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void get(RequestID request, StreamObserver<UserType> responseObserver) {
        try {
            UserTypeModel userTypeModel = server.userTypeDao.queryForId(request.getId());
            responseObserver.onNext(_userTypeModel2Pb(userTypeModel));
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void add(UserType request, StreamObserver<Response> responseObserver) {
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
    public void addGet(UserType request, StreamObserver<UserType> responseObserver) {
        try {
            if(request != null) {
                UserTypeModel userTypeModel = _add(request);
                responseObserver.onNext(_userTypeModel2Pb(userTypeModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void update(UserType request, StreamObserver<Response> responseObserver) {
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
    public void updateGet(UserType request, StreamObserver<UserType> responseObserver) {
        try {
            if(request != null) {
                UserTypeModel userTypeModel = _update(request);
                responseObserver.onNext(_userTypeModel2Pb(userTypeModel));
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
            int deletedRows = server.userTypeDao.deleteById(request.getId());
            if(deletedRows > 0) {

            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void search(SearchQuery request, StreamObserver<UserType> responseObserver) {
        try {
            for(UserTypeModel userTypeModel: server.userTypeDao.queryBuilder().where()
                    .like("code", request.getQuery())
                    .or().like("name", request.getQuery())
                    .query()) {
                responseObserver.onNext(_userTypeModel2Pb(userTypeModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    public static UserType _userTypeModel2Pb(UserTypeModel userTypeModel) {
        UserType userType = UserType.newBuilder()
                .setId(userTypeModel.getId())
                .setCode(userTypeModel.getCode())
                .setName(userTypeModel.getName())
                .build();
        return userType;
    }

    public static UserTypeModel _pb2UserTypeModel(UserType userType) {
        UserTypeModel userTypeModel = new UserTypeModel()
                .setId(userType.getId())
                .setName(userType.getName());
        return userTypeModel;
    }
}
