package id.go.bps.user.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "position_model")
public class PositionModel {
    @DatabaseField(id = true, width = 36)
    String id;
    @DatabaseField(width = 255)
    String name;

    public String getId() {
        return id;
    }

    public PositionModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PositionModel setName(String name) {
        this.name = name;
        return this;
    }
}
