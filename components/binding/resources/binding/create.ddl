-- -*- mode: SQL -*-

CREATE TABLE bindings
(id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
 project TEXT NOT NULL, env TEXT NOT NULL,
 conn TEXT NOT NULL,
 CONSTRAINT unique_env_per_project UNIQUE (project, env));
