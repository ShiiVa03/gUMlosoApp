use bigdecimal::{BigDecimal, ToPrimitive};
use diesel::dsl::avg;
use diesel::mysql::MysqlConnection;
use diesel::prelude::*;
use rocket::serde::Deserialize;

use super::restaurants::{Restaurant, UpdateRestaurant};
use crate::db::schema::{restaurants, reviews};
use crate::error::ServerResult;

#[derive(Queryable, Debug, Associations)]
#[belongs_to(Restaurant)]
pub struct Review {
    pub id: i32,
    pub score: i32,
    pub description: Option<String>,
    pub restaurant_id: i32,
}

#[derive(Insertable, Deserialize)]
#[table_name = "reviews"]
pub struct NewReview {
    pub score: i32,
    pub description: Option<String>,
    pub restaurant_id: i32,
}

impl Review {
    pub fn new(conn: &MysqlConnection, new_review: NewReview) -> ServerResult<()> {
        conn.transaction(|| {
            diesel::insert_into(reviews::table)
                .values(&new_review)
                .execute(conn)?;

            let avg: Option<BigDecimal> = reviews::table
                .filter(reviews::restaurant_id.eq(new_review.restaurant_id))
                .select(avg(reviews::score))
                .get_result(conn)?;

            let update = UpdateRestaurant {
                rating: Some(avg.and_then(|x| x.to_f32()).unwrap_or(0.)),
                ..Default::default()
            };

            diesel::update(restaurants::table.find(new_review.restaurant_id))
                .set(update)
                .execute(conn)?;

            Ok(())
        })
    }
}
