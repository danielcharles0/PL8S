-- Initialization of the database
DO $$
-- Declare variables for storing serial id's of cuisines, restaurants, dishes, ingredients
DECLARE
    admin INT; manager1 INT; manager2 INT; customer INT;
    r1 INT; r2 INT; r3 INT; r4 INT; r5 INT;
    d1 INT; d2 INT; d3 INT; d4 INT; d5 INT; d6 INT; d7 INT; d8 INT; d9 INT; d10 INT; d11 INT; d12 INT; d13 INT; d14 INT; d15 INT;
    i1 INT; i2 INT; i3 INT; i4 INT; i5 INT; i6 INT; i7 INT; i8 INT; i9 INT; i10 INT;
    i11 INT; i12 INT; i13 INT; i14 INT; i15 INT; i16 INT; i17 INT; i18 INT; i19 INT; i20 INT;
    pending INT; completed INT;

BEGIN
-- Insertion of users
INSERT INTO Festival."User"(user_id, email, password, name, surname, stripe_id, role) VALUES (DEFAULT, 'admin@pl8s.it', '7d916aaf1e4612eeaf2c06990a7d926657e3e2b2', 'Creator', 'Creator', 'cus_Pw8p2BY6bFX2mb', 'admin') RETURNING user_id INTO admin;
INSERT INTO Festival."User"(user_id, email, password, name, surname, stripe_id, role) VALUES (DEFAULT, 'manager.zero@studenti.unipd.it', '17c6231e2765066f4934a276cfdc52fecf44cb48', 'Gino', 'Bianchi', 'cus_Pw8qCUY88Wh20n', 'manager') RETURNING user_id INTO manager1;
INSERT INTO Festival."User"(user_id, email, password, name, surname, stripe_id, role) VALUES (DEFAULT, 'manager.one@studenti.unipd.it', '17c6231e2765066f4934a276cfdc52fecf44cb48', 'Joe', 'Bastianich', 'cus_QEmaDFiT1eNSoL', 'manager') RETURNING user_id INTO manager2;
INSERT INTO Festival."User"(user_id, email, password, name, surname, stripe_id, role) VALUES (DEFAULT, 'customer.zero@studenti.unipd.it', 'b306fbab67ee12266affbb5f9f9c0e19fd52390b', 'Mario', 'Rossi', 'cus_Pw8rJngyzWNrfy', 'customer') RETURNING user_id INTO customer;

-- Insertion of restaurants
INSERT INTO Festival.Restaurant(restaurant_id, name, description, manager, opening_at, closing_at) VALUES (DEFAULT, 'Festival', 'Festival Restaurants collector.', admin, '00:00:00', '23:59:59');
INSERT INTO Festival.Restaurant(restaurant_id, name, description, manager, opening_at, closing_at) VALUES (DEFAULT, 'Da Mimmo', 'Where the authentic flavors of Napoli come to life in every slice.', manager1, '16:00:00', '22:00:00') RETURNING restaurant_id INTO r1;
INSERT INTO Festival.Restaurant(restaurant_id, name, description, manager, opening_at, closing_at) VALUES (DEFAULT, 'La Parrilla Argentina', 'We take pride in our commitment to quality and authenticity.', manager1, '16:00:00', '22:00:00') RETURNING restaurant_id INTO r2;
INSERT INTO Festival.Restaurant(restaurant_id, name, description, manager, opening_at, closing_at) VALUES (DEFAULT, 'Hallernes Smørrebrød', 'The best Smørrebrød directly from Copenhagen.', manager1, '16:00:00', '22:00:00') RETURNING restaurant_id INTO r3;
INSERT INTO Festival.Restaurant(restaurant_id, name, description, manager, opening_at, closing_at) VALUES (DEFAULT, 'The Savory Spoon', 'Culinary haven that combines the rustic charm of a countryside bistro with the vibrant flavors of contemporary cuisine.', manager2, '16:00:00', '22:00:00') RETURNING restaurant_id INTO r4;
INSERT INTO Festival.Restaurant(restaurant_id, name, description, manager, opening_at, closing_at) VALUES (DEFAULT, 'Ocean Breeze Bistro', 'Our restaurant celebrates the bounty of the ocean with a menu rich in fresh seafood.', manager2, '16:00:00', '22:00:00') RETURNING restaurant_id INTO r5;

-- Insertion of cuisines
INSERT INTO Festival.Cuisine(cuisine_id, country, type, restaurant) VALUES (DEFAULT, 'Italy', 'pizza', r1);
INSERT INTO Festival.Cuisine(cuisine_id, country, type, restaurant) VALUES (DEFAULT, 'Argentina', 'steakhouse', r2);
INSERT INTO Festival.Cuisine(cuisine_id, country, type, restaurant) VALUES (DEFAULT, 'Denmark', 'seafood', r3);
INSERT INTO Festival.Cuisine(cuisine_id, country, type, restaurant) VALUES (DEFAULT, 'Denmark', 'street food', r3);
INSERT INTO Festival.Cuisine(cuisine_id, country, type, restaurant) VALUES (DEFAULT, 'France', 'traditional cuisine', r4);
INSERT INTO Festival.Cuisine(cuisine_id, country, type, restaurant) VALUES (DEFAULT, NULL, 'seasonal food', r4);
INSERT INTO Festival.Cuisine(cuisine_id, country, type, restaurant) VALUES (DEFAULT, 'Greece', 'mediterranean', r5);
INSERT INTO Festival.Cuisine(cuisine_id, country, type, restaurant) VALUES (DEFAULT, NULL, 'seafood', r5);

-- Insertion of dishes
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Margherita', 5.00, r1) RETURNING dish_id INTO d1;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Diavola', 6.00, r1) RETURNING dish_id INTO d2;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Quattro Formaggi', 7.00, r1) RETURNING dish_id INTO d3;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Asado', 15.00, r2) RETURNING dish_id INTO d4;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Empanadas', 8.00, r2) RETURNING dish_id INTO d5;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Choripàn', 10.00, r2) RETURNING dish_id INTO d6;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Shrimp with Dill and Lemon', 8.00, r3) RETURNING dish_id INTO d7;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Smoked Salmon and Fennel Salad', 9.00, r3) RETURNING dish_id INTO d8;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Potato, Garlic aioli and Crispy Shallots', 7.00, r3) RETURNING dish_id INTO d9;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Herb-Crusted Rack of Lamb', 15.00, r4) RETURNING dish_id INTO d10;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Wild Mushroom Risotto', 15.00, r4) RETURNING dish_id INTO d11;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Rustic Apple Tart', 7.00, r4) RETURNING dish_id INTO d12;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Grilled Prawns with Lemon-Garlic Sauce', 16.00, r5) RETURNING dish_id INTO d13;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Aromatic Thai Seafood Curry', 13.00, r5) RETURNING dish_id INTO d14;
INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, 'Mediterranean Clam Chowder', 14.00, r5) RETURNING dish_id INTO d15;

-- Insertion of ingredients
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Tomato Sauce', 'vegan') RETURNING ingredient_id INTO i1;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Spicy Salami', 'carnivorous') RETURNING ingredient_id INTO i2;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Cheese', 'vegetarian') RETURNING ingredient_id INTO i3;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Meat', 'carnivorous') RETURNING ingredient_id INTO i4;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Bread', 'vegan') RETURNING ingredient_id INTO i5;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Chorizo', 'carnivorous') RETURNING ingredient_id INTO i6;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Shrimps', 'carnivorous') RETURNING ingredient_id INTO i7;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Salmon', 'carnivorous') RETURNING ingredient_id INTO i8;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Potato', 'vegan') RETURNING ingredient_id INTO i9;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Lamb', 'carnivorous') RETURNING ingredient_id INTO i10;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Rosemary', 'vegan') RETURNING ingredient_id INTO i11;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Mushrooms', 'vegan') RETURNING ingredient_id INTO i12;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Apple', 'vegan') RETURNING ingredient_id INTO i13;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Cinnamon', 'vegan') RETURNING ingredient_id INTO i14;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Prawns', 'carnivorous') RETURNING ingredient_id INTO i15;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Lemon', 'vegan') RETURNING ingredient_id INTO i16;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Garlic', 'vegan') RETURNING ingredient_id INTO i17;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Squid', 'carnivorous') RETURNING ingredient_id INTO i18;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Clams', 'carnivorous') RETURNING ingredient_id INTO i19;
INSERT INTO Festival.Ingredient(ingredient_id, name, diet) VALUES (DEFAULT, 'Tomato', 'vegan') RETURNING ingredient_id INTO i20;

-- Population of Dish_Ingredient table
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d1, i1); -- Margherita; Tomato Sauce
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d2, i2); -- Diavola; Spicy Salami
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d3, i3); -- Quattro Formaggi; Cheese
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d4, i4); -- Asado; Meat
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d5, i4); -- Empanadas; Meat
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d6, i5); -- Choripàn; Bread
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d6, i6); -- Choripàn; Chorizo
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d7, i5); -- Shrimp with Dill and Lemon; Bread
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d7, i7); -- Shrimp with Dill and Lemon; Shrimps
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d8, i5); -- Smoked Salmon and Fennel Salad; Bread
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d8, i8); -- Smoked Salmon and Fennel Salad; Salmon
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d9, i5); -- Potato, Garlic aioli and Crispy Shallots; Bread
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d9, i9); -- Potato, Garlic aioli and Crispy Shallots; Potato
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d10, i10); -- Herb-Crusted Rack of Lamb; Lamb
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d10, i11); -- Herb-Crusted Rack of Lamb; Rosemary
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d11, i12); -- Wild Mushroom Risotto; Mushrooms
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d12, i13); -- Rustic Apple Tart; Apple
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d12, i14); -- Rustic Apple Tart; Cinnamon
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d13, i15); -- Grilled Prawns with Lemon-Garlic Sauce; Prawns
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d13, i16); -- Grilled Prawns with Lemon-Garlic Sauce; Lemon
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d13, i17); -- Grilled Prawns with Lemon-Garlic Sauce; Garlic
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d14, i18); -- Aromatic Thai Seafood Curry; Squid
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d14, i7); -- Aromatic Thai Seafood Curry; Shrimps
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d15, i19); -- Mediterranean Clam Chowder; Clams
INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (d15, i20); -- Mediterranean Clam Chowder; Tomato

-- Insertion of orders
INSERT INTO Festival."Order"(order_id, status, price, placedOn, "user") VALUES (DEFAULT, 'completed', 28.00, '2024-04-24 17:00:00', customer) RETURNING order_id INTO completed;
INSERT INTO Festival."Order"(order_id, status, price, placedOn, "user") VALUES (DEFAULT, 'pending', 22.00, '2024-04-24 11:00:00', customer) RETURNING order_id INTO pending;

-- Population of Order_Dish
INSERT INTO Festival.Order_Dish("order", dish, quantity) VALUES (pending, d2, 1);
INSERT INTO Festival.Order_Dish("order", dish, quantity) VALUES (pending, d7, 2);
INSERT INTO Festival.Order_Dish("order", dish, quantity) VALUES (completed, d1, 1);
INSERT INTO Festival.Order_Dish("order", dish, quantity) VALUES (completed, d5, 2);
INSERT INTO Festival.Order_Dish("order", dish, quantity) VALUES (completed, d9, 1);

END $$;