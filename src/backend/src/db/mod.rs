use rocket_sync_db_pools::{database, diesel};

pub mod models;
pub mod schema;

#[database("mysql_db")]
pub struct DbConn(diesel::MysqlConnection);
