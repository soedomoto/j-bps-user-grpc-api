package id.go.bps.user.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "rank_model")
public class RankModel {
    @DatabaseField(id = true, width = 36)
    String id;
    @DatabaseField(width = 10)
    String code;
    @DatabaseField(width = 255)
    String name;

    public String getId() {
        return id;
    }

    public RankModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public RankModel setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public RankModel setName(String name) {
        this.name = name;
        return this;
    }
}
