use diesel::result::Error as DieselError;
use rocket::http::Status;
use rocket::response;
use rocket::response::{Responder, Response};
use rocket::serde::json::Json;
use rocket::Request;
use thiserror::Error;

pub type ServerResult<T> = std::result::Result<T, ServerError>;

#[derive(Error, Debug)]
pub enum ServerError {
    #[error("Could not generate token: {}", _0)]
    JWTGenError(String),

    #[error("Email already in use")]
    AlreadyRegisteredError,
    #[error("User not registered")]
    NotRegisteredError,
    #[error("Wrong password")]
    WrongPasswordError,
    #[error("Database error: {}", _0)]
    DbError(String),
    #[error("Provided location not found")]
    LocationNotFoundError,
    #[error("The restaurant is not oppened during the provided date/time")]
    InvalidReservationDateError,
}

impl ServerError {
    fn http_status(&self) -> Status {
        match self {
            ServerError::JWTGenError(_) => Status::InternalServerError,
            ServerError::DbError(_) => Status::InternalServerError,
            ServerError::AlreadyRegisteredError => Status::Conflict,
            ServerError::NotRegisteredError => Status::Conflict,
            ServerError::WrongPasswordError => Status::Forbidden,
            ServerError::LocationNotFoundError => Status::NotFound,
            ServerError::InvalidReservationDateError => Status::ImATeapot,
        }
    }
}

impl<'r> Responder<'r, 'static> for ServerError {
    fn respond_to(self, request: &Request<'_>) -> response::Result<'static> {
        let json = Json(json!({ "error": format!("{}", self) }));
        println!("{:?}", json);
        Response::build_from(json.respond_to(request)?)
            .status(self.http_status())
            .ok()
    }
}

impl From<DieselError> for ServerError {
    fn from(err: DieselError) -> Self {
        ServerError::DbError(format!("{}", err))
    }
}
