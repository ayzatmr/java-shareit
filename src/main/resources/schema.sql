CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    username VARCHAR(100)                            NOT NULL,
    email    VARCHAR(255) UNIQUE                     NOT NULL
);

CREATE TABLE IF NOT EXISTS item_requests
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    description VARCHAR(2000)                           NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    user_id     BIGINT,
    FOREIGN KEY (user_id) REFERENCES users (id)
    );

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name        VARCHAR(100)                            NOT NULL,
    description VARCHAR(500)                            NOT NULL,
    available   BOOLEAN                                 NOT NULL,
    owner_id    BIGINT                                  NOT NULL,
    request_id  BIGINT,
    FOREIGN KEY (owner_id) REFERENCES users (id),
    FOREIGN KEY (request_id) REFERENCES item_requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    item_id    BIGINT                                  NOT NULL,
    user_id    BIGINT                                  NOT NULL,
    status     VARCHAR(10)                             NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    text         VARCHAR(2000)                           NOT NULL,
    item_id      BIGINT                                  NOT NULL,
    author_id    BIGINT                                  NOT NULL,
    date_created TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (author_id) REFERENCES users (id)
);