CREATE TABLE Step
(
    id                  NUMBER(38, 0) GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    duration            INTEGER                                        NOT NULL,
    moveNameAndDuration VARCHAR2(255),
    text                VARCHAR2(255),
    image               BLOB,
    stepIndex           INTEGER                                        NOT NULL,
    tagAlongStory_id    NUMBER(38, 0),
    CONSTRAINT pk_step PRIMARY KEY (id)
);

CREATE TABLE TagAlongStory
(
    id        NUMBER(38, 0) GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name      VARCHAR2(255),
    storyIcon BLOB,
    isEnabled NUMBER(1)                                      NOT NULL,
    CONSTRAINT pk_tagalongstory PRIMARY KEY (id)
);

ALTER TABLE Step
    ADD CONSTRAINT FK_STEP_ON_TAGALONGSTORY FOREIGN KEY (tagAlongStory_id) REFERENCES TagAlongStory (id);