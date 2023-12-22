-- -*-SQL-*-

CREATE TABLE `database` (
       `name` text PRIMARY KEY NOT NULL,
       `description` TEXT);

CREATE TABLE `migration` (
       `database` BIGINT REFERENCES `database`(`name`),
       `version` BIGINT NOT NULL,
       `description` TEXT,
       `patch` TEXT NOT NULL,
       PRIMARY KEY (`datbase`, `version`),
       CONSTRAINT `version_is_positive` CHECK (`version` >= 0 ));

CREATE TABLE `log` (
       `id` BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
       `database` INT NOT NULL REFERENCES `database`(`name`),
       `action` TEXT NOT NULL,
       `data` JSON NOT NULL,
       `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP());

CREATE VIEW `current_state` (
       id,
       database_id,
       action,
       data,
       created_at
) AS SELECT
  l.id,
  l.database_id,
  l.action,
  l.data,
  l.created_at
FROM `log` l
LEFT JOIN `log` l0 ON l0.action = 'state_transition' AND
     l0.database_id = l.database_id AND
     l0.id > l.id
WHERE l.action = 'state_transition'
      AND l0.id IS NULL;

CREATE VIEW `latest_migration` (
       id,
       database_id,
       action,
       data,
       created_at
) AS SELECT
  l.id,
  l.database_id,
  l.action,
  l.data,
  l.created_at
FROM `log` l
LEFT JOIN `log` l0 ON l0.action = 'state_transition' AND
     (l0.data)."to" = 'normal' AND
     l0.database_id = l.database_id AND
     l0.id > l.id
WHERE l.action = 'state_transition'
      AND (l.data)."to" = 'normal'
      AND l0.id IS NULL;

CREATE VIEW `database_plus` (
  id,
  `name`,
  `description`,
  `created_at`,
  `to`,
  `latest_timestamp`,
  `migration`
) AS SELECT
  d.id,
  d.`name`,
  d.`description`,
  cs.created_at,
  (cs.data)."to",
  lm.created_at,
  (lm.data)."migration"
FROM `database` d
LEFT JOIN `current_state` cs ON cs.`database_id` = d.`id`
LEFT JOIN `latest_migration` lm ON lm.`database_id` = d.`id`;
