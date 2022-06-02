use rocket::serde::json::Json;
use rocket::{Build, Rocket};
use rocket_cors::CorsOptions;
use serde_json::Value;

pub mod catchers;
pub mod customers;
pub mod managers;

use crate::error::ServerError;
type WebResult = Result<Json<Value>, ServerError>;

pub fn fuel(rocket: Rocket<Build>) -> Rocket<Build> {
    let cors = CorsOptions::default().to_cors().unwrap();

    let rocket = catchers::fuel(rocket);
    let rocket = customers::fuel(rocket);
    let rocket = managers::fuel(rocket);
    rocket.attach(cors)
}
