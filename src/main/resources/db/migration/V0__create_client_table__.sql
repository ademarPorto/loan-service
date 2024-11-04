CREATE TABLE public.client (
    id                    uuid            not null,
    name                  varchar (255)   not null,
    created_date          date            null,

    CONSTRAINT client_pkey PRIMARY KEY (id)
);
