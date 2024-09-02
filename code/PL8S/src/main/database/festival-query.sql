-- Specifies the procedures for the database
DO $$
BEGIN

    -- INSERT --

    -- Insert User
    CREATE OR REPLACE PROCEDURE Festival.insert_user(p_email VARCHAR(255), p_password VARCHAR(255), p_name VARCHAR(255), p_surname VARCHAR(255), p_role Festival.UserRole, p_stripe VARCHAR(255), OUT out_id INT)
        LANGUAGE plpgsql
        AS $insert_user$
        BEGIN
            INSERT INTO Festival."User"(user_id, email, password, name, surname, role, stripe_id) VALUES (DEFAULT, p_email, p_password, p_name, p_surname, p_role, p_stripe) RETURNING user_id INTO out_id;
            INSERT INTO Festival."Order"(order_id, status, price, placedOn, "user") VALUES (DEFAULT, 'pending', 0, CURRENT_TIMESTAMP, out_id);
        END $insert_user$;

    -- Insert Cuisine
    CREATE OR REPLACE PROCEDURE Festival.insert_cuisine(p_country Festival.Country, p_type VARCHAR(255), p_restaurant INT, OUT out_id INT)
        LANGUAGE plpgsql
        AS $insert_cuisine$
        BEGIN
            INSERT INTO Festival.Cuisine(cuisine_id, country, type, restaurant) VALUES (DEFAULT, p_country, p_type, p_restaurant) RETURNING cuisine_id INTO out_id;
        END $insert_cuisine$;

    -- Insert Restaurant
    CREATE OR REPLACE PROCEDURE Festival.insert_restaurant(p_name VARCHAR(255), p_description VARCHAR(255), p_manager INT, p_opening_at TIME, p_closing_at TIME, OUT out_id INT)
        LANGUAGE plpgsql
        AS $insert_restaurant$
        BEGIN
            INSERT INTO Festival.Restaurant(restaurant_id, name, description, manager, opening_at, closing_at) VALUES (DEFAULT, p_name, p_description, p_manager, p_opening_at, p_closing_at) RETURNING restaurant_id INTO out_id;
        END $insert_restaurant$;

    -- Insert Dish
    CREATE OR REPLACE PROCEDURE Festival.insert_dish(p_name VARCHAR(255), p_price REAL, p_restaurant INT, OUT out_id INT)
        LANGUAGE plpgsql
        AS $insert_dish$
        BEGIN
            INSERT INTO Festival.Dish(dish_id, name, price, restaurant) VALUES (DEFAULT, p_name, p_price, p_restaurant) RETURNING dish_id INTO out_id;
        END $insert_dish$;

    -- Insert Order
    CREATE OR REPLACE PROCEDURE Festival.insert_order(p_status Festival.OrderStatus, p_price REAL, p_placedOn TIMESTAMP,  p_user INT, OUT out_id INT)
        LANGUAGE plpgsql
        AS $insert_order$
        BEGIN
            INSERT INTO Festival."Order"(order_id, status, price, placedOn, "user") VALUES (DEFAULT, p_status, p_price, p_placedOn, p_user) RETURNING order_id INTO out_id;
        END $insert_order$;

    -- Insert Dish_Ingredient relation
    CREATE OR REPLACE PROCEDURE Festival.insert_dish_ingredient(p_dish INT, p_ingredient INT)
        LANGUAGE plpgsql
        AS $insert_dish_ingredient$
        BEGIN
            INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (p_dish, p_ingredient);
        END $insert_dish_ingredient$;

    -- Insert Order_Dish relation
    CREATE OR REPLACE PROCEDURE Festival.insert_order_dish(p_user INT, p_dish INT, OUT out_id INT)
        LANGUAGE plpgsql
        AS $insert_order_dish$
        BEGIN
            SELECT o.order_id INTO out_id FROM Festival."Order" o WHERE o."user" = p_user AND o.status = 'pending';
            -- Insert new Order_Dish relation
            INSERT INTO Festival.Order_Dish("order", dish, quantity) VALUES (out_id, p_dish, 1);
            -- Update the price of the Order (add dish price)
            UPDATE Festival."Order"
            SET price = price + (SELECT d.price FROM Festival.Dish d WHERE d.dish_id = p_dish)
            WHERE Festival."Order"."user" = p_user AND Festival."Order".status = 'pending';
        END $insert_order_dish$;

    -- SELECT --

    -- Select restaurants in order AZ for admin
    CREATE OR REPLACE FUNCTION Festival.select_restaurants()
    RETURNS TABLE (restaurant_id INT, name VARCHAR(255), description VARCHAR(255), manager INT, manager_email VARCHAR(255), opening_at TIME, closing_at TIME, countries TEXT[], cuisine_types TEXT[])
        LANGUAGE plpgsql
        AS $select_restaurants$
        BEGIN
            RETURN QUERY
            SELECT r.restaurant_id, r.name, r.description, r.manager, u.email, r.opening_at, r.closing_at, ARRAY_AGG(DISTINCT cu.country::TEXT) FILTER (WHERE cu.country IS NOT NULL) AS countries, ARRAY_AGG(DISTINCT cu.type::TEXT) FILTER (WHERE cu.type IS NOT NULL) AS cuisine_types
            FROM Festival.Restaurant r
            LEFT JOIN Festival.Cuisine cu
            ON r.restaurant_id = cu.restaurant
            JOIN Festival."User" u
            ON r.manager = u.user_id
            WHERE r.restaurant_id != 1
            GROUP BY r.restaurant_id, u.email
            ORDER BY r.name ASC;
        END $select_restaurants$;

    --select restaurants by name and by cuisine type. If any parameter is null it returns all for that parameter
    CREATE OR REPLACE FUNCTION Festival.select_restaurants_by_name_by_cuisinetype(p_restaurant_id INT, p_name VARCHAR(255), p_type VARCHAR(255))
    RETURNS TABLE (restaurant_id INT, name VARCHAR(255), description VARCHAR(255), manager INT, opening_at TIME, closing_at TIME, countries TEXT[], cuisine_types TEXT[])
        LANGUAGE plpgsql
        AS $select_restaurants_by_name_by_cuisinetype$
        BEGIN
            RETURN QUERY
            SELECT r.restaurant_id, r.name, r.description, r.manager, r.opening_at, r.closing_at, ARRAY_AGG(DISTINCT cu.country::TEXT) as countries, ARRAY_AGG(DISTINCT cu.type::TEXT) AS cuisine_types
            FROM Festival.Restaurant r
            LEFT JOIN Festival.Cuisine cu
            ON r.restaurant_id = cu.restaurant
            WHERE (p_restaurant_id IS NULL OR r.restaurant_id = p_restaurant_id) AND (p_type IS NULL OR cu.type = p_type) AND (p_name IS NULL OR lower(r.name) LIKE lower(p_name)) AND r.restaurant_id != 1
            GROUP BY r.restaurant_id
            ORDER BY r.name ASC;
        END $select_restaurants_by_name_by_cuisinetype$;

    -- Select restaurants by manager
    CREATE OR REPLACE FUNCTION Festival.select_restaurants_by_manager(p_manager INT)
    RETURNS TABLE (restaurant_id INT, name VARCHAR(255), description VARCHAR(255), opening_at TIME, closing_at TIME, countries TEXT[], cuisine_types TEXT[], manager INT, manager_email VARCHAR(255))
    	LANGUAGE plpgsql
    	AS $select_restaurants_by_manager$
        BEGIN
            -- If not admin, get restaurants owned by p_manager
            IF p_manager != 1 THEN
                RETURN QUERY
                SELECT r.restaurant_id, r.name, r.description, r.opening_at, r.closing_at, ARRAY_AGG(DISTINCT cu.country::TEXT) as countries, ARRAY_AGG(DISTINCT cu.type::TEXT) AS cuisine_types, r.manager, u.email
                FROM Festival.Restaurant r
                LEFT JOIN Festival."User" u
                ON r.manager = u.user_id
                LEFT JOIN Festival.Cuisine cu
                ON r.restaurant_id = cu.restaurant
                WHERE r.manager = p_manager
                GROUP BY r.restaurant_id, u.user_id;
            ELSE
                RETURN QUERY
                SELECT r.restaurant_id, r.name, r.description, r.opening_at, r.closing_at, ARRAY_AGG(DISTINCT cu.country::TEXT) as countries, ARRAY_AGG(DISTINCT cu.type::TEXT) AS cuisine_types, r.manager, u.email
                FROM Festival.Restaurant r
                LEFT JOIN Festival."User" u
                ON r.manager = u.user_id
                LEFT JOIN Festival.Cuisine cu
                ON r.restaurant_id = cu.restaurant
                WHERE r.restaurant_id != 1
                GROUP BY r.restaurant_id, u.user_id;
            END IF;
        END $select_restaurants_by_manager$;

    -- Select manager by restaurant
    CREATE OR REPLACE FUNCTION Festival.select_manager_by_restaurant(p_restaurant INT)
    RETURNS TABLE (user_id INT, email VARCHAR(255), password VARCHAR(255), name VARCHAR(255), surname VARCHAR(255), stripe_id VARCHAR(255), role Festival.UserRole)
        LANGUAGE plpgsql
        AS $select_manager_by_restaurant$
        BEGIN
            IF p_restaurant != 1 THEN
                RETURN QUERY
                SELECT u.user_id, u.email, u.password, u.name, u.surname, u.stripe_id, u.role
                FROM Festival.Restaurant r
                JOIN Festival."User" u
                ON r.manager = u.user_id
                WHERE r.restaurant_id = p_restaurant;
            END IF;
        END $select_manager_by_restaurant$;

    -- Select manager by restaurant
    CREATE OR REPLACE FUNCTION Festival.select_manager_by_dish(p_dish INT)
    RETURNS TABLE (user_id INT, email VARCHAR(255), password VARCHAR(255), name VARCHAR(255), surname VARCHAR(255), stripe_id VARCHAR(255), role Festival.UserRole)
        LANGUAGE plpgsql
        AS $select_manager_by_dish$
        BEGIN
            RETURN QUERY
            SELECT u.user_id, u.email, u.password, u.name, u.surname, u.stripe_id, u.role
            FROM Festival.Dish d
            JOIN Festival.Restaurant r
            ON r.restaurant_id = d.restaurant
            JOIN Festival."User" u
            ON r.manager = u.user_id
            WHERE d.dish_id = p_dish;
        END $select_manager_by_dish$;

    -- Select dishes by restaurant in order AZ
    CREATE OR REPLACE FUNCTION Festival.select_dishes_by_restaurant(p_restaurant INT) RETURNS TABLE (dish_id INT, name VARCHAR(255), price REAL)
    	LANGUAGE plpgsql
    	AS $select_dishes_by_restaurant$
    	BEGIN
    		RETURN QUERY
    		SELECT d.dish_id, d.name, d.price
        	FROM Festival.Dish d
        	WHERE d.restaurant = p_restaurant AND d.isDeleted = b'0'
        	ORDER BY d.name ASC;
    	END $select_dishes_by_restaurant$;

    -- Select dishes by restaurant by name and by diet in order AZ
    CREATE OR REPLACE FUNCTION Festival.select_dishes_by_restaurant_by_name_by_diet(p_restaurant INT, p_diet VARCHAR(255), p_dishname VARCHAR(255)) RETURNS TABLE (restaurant INT, dish_id INT, name VARCHAR(255), price REAL, ingredient_ids INT[], ingredient_names TEXT[], ingredient_diets TEXT[])
    	LANGUAGE plpgsql
    	AS $select_dishes_by_restaurant_by_name_by_diet$
    	BEGIN
    		RETURN QUERY
				SELECT
					d.restaurant
					, d.dish_id
					, d.name
					, d.price
					, ARRAY_AGG(i.ingredient_id::INT) as ingredient_ids
					, ARRAY_AGG(i.name::TEXT) as ingredient_names
					, ARRAY_AGG(i.diet::TEXT) as ingredient_diets
				FROM
					Festival.Dish d
					INNER JOIN Festival.Dish_Ingredient di ON d.dish_id = di.dish
					INNER JOIN Festival.Ingredient i ON di.ingredient = i.ingredient_id
				WHERE
					d.isDeleted = b'0'
					AND (p_restaurant IS NULL OR d.restaurant = p_restaurant)
				  	AND (p_dishname IS NULL OR lower(d.name) LIKE lower(p_dishname))
					AND (
						p_diet IS NULL
						OR d.dish_id NOT IN (
							SELECT
								d.dish_id
							FROM
								Festival.Dish_Ingredient di
								INNER JOIN Festival.Ingredient i ON di.ingredient = i.ingredient_id
							WHERE
								di.dish = d.dish_id
								AND (
									(p_diet='vegetarian' AND i.diet='carnivorous')
									OR (p_diet='vegan' AND (i.diet='carnivorous' OR i.diet='vegetarian'))
								)
						)
					)
				GROUP BY
					d.restaurant
					, d.dish_id
					, d.name
					, d.price
				ORDER BY d.name ASC;
    	END $select_dishes_by_restaurant_by_name_by_diet$;

    -- Select dishes by ingredient in order AZ
    CREATE OR REPLACE FUNCTION Festival.select_dishes_by_ingredient(p_ingredient INT) RETURNS TABLE (dish_id INT, name VARCHAR(255), price REAL)
    	LANGUAGE plpgsql
    	AS $select_dishes_by_ingredient$
    	BEGIN
    		RETURN QUERY
    		SELECT d.dish_id, d.name, d.price
        	FROM Festival.Dish d
    		JOIN Festival.Dish_Ingredient di
    		ON di.ingredient = p_ingredient
        	WHERE d.dish_id = di.dish
        	ORDER BY d.name ASC;
    	END $select_dishes_by_ingredient$;

    -- Select dishes by order with Order_Dish quantity
        CREATE OR REPLACE FUNCTION Festival.select_dishes_and_quantity_by_order(p_order INT) RETURNS TABLE (dish_id INT, name VARCHAR(255), price REAL, restaurant INT, quantity INT)
        	LANGUAGE plpgsql
        	AS $select_dishes_by_order$
        	BEGIN
        		RETURN QUERY
        		SELECT d.dish_id, d.name, d.price, d.restaurant, od.quantity
            	FROM Festival.Dish d
        		JOIN Festival.Order_Dish od
        		ON d.dish_id = od.dish
            	WHERE od."order" = p_order;
        	END $select_dishes_by_order$;
		
		CREATE OR REPLACE FUNCTION Festival.select_dish(p_dish INT) RETURNS TABLE (dish_id INT, name VARCHAR(255), price REAL, restaurant INT, diet VARCHAR(255), ingredient_ids INT[], ingredient_names TEXT[], ingredient_diets TEXT[])
        	LANGUAGE plpgsql
        	AS $select_dish$
        	BEGIN
        		RETURN QUERY
        		SELECT
					d.dish_id
					, d.name
					, d.price
					, d.restaurant
					, max(i.diet)::VARCHAR(255) as diet
					, ARRAY_AGG(i.ingredient_id::INT) as ingredient_ids
					, ARRAY_AGG(i.name::TEXT) as ingredient_names
					, ARRAY_AGG(i.diet::TEXT) as ingredient_diets
				FROM
					Festival.Dish d
					INNER JOIN Festival.dish_ingredient as di on d.dish_id = di.dish
					INNER JOIN Festival.ingredient as i on di.ingredient = i.ingredient_id
				WHERE
					d.dish_id = p_dish
					AND d.isdeleted = b'0'
				GROUP BY
					d.dish_id
					, d.name
					, d.price
					, d.restaurant;
        	END $select_dish$;

    --Select Cuisine_Types
    CREATE OR REPLACE FUNCTION Festival.select_cuisine_types() RETURNS TABLE (type VARCHAR(255))
        LANGUAGE plpgsql
    	AS $select_cuisine_types$
        BEGIN
            RETURN QUERY
            SELECT DISTINCT cu.type
            FROM Festival.Cuisine cu;
        END $select_cuisine_types$;


    -- Select ingredients by dish
    CREATE OR REPLACE FUNCTION Festival.select_ingredients_by_dish(p_dish INT) RETURNS TABLE (ingredient_id INT, name VARCHAR(255), diet Festival.Diet)
    	LANGUAGE plpgsql
    	AS $select_ingredients_by_dish$
    	BEGIN
    		RETURN QUERY
    		SELECT i.ingredient_id, i.name, i.diet
        	FROM Festival.Ingredient i
    		JOIN Festival.Dish_Ingredient di
    		ON i.ingredient_id = di.ingredient
        	WHERE di.dish = p_dish;
    	END $select_ingredients_by_dish$;

    -- Select orders by user and status
    CREATE OR REPLACE FUNCTION Festival.select_orders_by_user_and_status(p_user INT, p_status Festival.OrderStatus) RETURNS TABLE (order_id INT, o_price REAL, placedOn TIMESTAMP, quantity INT, dish_id INT, d_name VARCHAR(255), d_price REAL, isDeleted BIT, restaurant INT, r_name VARCHAR(255))
    LANGUAGE plpgsql
    AS $select_orders_by_user_and_status$
    BEGIN
        -- Recomputing cart price
        IF(p_status = 'pending'::Festival.OrderStatus)
        THEN
            -- Remove deleted dishes from the cart
            DELETE
            FROM
                Festival.Order_Dish AS OD
            WHERE
                OD.dish IN (
                    SELECT
                        D.dish_id
                    FROM
                        Festival."Order" AS O
                        INNER JOIN Festival.Order_Dish AS OD ON O.order_id = OD.order
                        INNER JOIN Festival.Dish AS D ON OD.dish = D.dish_id
                    WHERE
                        O."user" = p_user
                        AND O.status = 'pending'::Festival.OrderStatus
                        AND D.isDeleted = b'1'
                );
            UPDATE Festival."Order" AS O
            SET
                price = COALESCE((
                    SELECT
                        SUM(D.price * OD.quantity)
                    FROM
                        Festival.Order_Dish AS OD
                        INNER JOIN Festival.Dish AS D ON OD.dish = D.dish_id
                    WHERE
                        OD.order = O.order_id
                ), 0)
            WHERE
                O."user" = p_user
                AND O.status = 'pending'::Festival.OrderStatus;
        END IF;
        RETURN QUERY
            SELECT
                O.order_id
                , O.price
                , O.placedOn
                , OD.quantity
                , D.dish_id
                , D.name
                , D.price
                , D.isDeleted
                , D.restaurant
                , R.name
            FROM
                Festival."Order" AS O
                LEFT JOIN Festival.Order_Dish AS OD ON O.order_id = OD.order
                LEFT JOIN Festival.Dish AS D ON OD.dish = D.dish_id
                LEFT JOIN Festival.Restaurant AS R ON D.restaurant = R.restaurant_id
            WHERE
                O."user" = p_user
                AND O.status = p_status
            ORDER BY
                O.order_id DESC,
                R.name ASC,
                D.name ASC;
    END $select_orders_by_user_and_status$;

    -- Select user by email
    CREATE OR REPLACE FUNCTION Festival.select_user_by_email(p_email VARCHAR(255)) RETURNS TABLE (user_id INT, email VARCHAR(255), password VARCHAR(255), name VARCHAR(255), surname VARCHAR(255), stripe_id VARCHAR(255), role Festival.UserRole)
    	LANGUAGE plpgsql
    	AS $select_user_by_email$
    	BEGIN
    		RETURN QUERY
    		SELECT u.user_id, u.email, u.password, u.name, u.surname, u.stripe_id, u.role
        	FROM Festival."User" u
        	WHERE u.email = p_email;
    	END $select_user_by_email$;

     -- Select user by email (masked)
     CREATE OR REPLACE FUNCTION Festival.select_user_by_email_masked(p_email VARCHAR(255)) RETURNS TABLE (user_id INT, email VARCHAR(255), password VARCHAR(255), name VARCHAR(255), surname VARCHAR(255), stripe_id VARCHAR(255), role Festival.UserRole)
     	LANGUAGE plpgsql
     	AS $select_user_by_email_masked$
     	BEGIN
     		RETURN QUERY
     		SELECT u.user_id, u.email, u.password, u.name, u.surname, u.stripe_id, u.role
         	FROM Festival."User" u
         	WHERE lower(u.email) LIKE lower(p_email);
     	END $select_user_by_email_masked$;

    -- Select users
    CREATE OR REPLACE FUNCTION Festival.select_users() RETURNS TABLE (user_id INT, email VARCHAR(255), password VARCHAR(255), name VARCHAR(255), surname VARCHAR(255), stripe_id VARCHAR(255), role Festival.UserRole)
    	LANGUAGE plpgsql
    	AS $select_users$
    	BEGIN
    		RETURN QUERY
    		SELECT u.user_id, u.email, u.password, u.name, u.surname, u.stripe_id, u.role
        	FROM Festival."User" u
        	WHERE u.user_id != 1;
    	END $select_users$;
	
	CREATE OR REPLACE FUNCTION Festival.select_user_by_id(p_user_id INT) RETURNS TABLE (user_id INT, email VARCHAR(255), password VARCHAR(255), name VARCHAR(255), surname VARCHAR(255), stripe_id VARCHAR(255), role Festival.UserRole)
    	LANGUAGE plpgsql
    	AS $select_user_by_id$
    	BEGIN
    		RETURN QUERY
    		SELECT u.user_id, u.email, u.password, u.name, u.surname, u.stripe_id, u.role
        	FROM Festival."User" u
        	WHERE u.user_id = p_user_id;
    	END $select_user_by_id$;

    -- UPDATE --

    -- Update dish quantity in Order_Dish
    CREATE OR REPLACE PROCEDURE Festival.update_order_dish_quantity(p_user INT, p_dish INT, p_quantity INT, OUT o_order_id INT)
        LANGUAGE plpgsql
        AS $update_order_dish_quantity$
        BEGIN
            -- Retrieve order_id from the pending order of the p_user
            SELECT o.order_id
            INTO o_order_id
            FROM Festival."Order" o
            WHERE o."user" = p_user AND o.status = 'pending';
            -- Update the quantity of the dish in the pending order
            UPDATE Festival.Order_Dish
            SET quantity = p_quantity
            WHERE dish = p_dish AND "order" = o_order_id;
            -- Update the price of the order accordingly
            UPDATE Festival."Order"
            SET price =
                (
                -- Calculate total price of order
                SELECT SUM(d.price * od.quantity)
                FROM Festival.Dish d
                JOIN Festival.Order_Dish od
                ON d.dish_id = od.dish
                WHERE od."order" = o_order_id
                )
            WHERE order_id = o_order_id;
        END $update_order_dish_quantity$;

    -- Update order's price
    CREATE OR REPLACE PROCEDURE Festival.update_order_price(p_order INT, p_price REAL, p_placedOn TIMESTAMP)
        LANGUAGE plpgsql
        AS $update_order_price$
        BEGIN
            UPDATE Festival."Order" SET price = p_price, placedOn = p_placedOn WHERE order_id = p_order;
        END $update_order_price$;

    -- Update order's status to completed
    CREATE OR REPLACE FUNCTION Festival.update_order_status(p_user INT)
    RETURNS TABLE (order_id INT, price REAL, placedOn TIMESTAMP, status Festival.OrderStatus)
        LANGUAGE plpgsql
        AS $update_order_status$
        DECLARE
            o_order_id INT;
            o_price REAL;
        BEGIN
            -- Retrieve order_id from the pending order of the p_user
            SELECT o.order_id
            INTO o_order_id
            FROM Festival."Order" o
            WHERE o."user" = p_user AND o.status = 'pending';
            -- Retrieve order_id from the pending order of the p_user
            SELECT o.price
            INTO o_price
            FROM Festival."Order" o
            WHERE o.order_id = o_order_id;
            -- Check that the Order is not empty
            IF o_price > 0 THEN
                -- Update the pending order to completed and finalize placedOn
                UPDATE Festival."Order" o
                SET status = 'completed', placedOn = CURRENT_TIMESTAMP
                WHERE o.order_id = o_order_id;
                -- Create a new empty pending order for the p_user
                INSERT INTO Festival."Order"(order_id, status, price, placedOn, "user")
                VALUES (DEFAULT, 'pending', 0, CURRENT_TIMESTAMP, p_user);
                -- Returns order data
                RETURN QUERY
                SELECT o.order_id, o.price, o.placedOn, o.status
                FROM Festival."Order" o
                WHERE o.order_id = o_order_id;
            ELSE
                RAISE EXCEPTION 'Order cannot be completed when empty';
            END IF;
        END $update_order_status$;

    -- Update dish attributes
    CREATE OR REPLACE PROCEDURE Festival.update_dish(p_dish INT, p_name VARCHAR(255), p_price REAL, p_restaurant INT)
        LANGUAGE plpgsql
        AS $update_dish$
        BEGIN
            UPDATE Festival.Dish SET name = p_name, price = p_price, restaurant = p_restaurant WHERE dish_id = p_dish;
        END $update_dish$;

    -- Update restaurant manager
    CREATE OR REPLACE PROCEDURE Festival.update_restaurant_manager(p_restaurant INT, p_manager INT)
        LANGUAGE plpgsql
        AS $update_restaurant_manager$
        BEGIN
            UPDATE Festival.Restaurant SET manager = p_manager WHERE restaurant_id = p_restaurant;
        END $update_restaurant_manager$;

    -- Update restaurant
    CREATE OR REPLACE PROCEDURE Festival.update_restaurant(p_restaurant INT, p_name VARCHAR(255), p_description VARCHAR(255), p_manager INT, p_opening_at TIME, p_closing_at TIME)
        LANGUAGE plpgsql
        AS $update_restaurant$
        BEGIN
            UPDATE Festival.Restaurant
            SET name = p_name, description = p_description, manager = p_manager, opening_at = p_opening_at, closing_at = p_closing_at
            WHERE restaurant_id = p_restaurant;
        END $update_restaurant$;

    -- Update restaurant
    CREATE OR REPLACE FUNCTION Festival.update_restaurant_cuisines(p_restaurant INT, p_cuisines TEXT[])
    RETURNS TABLE (cuisine_id INT, country Festival.Country, type VARCHAR(255), restaurant INT)
        LANGUAGE plpgsql
        AS $update_restaurant_cuisines$
        DECLARE
            arr TEXT[];
        BEGIN
            -- First delete the current cuisines associated to p_restaurant
            DELETE FROM Festival.Cuisine c WHERE c.restaurant = p_restaurant;
            -- Second create new cuisines
            FOREACH arr SLICE 1 IN ARRAY p_cuisines
            LOOP
                INSERT INTO Festival.Cuisine(cuisine_id, country, type, restaurant) VALUES (DEFAULT, initcap(arr[1])::Festival.Country, arr[2], p_restaurant);
            END LOOP;
            -- Third return the created cuisines
            RETURN QUERY
            SELECT * FROM Festival.Cuisine fc WHERE fc.restaurant = p_restaurant;
        END $update_restaurant_cuisines$;

    -- Update user attributes
    CREATE OR REPLACE FUNCTION Festival.update_user(p_user INT, p_email VARCHAR(255), p_password VARCHAR(255), p_name VARCHAR(255), p_surname VARCHAR(255))
    RETURNS TABLE (user_id INT, email VARCHAR(255), password VARCHAR(255), name VARCHAR(255), surname VARCHAR(255), stripe_id VARCHAR(255), role Festival.UserRole)
        LANGUAGE plpgsql
        AS $update_user$
        BEGIN
            IF p_password = '' THEN
                -- Update all other than password
                UPDATE Festival."User" SET email = p_email, name = p_name, surname = p_surname
                WHERE Festival."User".user_id = p_user;
                RETURN QUERY
                SELECT * FROM Festival."User" WHERE Festival."User".user_id = p_user;
            ELSE
                -- Update password too
                UPDATE Festival."User" SET email = p_email, password = p_password, name = p_name, surname = p_surname
                WHERE Festival."User".user_id = p_user;
                RETURN QUERY
                SELECT * FROM Festival."User" WHERE Festival."User".user_id = p_user;
            END IF;
        END $update_user$;

    -- Update ingredients
    CREATE OR REPLACE FUNCTION Festival.update_ingredients(p_dish INT, p_ingredients TEXT[])
    RETURNS TABLE (ingredient_id INT, name VARCHAR(255), diet Festival.Diet)
        LANGUAGE plpgsql
        AS $update_ingredients$
        DECLARE
            arr TEXT[];
            ing_id INT;
        BEGIN
            -- First delete all the dish_ingredient relations of p_dish
            DELETE FROM Festival.Dish_Ingredient di WHERE di.dish = p_dish;
            -- For each ingredient (name, diet)
            FOREACH arr SLICE 1 IN ARRAY p_ingredients
            LOOP
                ing_id = NULL;
                -- Search if the ingredient is already present in the database
                SELECT i.ingredient_id INTO ing_id FROM Festival.Ingredient i WHERE lower(i.name) = lower(arr[1]);
                IF ing_id IS NULL THEN
                    -- If not present, then insert new ingredient in the database
                    INSERT INTO Festival.Ingredient(ingredient_id, name, diet)
                    VALUES (DEFAULT, initcap(arr[1]), lower(arr[2])::Festival.Diet) RETURNING Festival.Ingredient.ingredient_id INTO ing_id;
                END IF;
                -- Insert the relation between dish and ingredient
                INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (p_dish, ing_id);
            END LOOP;
            -- Return the ingredients
            RETURN QUERY
            SELECT ii.ingredient_id, ii.name, ii.diet
            FROM Festival.Ingredient ii
            RIGHT JOIN Festival.Dish_Ingredient dii
            ON ii.ingredient_id = dii.ingredient
            WHERE dii.dish = p_dish;
        END $update_ingredients$;
    -- Update Dish
    CREATE OR REPLACE PROCEDURE Festival.update_dish(p_dish INT, p_name VARCHAR(255), p_price REAL, p_restaurant INT, ings text[][])
    LANGUAGE plpgsql
    AS $update_dish$
    DECLARE
    ing text[];
        ing_id INT;
    BEGIN

        CALL Festival.update_dish(p_dish, p_name, p_price, p_restaurant);

        DELETE
        FROM Festival.dish_ingredient AS DI
        WHERE
            DI.dish = p_dish;

        FOREACH ing SLICE 1 IN ARRAY ings
            LOOP
                ing_id = NULL;
                -- Search if the ingredient is already present in the database
        SELECT i.ingredient_id INTO ing_id FROM Festival.Ingredient i WHERE lower(i.name) = lower(ing[1]);
        IF ing_id IS NULL THEN
                    -- If not present, then insert new ingredient in the database
                    INSERT INTO Festival.Ingredient(ingredient_id, name, diet)
                    VALUES (DEFAULT, initcap(ing[1]), lower(ing[2])::Festival.Diet) RETURNING Festival.Ingredient.ingredient_id INTO ing_id;
        END IF;
                -- Insert the relation between dish and ingredient
        INSERT INTO Festival.Dish_Ingredient(dish, ingredient) VALUES (p_dish, ing_id);
        END LOOP;

    END $update_dish$;

    -- DELETE --

    -- Delete dish in order
    CREATE OR REPLACE FUNCTION Festival.delete_order_dish(p_user INT, p_dish INT) RETURNS TABLE ("order" INT, dish INT, quantity INT)
        LANGUAGE plpgsql
        AS $delete_order_dish$
        DECLARE
            o_order_id INT;
        BEGIN
            -- Retrieve order_id from the pending order of the p_user
            SELECT o.order_id
            INTO o_order_id
            FROM Festival."Order" o
            WHERE o."user" = p_user AND o.status = 'pending';
            -- Retrieve and return Order_Dish to be deleted
            RETURN QUERY
            SELECT Festival.Order_Dish."order", Festival.Order_Dish.dish, Festival.Order_Dish.quantity
            FROM Festival.Order_Dish
            WHERE Festival.Order_Dish.dish = p_dish AND Festival.Order_Dish."order" = o_order_id;
            -- Update price of pending order (remove price of deleted item)
            UPDATE Festival."Order"
            SET price = price -
                (
                -- Calculate price of item to be deleted
                SELECT (d.price * od.quantity)
                FROM Festival.Dish d
                JOIN Festival.Order_Dish od
                ON d.dish_id = od.dish
                WHERE d.dish_id = p_dish AND od."order" = o_order_id
                )
            WHERE Festival."Order".order_id = o_order_id;
            -- Delete Order_Dish
            DELETE FROM Festival.Order_Dish
            WHERE Festival.Order_Dish.dish = p_dish AND Festival.Order_Dish."order" = o_order_id;
        END $delete_order_dish$;
	
	CREATE OR REPLACE FUNCTION Festival.select_dish_orders(p_dish INT) RETURNS TABLE (order_id INT, o_price REAL, o_user INT, o_status Festival.OrderStatus, o_placedOn TIMESTAMP)
	LANGUAGE plpgsql
	AS $select_dish_orders$
	BEGIN
		RETURN QUERY
			SELECT
				O.order_id
				, O.price
				, O.user
				, O.status
				, O.placedon
			FROM
				Festival."Order" AS O
				LEFT JOIN Festival.Order_Dish AS OD ON O.order_id = OD.order
			WHERE
				OD.dish = p_dish
			ORDER BY
				O.order_id DESC;
	END $select_dish_orders$;

    -- Delete all dishes in order
    CREATE OR REPLACE FUNCTION Festival.delete_order_dishes(p_user INT)
    RETURNS TABLE (order_id INT, price REAL, placedOn TIMESTAMP, status Festival.OrderStatus)
        LANGUAGE plpgsql
        AS $delete_order_dishes$
        DECLARE
            o_order_id INT;
        BEGIN
            -- Retrieve order_id from the pending order of the p_user
            SELECT o.order_id
            INTO o_order_id
            FROM Festival."Order" o
            WHERE o."user" = p_user AND o.status = 'pending';
            -- Delete all Order_Dish relations with order equal to o_order_id
            DELETE FROM Festival.Order_Dish WHERE "order" = o_order_id;
            -- Update the price of the Order to zero
            UPDATE Festival."Order"
            SET price = 0
            WHERE Festival."Order".order_id = o_order_id;
            -- Return empty order
            RETURN QUERY
            SELECT o.order_id, o.price, o.placedOn, o.status
            FROM Festival."Order" o
            WHERE o.order_id = o_order_id;
        END $delete_order_dishes$;

    -- Delete User
    CREATE OR REPLACE FUNCTION Festival.delete_user(p_user INT) RETURNS TABLE (user_id INT, email VARCHAR(255), password VARCHAR(255), name VARCHAR(255), surname VARCHAR(255), stripe_id VARCHAR(255), role Festival.UserRole)
        LANGUAGE plpgsql
        AS $delete_user$
        DECLARE
            user_role Festival.UserRole;
            restaurants INT[];
            restaurant INT;
        BEGIN
            SELECT Festival."User".role INTO user_role FROM Festival."User" WHERE Festival."User".user_id = p_user;

            IF user_role = 'customer' THEN
                RETURN QUERY
                DELETE FROM Festival."User" WHERE Festival."User".user_id = p_user RETURNING *;
            ELSIF user_role = 'manager' THEN
                SELECT ARRAY_AGG(restaurant_id) INTO restaurants FROM Festival.Restaurant WHERE manager = p_user;
                IF restaurants IS NOT NULL THEN
                    FOREACH restaurant IN ARRAY restaurants
                    LOOP
                        -- Update restaurant manager with festival admin (user_id = 1) until new manager found
                        CALL Festival.update_restaurant_manager(restaurant, 1);
                    END LOOP;
                END IF;
                RETURN QUERY
                DELETE FROM Festival."User" WHERE Festival."User".user_id = p_user RETURNING *;
            END IF;
        END $delete_user$;

    -- Delete Restaurant
    CREATE OR REPLACE FUNCTION Festival.delete_restaurant(p_restaurant INT) RETURNS TABLE (restaurant_id INT, name VARCHAR(255), description VARCHAR(255), manager INT, opening_at TIME, closing_at TIME)
        LANGUAGE plpgsql
        AS $delete_restaurant$
        DECLARE
            r_id INT; r_manager INT;
            r_name VARCHAR(255); r_description VARCHAR(255);
            r_opening_at TIME; r_closing_at TIME;
        BEGIN
            IF p_restaurant != 1 THEN
                -- set all p_restaurant's dishes as deleted and belonging to the festival's restaurants collector (restaurant_id = 1)
                UPDATE Festival.Dish SET isDeleted = b'1', restaurant = 1 WHERE restaurant = p_restaurant;
                DELETE FROM Festival.Cuisine WHERE restaurant = p_restaurant;
                -- delete and return the row
                RETURN QUERY
                DELETE FROM Festival.Restaurant WHERE Festival.Restaurant.restaurant_id = p_restaurant RETURNING *;
            END IF;
        END $delete_restaurant$;

    -- Delete Dish setting isDeleted to true
    CREATE OR REPLACE FUNCTION Festival.delete_dish(p_dish INT) RETURNS TABLE (dish_id INT, name VARCHAR(255), price REAL, isDeleted BIT, restaurant INT)
        LANGUAGE plpgsql
        AS $delete_dish$
        BEGIN
            UPDATE Festival.Dish SET isDeleted = b'1' WHERE Festival.Dish.dish_id = p_dish;
            RETURN QUERY
            SELECT * FROM Festival.Dish WHERE Festival.Dish.dish_id = p_dish;
        END $delete_dish$;

    -- Delete Cuisine
    CREATE OR REPLACE PROCEDURE Festival.delete_cuisine(p_cuisine INT)
        LANGUAGE plpgsql
        AS $delete_cuisine$
        BEGIN
            DELETE FROM Festival.Cuisine WHERE cuisine_id = p_cuisine;
        END $delete_cuisine$;

END $$;