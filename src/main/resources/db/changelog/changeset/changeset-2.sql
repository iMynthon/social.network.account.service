/*
* Database Change Log - SQL Format
* Version: 1.3
* Author: Mynthon
* Date: 22.06.2025
* Изменение поля таблицы accounts
* Упорядоченные по наборам изменений, соответствующим формату Liquibase
* Всего 1 таблиц
*/

ALTER TABLE accounts ALTER COLUMN id SET DEFAULT gen_random_uuid();