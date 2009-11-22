create table tweet (
	id BIGINT primary key,
	author varchar(255),
	userid BIGINT,
	text varchar(140),
	date timestamp,
	in_reply_to_userid bigint
	);
	