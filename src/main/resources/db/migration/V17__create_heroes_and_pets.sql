create table if not exists heroes (
    id varchar(50) primary key,
    name varchar(100) not null,
    nickname varchar(200),
    usage jsonb,
    gear jsonb,
    image varchar(300) not null,
    basic_skill_image varchar(300),
    skill1_image varchar(300),
    skill2_image varchar(300),
    passive_skill_image varchar(300),
    type varchar(20) not null,
    type_icon varchar(300),
    grade varchar(20) not null,
    grade_label varchar(50),
    acquisition jsonb,
    has_skill2 boolean not null default false
);

alter table if exists heroes
    add column if not exists nickname varchar(200),
    add column if not exists usage jsonb,
    add column if not exists gear jsonb,
    add column if not exists acquisition jsonb,
    add column if not exists type_icon varchar(300),
    add column if not exists grade_label varchar(50),
    add column if not exists has_skill2 boolean default false,
    add column if not exists basic_skill_image varchar(300),
    add column if not exists skill1_image varchar(300),
    add column if not exists skill2_image varchar(300),
    add column if not exists passive_skill_image varchar(300);

alter table if exists heroes
    alter column nickname drop not null,
    alter column usage drop not null,
    alter column gear drop not null,
    alter column acquisition drop not null,
    alter column type_icon drop not null,
    alter column grade_label drop not null;

create table if not exists pets (
    id varchar(50) primary key,
    name varchar(100) not null,
    grade varchar(20) not null,
    nickname varchar(200),
    acquisition jsonb,
    image varchar(300) not null,
    skill_image varchar(300),
    skill_name varchar(100),
    skill_target varchar(100),
    skill_description varchar(2000),
    skill_description_lines jsonb
);
