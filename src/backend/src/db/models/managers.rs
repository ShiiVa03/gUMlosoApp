use diesel::mysql::MysqlConnection;
use diesel::prelude::*;
use diesel::result::{DatabaseErrorKind, Error as DieselError};
use rocket::serde::{Deserialize, Serialize};

use crate::db::schema::managers;
use crate::error::{ServerError, ServerResult};

use super::restaurants::CreateRestaurant;

#[derive(Queryable, Debug, Serialize)]
pub struct Manager {
    pub id: i32,
    pub username: String,
    pub email: String,
    pub password: String,
}

#[derive(Deserialize)]
pub struct LoginManager {
    pub email: String,
    pub password: String,
}

#[derive(Insertable, Deserialize)]
#[table_name = "managers"]
pub struct NewManager {
    pub username: String,
    pub email: String,
    pub password: String,
}

#[derive(AsChangeset, Deserialize)]
#[table_name = "managers"]
pub struct UpdateManager {
    pub username: Option<String>,
    pub password: Option<String>,
}

#[derive(Deserialize)]
pub struct CreateManager {
    pub manager: NewManager,
    pub restaurant: CreateRestaurant,
}

impl Manager {
    pub fn new(conn: &MysqlConnection, new_manager: NewManager) -> ServerResult<Manager> {
        conn.transaction(|| {
            diesel::insert_into(managers::table)
                .values(&new_manager)
                .execute(conn)?;

            managers::table.order(managers::id.desc()).first(conn)
        })
        .map_err(|err| match err {
            DieselError::DatabaseError(DatabaseErrorKind::UniqueViolation, _) => {
                ServerError::AlreadyRegisteredError
            }
            _ => ServerError::DbError(format!("{}", err)),
        })
    }

    pub fn login(conn: &MysqlConnection, login_info: LoginManager) -> ServerResult<Manager> {
        let manager: Manager = managers::table
            .filter(managers::email.eq(login_info.email))
            .first(conn)
            .map_err(|_| ServerError::NotRegisteredError)?;

        if manager.password == login_info.password {
            Ok(manager)
        } else {
            Err(ServerError::WrongPasswordError)
        }
    }

    pub fn insert(conn: &MysqlConnection, new_manager: NewManager) -> ServerResult<Manager> {
        conn.transaction(|| {
            diesel::insert_into(managers::table)
                .values(&new_manager)
                .execute(conn)?;

            managers::table.order(managers::id.desc()).first(conn)
        })
        .map_err(|err| match err {
            DieselError::DatabaseError(DatabaseErrorKind::UniqueViolation, _) => {
                ServerError::AlreadyRegisteredError
            }
            _ => ServerError::DbError(format!("{}", err)),
        })
    }

    pub fn update(
        conn: &MysqlConnection,
        id: i32,
        update_manager: UpdateManager,
    ) -> ServerResult<Manager> {
        diesel::update(managers::table.find(id))
            .set(update_manager)
            .execute(conn)
            .map_err(|err| ServerError::DbError(format!("{}", err)))?;

        managers::table
            .find(id)
            .first(conn)
            .map_err(|err| ServerError::DbError(format!("{}", err)))
    }

    pub fn delete(conn: &MysqlConnection, id: i32) -> ServerResult<()> {
        diesel::delete(managers::table.find(id))
            .execute(conn)
            .map(|_| ())
            .map_err(|err| ServerError::DbError(format!("{}", err)))
    }
}
