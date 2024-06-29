package pw.pdm.pdmserver.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "notes")
@Getter
@Setter
public class notes {

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

    // Getters and setters
    // ...
}