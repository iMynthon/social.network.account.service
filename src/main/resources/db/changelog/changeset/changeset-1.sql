/*
* Database Change Log - SQL Format
* Version: 1.1
* Author: Mynthon
* Date: 19.06.2025
* Добавление таблицы для логирования долгих операций
* Упорядоченные по наборам изменений, соответствующим формату Liquibase
* Всего 1 таблиц
*/

CREATE TABLE logs (
  id BIGSERIAL PRIMARY KEY,
  task_name VARCHAR(100),
  execution_time DOUBLE PRECISION,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  additional_info TEXT
);