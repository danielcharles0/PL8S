-- DROP ROLE IF EXISTS webuser;
-- DROP OWNED BY webuser;

CREATE ROLE webuser LOGIN PASSWORD 'webuserpwd';

-- resources:
-- - https://www.postgresql.org/docs/current/errcodes-appendix.html
-- - https://www.postgresql.org/docs/16/ddl-priv.html
-- - https://www.postgresql.org/docs/16/sql-grant.html

GRANT USAGE ON SCHEMA Festival TO webuser;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA Festival TO webuser;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA Festival TO webuser;
