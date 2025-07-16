/*
* Database Change Log - SQL Format
* Version: 1.0
* Author: Mynthon
* Date: 09.06.2025
* Инициализация Таблиц
* Упорядоченные по наборам изменений, соответствующим формату Liquibase
* Всего 2 таблиц
*/

CREATE TABLE accounts(
  id UUID PRIMARY KEY NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  phone VARCHAR(20) UNIQUE,
  photo VARCHAR(255),
  about TEXT,
  city VARCHAR(100),
  country VARCHAR(100),
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  registration_date TIMESTAMP NOT NULL,
  birth_date DATE,
  last_online_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_online BOOLEAN DEFAULT true,
  is_blocked BOOLEAN DEFAULT false,
  is_deleted BOOLEAN DEFAULT false,
  photo_name VARCHAR(255),
  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  emoji_status VARCHAR(10)
);

CREATE INDEX idx_accounts_email ON accounts(email);
CREATE INDEX idx_accounts_name ON accounts(last_name, first_name);
CREATE INDEX idx_accounts_location ON accounts(country, city);
CREATE INDEX idx_accounts_phone ON accounts(phone);
CREATE INDEX idx_accounts_birthday ON accounts(birth_date);
CREATE INDEX idx_accounts_block ON accounts(is_blocked);
CREATE INDEX idx_accounts_delete ON accounts(is_deleted);