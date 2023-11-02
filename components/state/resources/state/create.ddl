CREATE TABLE `databases` (
       `id` INT PRIMARY KEY NOT NULL,
       `name` text UNIQUE NOT NULL,
       `description` TEXT);

CREATE TABLE `migrations` (
       `id` INT PRIMARY KEY NOT NULL,
       `database_id` INT REFERENCES databases(id),
       `version` INT NOT NULL,
       `description` TEXT,
       `patch` JSON NOT NULL,
       CONSTRAINT `migration_uniquenes` UNIQUE (`database_id`, `version`),
       CONSTRAINT `version_is_positive` CHECK (`version` >= 0 ));

CREATE TABLE `logs` (
       `id` INT PRIMARY KEY AUTOINCREMENT NOT NULL,
       `database_id` INT NOT NULL REFERENCES `databases`(id),
       `action` TEXT NOT NULL,
       `data` JSON NOT NULL,
       `created_at DATETIME NOT NULL DEFAULT NOW`);

CREATE VIEW `current_states` (
       id,
       database_id
       action,
       data,
       created_at
) AS SELECT
  l.id,
  l.database_id,
  l.action,
  l.data,
  l.created_at
FROM `logs` l
LEFT JOIN `logs` l0 ON l0.action = 'state_transition' AND
     l0.database_id = l.database_id AND
     l0.id > l.id
WHERE l.action = 'state_transition'
      AND l0.id IS NULL;

CREATE VIEW `latest_migrations` (
       id,
       database_id
       action,
       data,
       created_at
) AS SELECT
  l.id,
  l.database_id,
  l.action,
  l.data,
  l.created_at
FROM `logs` l
LEFT JOIN `logs` l0 ON l0.action = 'state_transition' AND
     l0.data ->> 'to' = 'normal' AND
     l0.database_id = l.database_id AND
     l0.id > l.id
WHERE l.action = 'state_transition'
      AND l.data ->> 'to' = 'normal'
      AND l0.id IS NULL

CREATE VIEW `database+` (
) AS SELECT
  d.id,
  d.`name`,
  d.`description`,
  cs.created_at,
  cs.data ->> 'to',
  lm.created_at,
  lm.data ->> 'migration'
FROM `database` d
LEFT JOIN `current_states` cs ON cs.`database_id` = d.`id`
LEFT JOIN `latest_migrations` lm ON lm.`database_id` = d.`id`;
