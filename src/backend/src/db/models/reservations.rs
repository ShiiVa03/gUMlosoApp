use chrono::naive::serde::ts_milliseconds::serialize as to_milli_ts;
use chrono::{Datelike, NaiveDateTime, Timelike};
use diesel::mysql::MysqlConnection;
use diesel::prelude::*;
use rocket::serde::Deserialize;
use serde::Serialize;

use super::restaurants::{Restaurant, RestaurantFullInfoWithFav, Timetable};
use crate::db::schema::{customers, reservations, restaurants, timetables};
use crate::error::{ServerError, ServerResult};

#[derive(Queryable, Debug, Serialize)]
pub struct Reservation {
    #[serde(skip_serializing)]
    pub id: i32,
    #[serde(serialize_with = "to_milli_ts")]
    pub date: NaiveDateTime,
    pub number_of_people: i32,
    #[serde(skip_serializing)]
    pub customer_id: i32,
    #[serde(skip_serializing)]
    pub restaurant_id: i32,
}

#[derive(Insertable)]
#[table_name = "reservations"]
pub struct NewReservation {
    pub date: NaiveDateTime,
    pub number_of_people: i32,
    pub customer_id: i32,
    pub restaurant_id: i32,
}

#[derive(Deserialize)]
pub struct CreateReservation {
    pub date: NaiveDateTime,
    pub number_of_people: i32,
    pub restaurant_id: i32,
}

#[derive(Serialize)]
pub struct ManagerReservation {
    pub reservation: Reservation,
    pub customer_username: String,
}

#[derive(Serialize, Debug)]
pub struct CustomerReservationInfo {
    pub reservation: Reservation,
    pub restaurant: RestaurantFullInfoWithFav,
}

impl Reservation {
    pub fn new(
        conn: &MysqlConnection,
        customer_id: i32,
        reservation: CreateReservation,
    ) -> ServerResult<()> {
        conn.transaction(|| {
            let restaurant: Restaurant = restaurants::table
                .find(reservation.restaurant_id)
                .get_result(conn)?;

            let weekday = reservation.date.weekday().number_from_monday() as i8;

            let restaurant_timetable: Timetable = Timetable::belonging_to(&restaurant)
                .filter(timetables::weekday.eq(weekday))
                .first(conn)
                .map_err(|_| ServerError::InvalidReservationDateError)?;

            let reservation_time =
                (reservation.date.time().num_seconds_from_midnight() / 60) as i32;

            if reservation_time > restaurant_timetable.opening_time
                && reservation_time < restaurant_timetable.closing_time
            {
                let new_reservation = NewReservation {
                    date: reservation.date,
                    number_of_people: reservation.number_of_people,
                    customer_id,
                    restaurant_id: reservation.restaurant_id,
                };
                diesel::insert_into(reservations::table)
                    .values(&new_reservation)
                    .execute(conn)?;

                Ok(())
            } else {
                Err(ServerError::InvalidReservationDateError)
            }
        })
    }

    pub fn from_customer(
        conn: &MysqlConnection,
        customer_id: i32,
    ) -> ServerResult<Vec<CustomerReservationInfo>> {
        let reservations = reservations::table
            .inner_join(restaurants::table)
            .filter(reservations::customer_id.eq(customer_id))
            .select((reservations::all_columns, restaurants::all_columns))
            .get_results::<(Reservation, Restaurant)>(conn)?
            .into_iter()
            .map(|(reservation, restaurant)| CustomerReservationInfo {
                reservation,
                restaurant: Restaurant::get_full_info_with_fav(conn, customer_id, vec![restaurant])
                    .unwrap()
                    .pop()
                    .unwrap(),
            })
            .collect();

        Ok(reservations)
    }

    pub fn for_manager_restaurants(
        conn: &MysqlConnection,
        manager_id: i32,
    ) -> ServerResult<Vec<ManagerReservation>> {
        let reservations = reservations::table
            .inner_join(restaurants::table)
            .inner_join(customers::table)
            .filter(restaurants::manager_id.eq(manager_id))
            .select((reservations::all_columns, customers::username))
            .get_results::<(Reservation, String)>(conn)?
            .into_iter()
            .map(|(reservation, customer_username)| ManagerReservation {
                reservation,
                customer_username,
            })
            .collect();

        Ok(reservations)
    }
}
