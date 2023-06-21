DROP TABLE IF EXISTS endpoint_hit;

CREATE TABLE IF NOT EXISTS endpoint_hit (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR(255) NOT NULL,
    uri VARCHAR(512) NOT NULL,
    ip VARCHAR(512) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_endpoint_hit PRIMARY KEY (id)
);