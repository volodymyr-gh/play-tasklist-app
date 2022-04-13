-- !Ups

CREATE TABLE IF NOT EXISTS public."USER" (
    "UUID" uuid NOT NULL,
    "EMAIL" character varying COLLATE pg_catalog."default" NOT NULL,
    "PASSWORD" character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "USER_pkey" PRIMARY KEY ("UUID"),
    CONSTRAINT "USER_EMAIL_unique" UNIQUE ("EMAIL")
);

CREATE TABLE IF NOT EXISTS public."TASK" (
    "UUID" uuid NOT NULL,
    "SUMMARY" character varying COLLATE pg_catalog."default" NOT NULL,
    "STATUS" character varying COLLATE pg_catalog."default" NOT NULL,
    "CREATED_BY" uuid NOT NULL,
    CONSTRAINT "TASK_pkey" PRIMARY KEY ("UUID"),
    CONSTRAINT "TASK_USER_rel" FOREIGN KEY ("CREATED_BY")
        REFERENCES public."USER" ("UUID") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

INSERT INTO public."USER" ("UUID", "EMAIL", "PASSWORD")
    VALUES (gen_random_uuid(), 'john@example.com', '$2a$10$5WtVnvrcZRpqc4yW2RqopORU1zKvqa7YrPRmjF6IKt059xVYJ7Icm');

INSERT INTO public."USER" ("UUID", "EMAIL", "PASSWORD")
    VALUES (gen_random_uuid(), 'jane@example.com', '$2a$10$wHpkUVsyTrmR8rm0EiGXyOK7NMOGdoxhtViAVVbvUbaTtBWkON6tu');

-- !Downs
