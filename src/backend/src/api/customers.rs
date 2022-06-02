use rocket::serde::json::Json;
use rocket::{Build, Rocket};
use serde::Deserialize;

use super::WebResult;
use crate::auth::{self, CustomerClaims, Role};
use crate::db::models::customers::{Customer, LoginCustomer, NewCustomer, UpdateCustomer};
use crate::db::models::reservations::{CreateReservation, Reservation};
use crate::db::models::restaurants::Restaurant;
use crate::db::models::reviews::{NewReview, Review};
use crate::db::DbConn;

#[post("/register", format = "json", data = "<new_customer>")]
async fn register(conn: DbConn, new_customer: Json<NewCustomer>) -> WebResult {
    let customer = conn.run(|c| Customer::new(c, new_customer.0)).await?;
    let token = auth::gen_token(customer.id, Role::Customer)?;

    Ok(Json(json!({ "token": token, "customer": customer })))
}

#[post("/login", format = "json", data = "<login_info>")]
async fn login(conn: DbConn, login_info: Json<LoginCustomer>) -> WebResult {
    let customer = conn.run(|c| Customer::login(c, login_info.0)).await?;
    let token = auth::gen_token(customer.id, Role::Customer)?;

    Ok(Json(json!({ "token": token, "customer": customer })))
}

#[put("/", format = "json", data = "<update_customer>")]
async fn update(
    conn: DbConn,
    claims: CustomerClaims,
    update_customer: Json<UpdateCustomer>,
) -> WebResult {
    let customer = conn
        .run(move |c| Customer::update(c, claims.id, update_customer.0))
        .await?;
    Ok(Json(json!({ "customer": customer })))
}

#[delete("/")]
async fn delete(conn: DbConn, claims: CustomerClaims) -> WebResult {
    conn.run(move |c| Customer::delete(c, claims.id)).await?;
    Ok(Json(json!({})))
}

#[derive(Deserialize)]
struct RestaurantID {
    restaurant_id: i32,
}

#[post("/favorite", format = "json", data = "<restaurant_id>")]
async fn toggle_favorite(
    conn: DbConn,
    claims: CustomerClaims,
    restaurant_id: Json<RestaurantID>,
) -> WebResult {
    conn.run(move |c| Customer::toggle_favorite(c, claims.id, restaurant_id.restaurant_id))
        .await?;
    Ok(Json(json!({})))
}

#[get("/favorite")]
async fn get_favorites(conn: DbConn, claims: CustomerClaims) -> WebResult {
    let restaurants = conn
        .run(move |c| Customer::get_favorites(c, claims.id))
        .await?;
    Ok(Json(serde_json::to_value(restaurants).unwrap()))
}

#[get("/restaurants_near/<latitude>/<longitude>/<radius>")]
async fn restaurants_near(
    conn: DbConn,
    claims: CustomerClaims,
    latitude: f32,
    longitude: f32,
    radius: i32,
) -> WebResult {
    let restaurants = conn
        .run(move |c| Restaurant::near(c, claims.id, latitude, longitude, radius))
        .await?;
    Ok(Json(serde_json::to_value(restaurants).unwrap()))
}

#[post("/reservation", format = "json", data = "<reservation>")]
async fn make_reservation(
    conn: DbConn,
    claims: CustomerClaims,
    reservation: Json<CreateReservation>,
) -> WebResult {
    conn.run(move |c| Reservation::new(c, claims.id, reservation.0))
        .await?;
    Ok(Json(json!({})))
}

#[get("/reservation")]
async fn get_reservations(conn: DbConn, claims: CustomerClaims) -> WebResult {
    let reservations = conn
        .run(move |c| Reservation::from_customer(c, claims.id))
        .await?;
    Ok(Json(serde_json::to_value(reservations).unwrap()))
}

#[post("/review", format = "json", data = "<review>")]
async fn make_review(conn: DbConn, _claims: CustomerClaims, review: Json<NewReview>) -> WebResult {
    conn.run(move |c| Review::new(c, review.0)).await?;
    Ok(Json(json!({})))
}

pub fn fuel(rocket: Rocket<Build>) -> Rocket<Build> {
    rocket.mount(
        "/customer",
        routes![
            register,
            login,
            update,
            delete,
            toggle_favorite,
            get_favorites,
            restaurants_near,
            make_reservation,
            get_reservations,
            make_review
        ],
    )
}
