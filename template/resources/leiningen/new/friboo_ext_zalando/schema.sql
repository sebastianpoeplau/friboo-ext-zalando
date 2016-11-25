CREATE SCHEMA {{db-prefix}}_data;
SET search_path TO {{db-prefix}}_data;

CREATE TABLE memories (
  id   TEXT NOT NULL PRIMARY KEY,
  text TEXT NOT NULL
);
