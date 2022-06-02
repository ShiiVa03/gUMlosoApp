#[macro_use]
extern crate diesel;
#[macro_use]
extern crate rocket;
#[macro_use]
extern crate serde_json;

pub mod api;
pub mod auth;
pub mod bing_helpers;
pub mod db;
pub mod error;

#[rocket::main]
async fn main() -> Result<(), rocket::Error> {
    dotenv::dotenv().ok();

    let rocket = rocket::build().attach(db::DbConn::fairing());
    let rocket = api::fuel(rocket);
    rocket.ignite().await?.launch().await
}
