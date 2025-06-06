CREATE TABLE IF NOT EXISTS users (
   id bigint primary key auto_increment,
   name varchar(100),
   email varchar(255) unique,
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
       on update cascade,
    index interview_session_covering_idx (id, u_id, status)
);

CREATE TABLE IF NOT EXISTS interview_result_summary (
    id bigint primary key auto_increment,
    overall_score double,
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
     user_content text,
     llm_content text,
     user_created_at datetime,
     llm_created_at datetime,
     i_id bigint,
     foreign key (i_id) references interview_session(id)
         on delete cascade
         on update cascade
);
CREATE TABLE IF NOT EXISTS interview_detail_feedback (
     id bigint primary key auto_increment,
     core_question varchar(50),
     feedback_text text,
     score double,
     created_at datetime,
     m_id bigint,
     foreign key (m_id) references interview_message(id)
         on delete cascade
         on update cascade,
    index feedback_idx1 (m_id, created_at, core_question, score)
);
CREATE TABLE  IF NOT EXISTS chat_memory (

    memory_id varchar(255) PRIMARY KEY,
    messages_json TEXT

);
