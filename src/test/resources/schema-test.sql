DROP TABLE IF EXISTS pixel_update_history;
DROP TABLE IF EXISTS chzzk_member;

CREATE TABLE chzzk_member (
    id  BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL ,
    channel_id VARCHAR(255) NOT NULL ,
    channel_name VARCHAR(255) NOT NULL ,
    access_token VARCHAR(255) NOT NULL ,
    refresh_token VARCHAR(255) NOT NULL ,
    last_active_at TIMESTAMP ,
    banned BOOLEAN NOT NULL ,
    is_admin BOOLEAN NOT NULL ,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE pixel_update_history (
    id  BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL ,
    position_x INTEGER NOT NULL ,
    position_y INTEGER NOT NULL ,
    color      VARCHAR(255) NOT NULL ,
    chzzk_member_id BIGINT ,
    created_at TIMESTAMP NOT NULL
);