CREATE TABLE IF NOT EXISTS course_schema.module(
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    course_id UUID NOT NULL REFERENCES course_schema.course(id) ON DELETE CASCADE,
    module_name VARCHAR(255) NOT NULL,
    order_position INTEGER NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT NULL
)