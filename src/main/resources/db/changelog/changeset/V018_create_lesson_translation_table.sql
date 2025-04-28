CREATE TABLE IF NOT EXISTS course_schema.lesson_translation(
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    lesson_id UUID NOT NULL REFERENCES course_schema.lesson(id) ON DELETE CASCADE,
    language VARCHAR(50) NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    UNIQUE(lesson_id, language)
)