use diesel::prelude::*;
use diesel::result::{DatabaseErrorKind, Error as DieselError};
use diesel::MysqlConnection;
use rocket::serde::{Deserialize, Serialize};

use super::restaurants::{Restaurant, RestaurantFullInfoWithFav};
use crate::db::schema::{customers, favorites, restaurants};
use crate::error::{ServerError, ServerResult};

#[derive(Queryable, Debug, Serialize)]
pub struct Customer {
    pub id: i32,
    pub username: String,
    pub email: String,
    #[serde(skip_serializing)]
    pub password: String,
}

#[derive(Insertable, Deserialize)]
#[table_name = "customers"]
pub struct NewCustomer {
    pub username: String,
    pub email: String,
    pub password: String,
}

#[derive(AsChangeset, Deserialize, Debug)]
#[table_name = "customers"]
pub struct UpdateCustomer {
    pub username: Option<String>,
    pub password: Option<String>,
}

#[derive(Deserialize)]
pub struct LoginCustomer {
    pub email: String,
    pub password: String,
}

#[derive(Queryable, Debug, Identifiable, Insertable, Associations)]
#[belongs_to(Customer)]
#[belongs_to(Restaurant)]
#[primary_key(customer_id, restaurant_id)]
pub struct Favorite {
    pub customer_id: i32,
    pub restaurant_id: i32,
}

impl Customer {
    pub fn new(conn: &MysqlConnection, new_customer: NewCustomer) -> ServerResult<Customer> {
        conn.transaction(|| {
            diesel::insert_into(customers::table)
                .values(&new_customer)
                .execute(conn)?;

            customers::table.order(customers::id.desc()).first(conn)
        })
        .map_err(|err| match err {
            DieselError::DatabaseError(DatabaseErrorKind::UniqueViolation, _) => {
                ServerError::AlreadyRegisteredError
            }
            _ => ServerError::DbError(format!("{}", err)),
        })
    }

    pub fn login(conn: &MysqlConnection, login_info: LoginCustomer) -> ServerResult<Customer> {
        let customer: Customer = customers::table
            .filter(customers::email.eq(login_info.email))
            .first(conn)
            .map_err(|_| ServerError::NotRegisteredError)?;

        if customer.password == login_info.password {
            Ok(customer)
        } else {
            Err(ServerError::WrongPasswordError)
        }
    }

    pub fn update(
        conn: &MysqlConnection,
        id: i32,
        update_customer: UpdateCustomer,
    ) -> ServerResult<Customer> {
        diesel::update(customers::table.find(id))
            .set(update_customer)
            .execute(conn)?;

        Ok(customers::table.find(id).first(conn)?)
    }

    pub fn delete(conn: &MysqlConnection, id: i32) -> ServerResult<()> {
        diesel::delete(customers::table.find(id)).execute(conn)?;

        Ok(())
    }

    pub fn toggle_favorite(
        conn: &MysqlConnection,
        customer_id: i32,
        restaurant_id: i32,
    ) -> ServerResult<()> {
        let fav = Favorite {
            customer_id,
            restaurant_id,
        };

        conn.transaction(|| {
            let exists = diesel::dsl::select(diesel::dsl::exists(
                favorites::table.filter(
                    favorites::customer_id
                        .eq(customer_id)
                        .and(favorites::restaurant_id.eq(restaurant_id)),
                ),
            ))
            .get_result::<bool>(conn)?;

            if exists {
                diesel::delete(favorites::table.find((customer_id, restaurant_id)))
                    .execute(conn)?;
            } else {
                diesel::insert_into(favorites::table)
                    .values(fav)
                    .execute(conn)?;
            }
            Ok(())
        })
    }

    pub fn get_favorites(
        conn: &MysqlConnection,
        customer_id: i32,
    ) -> ServerResult<Vec<RestaurantFullInfoWithFav>> {
        let restaurants = Restaurant::get_full_info_with_fav(
            conn,
            customer_id,
            favorites::table
                .inner_join(restaurants::table)
                .filter(favorites::customer_id.eq(customer_id))
                .select(restaurants::all_columns)
                .get_results::<Restaurant>(conn)?,
        )?;

        Ok(restaurants)
    }
}
