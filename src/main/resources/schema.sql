CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
    mpa_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    film_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE,
    duration INTEGER,
    mpa_id INTEGER NOT NULL,
    CONSTRAINT fk_films_mpa FOREIGN KEY (mpa_id) REFERENCES mpa_ratings(mpa_id)
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    CONSTRAINT fk_film_genres_film FOREIGN KEY (film_id) REFERENCES films(film_id),
    CONSTRAINT fk_film_genres_genre FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
    user_id INTEGER NOT NULL,
    film_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, film_id),
    CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_likes_film FOREIGN KEY (film_id) REFERENCES films(film_id)
);

CREATE TABLE IF NOT EXISTS friendship_statuses (
    status_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS friendships (
    user_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    status_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_friendships_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_friendships_friend FOREIGN KEY (friend_id) REFERENCES users(user_id),
    CONSTRAINT fk_friendships_status FOREIGN KEY (status_id) REFERENCES friendship_statuses(status_id)
);
