#!/usr/bin/env sh
set -eu

if [ "$#" -lt 1 ]; then
  echo "Usage: ./scripts/restore-db.sh <path-to-backup.sql>"
  exit 1
fi

BACKUP_FILE="$1"
if [ ! -f "$BACKUP_FILE" ]; then
  echo "File not found: $BACKUP_FILE"
  exit 1
fi

docker compose exec -T mysql mysql \
  -u"${MYSQL_USER:-shift_user}" \
  -p"${MYSQL_PASSWORD:-shift_pass}" \
  "${MYSQL_DATABASE:-shiftmanage}" < "$BACKUP_FILE"

echo "Restore completed from: $BACKUP_FILE"
