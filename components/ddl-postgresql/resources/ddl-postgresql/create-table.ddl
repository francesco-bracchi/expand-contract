-- Creates a new real table (i.e. where to store data)
-- table: {{table-name}}
-- columns: {{columns}}

CREATE TABLE {{namespace | name}}.{{table-name | name}} ({%
  for col in columns %}
  {{col.name | name}} {{col.type | type}}{%
  if col.nullable? %} NULL{% else %} NOT NULL{% endif %}{%
  if col.unique? %} UNIQUE{% endif %}{%
  if col.primary-key? %} PRIMARY KEY{% endif %}{%
  if not forloop.last %},{% endif %}{% endfor %}
);
