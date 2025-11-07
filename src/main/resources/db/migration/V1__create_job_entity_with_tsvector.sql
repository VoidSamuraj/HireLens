
SET client_encoding = 'UTF8';

CREATE TABLE job_entity (
    id BIGSERIAL PRIMARY KEY,
    api_name VARCHAR(100) NOT NULL,
    api_id VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    company_name VARCHAR(255),
    company_logo VARCHAR(255),
    url VARCHAR(255),
    category VARCHAR(255),
    job_type VARCHAR(255),
    experience_level VARCHAR(50) NOT NULL,
    publication_date VARCHAR(100),
    candidate_required_location VARCHAR(255),
    salary VARCHAR(255),
    description TEXT,
    tsv_en tsvector,
    CONSTRAINT uq_api UNIQUE (api_name, api_id)
);

CREATE TABLE groups (
    skill VARCHAR(255) PRIMARY KEY,
    group_name VARCHAR(100)
);

CREATE TABLE job_skills (
    id BIGSERIAL PRIMARY KEY,
    job_id BIGINT NOT NULL REFERENCES job_entity(id) ON DELETE CASCADE,
    skill  VARCHAR(255) REFERENCES groups(skill) ON DELETE NO ACTION,
    level INT
);


-- Index GIN(Generalized Inverted Index) for fast search
CREATE INDEX idx_job_entity_tsv_en ON job_entity USING GIN(tsv_en);


-- Trigger to automatically update tsvector on INSERT / UPDATE
CREATE FUNCTION job_entity_tsv_trigger() RETURNS trigger AS $$
BEGIN

    IF NEW.api_name = 'JOINRISE' AND NEW.api_id IS NULL THEN
        NEW.api_id := md5(
            coalesce(NEW.title,'') ||
            coalesce(NEW.company_name,'') ||
            coalesce(NEW.publication_date,'')||
            coalesce(NEW.description,'')
        );
    END IF;

    NEW.tsv_en := to_tsvector('english', coalesce(NEW.title,'') || ' ' || coalesce(NEW.description,''));
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE TRIGGER tsvectorupdate
BEFORE INSERT OR UPDATE ON job_entity
FOR EACH ROW EXECUTE FUNCTION job_entity_tsv_trigger();