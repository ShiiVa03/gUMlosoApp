use serde_json::Value;
use std::env;

use crate::error::{ServerError, ServerResult};

pub async fn coords_from_address(address: &str) -> ServerResult<(f32, f32)> {
    let key = env::var("BING_MAPS_KEY").unwrap();

    let url = format!(
        "http://dev.virtualearth.net/REST/v1/Locations/{}?includemaxResults=1&key={}",
        address, key
    );

    let result = reqwest::get(url)
        .await
        .unwrap()
        .text()
        .await
        .map_err(|_| ServerError::LocationNotFoundError)?;

    let v: Value = serde_json::from_str(&result).map_err(|_| ServerError::LocationNotFoundError)?;
    let coords = &v["resourceSets"][0]["resources"][0]["point"]["coordinates"];

    let x = coords[0]
        .as_f64()
        .ok_or(ServerError::LocationNotFoundError)?;
    let y = coords[1]
        .as_f64()
        .ok_or(ServerError::LocationNotFoundError)?;

    Ok((x as f32, y as f32))
}
