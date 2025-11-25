
# API Documentation

## People Endpoints

- **GET** `http://localhost:3000/api/people`  
  Returns all stored people.

- **GET** `http://localhost:3000/api/people?LastName=Stroh`  
  Returns all people with the last name "Stroh".

- **GET** `http://localhost:3000/api/people/?FirstName=Leni`  
  Returns all people with the first name "Leni".

- **DELETE** `http://localhost:3000/api/people/1`  
  Deletes Person with ID 1

- **GET** `http://localhost:3000/api/people/?DOB=1937-11-21`  
  Returns all people with the date of birth "1937-11-21".

- **POST** `http://localhost:3000/api/people/`  
  Creates a new person (either with DOB if someone with the same name exists or without).  
  **Content-Type:** `application/json`  
  Example:
  ```json
  {
    "FirstName": "Maria",
    "LastName": "Römer",
    "DOB": "1937-03-14"
  }
  ```
  OR
  ```json
  {
    "FirstName": "Maria",
    "LastName": "Römer"
  }
  ```

## Allergens Endpoints

- **GET** `http://localhost:3000/api/allergens`  
  Returns all allergens and their abbreviations.

- **GET** `http://localhost:3000/api/allergens/M`  
  Returns the allergen with the abbreviation "M".

## Foods Endpoint

- **GET** `http://localhost:3000/api/foods`  
  Returns all foods.

- **GET** `http://localhost:3000/api/foods/name/Topfen`  
  Returns all foods containing "Topfen" in their name.

- **GET** `http://localhost:3000/api/foods/name/Topfen?strict=true`  
  Returns all foods with the exact name "Topfen" (none in this case).

- **GET** `http://localhost:3000/api/foods/name/Topfenpalatschinken?strict=true`  
  Returns all foods with the exact name "Topfenpalatschinken".

- **GET** `http://localhost:3000/api/foods/id/1`  
  Returns the food with ID 1.

- **GET** `http://localhost:3000/api/foods/type/dessert`  
  Returns all foods of a specific type (e.g., desserts).

- **POST** `http://localhost:3000/api/foods`  
  Adds a new food item, including an image.  
  **Content-Type:** `application/json`  
  Example:
  ```json
  {
    "Name": "Pizza Margherita",
    "Picture": {
      "Bytes": "BASE64_ENCODED_IMAGE_DATA",
      "Name": "pizza.jpg",
      "MediaType": "image/jpeg"
    },
    "Type": "main"
  }
  ```

## Menu Endpoints

- **GET** `http://localhost:3000/api/menu/day/3/0`  
  Retrieves the menu for the 0th day (Monday) of the third week.

- **GET** `http://localhost:3000/api/menu/week/3`  
  Retrieves the menu plan for the third week.

- **GET** `http://localhost:3000/api/menu/next-week`  
  Retrieves the menu plan for the next week.

- **DELETE** `http://localhost:3000/api/menu/1`  
  Deletes a Menu Entry by id

- **DELETE** `http://localhost:3000/api/menu/wipe`  
  Deletes all Menu Entries

- **POST** `http://localhost:3000/api/menu/`  
  Creates a new menu entry.  
  **Content-Type:** `application/json`  
  Example:
  ```json
  {
    "WeekNumber": 1,
    "WeekDay": 0,
    "SoupID": 30,
    "M1ID": 4,
    "M2ID": 5,
    "LunchDessertID": 10,
    "A1ID": 9,
    "A2ID": 16
  }
  ```

## Special Meal Endpoints

- **GET** `http://localhost:3000/api/special-meals`  
  Retrieves all special meals.

- **GET** `http://localhost:3000/api/special-meals/2025-01-17`  
  Retrieves a special meal for a specific date.

- **DELETE** `http://localhost:3000/api/special-meals/1`  
  Deletes a special meal by ID.

- **POST** `http://localhost:3000/api/special-meals`  
  Creates a new special meal.  
  **Content-Type:** `application/json`  
  Example:
  ```json
  {
    "Date": "2025-01-17",
    "SoupID": 1,
    "M1ID": 2,
    "M2ID": 4,
    "LunchDessertID": 3,
    "A1ID": 5,
    "A2ID": 6
  }
  ```

## Orders Endpoints

- **GET** `http://localhost:3000/api/orders`  
  Retrieves all orders.

- **GET** `http://localhost:3000/api/orders/1`  
  Retrieves the order with ID 1.

- **GET** `http://localhost:3000/api/orders/date/2025-01-20`  
  Retrieves all orders for a specific date.

- **DELETE** `http://localhost:3000/api/orders/1`  
  Deletes an order by ID.

- **POST** `http://localhost:3000/api/orders/`  
  Creates a new order.  
  **Content-Type:** `application/json`  
  Example:
  ```json
  {
    "Date": "2025-01-20",
    "UserID": 1,
    "MenuID": 1,
    "SelectedLunchID": 4,
    "SelectedDinnerID": 9
  }
  ```
