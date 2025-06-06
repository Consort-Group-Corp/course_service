CREATE TABLE IF NOT EXISTS course_schema.module_translation(
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    module_id UUID NOT NULL REFERENCES course_schema.module(id) ON DELETE CASCADE,
    language VARCHAR(50) NOT NULL,
    title TEXT,
    description TEXT,
    UNIQUE(module_id, language)
)