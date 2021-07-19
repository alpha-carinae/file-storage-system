package com.project.filestoragesystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetaData {

    public FileMetaData(String name) {
        this(name, 1);
    }

    public FileMetaData(String name, int version)  {
        this.name = name;
        this.version = version;
        this.dateModified = Timestamp.valueOf(LocalDateTime.now());
        this.id = generateId();
    }

    @JsonIgnore
    private String id;
    private String name;
    private Integer version;
    private String size;
    private Timestamp dateModified;

    // TODO: hash content of the file?
    private String generateId() {
        return DigestUtils.sha256Hex(String.format("%s%s", this.name, this.version));
    }


}
