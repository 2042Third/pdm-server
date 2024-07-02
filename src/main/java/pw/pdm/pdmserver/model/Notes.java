package pw.pdm.pdmserver.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "notes")
public class Notes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer noteid;

    @Column(name = "userid")
    private Integer userId;

    @Column(name = "content")
    private String content;

    @Column(name = "h")
    private String h;

    @Column(name = "intgrh")
    private String intgrh;

    @Column(name = "time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private ZonedDateTime time;

    @Column(name = "update_time", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private ZonedDateTime updateTime;

    @Column(name = "heading")
    private String heading;

    @Column(name = "deleted", nullable = false)
    private Integer deleted = 0;

    // Getters and Setters
    public Integer getNoteid() {
        return noteid;
    }

    public void setNoteid(Integer noteid) {
        this.noteid = noteid;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public String getIntgrh() {
        return intgrh;
    }

    public void setIntgrh(String intgrh) {
        this.intgrh = intgrh;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
