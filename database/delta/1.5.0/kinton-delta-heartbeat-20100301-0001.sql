DROP TABLE IF EXISTS alerts;
CREATE TABLE IF NOT EXISTS alerts (
  id char(36) NOT NULL,
  `type` varchar(60) NOT NULL,
  `value` varchar(60) NOT NULL,
  description varchar(240) DEFAULT NULL,
  tstamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into alerts (id, type, value, tstamp) values ("1", "REGISTER", "LATER", date_sub(now(), INTERVAL 4 DAY)), ("2", "HEARTBEAT", "LATER", date_sub(now(), INTERVAL 4 DAY));

DROP TABLE IF EXISTS heartbeatlog;
CREATE TABLE IF NOT EXISTS heartbeatlog (
  id char(36) NOT NULL,
  abicloud_id varchar(60),
  client_ip varchar(16) NOT NULL,
  physical_servers int(11) NOT NULL,
  virtual_machines int(11) NOT NULL,
  volumes int(11) NOT NULL,
  virtual_datacenters int(11) NOT NULL,
  virtual_appliances int(11) NOT NULL,
  organizations int(11) NOT NULL,
  total_virtual_cores_allocated bigint(20) NOT NULL,
  total_virtual_cores_used bigint(20) NOT NULL,
  total_virtual_memory_allocated bigint(20) NOT NULL,
  total_virtual_memory_used bigint(20) NOT NULL,
  total_volume_space_allocated bigint(20) NOT NULL,
  total_volume_space_used bigint(20) NOT NULL,
  virtual_images bigint(20) NOT NULL,
  operating_system_name varchar(60) NOT NULL,
  operating_system_version varchar(60) NOT NULL,
  database_name varchar(60) NOT NULL,
  database_version varchar(60) NOT NULL,
  application_server_name varchar(60) NOT NULL,
  application_server_version varchar(60) NOT NULL,
  java_version varchar(60) NOT NULL,
  abicloud_version varchar(60) NOT NULL,
  abicloud_distribution varchar(60) NOT NULL,
  tstamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS register;
CREATE TABLE IF NOT EXISTS register (
  id char(36) NOT NULL,
  company_name varchar(60) NOT NULL,
  company_address varchar(240) NOT NULL,
  company_state varchar(60) NOT NULL,
  company_country_code varchar(2) NOT NULL,
  company_industry varchar(255),
  contact_title varchar(60) NOT NULL,
  contact_name varchar(60) NOT NULL,
  contact_email varchar(60) NOT NULL,
  contact_phone varchar(60) NOT NULL,
  company_size_revenue varchar(60) NOT NULL,
  company_size_employees varchar(60) NOT NULL,
  subscribe_development_news tinyint(1) NOT NULL DEFAULT '0',
  subscribe_commercial_news tinyint(1) NOT NULL DEFAULT '0',
  allow_commercial_contact tinyint(1) NOT NULL DEFAULT '0',
  creation_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  last_updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
