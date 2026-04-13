#!/usr/bin/env sh
set -eu

BACKUP_DIR="${1:-./backups}"
mkdir -p "$BACKUP_DIR"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
OUT_FILE="$BACKUP_DIR/backup_${TIMESTAMP}.sql"

docker compose exec -T mysql mysqldump \
  -u"${MYSQL_USER:-shift_user}" \
  -p"${MYSQL_PASSWORD:-shift_pass}" \
  "${MYSQL_DATABASE:-shiftmanage}" > "$OUT_FILE"

echo "Backup created: $OUT_FILE"
