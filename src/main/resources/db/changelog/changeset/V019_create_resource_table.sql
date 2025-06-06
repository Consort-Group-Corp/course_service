CREATE TABLE IF NOT EXISTS course_schema.resource(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    lesson_id UUID NOT NULL REFERENCES course_schema.lesson(id) ON DELETE CASCADE,
    resource_type VARCHAR(50) NOT NULL,
    file_url TEXT NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    order_position INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT NULL
)