GRANT ALL PRIVILEGES ON DATABASE mars TO marsdbu;
ALTER USER marsdbu WITH SUPERUSER;

CREATE TABLE user_account (
	user_account_id 	SERIAL NOT NULL,
	username			    VARCHAR(255) NOT NULL,
	password			    VARCHAR(255) NOT NULL,
	email				      VARCHAR(255) NOT NULL,
	enabled				    BOOLEAN NOT NULL,
	created				    TIMESTAMP NOT NULL,
	PRIMARY KEY (user_account_id)
);

CREATE TABLE role (
	role_id				    SERIAL NOT NULL,
	role_name			    VARCHAR(45) NOT NULL,
	PRIMARY KEY (role_id)
);

CREATE TABLE user_has_role (
	user_account_id		INT NOT NULL,
	role_id					  INT NOT NULL,
	FOREIGN KEY (user_account_id) REFERENCES user_account(user_account_id),
	FOREIGN KEY (role_id) REFERENCES role(role_id)
);

CREATE TABLE user_log(
	user_log_id				  SERIAL NOT NULL,
	user_account_id			INT NOT NULL,
	log_date				    TIMESTAMP NOT NULL,
	ip_address				  VARCHAR(45),
	PRIMARY KEY (user_log_id),
	FOREIGN KEY (user_account_id) REFERENCES user_account(user_account_id)
);

ALTER TABLE user_account
   ADD CONSTRAINT uk_email UNIQUE (email);

ALTER TABLE user_account
  ADD CONSTRAINT uk_username UNIQUE (username);

CREATE TABLE verification_token (
	verification_token_id			SERIAL NOT NULL,
	user_account_id					  INT NOT NULL,
	verification_token				VARCHAR(20) NOT NULL,
	expired_date					    TIMESTAMP,
	PRIMARY KEY (verification_token_id),
	FOREIGN KEY (user_account_id) REFERENCES user_account(user_account_id)
);

ALTER TABLE verification_token
    ADD UNIQUE (verification_token);

CREATE INDEX verification_token_idx ON verification_token(verification_token);

CREATE TABLE password_reset_token (
	password_reset_token_id			SERIAL NOT NULL,
	user_account_id					    INT NOT NULL,
	token							          VARCHAR(60) NOT NULL,
	used							          BOOLEAN NOT NULL,
	expired_date					      TIMESTAMP NOT NULL,
	PRIMARY KEY (password_reset_token_id),
	FOREIGN KEY (user_account_id) REFERENCES user_account(user_account_id)
);

ALTER TABLE password_reset_token
    ADD UNIQUE (token);

CREATE INDEX password_reset_token_idx ON password_reset_token(token);

COPY role(role_name) FROM '/home/csv/role.csv' WITH CSV HEADER;
COPY user_account(username, password, email, enabled, created) FROM '/home/csv/user_account.csv' WITH CSV HEADER;