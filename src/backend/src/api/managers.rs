use rocket::serde::json::Json;
use rocket::{Build, Rocket};

use super::WebResult;
use crate::db::models::reservations::Reservation;
use crate::db::models::restaurants::Restaurant;

use crate::db::models::{
    managers::{CreateManager, LoginManager, Manager, UpdateManager},
    restaurants::{CreateRestaurant, EditRestaurant},
};
use crate::db::DbConn;
use crate::{
    auth::{self, ManagerClaims, Role},
    bing_helpers,
};

#[post("/register", format = "json", data = "<new_manager>")]
async fn register(conn: DbConn, new_manager: Json<CreateManager>) -> WebResult {
    let manager = conn.run(|c| Manager::new(c, new_manager.0.manager)).await?;
    let token = auth::gen_token(manager.id, Role::Manager)?;

    let coords = bing_helpers::coords_from_address(&new_manager.0.restaurant.address).await?;
    conn.run(move |c| Restaurant::new(c, manager.id, new_manager.0.restaurant, coords))
        .await?;

    Ok(Json(json!({
        "token": token,
        "manager": manager,
    })))
}

#[post("/login", format = "json", data = "<login_info>")]
async fn login(conn: DbConn, login_info: Json<LoginManager>) -> WebResult {
    let manager = conn.run(|c| Manager::login(c, login_info.0)).await?;
    let token = auth::gen_token(manager.id, Role::Manager)?;

    Ok(Json(json!({ "token": token, "manager": manager })))
}

#[put("/", format = "json", data = "<update_manager>")]
async fn update(
    conn: DbConn,
    claims: ManagerClaims,
    update_manager: Json<UpdateManager>,
) -> WebResult {
    let manager = conn
        .run(move |c| Manager::update(c, claims.id, update_manager.0))
        .await?;
    Ok(Json(json!({ "manager": manager })))
}

#[delete("/")]
async fn delete(conn: DbConn, claims: ManagerClaims) -> WebResult {
    conn.run(move |c| Manager::delete(c, claims.id)).await?;
    Ok(Json(json!({})))
}

#[post("/restaurant", format = "json", data = "<new_restaurant>")]
async fn add_restaurant(
    conn: DbConn,
    claims: ManagerClaims,
    new_restaurant: Json<CreateRestaurant>,
) -> WebResult {
    let coords = bing_helpers::coords_from_address(&new_restaurant.address).await?;
    conn.run(move |c| Restaurant::new(c, claims.id, new_restaurant.0, coords))
        .await?;
    Ok(Json(json!({})))
}

#[put(
    "/restaurant/<restaurant_id>",
    format = "json",
    data = "<edit_restaurant>"
)]
async fn update_restaurant(
    conn: DbConn,
    _claims: ManagerClaims,
    restaurant_id: i32,
    edit_restaurant: Json<EditRestaurant>,
) -> WebResult {
    conn.run(move |c| Restaurant::update(c, restaurant_id, edit_restaurant.0))
        .await?;
    Ok(Json(json!({})))
}

#[get("/restaurant")]
async fn get_restaurants(conn: DbConn, claims: ManagerClaims) -> WebResult {
    let restaurants = conn
        .run(move |c| Restaurant::from_manager(c, claims.id))
        .await?;
    Ok(Json(serde_json::to_value(restaurants).unwrap()))
}

#[get("/reservation")]
async fn get_reservations(conn: DbConn, claims: ManagerClaims) -> WebResult {
    let reservations = conn
        .run(move |c| Reservation::for_manager_restaurants(c, claims.id))
        .await?;
    Ok(Json(serde_json::to_value(reservations).unwrap()))
}

pub fn fuel(rocket: Rocket<Build>) -> Rocket<Build> {
    rocket.mount(
        "/manager",
        routes![
            register,
            login,
            update,
            delete,
            add_restaurant,
            update_restaurant,
            get_restaurants,
            get_reservations
        ],
    )
}
