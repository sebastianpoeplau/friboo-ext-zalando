-- name: get-all-memories
SELECT id,
       text
  FROM {{db-prefix}}_data.memories;

-- name: get-memory
SELECT id,
       text
  FROM {{db-prefix}}_data.memories
 WHERE id = :id;

-- name: create-or-update-memory!
WITH memory_update AS (
     UPDATE {{db-prefix}}_data.memories
        SET text = :text
      WHERE id = :id
  RETURNING id
)
INSERT INTO {{db-prefix}}_data.memories (
            id,
            text
            )
     SELECT :id,
            :text
      WHERE NOT EXISTS(SELECT 1 FROM memory_update);

-- name: delete-memory!
DELETE FROM {{db-prefix}}_data.memories WHERE id = :id;
