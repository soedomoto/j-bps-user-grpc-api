package id.go.bps.user.impl;

import com.google.protobuf.Empty;
import id.go.bps.request.RequestID;
import id.go.bps.request.Response;
import id.go.bps.request.SearchQuery;
import id.go.bps.user.Section;
import id.go.bps.user.SectionServiceGrpc;
import id.go.bps.user.Server;
import id.go.bps.user.model.SectionModel;
import io.grpc.stub.StreamObserver;

import java.sql.SQLException;
import java.util.logging.Logger;

public class SectionServiceGrpcImpl extends SectionServiceGrpc.SectionServiceImplBase {
    private static final Logger logger = Logger.getLogger(SectionServiceGrpcImpl.class.getName());
    private final Server server;

    public SectionServiceGrpcImpl(Server server) {
        this.server = server;
    }

    private SectionModel _add(Section request) throws SQLException {
        SectionModel sectionModel = new SectionModel()
                .setId(request.getId())
                .setCode(request.getCode())
                .setName(request.getName());
        server.sectionDao.createIfNotExists(sectionModel);
        return sectionModel;
    }

    private SectionModel _update(Section request) throws SQLException {
        SectionModel sectionModel = new SectionModel()
                .setId(request.getId())
                .setCode(request.getCode())
                .setName(request.getName());
        server.sectionDao.createOrUpdate(sectionModel);
        return sectionModel;
    }

    @Override
    public void list(Empty request, StreamObserver<Section> responseObserver) {
        try {
            for(SectionModel sectionModel: server.sectionDao.queryForAll()) {
                responseObserver.onNext(_sectionModel2Pb(sectionModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void get(RequestID request, StreamObserver<Section> responseObserver) {
        try {
            SectionModel sectionModel = server.sectionDao.queryForId(request.getId());
            responseObserver.onNext(_sectionModel2Pb(sectionModel));
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void add(Section request, StreamObserver<Response> responseObserver) {
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
    public void addGet(Section request, StreamObserver<Section> responseObserver) {
        try {
            if(request != null) {
                SectionModel sectionModel = _add(request);
                responseObserver.onNext(_sectionModel2Pb(sectionModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void update(Section request, StreamObserver<Response> responseObserver) {
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
    public void updateGet(Section request, StreamObserver<Section> responseObserver) {
        try {
            if(request != null) {
                SectionModel sectionModel = _update(request);
                responseObserver.onNext(_sectionModel2Pb(sectionModel));
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
            int deletedRows = server.sectionDao.deleteById(request.getId());
            if(deletedRows > 0) {

            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void search(SearchQuery request, StreamObserver<Section> responseObserver) {
        try {
            for(SectionModel sectionModel: server.sectionDao.queryBuilder().where()
                    .like("code", request.getQuery())
                    .or().like("name", request.getQuery())
                    .query()) {
                responseObserver.onNext(_sectionModel2Pb(sectionModel));
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            responseObserver.onError(e);
        }

        responseObserver.onCompleted();
    }

    public static Section _sectionModel2Pb(SectionModel sectionModel) {
        Section section = Section.newBuilder()
                .setId(sectionModel.getId())
                .setCode(sectionModel.getCode())
                .setName(sectionModel.getName())
                .build();
        return section;
    }

    public static SectionModel _pb2SectionModel(Section section) {
        SectionModel sectionModel = new SectionModel()
                .setId(section.getId())
                .setCode(section.getCode())
                .setName(section.getName());
        return sectionModel;
    }
}
