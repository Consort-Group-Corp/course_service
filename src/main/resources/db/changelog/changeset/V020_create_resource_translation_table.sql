CREATE TABLE IF NOT EXISTS course_schema.resource_translation(
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    resource_id UUID NOT NULL REFERENCES course_schema.resource(id) ON DELETE CASCADE,
    language VARCHAR(50) NOT NULL,
    title TEXT,
    description TEXT,
    UNIQUE(resource_id, language)
)