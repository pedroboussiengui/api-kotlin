
run:
    @gradle run -q

build:
    @gradle build

migrate:
    @sqlite3 sample.db < migrations.sql

minio:
    @./minio server ./data