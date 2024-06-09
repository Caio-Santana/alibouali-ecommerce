-- Insert sample data into the category table
INSERT INTO category (id, description, name) VALUES
    (nextval('category_seq'), 'Electronics and gadgets', 'Electronics'),
    (nextval('category_seq'), 'Books and literature', 'Books'),
    (nextval('category_seq'), 'Home appliances and furniture', 'Home Appliances');

-- Insert sample data into the product table
INSERT INTO product (id, description, name, available_quantity, price, category_id) VALUES
    (nextval('product_seq'), 'A smartphone with 128GB storage', 'Smartphone', 100, 699.99, (SELECT id FROM category WHERE name = 'Electronics')),
    (nextval('product_seq'), 'A science fiction novel', 'Sci-Fi Book', 250, 15.99, (SELECT id FROM category WHERE name = 'Books')),
    (nextval('product_seq'), 'A high-efficiency washing machine', 'Washing Machine', 50, 499.99, (SELECT id FROM category WHERE name = 'Home Appliances')),
    (nextval('product_seq'), 'Noise-cancelling over-ear headphones', 'Headphones', 150, 199.99, (SELECT id FROM category WHERE name = 'Electronics')),
    (nextval('product_seq'), 'A fantasy novel', 'Fantasy Book', 200, 12.99, (SELECT id FROM category WHERE name = 'Books')),
    (nextval('product_seq'), 'A modern coffee maker', 'Coffee Maker', 75, 99.99, (SELECT id FROM category WHERE name = 'Home Appliances'));
