\c store;

create table if not exists total_revenue(
     id SERIAL PRIMARY KEY,
     revenue DOUBLE PRECISION,
     quantity INTEGER,
     transaction_time TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

create table if not exists product_sales(
    id SERIAL PRIMARY KEY,
    product_id VARCHAR(255) UNIQUE,
    product_name VARCHAR(255),
    quantity INTEGER,
    revenue DOUBLE PRECISION,
    transaction_time TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

create table if not exists store_sales(
    id SERIAL PRIMARY KEY,
    store_id VARCHAR(255) UNIQUE,
    quantity INTEGER,
    revenue DOUBLE PRECISION,
    transaction_time TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
