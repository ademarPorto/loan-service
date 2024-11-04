CREATE TABLE public.loan (
    id                     uuid          not null,
    client_id              uuid          not null,
    loan_value             numeric       not null,
    interest_rate          numeric       not null,
    loan_term              integer       not null,
    monthly_payment        numeric       null,
    loan_type              varchar (255) null,
    created_date           date          null,

    CONSTRAINT loan_pkey PRIMARY KEY (id)
);

ALTER TABLE public.loan add CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES public.client(id) on delete CASCADE;
