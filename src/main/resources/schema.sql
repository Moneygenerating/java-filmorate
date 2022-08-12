CREATE TABLE IF NOT EXISTS "FRIENDS"(
                                        "USER_ID" INTEGER NULL,
                                        "FRIENDS_ID" INTEGER NULL
);
CREATE TABLE IF NOT EXISTS "FILM_GENRES"(
                                            "GENRES_ID" INTEGER auto_increment primary key,
                                            "FILM_GENRE" VARCHAR(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS "FILMS_GENRES_IDS"(
                                                 "FILMS_ID" INTEGER NOT NULL,
                                                 "FILM_GENRE_ID" INTEGER NULL
);
CREATE TABLE IF NOT EXISTS "FILM_RATING_MPA"(
                                                "MPA_ID" INTEGER auto_increment primary key,
                                                "MPA_RATE" VARCHAR(5) NOT NULL
);

CREATE TABLE IF NOT EXISTS "USERS"(
                                      "USER_ID" INTEGER auto_increment primary key,
                                      "USER_NAME" VARCHAR(255) NULL,
                                      "LOGIN" VARCHAR(255) NOT NULL,
                                      "EMAIL" VARCHAR(255) NOT NULL,
                                      "BIRTHDAY" DATE NOT NULL
);

ALTER TABLE
    "USERS" ADD CONSTRAINT IF NOT EXISTS "users_login_unique" UNIQUE("LOGIN");
ALTER TABLE
    "USERS" ADD CONSTRAINT IF NOT EXISTS "users_email_unique" UNIQUE("EMAIL");
CREATE TABLE  IF NOT EXISTS "FILMS"(
                                       "FILM_ID" INTEGER auto_increment primary key,
                                       "FILMS_NAME" VARCHAR(255) NULL,
                                       "DESCRIPTION" VARCHAR(255) NULL,
                                       "RATING_MPA" INTEGER NULL,
                                       "DURATION" INTEGER NULL,
                                       "RELEASE_DATE" DATE NULL
);

CREATE TABLE  IF NOT EXISTS "FILM_LIKES"(
                                            "USER_ID" INTEGER NULL,
                                            "FILM_ID" INTEGER NULL
);
ALTER TABLE
    "FILMS_GENRES_IDS" ADD CONSTRAINT IF NOT EXISTS "films_genres_ids_film_genre_id_foreign" FOREIGN KEY("FILM_GENRE_ID") REFERENCES "FILM_GENRES"("GENRES_ID") ON DELETE CASCADE;
ALTER TABLE
    "FRIENDS" ADD CONSTRAINT IF NOT EXISTS "friends_user_id_foreign" FOREIGN KEY("USER_ID") REFERENCES "USERS"("USER_ID") ON DELETE CASCADE;
//ALTER TABLE
    //"FILM_LIKES" ADD CONSTRAINT IF NOT EXISTS "film_likes_user_id_foreign" FOREIGN KEY("USER_ID") REFERENCES "USERS"("USER_ID") ON DELETE CASCADE;
ALTER TABLE
    "FILMS_GENRES_IDS" ADD CONSTRAINT IF NOT EXISTS "films_genres_ids_films_id_foreign" FOREIGN KEY("FILMS_ID") REFERENCES "FILMS"("FILM_ID") ON DELETE CASCADE;
ALTER TABLE
    "FILMS" ADD CONSTRAINT IF NOT EXISTS "films_rating_mpa_foreign" FOREIGN KEY("RATING_MPA") REFERENCES "FILM_RATING_MPA"("MPA_ID") ON DELETE CASCADE;
ALTER TABLE
    "FILM_LIKES" ADD CONSTRAINT IF NOT EXISTS "film_likes_film_id_foreign" FOREIGN KEY("FILM_ID") REFERENCES "FILMS"("FILM_ID") ON DELETE CASCADE;