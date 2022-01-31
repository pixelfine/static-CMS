install:
	docker-compose up --build

inside:
	docker-compose run -p 8080:8080/tcp -p 8080:8080/udp ssg bash
