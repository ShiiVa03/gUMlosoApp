table! {
    #[sql_name = "consumidor"]
    customers {
        #[sql_name = "consumidorID"]
        id -> Integer,
        username -> Varchar,
        email -> Varchar,
        password -> Varchar,
    }
}

table! {
    #[sql_name = "favoritos"]
    favorites (customer_id, restaurant_id) {
        #[sql_name = "consumidorID"]
        customer_id -> Integer,
        #[sql_name = "restaurantID"]
        restaurant_id -> Integer,
    }
}

table! {
    #[sql_name = "gestores"]
    managers {
        #[sql_name = "gestorID"]
        id -> Integer,
        username -> Varchar,
        email -> Varchar,
        password -> Varchar,
    }
}

table! {
    #[sql_name = "horáriosDeFuncionamento"]
    timetables {
        #[sql_name = "idhorárioDeFuncionamento"]
        id -> Integer,
        #[sql_name = "abertura"]
        opening_time -> Integer,
        #[sql_name = "fecho"]
        closing_time -> Integer,
        #[sql_name = "diaDaSemana"]
        weekday -> TinyInt,
        #[sql_name = "restaurantID"]
        restaurant_id -> Integer,
    }
}

table! {
    #[sql_name = "imagens"]
    images {
        #[sql_name = "idimagem"]
        id -> Integer,
        #[sql_name = "restaurantID"]
        restaurant_id -> Integer,
        #[sql_name = "imagem"]
        image -> Blob,
    }
}

table! {
    #[sql_name = "localizações"]
    locations {
        #[sql_name = "localizationID"]
        id -> Integer,
        #[sql_name = "morada"]
        address -> Varchar,
        latitude -> Float,
        longitude -> Float,
    }
}

table! {
    #[sql_name = "reservas"]
    reservations {
        #[sql_name = "reservaID"]
        id -> Integer,
        #[sql_name = "diaHora"]
        date -> Datetime,
        #[sql_name = "numeroDePessoas"]
        number_of_people -> Integer,
        #[sql_name = "consumidorID"]
        customer_id -> Integer,
        #[sql_name = "restaurantID"]
        restaurant_id -> Integer,
    }
}

table! {
    #[sql_name = "restaurantes"]
    restaurants {
        #[sql_name = "restaurantID"]
        id -> Integer,
        #[sql_name = "nomeRestaurante"]
        name -> Varchar,
        #[sql_name = "pontuação"]
        rating -> Float,
        #[sql_name = "contacto"]
        contact -> Varchar,
        #[sql_name = "capacidade"]
        capacity -> Integer,
        #[sql_name = "localizationID"]
        location_id -> Integer,
        #[sql_name = "gestorID"]
        manager_id -> Integer,
    }
}

table! {
    reviews {
        #[sql_name = "reviewID"]
        id -> Integer,
        #[sql_name = "stars"]
        score -> Integer,
        #[sql_name = "descrição"]
        description -> Nullable<Varchar>,
        #[sql_name = "restaurantID"]
        restaurant_id -> Integer,
    }
}

table! {
    #[sql_name = "tiposDeComida"]
    food_types {
        #[sql_name = "idtipoDeComida"]
        id -> Integer,
        #[sql_name = "tipoDeComida"]
        food_type -> Varchar,
        #[sql_name = "restaurantID"]
        restaurant_id -> Integer,
    }
}

joinable!(favorites -> customers (customer_id));
joinable!(favorites -> restaurants (restaurant_id));
joinable!(timetables -> restaurants (restaurant_id));
joinable!(images -> restaurants (restaurant_id));
joinable!(reservations -> customers (customer_id));
joinable!(reservations -> restaurants (restaurant_id));
joinable!(restaurants -> managers (manager_id));
joinable!(restaurants -> locations (location_id));
joinable!(reviews -> restaurants (restaurant_id));
joinable!(food_types -> restaurants (restaurant_id));

allow_tables_to_appear_in_same_query!(
    customers,
    favorites,
    managers,
    timetables,
    images,
    locations,
    reservations,
    restaurants,
    reviews,
    food_types,
);
