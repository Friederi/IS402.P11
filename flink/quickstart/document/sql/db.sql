\c store;

create table if not exists revenueandquantity(
     id SERIAL PRIMARY KEY,
     revenue DOUBLE PRECISION,
     quantity INTEGER,
     transaction_time TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

create table if not exists revenueofproduct(
    id SERIAL PRIMARY KEY,
    product_id VARCHAR(255) UNIQUE,
    revenue DOUBLE PRECISION,
    transaction_time TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

create table if not exists revenueofstore(
    id SERIAL PRIMARY KEY,
    store_id VARCHAR(255) UNIQUE,
    revenue DOUBLE PRECISION,
    transaction_time TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
