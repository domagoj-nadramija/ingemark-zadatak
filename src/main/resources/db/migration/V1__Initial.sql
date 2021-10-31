CREATE TYPE order_status_type AS ENUM ('DRAFT', 'SUBMITTED');

CREATE TABLE webshop_product
(
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(10) UNIQUE CHECK ( length(code) = 10 ) NOT NULL,
    name            TEXT,
    price_hrk       NUMERIC CHECK ( price_hrk >= 0 ) NOT NULL,
    description     TEXT,
    is_available    BOOLEAN
);

CREATE TABLE webshop_customer
(
    id                BIGSERIAL PRIMARY KEY,
    first_name        TEXT,
    last_name         TEXT,
    email             TEXT

);

CREATE TABLE webshop_order
(
    id                BIGSERIAL PRIMARY KEY,
    customer_id       INTEGER NOT NULL,
    status            ORDER_STATUS_TYPE,
    price_hrk         NUMERIC,
    price_eur         NUMERIC,
    CONSTRAINT fk_customer
        FOREIGN KEY (customer_id)
            REFERENCES webshop_customer(id)
);

CREATE TABLE webshop_order_item
(
    id                BIGSERIAL PRIMARY KEY,
    order_id          INTEGER NOT NULL,
    product_id        INTEGER NOT NULL,
    quantity          INTEGER,
    CONSTRAINT fk_order
        FOREIGN KEY (order_id)
            REFERENCES webshop_order(id),
    CONSTRAINT fk_product
        FOREIGN KEY (product_id)
            REFERENCES webshop_product(id)
);
