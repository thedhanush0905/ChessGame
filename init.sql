-- MySQL initialization script for IndiChess Microservices

-- Create databases
CREATE DATABASE IF NOT EXISTS indichess_auth;
CREATE DATABASE IF NOT EXISTS indichess_user;
CREATE DATABASE IF NOT EXISTS indichess_game;
CREATE DATABASE IF NOT EXISTS indichess_matchmaking;

USE indichess_auth;

-- Auth Service Tables
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE indichess_user;

-- User Service Tables
CREATE TABLE user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    display_name VARCHAR(100),
    bio TEXT,
    avatar_url VARCHAR(500),
    elo_rating INT DEFAULT 1200,
    wins INT DEFAULT 0,
    losses INT DEFAULT 0,
    draws INT DEFAULT 0,
    total_games INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_elo_rating (elo_rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE indichess_game;

-- Game Service Tables
CREATE TABLE games (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    white_player_id BIGINT NOT NULL,
    black_player_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    game_status VARCHAR(20),
    result VARCHAR(20),
    white_time INT,
    black_time INT,
    last_move_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_white_player (white_player_id),
    INDEX idx_black_player (black_player_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE moves (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    from_square VARCHAR(2) NOT NULL,
    to_square VARCHAR(2) NOT NULL,
    piece_type VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    INDEX idx_game_id (game_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE game_pgn (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT UNIQUE NOT NULL,
    pgn_text LONGTEXT,
    fen_position VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE indichess_matchmaking;

-- Matchmaking Service Tables
CREATE TABLE match_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'WAITING',
    skill_level INT,
    time_control VARCHAR(20) DEFAULT 'rapid',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    matched_opponent_id BIGINT,
    matched_at TIMESTAMP NULL,
    game_id BIGINT,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_skill_level (skill_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Grant permissions to indichess_user
GRANT ALL PRIVILEGES ON indichess_auth.* TO 'indichess_user'@'%';
GRANT ALL PRIVILEGES ON indichess_user.* TO 'indichess_user'@'%';
GRANT ALL PRIVILEGES ON indichess_game.* TO 'indichess_user'@'%';
GRANT ALL PRIVILEGES ON indichess_matchmaking.* TO 'indichess_user'@'%';
FLUSH PRIVILEGES;
