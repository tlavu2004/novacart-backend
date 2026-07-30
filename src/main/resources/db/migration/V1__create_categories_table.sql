CREATE TABLE categories (
    id BIGSERIAL,

    name VARCHAR(255) NOT NULL,
    description TEXT,
    slug VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ,

    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_categories_name ON categories (LOWER(name));
CREATE UNIQUE INDEX uk_categories_slug ON categories (LOWER(slug));
