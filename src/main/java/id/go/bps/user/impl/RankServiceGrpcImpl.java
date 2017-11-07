package id.go.bps.user.impl;

import com.google.protobuf.Empty;
import id.go.bps.request.RequestID;
import id.go.bps.request.Response;
import id.go.bps.request.SearchQuery;
import id.go.bps.user.Rank;
import id.go.bps.user.RankServiceGrpc;
import id.go.bps.user.Server;
import id.go.bps.user.model.RankModel;
import io.grpc.stub.StreamObserver;

import java.sql.SQLException;
import java.util.logging.Logger;

public class RankServiceGrpcImpl extends RankServiceGrpc.RankServiceImplBase {
    private static final Logger logger = Logger.getLogger(RankServiceGrpcImpl.class.getName());
    private final Server server;

    public RankServiceGrpcImpl(Server server) {
        this.server = server;
    }

    private RankModel _add(Rank request) throws SQLException {
        RankModel rankModel = new RankModel()
                .setId(request.getId())
                .setCode(request.getCode())
                .setName(request.getName());
        server.rankDao.createIfNotExists(rankModel);
        return rankModel;
    }

    private RankModel _update(Rank request) throws SQLException {
        RankModel rankModel = new RankModel()
                .setId(request.getId())
                .setCode(request.getCode())
                .setName(request.getName());
        server.rankDao.createOrUpdate(rankModel);
        return rankModel;
    }

    @Override
    public void list(Empty request, StreamObserver<Rank> responseObserver) {
        try {
            for(RankModel rankModel: server.rankDao.queryForAll()) {
                responseObserver.onNext(_rankModel2Pb(rankModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void get(RequestID request, StreamObserver<Rank> responseObserver) {
        try {
            RankModel rankModel = server.rankDao.queryForId(request.getId());
            responseObserver.onNext(_rankModel2Pb(rankModel));
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void add(Rank request, StreamObserver<Response> responseObserver) {
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
    public void addGet(Rank request, StreamObserver<Rank> responseObserver) {
        try {
            if(request != null) {
                RankModel rankModel = _add(request);
                responseObserver.onNext(_rankModel2Pb(rankModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void update(Rank request, StreamObserver<Response> responseObserver) {
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
    public void updateGet(Rank request, StreamObserver<Rank> responseObserver) {
        try {
            if(request != null) {
                RankModel rankModel = _update(request);
                responseObserver.onNext(_rankModel2Pb(rankModel));
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
            int deletedRows = server.rankDao.deleteById(request.getId());
            if(deletedRows > 0) {

            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void search(SearchQuery request, StreamObserver<Rank> responseObserver) {
        try {
            for(RankModel rankModel: server.rankDao.queryBuilder().where()
                    .like("code", request.getQuery())
                    .or().like("name", request.getQuery())
                    .query()) {
                responseObserver.onNext(_rankModel2Pb(rankModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    public static Rank _rankModel2Pb(RankModel rankModel) {
        Rank rank = Rank.newBuilder()
                .setId(rankModel.getId())
                .setCode(rankModel.getCode())
                .setName(rankModel.getName())
                .build();
        return rank;
    }

    public static RankModel _pb2RankModel(Rank rank) {
        RankModel rankModel = new RankModel()
                .setId(rank.getId())
                .setCode(rank.getCode())
                .setName(rank.getName());
        return rankModel;
    }
}
