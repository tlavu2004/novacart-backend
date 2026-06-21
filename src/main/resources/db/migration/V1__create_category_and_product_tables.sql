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

CREATE UNIQUE INDEX uk_categories_name
    ON categories (LOWER(name));
CREATE UNIQUE INDEX uk_categories_slug
    ON categories (LOWER(slug));

CREATE TABLE products (
    id BIGSERIAL,

    name VARCHAR(255) NOT NULL,
    description TEXT,
    slug VARCHAR(255) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT', -- DRAFT | ACTIVE | INACTIVE
    category_id BIGINT NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ,

    CONSTRAINT pk_products PRIMARY KEY (id),

    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id)
        REFERENCES categories (id),

    CONSTRAINT chk_products_price
        CHECK (price > 0),
    CONSTRAINT chk_products_stock
        CHECK (stock_quantity >= 0)
);

CREATE UNIQUE INDEX uk_products_name
    ON products (LOWER(name));
CREATE UNIQUE INDEX uk_products_slug
    ON products (LOWER(slug));

CREATE INDEX idx_products_status
    ON products (status);
CREATE INDEX idx_products_category_id
    ON products (category_id);
