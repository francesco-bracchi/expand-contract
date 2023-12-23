-- -*-SQL-*-

CREATE TABLE `project` (
       `name` text PRIMARY KEY NOT NULL,
       `description` TEXT);

CREATE TABLE `migration` (
       `project` BIGINT REFERENCES `project`(`name`),
       `version` BIGINT NOT NULL,
       `description` TEXT,
       `patch` TEXT NOT NULL,
       PRIMARY KEY (`datbase`, `version`),
       CONSTRAINT `version_is_positive` CHECK (`version` >= 0 ));

CREATE TABLE `log` (
       `id` BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
       `project` INT NOT NULL REFERENCES `project`(`name`),
       `action` TEXT NOT NULL,
       `data` JSON NOT NULL,
       `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP());

CREATE VIEW `current_state` (
       id,
       project_id,
       action,
       data,
       created_at
) AS SELECT
  l.id,
  l.project_id,
  l.action,
  l.data,
  l.created_at
FROM `log` l
LEFT JOIN `log` l0 ON l0.action = 'state_transition' AND
     l0.project_id = l.project_id AND
     l0.id > l.id
WHERE l.action = 'state_transition'
      AND l0.id IS NULL;

CREATE VIEW `latest_migration` (
       id,
       project_id,
       action,
       data,
       created_at
) AS SELECT
  l.id,
  l.project_id,
  l.action,
  l.data,
  l.created_at
FROM `log` l
LEFT JOIN `log` l0 ON l0.action = 'state_transition' AND
     (l0.data)."to" = 'normal' AND
     l0.project_id = l.project_id AND
     l0.id > l.id
WHERE l.action = 'state_transition'
      AND (l.data)."to" = 'normal'
      AND l0.id IS NULL;

CREATE VIEW `project_plus` (
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
FROM `project` d
LEFT JOIN `current_state` cs ON cs.`project_id` = d.`id`
LEFT JOIN `latest_migration` lm ON lm.`project_id` = d.`id`;
