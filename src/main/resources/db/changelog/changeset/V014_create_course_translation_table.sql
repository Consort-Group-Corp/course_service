CREATE TABLE IF NOT EXISTS course_schema.course_translation(
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    course_id UUID NOT NULL REFERENCES course_schema.course(id) ON DELETE CASCADE,
    language VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    slug TEXT NOT NULL UNIQUE,
    UNIQUE(course_id, language)
)
