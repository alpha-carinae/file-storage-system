package com.project.filestoragesystem.repository;

import com.project.filestoragesystem.entity.FileMetaData;
import com.project.filestoragesystem.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class FileRepository {

    private final JdbcTemplate jdbcTemplate;

    public FileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean saveStreaming(FileMetaData fileMetaData, InputStream inputStream) {
        log.info("Saving '{}'...", fileMetaData.getName());

        int rowsAffected = jdbcTemplate.update("INSERT INTO file(id, name, version, data, date_modified) VALUES(?, ?, ?, ?, ?)",
                fileMetaData.getId(),
                fileMetaData.getName(),
                fileMetaData.getVersion(),
                inputStream,
                fileMetaData.getDateModified()
        );

        if (rowsAffected != 0) {
            log.info("'{}' saved.", fileMetaData.getName());
            return true;
        } else {
            log.warn("Couldn't save '{}'.", fileMetaData.getName());
            return false;
        }
    }

    public List<FileMetaData> getFileMetaDataList() {
        List<FileMetaData> result = new ArrayList<>();

        String sql = "SELECT name, version, length(data) as size, date_modified FROM file f1 " +
                "WHERE f1.version = (SELECT max(version) FROM file f2 WHERE f2.name = f1.name)";

        jdbcTemplate.query(sql, (ResultSetExtractor<Map>) resultSet -> {
            while (resultSet.next()) {
                FileMetaData fileMetaData = FileMetaData.builder()
                        .name(resultSet.getString("name"))
                        .version(resultSet.getInt("version"))
                        .size(Util.humanReadableByteCountSI(resultSet.getLong("size")))
                        .dateModified(resultSet.getTimestamp("date_modified"))
                        .build();

                result.add(fileMetaData);
            }
            resultSet.close();
            return null;
        });

        return result;
    }

    public boolean delete(String fileName) {
        String query = "DELETE FROM file WHERE name = ?";
        return jdbcTemplate.update(query, preparedStatement -> preparedStatement.setString(1, fileName)) > 0;
    }

    public Integer getLatestVersion(String fileName) {
        String sql = "SELECT max(version) FROM file WHERE name = ?";

        final Integer[] version = {null}; // effectively final variable
        Object[] args = {fileName};

        jdbcTemplate.query(sql,
                preparedStatement -> preparedStatement.setString(1, fileName),
                (ResultSetExtractor<Map>) resultSet -> {
                    while (resultSet.next()) {
                        version[0] = resultSet.getInt(1); // returns 0 even if it's null
                        if (resultSet.wasNull()) {
                            version[0] = null;
                        }
                    }
                    resultSet.close();
                    return null;
                });

        return version[0];
    }

    public byte[] getFileData(String fileName, int version) {
        String sql = "SELECT data FROM file WHERE name = ? AND version = ?";
        Object[] args = {fileName, version};

        return jdbcTemplate.queryForObject(sql, (resultSet, i) -> resultSet.getBytes(1), args);
    }
}
