CREATE TABLE IF NOT EXISTS members (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       email VARCHAR(255) UNIQUE,
    name VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS interview_room (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      member_id BIGINT,  -- 🔥 여기에 FK 추가
      status VARCHAR(20),
    feedback TEXT,
    created_at TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS interview_messages (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      room_id BIGINT,
      sender VARCHAR(255),
        message TEXT,
        created_at TIMESTAMP,
        FOREIGN KEY (room_id) REFERENCES interview_room(id) ON DELETE CASCADE
    );
