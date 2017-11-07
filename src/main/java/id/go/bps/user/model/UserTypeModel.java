package id.go.bps.user.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user_type_model")
public class UserTypeModel {
    @DatabaseField(id = true, width = 36)
    String id;
    @DatabaseField(width = 10)
    String code;
    @DatabaseField(width = 255)
    String name;

    public String getId() {
        return id;
    }

    public UserTypeModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public UserTypeModel setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserTypeModel setName(String name) {
        this.name = name;
        return this;
    }
}
