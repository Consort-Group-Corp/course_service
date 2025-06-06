CREATE TABLE IF NOT EXISTS course_schema.course(
   id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
   author_id UUID NOT NULL,
   course_type VARCHAR(50) NOT NULL,
   price_type VARCHAR(50) NOT NULL,
   price_amount NUMERIC(10, 2),
   discount_percent NUMERIC(10, 2) DEFAULT 0.0,
   start_time TIMESTAMP WITH TIME ZONE NULL,
   end_time TIMESTAMP WITH TIME ZONE NULL,
   access_duration_min INT NULL,
   course_status VARCHAR(50) NOT NULL,
   cover_image_url TEXT,
   created_at TIMESTAMP NOT NULL,
   updated_at TIMESTAMP DEFAULT NULL
)