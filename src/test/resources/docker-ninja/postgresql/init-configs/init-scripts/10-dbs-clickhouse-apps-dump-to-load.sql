--
-- PostgreSQL database dump
--

-- Dumped from database version 11.8 (Debian 11.8-1.pgdg100+1)
-- Dumped by pg_dump version 11.15

-- Started on 2022-04-05 17:57:25 UTC

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;


--
-- Name: tb_password_reset_token; Type: TABLE; Schema: public; Owner: dbaiamplatform01
--

CREATE TABLE public.tb_password_reset_token (
                                                id bigint NOT NULL,
                                                expiry_date timestamp(6) without time zone,
                                                token character varying(255),
                                                user_id bigint NOT NULL
);


ALTER TABLE public.tb_password_reset_token OWNER TO dbaiamplatform01;

--
-- Name: tb_password_reset_token_seq; Type: SEQUENCE; Schema: public; Owner: dbaiamplatform01
--

CREATE SEQUENCE public.tb_password_reset_token_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tb_password_reset_token_seq OWNER TO dbaiamplatform01;

--
-- Name: tb_privilege; Type: TABLE; Schema: public; Owner: dbaiamplatform01
--

CREATE TABLE public.tb_privilege (
                                     id bigint NOT NULL,
                                     name character varying(255)
);


ALTER TABLE public.tb_privilege OWNER TO dbaiamplatform01;

--
-- Name: tb_privilege_seq; Type: SEQUENCE; Schema: public; Owner: dbaiamplatform01
--

CREATE SEQUENCE public.tb_privilege_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tb_privilege_seq OWNER TO dbaiamplatform01;

--
-- Name: tb_resource_thing; Type: TABLE; Schema: public; Owner: dbaiamplatform01
--

CREATE TABLE public.tb_resource_thing (
                                          id character varying(255) NOT NULL,
                                          created_at timestamp(6) with time zone,
                                          full_content character varying(255),
                                          metadata character varying(255),
                                          summary_content character varying(255),
                                          title character varying(255),
                                          updated_at timestamp(6) with time zone,
                                          user_creator_id bigint
);


ALTER TABLE public.tb_resource_thing OWNER TO dbaiamplatform01;

--
-- Name: tb_role; Type: TABLE; Schema: public; Owner: dbaiamplatform01
--

CREATE TABLE public.tb_role (
                                id bigint NOT NULL,
                                name character varying(255)
);


ALTER TABLE public.tb_role OWNER TO dbaiamplatform01;

--
-- Name: tb_role_seq; Type: SEQUENCE; Schema: public; Owner: dbaiamplatform01
--

CREATE SEQUENCE public.tb_role_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tb_role_seq OWNER TO dbaiamplatform01;

--
-- Name: tb_roles_privileges; Type: TABLE; Schema: public; Owner: dbaiamplatform01
--

CREATE TABLE public.tb_roles_privileges (
                                            role_id bigint NOT NULL,
                                            privilege_id bigint NOT NULL
);


ALTER TABLE public.tb_roles_privileges OWNER TO dbaiamplatform01;

--
-- Name: tb_user_account; Type: TABLE; Schema: public; Owner: dbaiamplatform01
--

CREATE TABLE public.tb_user_account (
                                        id bigint NOT NULL,
                                        email character varying(255),
                                        enabled boolean NOT NULL,
                                        first_name character varying(255),
                                        last_name character varying(255),
                                        password character varying(60),
                                        username character varying(255)
);


ALTER TABLE public.tb_user_account OWNER TO dbaiamplatform01;

--
-- Name: tb_user_account_seq; Type: SEQUENCE; Schema: public; Owner: dbaiamplatform01
--

CREATE SEQUENCE public.tb_user_account_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tb_user_account_seq OWNER TO dbaiamplatform01;

--
-- Name: tb_users_roles; Type: TABLE; Schema: public; Owner: dbaiamplatform01
--

CREATE TABLE public.tb_users_roles (
                                       user_id bigint NOT NULL,
                                       role_id bigint NOT NULL
);


ALTER TABLE public.tb_users_roles OWNER TO dbaiamplatform01;

--
-- Name: tb_verification_token; Type: TABLE; Schema: public; Owner: dbaiamplatform01
--

CREATE TABLE public.tb_verification_token (
                                              id bigint NOT NULL,
                                              expiry_date timestamp(6) without time zone,
                                              token character varying(255),
                                              user_id bigint NOT NULL
);


ALTER TABLE public.tb_verification_token OWNER TO dbaiamplatform01;

--
-- Name: tb_verification_token_seq; Type: SEQUENCE; Schema: public; Owner: dbaiamplatform01
--

CREATE SEQUENCE public.tb_verification_token_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tb_verification_token_seq OWNER TO dbaiamplatform01;

--
-- Data for Name: spatial_ref_sys; Type: TABLE DATA; Schema: public; Owner: dbaiamplatform01
--



--
-- Data for Name: tb_password_reset_token; Type: TABLE DATA; Schema: public; Owner: dbaiamplatform01
--



--
-- Data for Name: tb_privilege; Type: TABLE DATA; Schema: public; Owner: dbaiamplatform01
--

INSERT INTO public.tb_privilege (id, name) VALUES (1, 'READ_PRIVILEGE');
INSERT INTO public.tb_privilege (id, name) VALUES (2, 'WRITE_PRIVILEGE');
INSERT INTO public.tb_privilege (id, name) VALUES (3, 'CHANGE_PASSWORD_PRIVILEGE');


--
-- Data for Name: tb_resource_thing; Type: TABLE DATA; Schema: public; Owner: dbaiamplatform01
--

INSERT INTO public.tb_resource_thing (id, created_at, full_content, metadata, summary_content, title, updated_at, user_creator_id) VALUES ('doc-1000', '2026-04-09 19:40:49.579983+00', 'Long full Content - doc-1000', 'Metadata Ninja - doc-1000', 'Short Content - doc-1000', 'Document-#1000', '2026-04-09 19:40:49.579984+00', 152);
INSERT INTO public.tb_resource_thing (id, created_at, full_content, metadata, summary_content, title, updated_at, user_creator_id) VALUES ('doc-1001', '2026-04-09 19:41:57.450207+00', 'Long full Content - doc-1001', 'Metadata Ninja - doc-1001', 'Short Content - doc-1001', 'Document-#1001', '2026-04-09 19:41:57.450208+00', 2);
INSERT INTO public.tb_resource_thing (id, created_at, full_content, metadata, summary_content, title, updated_at, user_creator_id) VALUES ('doc-1002', '2026-04-09 19:41:57.487692+00', 'Long full Content - doc-1002', 'Metadata Ninja - doc-1002', 'Short Content - doc-1002', 'Document-#1002', '2026-04-09 19:41:57.487692+00', 104);
INSERT INTO public.tb_resource_thing (id, created_at, full_content, metadata, summary_content, title, updated_at, user_creator_id) VALUES ('doc-1003', '2026-04-09 19:41:57.509644+00', 'Long full Content - doc-1003', 'Metadata Ninja - doc-1003', 'Short Content - doc-1003', 'Document-#1003', '2026-04-09 19:41:57.509644+00', 105);
INSERT INTO public.tb_resource_thing (id, created_at, full_content, metadata, summary_content, title, updated_at, user_creator_id) VALUES ('doc-1004', '2026-04-09 19:41:57.538618+00', 'Long full Content - doc-1004', 'Metadata Ninja - doc-1004', 'Short Content - doc-1004', 'Document-#1004', '2026-04-09 19:41:57.538618+00', 2);
INSERT INTO public.tb_resource_thing (id, created_at, full_content, metadata, summary_content, title, updated_at, user_creator_id) VALUES ('doc-1005', '2026-04-09 19:41:57.567834+00', 'Long full Content - doc-1005', 'Metadata Ninja - doc-1005', 'Short Content - doc-1005', 'Document-#1005', '2026-04-09 19:41:57.567834+00', 104);
INSERT INTO public.tb_resource_thing (id, created_at, full_content, metadata, summary_content, title, updated_at, user_creator_id) VALUES ('doc-1006', '2026-04-09 19:41:57.598419+00', 'Long full Content - doc-1006', 'Metadata Ninja - doc-1006', 'Short Content - doc-1006', 'Document-#1006', '2026-04-09 19:41:57.598419+00', 105);
INSERT INTO public.tb_resource_thing (id, created_at, full_content, metadata, summary_content, title, updated_at, user_creator_id) VALUES ('doc-1007', '2026-04-09 19:43:21.219198+00', 'Long full Content - doc-1007', 'Metadata Ninja - doc-1007', 'Short Content - doc-1007', 'Document-#1007', '2026-04-09 19:43:21.219199+00', 52);
INSERT INTO public.tb_resource_thing (id, created_at, full_content, metadata, summary_content, title, updated_at, user_creator_id) VALUES ('doc-1008', '2026-04-09 19:43:21.25057+00', 'Long full Content - doc-1008', 'Metadata Ninja - doc-1008', 'Short Content - doc-1008', 'Document-#1008', '2026-04-09 19:43:21.250571+00', 102);
INSERT INTO public.tb_resource_thing (id, created_at, full_content, metadata, summary_content, title, updated_at, user_creator_id) VALUES ('doc-1009', '2026-04-09 19:43:21.278226+00', 'Long full Content - doc-1009', 'Metadata Ninja - doc-1009', 'Short Content - doc-1009', 'Document-#1009', '2026-04-09 19:43:21.278226+00', 103);
INSERT INTO public.tb_resource_thing (id, created_at, full_content, metadata, summary_content, title, updated_at, user_creator_id) VALUES ('doc-1010', '2026-04-09 19:43:21.308212+00', 'Long full Content - doc-1010', 'Metadata Ninja - doc-1010', 'Short Content - doc-1010', 'Document-#1010', '2026-04-09 19:43:21.308212+00', 52);
INSERT INTO public.tb_resource_thing (id, created_at, full_content, metadata, summary_content, title, updated_at, user_creator_id) VALUES ('doc-1011', '2026-04-09 19:43:21.33371+00', 'Long full Content - doc-1011', 'Metadata Ninja - doc-1011', 'Short Content - doc-1011', 'Document-#1011', '2026-04-09 19:43:21.33371+00', 102);
INSERT INTO public.tb_resource_thing (id, created_at, full_content, metadata, summary_content, title, updated_at, user_creator_id) VALUES ('doc-1012', '2026-04-09 19:43:21.356136+00', 'Long full Content - doc-1012', 'Metadata Ninja - doc-1012', 'Short Content - doc-1012', 'Document-#1012', '2026-04-09 19:43:21.356137+00', 103);


--
-- Data for Name: tb_role; Type: TABLE DATA; Schema: public; Owner: dbaiamplatform01
--

INSERT INTO public.tb_role (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO public.tb_role (id, name) VALUES (2, 'ROLE_USER');


--
-- Data for Name: tb_roles_privileges; Type: TABLE DATA; Schema: public; Owner: dbaiamplatform01
--

INSERT INTO public.tb_roles_privileges (role_id, privilege_id) VALUES (1, 1);
INSERT INTO public.tb_roles_privileges (role_id, privilege_id) VALUES (1, 2);
INSERT INTO public.tb_roles_privileges (role_id, privilege_id) VALUES (1, 3);
INSERT INTO public.tb_roles_privileges (role_id, privilege_id) VALUES (2, 1);
INSERT INTO public.tb_roles_privileges (role_id, privilege_id) VALUES (2, 3);


--
-- Data for Name: tb_user_account; Type: TABLE DATA; Schema: public; Owner: dbaiamplatform01
--

INSERT INTO public.tb_user_account (id, email, enabled, first_name, last_name, password, username) VALUES (1, 'test@test.com', true, 'Test', 'Test', '$2a$11$PCVM6q7uZbszUdBVYEbxZ.z9k8EXEcmyPW/JaguwiKUarcI0hG4Ie', NULL);
INSERT INTO public.tb_user_account (id, email, enabled, first_name, last_name, password, username) VALUES (52, 'bruce.wayne@dc-comics.com', true, 'Bruce', 'Wayne', '$2a$11$ntT.wljmuwWgRSjanpYf7.8CQDsQh5jLzQVzZF8VSTVfDDtTcwE46', 'batman');
INSERT INTO public.tb_user_account (id, email, enabled, first_name, last_name, password, username) VALUES (2, 'peter.parker@marvel.com', true, 'Peter', 'Parker', '$2a$11$lH/N62x.KR3jiHVXojDLwuqcwxKv0TN.7CTuYXIx92MHXAd9mtd6i', 'spiderman');
INSERT INTO public.tb_user_account (id, email, enabled, first_name, last_name, password, username) VALUES (102, 'clark.kent@dc-comics.com', true, 'Clark', 'Kent', '$2a$11$DfDB7RsXC.njtUKdRCHoUe/yG7Zjg.jkbrRi8/R7ctsM1gUB.eajq', 'superman');
INSERT INTO public.tb_user_account (id, email, enabled, first_name, last_name, password, username) VALUES (103, 'diana.prince@dc-comics.com', true, 'Diana', 'Prince', '$2a$11$GlkCltRtducYjFy6meHLie..0ZE0u/MmbUxHyPJMtj9CTWxrtUPTG', 'wonder-woman');
INSERT INTO public.tb_user_account (id, email, enabled, first_name, last_name, password, username) VALUES (104, 'tony.stark@marvel.com', true, 'Tony', 'Stark', '$2a$11$61q4ha6BGgIvqzjPTO.lTeFXVGBdJOzaInOhvpuThmN.4yWiY17Sm', 'ironman');
INSERT INTO public.tb_user_account (id, email, enabled, first_name, last_name, password, username) VALUES (105, 'natasha.romanova@marvel.com', true, 'Natasha', 'Romanova', '$2a$11$NK8PxTv6CsDmLpGdvXMeC.O0Lg3VEV8gjvQR3E77ZUboWg82fFMJK', 'black-widow');
INSERT INTO public.tb_user_account (id, email, enabled, first_name, last_name, password, username) VALUES (152, 'chapolin.colorado@latin-america.com.mx', true, 'Roberto', 'Bolaños', '$2a$11$Hlbj3eCktH2TjelR68QrFeM4ENuG9AbsDRk3FNd9oYlkVEnexwBpm', 'chapolin');


--
-- Data for Name: tb_users_roles; Type: TABLE DATA; Schema: public; Owner: dbaiamplatform01
--

INSERT INTO public.tb_users_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO public.tb_users_roles (user_id, role_id) VALUES (52, 2);
INSERT INTO public.tb_users_roles (user_id, role_id) VALUES (102, 2);
INSERT INTO public.tb_users_roles (user_id, role_id) VALUES (103, 2);
INSERT INTO public.tb_users_roles (user_id, role_id) VALUES (104, 2);
INSERT INTO public.tb_users_roles (user_id, role_id) VALUES (105, 2);
INSERT INTO public.tb_users_roles (user_id, role_id) VALUES (152, 2);
INSERT INTO public.tb_users_roles (user_id, role_id) VALUES (1, 1);


--
-- Data for Name: tb_verification_token; Type: TABLE DATA; Schema: public; Owner: dbaiamplatform01
--

INSERT INTO public.tb_verification_token (id, expiry_date, token, user_id) VALUES (1, '2026-04-08 14:16:08.159', 'af229944-dbf1-42d5-aa50-92e21e76667f', 2);
INSERT INTO public.tb_verification_token (id, expiry_date, token, user_id) VALUES (2, '2026-04-08 14:27:03.82', 'dc3a9dbf-846d-4b48-aaff-47653e014339', 52);
INSERT INTO public.tb_verification_token (id, expiry_date, token, user_id) VALUES (52, '2026-04-08 15:48:49.832', '63fd56d4-cfcd-4445-b035-dd1c95ba3714', 102);
INSERT INTO public.tb_verification_token (id, expiry_date, token, user_id) VALUES (54, '2026-04-08 15:53:00.809', '9adb2f5b-b2d5-4e42-a04f-538f51eb93ad', 103);
INSERT INTO public.tb_verification_token (id, expiry_date, token, user_id) VALUES (56, '2026-04-08 15:54:49.722', '1a5a65ea-b97f-4561-92dd-0c9277ce8909', 104);
INSERT INTO public.tb_verification_token (id, expiry_date, token, user_id) VALUES (58, '2026-04-08 15:56:54.783', '78231714-9a42-44b5-97f5-37b6897837ac', 105);
INSERT INTO public.tb_verification_token (id, expiry_date, token, user_id) VALUES (102, '2026-04-10 19:25:14.583', '541f9d9b-270d-4541-9641-3521672959a1', 152);


--
-- Name: tb_password_reset_token_seq; Type: SEQUENCE SET; Schema: public; Owner: dbaiamplatform01
--

SELECT pg_catalog.setval('public.tb_password_reset_token_seq', 251, true);


--
-- Name: tb_privilege_seq; Type: SEQUENCE SET; Schema: public; Owner: dbaiamplatform01
--

SELECT pg_catalog.setval('public.tb_privilege_seq', 51, true);


--
-- Name: tb_role_seq; Type: SEQUENCE SET; Schema: public; Owner: dbaiamplatform01
--

SELECT pg_catalog.setval('public.tb_role_seq', 51, true);


--
-- Name: tb_user_account_seq; Type: SEQUENCE SET; Schema: public; Owner: dbaiamplatform01
--

SELECT pg_catalog.setval('public.tb_user_account_seq', 201, true);


--
-- Name: tb_verification_token_seq; Type: SEQUENCE SET; Schema: public; Owner: dbaiamplatform01
--

SELECT pg_catalog.setval('public.tb_verification_token_seq', 151, true);


--
-- Name: tb_resource_thing idx_resourcething_title; Type: CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_resource_thing
    ADD CONSTRAINT idx_resourcething_title UNIQUE (title);


--
-- Name: tb_user_account idx_useraccount_username; Type: CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_user_account
    ADD CONSTRAINT idx_useraccount_username UNIQUE (username);


--
-- Name: tb_password_reset_token tb_password_reset_token_pkey; Type: CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_password_reset_token
    ADD CONSTRAINT tb_password_reset_token_pkey PRIMARY KEY (id);


--
-- Name: tb_privilege tb_privilege_pkey; Type: CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_privilege
    ADD CONSTRAINT tb_privilege_pkey PRIMARY KEY (id);


--
-- Name: tb_resource_thing tb_resource_thing_pkey; Type: CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_resource_thing
    ADD CONSTRAINT tb_resource_thing_pkey PRIMARY KEY (id);


--
-- Name: tb_role tb_role_pkey; Type: CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_role
    ADD CONSTRAINT tb_role_pkey PRIMARY KEY (id);


--
-- Name: tb_user_account tb_user_account_pkey; Type: CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_user_account
    ADD CONSTRAINT tb_user_account_pkey PRIMARY KEY (id);


--
-- Name: tb_verification_token tb_verification_token_pkey; Type: CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_verification_token
    ADD CONSTRAINT tb_verification_token_pkey PRIMARY KEY (id);


--
-- Name: tb_verification_token uk3iemcfd81q5fybbrsjamk2ybn; Type: CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_verification_token
    ADD CONSTRAINT uk3iemcfd81q5fybbrsjamk2ybn UNIQUE (user_id);


--
-- Name: tb_password_reset_token uk642iqcc6ow1fwqi1n7pdi03xa; Type: CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_password_reset_token
    ADD CONSTRAINT uk642iqcc6ow1fwqi1n7pdi03xa UNIQUE (user_id);


--
-- Name: tb_roles_privileges fk16d4c9pjnjo23gq5lixjsnbcf; Type: FK CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_roles_privileges
    ADD CONSTRAINT fk16d4c9pjnjo23gq5lixjsnbcf FOREIGN KEY (privilege_id) REFERENCES public.tb_privilege(id);


--
-- Name: tb_users_roles fk6p4o2kxbq23rthm174k19xo2h; Type: FK CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_users_roles
    ADD CONSTRAINT fk6p4o2kxbq23rthm174k19xo2h FOREIGN KEY (role_id) REFERENCES public.tb_role(id);


--
-- Name: tb_verification_token fk_verify_user; Type: FK CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_verification_token
    ADD CONSTRAINT fk_verify_user FOREIGN KEY (user_id) REFERENCES public.tb_user_account(id);


--
-- Name: tb_password_reset_token fkawo52nnu4xwm45qgkq3e98764; Type: FK CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_password_reset_token
    ADD CONSTRAINT fkawo52nnu4xwm45qgkq3e98764 FOREIGN KEY (user_id) REFERENCES public.tb_user_account(id);


--
-- Name: tb_roles_privileges fkbht50m8tjwu81l4kiji4mlxw0; Type: FK CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_roles_privileges
    ADD CONSTRAINT fkbht50m8tjwu81l4kiji4mlxw0 FOREIGN KEY (role_id) REFERENCES public.tb_role(id);


--
-- Name: tb_resource_thing fkgodafj86iq0t5uc2pt4codrm0; Type: FK CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_resource_thing
    ADD CONSTRAINT fkgodafj86iq0t5uc2pt4codrm0 FOREIGN KEY (user_creator_id) REFERENCES public.tb_user_account(id);


--
-- Name: tb_users_roles fkim74qu8ydqwltn3kv14uktdqw; Type: FK CONSTRAINT; Schema: public; Owner: dbaiamplatform01
--

ALTER TABLE ONLY public.tb_users_roles
    ADD CONSTRAINT fkim74qu8ydqwltn3kv14uktdqw FOREIGN KEY (user_id) REFERENCES public.tb_user_account(id);


--
-- PostgreSQL database dump complete
--
