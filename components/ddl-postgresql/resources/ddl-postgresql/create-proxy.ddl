-- Creates an updateable/insertable view tha proxies to {{table/name | name}}
-- proxy namesapce:{{proxy/namespace}}
-- proxy: {{proxy/name}}
-- linked namespace: {{table/namespace}}
-- linked table: {{table/name}}
-- columns: {{column/names}}

CREATE VIEW {{proxy/namespace | name}}.{{proxy/name | name}} AS
SELECT {%
  for col in column/names %}{{col | name}}{%
  if not forloop.last %}, {%
  endif %}{%
  endfor %}
FROM {{table/namespace | name}}.{{table/name | name}};
