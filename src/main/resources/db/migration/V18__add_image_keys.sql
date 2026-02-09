alter table if exists heroes
    add column if not exists image_key varchar(300);

alter table if exists heroes
    alter column image drop not null;

alter table if exists pets
    add column if not exists image_key varchar(300);

alter table if exists pets
    alter column image drop not null;
