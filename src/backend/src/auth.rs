use jsonwebtoken::errors::Result;
use jsonwebtoken::{
    decode, encode, Algorithm, DecodingKey, EncodingKey, Header, TokenData, Validation,
};
use rocket::http::Status;
use rocket::request::{FromRequest, Outcome};
use rocket::Request;
use serde::{Deserialize, Serialize};
use std::ops::{Deref, DerefMut};

use crate::error::{ServerError, ServerResult};

const BEARER: &str = "Bearer ";
const SECRET: &[u8] = b"secret"; // In a real system, this would be a long,
                                 // securely stored string that is changed
                                 // regularly.

#[derive(Debug, Serialize, Deserialize, PartialEq)]
pub enum Role {
    Customer,
    Manager,
}

#[derive(Debug)]
pub enum ApiKeyError {
    Missing,
    Invalid,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Claims {
    pub id: i32,
    pub role: Role,
    exp: usize,
}

#[rocket::async_trait]
impl<'r> FromRequest<'r> for Claims {
    type Error = ApiKeyError;
    async fn from_request(request: &'r Request<'_>) -> Outcome<Self, Self::Error> {
        let auth_header = request
            .headers()
            .get_one("Authorization")
            .ok_or((Status::BadRequest, ApiKeyError::Missing))
            .map(|x| x.to_string())
            .and_then(|x| {
                if x.starts_with(BEARER) {
                    Ok(x)
                } else {
                    Err((Status::BadRequest, ApiKeyError::Missing))
                }
            })
            .map(|x| x[7..x.len()].trim().to_owned())
            .and_then(|x| decode_token(&x).map_err(|_| (Status::Forbidden, ApiKeyError::Invalid)))
            .map(|x| x.claims);

        match auth_header {
            Ok(c) => Outcome::Success(c),
            Err(e) => Outcome::Failure(e),
        }
    }
}

pub struct CustomerClaims(Claims);

impl Deref for CustomerClaims {
    type Target = Claims;
    fn deref(&self) -> &Self::Target {
        &self.0
    }
}

impl DerefMut for CustomerClaims {
    fn deref_mut(&mut self) -> &mut Claims {
        &mut self.0
    }
}

#[rocket::async_trait]
impl<'r> FromRequest<'r> for CustomerClaims {
    type Error = ApiKeyError;
    async fn from_request(request: &'r Request<'_>) -> Outcome<Self, Self::Error> {
        Claims::from_request(request).await.and_then(|c| {
            if c.role == Role::Customer {
                Outcome::Success(CustomerClaims(c))
            } else {
                Outcome::Failure((Status::Forbidden, ApiKeyError::Invalid))
            }
        })
    }
}

pub struct ManagerClaims(Claims);

impl Deref for ManagerClaims {
    type Target = Claims;
    fn deref(&self) -> &Self::Target {
        &self.0
    }
}

impl DerefMut for ManagerClaims {
    fn deref_mut(&mut self) -> &mut Claims {
        &mut self.0
    }
}

#[rocket::async_trait]
impl<'r> FromRequest<'r> for ManagerClaims {
    type Error = ApiKeyError;
    async fn from_request(request: &'r Request<'_>) -> Outcome<Self, Self::Error> {
        Claims::from_request(request).await.and_then(|c| {
            if c.role == Role::Manager {
                Outcome::Success(ManagerClaims(c))
            } else {
                Outcome::Failure((Status::Forbidden, ApiKeyError::Invalid))
            }
        })
    }
}

pub fn gen_token(id: i32, role: Role) -> ServerResult<String> {
    encode(
        &Header::new(Algorithm::HS512),
        &Claims {
            id,
            role,
            exp: std::usize::MAX,
        },
        &EncodingKey::from_secret(SECRET),
    )
    .map_err(|err| ServerError::JWTGenError(format!("{:?}", err.kind())))
}

pub fn decode_token(token: &str) -> Result<TokenData<Claims>> {
    decode::<Claims>(
        token,
        &DecodingKey::from_secret(SECRET),
        &Validation::new(Algorithm::HS512),
    )
}
