#!/usr/bin/env bash
set -euo pipefail

ENV_FILE=".env.production"
MAX_RETRIES=5
RETRY_INTERVAL=10
HEALTHY=false

if [[ ! -f $ENV_FILE ]]; then
  echo "ERROR: Tệp $ENV_FILE không tồn tại." >&2
  exit 1
fi

# # Tải các biến môi trường từ tệp .env một cách an toàn
# # Bỏ qua các dòng trống và dòng bắt đầu bằng '#'
# set -o allexport
# while IFS='=' read -r key value; do
#   # Bỏ qua các dòng trống hoặc dòng bắt đầu bằng '#'
#   if [[ -z $key || $key =~ ^# ]]; then
#     continue
#   fi
#   # Loại bỏ khoảng trắng xung quanh key và value
#   key=$(echo $key | xargs)
#   value=$(echo $value | xargs)
#   # Kiểm tra xem key có hợp lệ không
#   if [[ ! $key =~ ^[a-zA-Z_][a-zA-Z0-9_]*$ ]]; then
#     echo "WARNING: Biến môi trường '$key' không hợp lệ và sẽ bị bỏ qua." >&2
#     continue
#   fi
#   export "$key=$value"
# done < <(grep -v '^[[:space:]]*$' $ENV_FILE)
# set +o allexport

docker-compose --env-file $ENV_FILE -f docker-compose.prod.yml up --build

docker-compose --env-file $ENV_FILE -f docker-compose.prod.yml up -d db

echo "Waiting for database to start..."
docker-compose --env-file $ENV_FILE -f docker-compose.prod.yml exec -T db \
  bash -c 'while ! pg_isready -U $POSTGRES_USER -d $POSTGRES_DB; do sleep 2; done'

docker-compose --env-file $ENV_FILE -f docker-compose.prod.yml up -d app

# check health status of container
for ((i=1; i<=MAX_RETRIES; i++)); do
  CONTAINER_ID=$(docker-compose ps -q app)

  if [[ -z "$CONTAINER_ID" ]]; then
    echo "Cannot find container. Retrying $i/$MAX_RETRIES..."
    sleep $RETRY_INTERVAL
    continue
  fi

  HEALTH_STATUS=$(docker inspect --format='{{.State.Health.Status}}' $CONTAINER_ID 2>/dev/null || true)

  if [[ $HEALTH_STATUS == "healthy" ]]; then
    HEALTHY=true
    break
  fi

  echo "Waiting for container to become healthy (lần $i/$MAX_RETRIES...)"
  sleep $RETRY_INTERVAL
done

# check final result of health status
if [[ $HEALTHY == false ]]; then
  echo "ERROR: Container does not start properly."
  docker-compose logs --tail=50 app
  docker-compose down -v
  exit 1
fi

# perform smoke test to ensure the application is working
SMOKE_TEST_URL="http://localhost:${SERVER_PORT:-8080}/actuator/health"
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$SMOKE_TEST_URL" || true)

if [[ $HTTP_STATUS -eq 200 ]]; then
  echo "Production deployment SUCCESS."
  exit 0
else
  echo "Smoke test FAILED (HTTP $HTTP_STATUS)."
  docker-compose logs --tail=50 app
  exit 1
fi
