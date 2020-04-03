CREATE TABLE user (
  id int(3) NOT NULL AUTO_INCREMENT,
  first_name varchar(30) NOT NULL,
  last_name varchar(30) NOT NULL,
  email varchar(50) NOT NULL UNIQUE,
  password varchar(20) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE contact (
  user int(3) NOT NULL,
  contact int(3) NOT NULL,
  PRIMARY KEY (user, contact),
  FOREIGN KEY (user) REFERENCES user(id),
  FOREIGN KEY (contact) REFERENCES user(id)
);


CREATE TABLE contact_request (
  user int(3) NOT NULL,
  contact int(3) NOT NULL,
  status varchar(10) NOT NULL DEFAULT 'pending',
  PRIMARY KEY (user,contact),
  FOREIGN KEY (user) REFERENCES user(id),
  FOREIGN KEY (contact) REFERENCES user(id)
);

CREATE TABLE contact_group (
  id int(3) NOT NULL AUTO_INCREMENT,
  name varchar(20) NOT NULL,
  owner int(3) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (owner) REFERENCES user(id)
);

CREATE TABLE contact_group_member (
  group_id int(3) NOT NULL,
  user int(3) NOT NULL,
  PRIMARY KEY (group_id, user),
  FOREIGN KEY (group_id) REFERENCES contact_group(id) ON DELETE CASCADE,
  FOREIGN KEY (user) REFERENCES user(id)
);

CREATE TABLE transaction (
  id int(3) NOT NULL AUTO_INCREMENT,
  debtor int(3) NOT NULL,
  creditor int(3) NOT NULL,
  description varchar(50) DEFAULT NULL,
  amount decimal(12,2) NOT NULL,
  date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (debtor) REFERENCES user(id),
  FOREIGN KEY (creditor) REFERENCES user(id)
);

