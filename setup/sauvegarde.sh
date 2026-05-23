#!/bin/bash

BACKUP_DIR="$HOME/Bureau/hedi"
mkdir -p "$BACKUP_DIR"
rm -f "$BACKUP_DIR/sauvgarde_donnees.sql"
mysqldump --databases manyprices -u root -proot >> "$BACKUP_DIR/sauvgarde_donnees.sql"
echo "Sauvegarde terminée : $BACKUP_DIR/sauvgarde_donnees.sql"
