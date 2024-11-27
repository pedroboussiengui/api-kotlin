
run:
	@gradle run -q

build:
	@gradle build

migrate:
	@sqlite3 sample.db < migrations.sql

minio:
	@./minio server ./data

redis:
	@docker run -d --name redis-api-kotlin -p 6379:6379 redis/redis-stack-server:latest
