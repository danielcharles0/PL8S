-- Drop the Festival schema, if exists, and any object within it
DROP SCHEMA IF EXISTS Festival CASCADE;

-- Create the Festival schema
CREATE SCHEMA Festival;

COMMENT ON SCHEMA Festival IS 'Schema for containing the food choices and orders at the festival';

-- Create Types
CREATE TYPE Festival.Diet AS ENUM(
    'vegan',
    'vegetarian',
    'carnivorous'
);

COMMENT ON TYPE Festival.Diet IS 'Specifies for which diet an ingredient is';

CREATE TYPE Festival.UserRole AS ENUM(
    'customer',
    'manager',
    'admin'
);

COMMENT ON TYPE Festival.UserRole IS 'Defines what a user can do inside the app';

CREATE TYPE Festival.OrderStatus AS ENUM(
    'pending',
    'completed'
);

COMMENT ON TYPE Festival.OrderStatus IS 'Defines the status of the order, the cart is seen as a pending order';

CREATE TYPE Festival.Country AS ENUM(
    'Afghanistan', 'Albania', 'Algeria', 'Andorra', 'Angola', 'Antigua and Barbuda',
    'Argentina', 'Armenia', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain',
    'Bangladesh', 'Barbados', 'Belarus', 'Belgium', 'Belize', 'Benin', 'Bhutan', 'Bolivia',
    'Bosnia Herzegovina', 'Botswana', 'Brazil', 'Brunei', 'Bulgaria', 'Burkina', 'Burundi',
    'Cambodia', 'Cameroon', 'Canada', 'Cape Verde', 'Central African Republic', 'Chad',
    'Chile', 'China', 'Colombia', 'Comoros', 'Congo', 'Congo, Democratic Republic', 'Costa Rica',
    'Croatia', 'Cuba', 'Cyprus', 'Czech Republic', 'Denmark', 'Djibouti', 'Dominica',
    'Dominican Republic', 'East Timor', 'Ecuador', 'Egypt', 'El Salvador',
    'Equatorial Guinea', 'Eritrea', 'Estonia', 'Ethiopia', 'Fiji', 'Finland',
    'France', 'Gabon', 'Gambia', 'Georgia', 'Germany', 'Ghana', 'Greece', 'Grenada',
    'Guatemala', 'Guinea', 'Guinea-Bissau', 'Guyana', 'Haiti', 'Honduras', 'Hungary',
    'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'Israel',
    'Italy', 'Ivory Coast', 'Jamaica', 'Japan', 'Jordan', 'Kazakhstan', 'Kenya',
    'Kiribati', 'Kosovo', 'Kuwait', 'Kyrgyzstan', 'Laos', 'Latvia', 'Lebanon', 'Lesotho',
    'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 'Luxembourg', 'Macedonia',
    'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 'Marshall Islands',
    'Mauritania', 'Mauritius', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia',
    'Montenegro', 'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal',
    'Netherlands', 'New Zealand', 'Nicaragua', 'Niger', 'Nigeria', 'North Korea', 'Norway',
    'Oman', 'Pakistan', 'Palau',  'Panama', 'Papua New Guinea', 'Paraguay', 'Peru',
    'Philippines', 'Poland', 'Portugal', 'Qatar', 'Romania', 'Russian Federation', 'Rwanda',
    'Saint Kitts and Nevis', 'St Lucia', 'Saint Vincent & the Grenadines', 'Samoa',
    'San Marino', 'Sao Tome and Principe', 'Saudi Arabia', 'Senegal', 'Serbia', 'Seychelles',
    'Sierra Leone', 'Singapore', 'Slovakia', 'Slovenia', 'Solomon Islands', 'Somalia',
    'South Africa', 'South Korea', 'South Sudan', 'Spain', 'Sri Lanka', 'Sudan', 'Suriname',
    'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania',
    'Thailand', 'Togo', 'Tonga', 'Trinidad and Tobago', 'Tunisia', 'Turkey', 'Turkmenistan',
    'Tuvalu', 'Uganda', 'Ukraine', 'United Arab Emirates', 'United Kingdom', 'United States',
    'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican City', 'Venezuela', 'Vietnam', 'Yemen',
    'Zambia', 'Zimbabwe'
);

-- Create tables
CREATE TABLE Festival."User"(
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    stripe_id VARCHAR(255) NULL,
    role Festival.UserRole NOT NULL
);

COMMENT ON COLUMN Festival."User".stripe_id IS 'Custom id in Stripe necessary for payment, it takes null value for non-customer users';

CREATE TABLE Festival.Restaurant(
    restaurant_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    manager INT NOT NULL,
    opening_at TIME NOT NULL,
    closing_at TIME NOT NULL,
    CONSTRAINT fk_manager FOREIGN KEY (manager) REFERENCES Festival."User"(user_id)
);

COMMENT ON COLUMN Festival.Restaurant.opening_at IS 'Expressed as HH:MM:SS';
COMMENT ON COLUMN Festival.Restaurant.closing_at IS 'Expressed as HH:MM:SS';

CREATE TABLE Festival.Cuisine(
    cuisine_id SERIAL PRIMARY KEY,
    country Festival.Country NULL,
    type VARCHAR(255) NULL,
    restaurant INT NULL,
    CONSTRAINT fk_restaurant FOREIGN KEY (restaurant) REFERENCES Festival.Restaurant(restaurant_id)
);

COMMENT ON COLUMN Festival.Cuisine.type IS 'e.g. seafood, pizza, pub, steakhouse';

CREATE TABLE Festival.Dish(
    dish_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price REAL NOT NULL,
    isDeleted BIT DEFAULT b'0',
    restaurant INT NOT NULL,
    CONSTRAINT fk_restaurant FOREIGN KEY (restaurant) REFERENCES Festival.Restaurant(restaurant_id),
    CHECK (price > 0)
);

COMMENT ON COLUMN Festival.Dish.name IS 'Full name of the dish';

CREATE TABLE Festival.Ingredient(
    ingredient_id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    diet Festival.Diet NOT NULL
);

COMMENT ON COLUMN Festival.Ingredient.diet IS 'Defined by the most strict diet e.g. salad is vegan even if it is eaten by all diets';

CREATE TABLE Festival."Order"(
    order_id SERIAL PRIMARY KEY,
    status Festival.OrderStatus NOT NULL,
    price REAL NOT NULL,
    placedOn TIMESTAMP NOT NULL,
    "user" INT NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY ("user") REFERENCES Festival."User"(user_id) ON DELETE CASCADE,
    CHECK (price >= 0)
);

COMMENT ON COLUMN Festival."Order".price IS 'Total cost of the order spent by the user';
COMMENT ON COLUMN Festival."Order".placedOn IS 'If pending status the date refers to when the order has been created, if completed status the date refers to when the payment took place';

-- Many-to-Many tables
CREATE TABLE Festival.Dish_Ingredient(
    dish INT NOT NULL,
    ingredient INT NOT NULL,
    PRIMARY KEY (dish, ingredient),
    CONSTRAINT fk_dish FOREIGN KEY (dish) REFERENCES Festival.Dish(dish_id),
    CONSTRAINT fk_ingredient FOREIGN KEY (ingredient) REFERENCES Festival.Ingredient(ingredient_id)
);

CREATE TABLE Festival.Order_Dish(
    "order" INT NOT NULL,
    dish INT NOT NULL,
    quantity INT DEFAULT 1,
    PRIMARY KEY ("order", dish),
    CONSTRAINT fk_order FOREIGN KEY ("order") REFERENCES Festival."Order"(order_id) ON DELETE CASCADE,
    CONSTRAINT fk_dish FOREIGN KEY (dish) REFERENCES Festival.Dish(dish_id),
    CHECK (quantity >= 1)
);