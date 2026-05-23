#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
mysql -u root -proot manyprices < "$SCRIPT_DIR/sauvgarde_donnees.sql"
echo "Import terminé."
