use diesel::{dsl::sql, mysql::MysqlConnection};
use diesel::{prelude::*, sql_types::Bool};
use itertools::izip;
use rocket::serde::{Deserialize, Serialize};

use super::managers::Manager;
use crate::db::schema::{favorites, food_types, images, locations, restaurants, timetables};
use crate::error::ServerResult;

#[derive(Identifiable, Queryable, Debug, Associations, Serialize)]
#[belongs_to(Manager)]
#[belongs_to(Location)]
pub struct Restaurant {
    pub id: i32,
    pub name: String,
    pub rating: f32,
    pub contact: String,
    pub capacity: i32,
    #[serde(skip_serializing)]
    pub location_id: i32,
    #[serde(skip_serializing)]
    pub manager_id: i32,
}

#[derive(Insertable, Deserialize)]
#[table_name = "restaurants"]
pub struct NewRestaurant {
    pub name: String,
    pub contact: Option<String>,
    pub capacity: i32,
    pub location_id: i32,
    pub manager_id: i32,
}

#[derive(AsChangeset, Deserialize)]
#[table_name = "restaurants"]
pub struct UpdateRestaurant {
    pub name: Option<String>,
    pub rating: Option<f32>,
    pub contact: Option<String>,
    pub capacity: Option<i32>,
}

#[derive(Identifiable, Queryable, Debug, Associations, Serialize)]
#[belongs_to(Restaurant)]
pub struct Timetable {
    #[serde(skip_serializing)]
    pub id: i32,
    pub opening_time: i32,
    pub closing_time: i32,
    pub weekday: i8,
    #[serde(skip_serializing)]
    pub restaurant_id: i32,
}

#[derive(Insertable)]
#[table_name = "timetables"]
pub struct NewTimetable {
    pub opening_time: i32,
    pub closing_time: i32,
    pub weekday: i8,
    pub restaurant_id: i32,
}

#[derive(Identifiable, Queryable, Debug, Associations, Serialize)]
#[belongs_to(Restaurant)]
pub struct Image {
    #[serde(skip_serializing)]
    pub id: i32,
    #[serde(skip_serializing)]
    pub restaurant_id: i32,
    pub image: Vec<u8>,
}

#[derive(Insertable)]
#[table_name = "images"]
pub struct NewImage {
    pub restaurant_id: i32,
    pub image: Vec<u8>,
}

#[derive(Identifiable, Queryable, Debug, QueryableByName, Serialize)]
#[table_name = "locations"]
pub struct Location {
    #[serde(skip_serializing)]
    pub id: i32,
    pub address: String,
    pub latitude: f32,
    pub longitude: f32,
}

#[derive(Insertable)]
#[table_name = "locations"]
pub struct NewLocation {
    pub address: String,
    pub latitude: f32,
    pub longitude: f32,
}

#[derive(Identifiable, Queryable, Debug, Associations, Serialize)]
#[belongs_to(Restaurant)]
pub struct FoodType {
    #[serde(skip_serializing)]
    pub id: i32,
    pub food_type: String,
    #[serde(skip_serializing)]
    pub restaurant_id: i32,
}

#[derive(Insertable)]
#[table_name = "food_types"]
pub struct NewFoodType {
    pub food_type: String,
    pub restaurant_id: i32,
}

#[derive(Deserialize)]
pub struct CreateRestaurant {
    pub name: String,
    pub image: Vec<i8>,
    pub address: String,
    pub contact: Option<String>,
    pub food_type: String,
    pub timetable: Vec<CreateTimetable>,
    pub capacity: i32,
}

#[derive(Deserialize)]
pub struct EditRestaurant {
    pub image: Option<Vec<i8>>,
    pub food_type: Option<String>,
    pub timetable: Option<Vec<CreateTimetable>>,
    pub capacity: Option<i32>,
}

#[derive(Deserialize)]
pub struct CreateTimetable {
    pub opening_time: i32,
    pub closing_time: i32,
    pub weekday: i8,
}

#[derive(Serialize)]
pub struct RestaurantFullInfo {
    #[serde(flatten)]
    pub restaurant: Restaurant,
    pub location: Location,
    pub timetable: Vec<Timetable>,
    #[serde(flatten)]
    pub image: Image,
    #[serde(flatten)]
    pub food_type: FoodType,
}

#[derive(Serialize, Debug)]
pub struct RestaurantFullInfoWithFav {
    #[serde(flatten)]
    pub restaurant: Restaurant,
    pub location: Location,
    pub timetable: Vec<Timetable>,
    #[serde(flatten)]
    pub image: Image,
    #[serde(flatten)]
    pub food_type: FoodType,
    pub favorite: bool,
}

fn vec_i8_into_u8(v: Vec<i8>) -> Vec<u8> {
    // ideally we'd use Vec::into_raw_parts, but it's unstable,
    // so we have to do it manually:

    // first, make sure v's destructor doesn't free the data
    // it thinks it owns when it goes out of scope
    let mut v = std::mem::ManuallyDrop::new(v);

    // then, pick apart the existing Vec
    let p = v.as_mut_ptr();
    let len = v.len();
    let cap = v.capacity();

    // finally, adopt the data into a new Vec
    unsafe { Vec::from_raw_parts(p as *mut u8, len, cap) }
}

impl Restaurant {
    pub fn new(
        conn: &MysqlConnection,
        manager_id: i32,
        restaurant_info: CreateRestaurant,
        (latitude, longitude): (f32, f32),
    ) -> ServerResult<()> {
        conn.transaction(|| {
            let location = NewLocation {
                address: restaurant_info.address,
                latitude,
                longitude,
            };

            diesel::insert_into(locations::table)
                .values(&location)
                .execute(conn)?;

            let location_id = locations::table
                .order(locations::id.desc())
                .select(locations::id)
                .first(conn)?;

            let new_restaurant = NewRestaurant {
                name: restaurant_info.name,
                contact: restaurant_info.contact,
                capacity: restaurant_info.capacity,
                location_id,
                manager_id,
            };

            diesel::insert_into(restaurants::table)
                .values(&new_restaurant)
                .execute(conn)?;

            let restaurant_id = restaurants::table
                .order(restaurants::id.desc())
                .select(restaurants::id)
                .first(conn)?;

            let food_type = NewFoodType {
                food_type: restaurant_info.food_type,
                restaurant_id,
            };

            diesel::insert_into(food_types::table)
                .values(&food_type)
                .execute(conn)?;

            let image = NewImage {
                image: vec_i8_into_u8(restaurant_info.image),
                restaurant_id,
            };

            diesel::insert_into(images::table)
                .values(&image)
                .execute(conn)?;

            let timetable: Vec<NewTimetable> = restaurant_info
                .timetable
                .iter()
                .map(|t| NewTimetable {
                    opening_time: t.opening_time,
                    closing_time: t.closing_time,
                    weekday: t.weekday.clone(),
                    restaurant_id,
                })
                .collect();

            diesel::insert_into(timetables::table)
                .values(&timetable)
                .execute(conn)?;

            Ok(())
        })
    }

    pub fn update(
        conn: &MysqlConnection,
        restaurant_id: i32,
        edit_restaurant: EditRestaurant,
    ) -> ServerResult<()> {
        conn.transaction(|| {
            if let Some(image) = edit_restaurant.image {
                diesel::delete(images::table.filter(images::restaurant_id.eq(restaurant_id)))
                    .execute(conn)?;
                diesel::insert_into(images::table)
                    .values(NewImage {
                        image: vec_i8_into_u8(image),
                        restaurant_id,
                    })
                    .execute(conn)?;
            }

            if let Some(food_type) = edit_restaurant.food_type {
                diesel::delete(
                    food_types::table.filter(food_types::restaurant_id.eq(restaurant_id)),
                )
                .execute(conn)?;
                diesel::insert_into(food_types::table)
                    .values(NewFoodType {
                        food_type,
                        restaurant_id,
                    })
                    .execute(conn)?;
            }

            if let Some(timetable) = edit_restaurant.timetable {
                diesel::delete(
                    timetables::table.filter(timetables::restaurant_id.eq(restaurant_id)),
                )
                .execute(conn)?;

                let timetable: Vec<NewTimetable> = timetable
                    .iter()
                    .map(|t| NewTimetable {
                        opening_time: t.opening_time,
                        closing_time: t.closing_time,
                        weekday: t.weekday.clone(),
                        restaurant_id,
                    })
                    .collect();

                diesel::insert_into(timetables::table)
                    .values(&timetable)
                    .execute(conn)?;
            }

            let restaurant = UpdateRestaurant {
                capacity: edit_restaurant.capacity,
                ..Default::default()
            };
            diesel::update(restaurants::table.find(restaurant_id))
                .set(restaurant)
                .execute(conn)?;

            Ok(())
        })
    }

    pub fn get_full_info(
        conn: &MysqlConnection,
        restaurants: Vec<Restaurant>,
    ) -> ServerResult<Vec<RestaurantFullInfo>> {
        let locations: Vec<Location> = restaurants
            .iter()
            .map(|r| {
                locations::table
                    .filter(locations::id.eq(r.location_id))
                    .first::<Location>(conn)
                    .unwrap()
            })
            .collect();

        let timetable = Timetable::belonging_to(&restaurants)
            .load::<Timetable>(conn)?
            .grouped_by(&restaurants);

        let images = Image::belonging_to(&restaurants)
            .load::<Image>(conn)?
            .grouped_by(&restaurants)
            .into_iter()
            .flatten();

        let food_types = FoodType::belonging_to(&restaurants)
            .load::<FoodType>(conn)?
            .grouped_by(&restaurants)
            .into_iter()
            .flatten();

        Ok(izip!(restaurants, locations, timetable, images, food_types)
            .map(
                |(restaurant, location, timetable, image, food_type)| RestaurantFullInfo {
                    restaurant,
                    location,
                    timetable,
                    image,
                    food_type,
                },
            )
            .collect())
    }

    pub fn get_full_info_with_fav(
        conn: &MysqlConnection,
        customer_id: i32,
        restaurants: Vec<Restaurant>,
    ) -> ServerResult<Vec<RestaurantFullInfoWithFav>> {
        let locations: Vec<Location> = restaurants
            .iter()
            .map(|r| {
                locations::table
                    .filter(locations::id.eq(r.location_id))
                    .first::<Location>(conn)
                    .unwrap()
            })
            .collect();

        let timetables = Timetable::belonging_to(&restaurants)
            .load::<Timetable>(conn)?
            .grouped_by(&restaurants);

        let images = Image::belonging_to(&restaurants)
            .load::<Image>(conn)?
            .grouped_by(&restaurants)
            .into_iter()
            .flatten();

        let food_types = FoodType::belonging_to(&restaurants)
            .load::<FoodType>(conn)?
            .grouped_by(&restaurants)
            .into_iter()
            .flatten();

        let favorites: Vec<bool> = restaurants
            .iter()
            .map(|r| {
                diesel::dsl::select(diesel::dsl::exists(
                    favorites::table.filter(
                        favorites::restaurant_id
                            .eq(r.id)
                            .and(favorites::customer_id.eq(customer_id)),
                    ),
                ))
                .first(conn)
                .unwrap()
            })
            .collect();

        Ok(izip!(
            restaurants,
            locations,
            timetables,
            images,
            food_types,
            favorites
        )
        .map(
            |(restaurant, location, timetable, image, food_type, favorite)| {
                RestaurantFullInfoWithFav {
                    restaurant,
                    location,
                    timetable,
                    image,
                    food_type,
                    favorite,
                }
            },
        )
        .collect())
    }

    pub fn near(
        conn: &MysqlConnection,
        customer_id: i32,
        latitude: f32,
        longitude: f32,
        radius: i32,
    ) -> ServerResult<Vec<RestaurantFullInfoWithFav>> {
        let locations = locations::table
            .select(locations::all_columns)
            .filter(
                    sql::<Bool>(&format!(
                        "(6371.0 * acos(cos(radians({lat})) * cos(radians(latitude)) * cos(radians(longitude) - radians({long})) + sin(radians({lat})) * sin(radians(latitude)))) <= {radius}",
                         lat = latitude,
                         long = longitude,
                         radius = radius,
                )))
            .load::<Location>(conn)?;

        let restaurants = Restaurant::belonging_to(&locations).load::<Restaurant>(conn)?;
        let restaurants = Self::get_full_info_with_fav(conn, customer_id, restaurants)?;

        Ok(restaurants)
    }

    pub fn from_manager(conn: &MysqlConnection, id: i32) -> ServerResult<Vec<RestaurantFullInfo>> {
        let restaurants = restaurants::table
            .filter(restaurants::manager_id.eq(id))
            .get_results::<Restaurant>(conn)?;

        Self::get_full_info(conn, restaurants)
    }
}

impl Default for UpdateRestaurant {
    fn default() -> Self {
        UpdateRestaurant {
            name: None,
            rating: None,
            contact: None,
            capacity: None,
        }
    }
}
