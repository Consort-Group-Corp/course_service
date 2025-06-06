CREATE TABLE IF NOT EXISTS course_schema.video_meta_data(
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    video_id UUID NOT NULL REFERENCES course_schema.resource(id) ON DELETE CASCADE UNIQUE,
    duration INT NOT NULL,
    resolution VARCHAR(50)
)