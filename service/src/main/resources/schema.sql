CREATE TABLE IF NOT EXISTS Categories
(
    id   bigint GENERATED BY DEFAULT AS IDENTITY,
    name varchar(255) NOT NULL UNIQUE,
    CONSTRAINT pk_Categories PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Compilations
(
    id        bigint   GENERATED BY DEFAULT AS IDENTITY,
--     events_id bigint[] NOT NULL,
    pinned    boolean   NOT NULL,
    title     varchar   NOT NULL,
    CONSTRAINT pk_Compilations PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Users
(
    id    bigint GENERATED BY DEFAULT AS IDENTITY,
    name  varchar NOT NULL,
    email varchar NOT NULL UNIQUE,
    CONSTRAINT pk_Users PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Events
(
    id                bigint   GENERATED BY DEFAULT AS IDENTITY,
    annotation        varchar   NOT NULL,
    category_id       bigint   NOT NULL,
    created_on         timestamp WITHOUT TIME ZONE NOT NULL,
    description       varchar   NOT NULL,
    event_date         timestamp WITHOUT TIME ZONE NOT NULL,
    initiator_id      bigint   NOT NULL,
    paid              boolean   NOT NULL,
    participant_limit  bigint,
    published_on       timestamp WITHOUT TIME ZONE,
    request_moderation boolean   NOT NULL,
    state             varchar   NOT NULL,
    title             varchar   NOT NULL,
    CONSTRAINT pk_Events PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES Categories (id) ON DELETE CASCADE,
    FOREIGN KEY (initiator_id) REFERENCES Users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Events_Compilations
(
    compilation_id bigint   NOT NULL,
    event_id bigint   NOT NULL,
    CONSTRAINT pk_Events_Compilations PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES Compilations (id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES Events (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Requests
(
    id           bigint   GENERATED BY DEFAULT AS IDENTITY,
    crated       timestamp WITHOUT TIME ZONE NOT NULL,
    event_id     bigint   NOT NULL,
    requester_id bigint   NOT NULL,
    status       varchar   NOT NULL,
    CONSTRAINT pk_Requests PRIMARY KEY (id),
    FOREIGN KEY (event_id) REFERENCES Events (id) ON DELETE CASCADE,
    FOREIGN KEY (requester_id) REFERENCES Users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Comments
(
    id        bigint   GENERATED BY DEFAULT AS IDENTITY,
    text      varchar   NOT NULL,
    author_id bigint   NOT NULL,
    created   timestamp WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_Comments PRIMARY KEY (id),
    FOREIGN KEY (author_id) REFERENCES Users (id) ON DELETE CASCADE
);







