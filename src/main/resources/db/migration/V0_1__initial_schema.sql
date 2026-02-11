create table if not exists users (
    id bigserial primary key,
    email varchar(255) not null unique,
    name varchar(255) not null,
    nickname varchar(255) not null unique,
    password varchar(255) not null,
    role varchar(20) not null,
    created_at timestamp not null default now()
);

create table if not exists email_verifications (
    id bigserial primary key,
    email varchar(255) not null unique,
    code varchar(6) not null,
    expires_at timestamp not null,
    verified boolean not null,
    verified_at timestamp,
    last_sent_date date not null,
    daily_send_count integer not null,
    failed_attempts integer not null default 0
);

create table if not exists refresh_tokens (
    id bigserial primary key,
    token varchar(512) not null unique,
    user_id bigint not null,
    expiry_date timestamp not null,
    constraint fk_refresh_tokens_user
        foreign key (user_id) references users (id) on delete cascade
);

create index if not exists idx_refresh_tokens_user
    on refresh_tokens (user_id);

create table if not exists community_posts (
    id bigserial primary key,
    title varchar(200) not null,
    content text not null,
    author_type varchar(20) not null,
    author_name varchar(100) not null,
    member_username varchar(200),
    guest_password_hash varchar(200),
    view_count bigint not null default 0,
    notice boolean not null default false,
    file_original_name varchar(300),
    file_storage_path varchar(500),
    file_content_type varchar(100),
    file_size bigint,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

create table if not exists heroes (
    id varchar(50) primary key,
    name varchar(100) not null,
    nickname varchar(200),
    usage jsonb,
    gear jsonb,
    image varchar(300),
    image_key varchar(300),
    basic_skill_image varchar(300),
    skill1_image varchar(300),
    skill2_image varchar(300),
    passive_skill_image varchar(300),
    type varchar(20),
    type_icon varchar(300),
    type_label varchar(50),
    grade varchar(20),
    grade_label varchar(50),
    acquisition jsonb,
    has_skill2 boolean not null default false
);

create table if not exists pets (
    id varchar(50) primary key,
    name varchar(100) not null,
    grade varchar(20) not null,
    nickname varchar(200),
    acquisition jsonb,
    image varchar(300),
    image_key varchar(300),
    skill_image varchar(300),
    skill_name varchar(100),
    skill_target varchar(100),
    skill_description varchar(2000),
    skill_description_lines jsonb
);
