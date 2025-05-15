CREATE TABLE IF NOT EXISTS users (
   id bigint primary key auto_increment,
   name varchar(100),
   email varchar(255),
   password varchar(255),
   role varchar(50),
   created_at datetime
);

CREATE TABLE IF NOT EXISTS interview_session (
    id bigint primary key auto_increment,
    started_at datetime,
    ended_at datetime,
    status varchar(50),
    u_id bigint,
    foreign key (u_id) references users(id)
       on delete cascade
       on update cascade
);

CREATE TABLE IF NOT EXISTS interview_result_summary (
    id bigint primary key auto_increment,
    overall_score int,
    overall_feedback text,
    created_at datetime,
    i_id bigint,
    foreign key (i_id) references interview_session(id)
      on delete cascade
      on update cascade
);

CREATE TABLE IF NOT EXISTS interview_recap_session (
    id bigint primary key auto_increment,
    started_at datetime,
    ended_at datetime,
    status varchar(50),
    u_id bigint,
    i_id bigint,
    foreign key (u_id) references users(id)
     on delete cascade
     on update cascade,
    foreign key (i_id) references interview_session(id)
     on delete cascade
     on update cascade
);

CREATE TABLE IF NOT EXISTS interview_recap_problem (
    id bigint primary key auto_increment,
    user_answer text,
    result varchar(20),
    created_at datetime,
    irs_id bigint,
    foreign key (irs_id) references interview_recap_session(id)
     on delete cascade
     on update cascade
);

CREATE TABLE IF NOT EXISTS interview_recap_feedback (
    id bigint primary key auto_increment,
    recap_feedback text,
    created_at datetime,
    irp_id bigint,
    foreign key (irp_id) references interview_recap_problem(id)
      on delete cascade
      on update cascade
);

CREATE TABLE IF NOT EXISTS interview_message (
    id bigint primary key auto_increment,
    content text,
    created_at datetime,
    sender varchar(10), -- USER or LLM
    i_id bigint,
    irp_id bigint,
    foreign key (i_id) references interview_session(id)
       on delete cascade
       on update cascade,
    foreign key (irp_id) references interview_recap_problem(id)
       on delete cascade
       on update cascade
);

CREATE TABLE IF NOT EXISTS interview_detail_feedback (
    id bigint primary key auto_increment,
    feedback_text text,
    score int,
    created_at datetime,
    im_id bigint,
    foreign key (im_id) references interview_message(id)
       on delete cascade
       on update cascade
);
CREATE TABLE  IF NOT EXISTS chat_memory (

                                            memory_id varchar(255) PRIMARY KEY,

                                            messages_json TEXT

);
