alter table if exists users
    add column if not exists created_at timestamp not null default now();
