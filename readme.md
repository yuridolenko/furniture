# Docker
https://docs.docker.com/desktop/install/windows-install/

# Monogo DB
docker run --name mongo-local -p 27017:27017 -d mongo

# Furniture

    -- Build image
    docker build . -t furniture -f Dockerfile

    -- Run image
    docker run --name furniture -p 8080:8080 -d furniture

# Network
docker network create furniture-network

## Connect mongo and furniture to this network
docker network connect furniture-network furniture
docker network connect furniture-network mongo-local

# Restart both containers
docker restart mongo-local 
docker restart furniture