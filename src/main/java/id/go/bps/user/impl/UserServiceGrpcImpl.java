package id.go.bps.user.impl;

import com.google.protobuf.Empty;
import id.go.bps.request.RequestID;
import id.go.bps.request.Response;
import id.go.bps.request.SearchQuery;
import id.go.bps.user.Server;
import id.go.bps.user.User;
import id.go.bps.user.UserServiceGrpc;
import id.go.bps.user.model.UserModel;
import io.grpc.stub.StreamObserver;

import java.sql.SQLException;
import java.util.logging.Logger;

public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {
    private static final Logger logger = Logger.getLogger(UserServiceGrpcImpl.class.getName());
    private final Server server;

    public UserServiceGrpcImpl(Server server) {
        this.server = server;
    }

    private UserModel _add(User request) throws SQLException {
        UserModel userModel = _pb2UserModel(request);
        server.userDao.createIfNotExists(userModel);
        return userModel;
    }

    private UserModel _update(User request) throws SQLException {
        UserModel userModel = _pb2UserModel(request);
        server.userDao.createOrUpdate(userModel);
        return userModel;
    }

    @Override
    public void list(Empty request, StreamObserver<User> responseObserver) {
        try {
            for(UserModel userModel: server.userDao.queryForAll()) {
                responseObserver.onNext(_userModel2Pb(userModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void get(RequestID request, StreamObserver<User> responseObserver) {
        try {
            UserModel userModel = server.userDao.queryForId(request.getId());
            responseObserver.onNext(_userModel2Pb(userModel));
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void add(User request, StreamObserver<Response> responseObserver) {
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
    public void addGet(User request, StreamObserver<User> responseObserver) {
        try {
            if(request != null) {
                UserModel userModel = _add(request);
                responseObserver.onNext(_userModel2Pb(userModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void update(User request, StreamObserver<Response> responseObserver) {
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
    public void updateGet(User request, StreamObserver<User> responseObserver) {
        try {
            if(request != null) {
                UserModel userModel = _update(request);
                responseObserver.onNext(_userModel2Pb(userModel));
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
            int deletedRows = server.userDao.deleteById(request.getId());
            if(deletedRows > 0) {

            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void search(SearchQuery request, StreamObserver<User> responseObserver) {
        try {
            for(UserModel userModel: server.userDao.queryBuilder().where()
                    .like("code", request.getQuery())
                    .or().like("name", request.getQuery())
                    .query()) {
                responseObserver.onNext(_userModel2Pb(userModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    public static User _userModel2Pb(UserModel userModel) {
        User user = User.newBuilder()
                .setId(userModel.getId())
                .setNip(userModel.getNip())
                .setFullname(userModel.getFullname())
                .setColor(userModel.getColor())
                .setSection(SectionServiceGrpcImpl._sectionModel2Pb(userModel.getSection()))
                .setRank(RankServiceGrpcImpl._rankModel2Pb(userModel.getRank()))
                .setPosition(PositionServiceGrpcImpl._positionModel2Pb(userModel.getPosition()))
                .setSupervisor(_userModel2Pb(userModel.getSupervisor()))
                .setType(UserTypeServiceGrpcImpl._userTypeModel2Pb(userModel.getType()))
                .build();
        return user;
    }

    public static UserModel _pb2UserModel(User user) {
        UserModel userModel = new UserModel()
                .setId(user.getId())
                .setNip(user.getNip())
                .setPassword(user.getPassword())
                .setFullname(user.getFullname())
                .setColor(user.getColor())
                .setSection(SectionServiceGrpcImpl._pb2SectionModel(user.getSection()))
                .setRank(RankServiceGrpcImpl._pb2RankModel(user.getRank()))
                .setPosition(PositionServiceGrpcImpl._pb2PositionModel(user.getPosition()))
                .setSupervisor(_pb2UserModel(user.getSupervisor()))
                .setType(UserTypeServiceGrpcImpl._pb2UserTypeModel(user.getType()));
        return userModel;
    }
}
