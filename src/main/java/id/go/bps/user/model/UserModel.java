package id.go.bps.user.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user_model")
public class UserModel {
    @DatabaseField(id = true, width = 36)
    String id;
    @DatabaseField(width = 18, canBeNull = true)
    String nip;
    @DatabaseField(width = 255, canBeNull = true)
    String password;
    @DatabaseField(width = 255, canBeNull = false)
    String fullname;
    @DatabaseField(width = 6, canBeNull = false)
    String color;
    @DatabaseField(columnName = "section_id", foreign = true, foreignAutoRefresh = true, width = 36, canBeNull = true)
    SectionModel section;
    @DatabaseField(columnName = "rank_id", foreign = true, foreignAutoRefresh = true, width = 36, canBeNull = true)
    RankModel rank;
    @DatabaseField(columnName = "position_id", foreign = true, foreignAutoRefresh = true, width = 36, canBeNull = false)
    PositionModel position;
    @DatabaseField(columnName = "supervisor_id", foreign = true, foreignAutoRefresh = true, width = 36, canBeNull = true)
    UserModel supervisor;
    @DatabaseField(columnName = "type_id", foreign = true, foreignAutoRefresh = true, width = 36, canBeNull = false)
    UserTypeModel type;

    public String getId() {
        return id;
    }

    public UserModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getNip() {
        return nip;
    }

    public UserModel setNip(String nip) {
        this.nip = nip;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserModel setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFullname() {
        return fullname;
    }

    public UserModel setFullname(String fullname) {
        this.fullname = fullname;
        return this;
    }

    public String getColor() {
        return color;
    }

    public UserModel setColor(String color) {
        this.color = color;
        return this;
    }

    public SectionModel getSection() {
        return section;
    }

    public UserModel setSection(SectionModel section) {
        this.section = section;
        return this;
    }

    public RankModel getRank() {
        return rank;
    }

    public UserModel setRank(RankModel rank) {
        this.rank = rank;
        return this;
    }

    public PositionModel getPosition() {
        return position;
    }

    public UserModel setPosition(PositionModel position) {
        this.position = position;
        return this;
    }

    public UserModel getSupervisor() {
        return supervisor;
    }

    public UserModel setSupervisor(UserModel supervisor) {
        this.supervisor = supervisor;
        return this;
    }

    public UserTypeModel getType() {
        return type;
    }

    public UserModel setType(UserTypeModel type) {
        this.type = type;
        return this;
    }
}
