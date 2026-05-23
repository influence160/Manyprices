#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MYSQL="mysql --user=root -proot"

$MYSQL -e "source $SCRIPT_DIR/selItemInsertTrg.sql"
$MYSQL -e "source $SCRIPT_DIR/selItemUpdateTrg.sql"
$MYSQL -e "source $SCRIPT_DIR/selItemDeleteTrg.sql"
$MYSQL -e "source $SCRIPT_DIR/entryInsertTrg.sql"
$MYSQL -e "source $SCRIPT_DIR/entryUpdateTrg.sql"
$MYSQL -e "source $SCRIPT_DIR/entryDeleteTrg.sql"
echo "Triggers créés avec succès."
