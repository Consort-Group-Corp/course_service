CREATE TABLE IF NOT EXISTS course_schema.lesson(
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    module_id UUID NOT NULL REFERENCES course_schema.module(id) ON DELETE CASCADE,
    order_position INTEGER NOT NULL,
    lesson_type VARCHAR(50) NOT NULL,
    content_url TEXT NOT NULL,
    duration_minutes INT,
    is_preview BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT NULL
)