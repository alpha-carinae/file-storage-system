CREATE TABLE IF NOT EXISTS file (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(1024),
    version INT,
    data LONGBLOB,
    date_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
