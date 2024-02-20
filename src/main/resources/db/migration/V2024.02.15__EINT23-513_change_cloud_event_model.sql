CREATE TABLE IF NOT EXISTS event_data
(
    event_data_id          VARCHAR(255) PRIMARY KEY,
    "message"              VARCHAR(255) NULL,
    CONSTRAINT fk_cloud_event FOREIGN KEY (event_data_id) REFERENCES event_record (id)
);

CREATE TABLE IF NOT EXISTS event_data_related_objects
(
    event_data_id            VARCHAR(255),
    related_object_id        VARCHAR(255) NULL,
    related_object_type      VARCHAR(255) NULL,
    CONSTRAINT fk_event_data FOREIGN KEY (event_data_id) REFERENCES event_data (event_data_id)
);

CREATE TABLE IF NOT EXISTS event_data_fields_impacted
(
    event_data_id            VARCHAR(255),
    field_source             VARCHAR(255) NULL,
    field_name             VARCHAR(255) NULL,
    field_value              VARCHAR(255) NULL,
    field_exception_type     VARCHAR(255) NULL,
    CONSTRAINT fk_event_data FOREIGN KEY (event_data_id) REFERENCES event_data (event_data_id)
);


ALTER TABLE event_record DROP COLUMN IF EXISTS "data";
