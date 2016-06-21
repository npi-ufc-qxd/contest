--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.8
-- Dumped by pg_dump version 9.5.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: evento; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE evento (
    id integer NOT NULL,
    descricao character varying(255),
    estado character varying(255),
    nome character varying(255) NOT NULL,
    prazo_revisao date,
    prazo_submissao date,
    visibilidade character varying(255)
);


-- ALTER TABLE evento OWNER TO postgres;

--
-- Name: evento_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE evento_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- ALTER TABLE evento_id_seq OWNER TO postgres;

--
-- Name: evento_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE evento_id_seq OWNED BY evento.id;


--
-- Name: notificacao; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE notificacao (
    id bigint NOT NULL,
    descricao character varying(255) NOT NULL,
    nova boolean,
    titulo character varying(255) NOT NULL,
    pessoa_id bigint
);


-- ALTER TABLE notificacao OWNER TO postgres;

--
-- Name: notificacao_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE notificacao_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- ALTER TABLE notificacao_id_seq OWNER TO postgres;

--
-- Name: notificacao_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE notificacao_id_seq OWNED BY notificacao.id;


--
-- Name: participacao_evento; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE participacao_evento (
    id bigint NOT NULL,
    papel character varying(255) NOT NULL,
    evento_id integer,
    pessoa_id bigint
);


-- ALTER TABLE participacao_evento OWNER TO postgres;

--
-- Name: participacao_evento_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE participacao_evento_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- ALTER TABLE participacao_evento_id_seq OWNER TO postgres;

--
-- Name: participacao_evento_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE participacao_evento_id_seq OWNED BY participacao_evento.id;


--
-- Name: participacao_trabalho; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE participacao_trabalho (
    id bigint NOT NULL,
    papel character varying(255) NOT NULL,
    pessoa_id bigint,
    trabalho_id bigint
);


-- ALTER TABLE participacao_trabalho OWNER TO postgres;

--
-- Name: participacao_trabalho_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE participacao_trabalho_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- ALTER TABLE participacao_trabalho_id_seq OWNER TO postgres;

--
-- Name: participacao_trabalho_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE participacao_trabalho_id_seq OWNED BY participacao_trabalho.id;


--
-- Name: pessoa; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE pessoa (
    id bigint NOT NULL,
    cpf character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    nome character varying(255) NOT NULL
);


-- ALTER TABLE pessoa OWNER TO postgres;

--
-- Name: pessoa_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE pessoa_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- ALTER TABLE pessoa_id_seq OWNER TO postgres;

--
-- Name: pessoa_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE pessoa_id_seq OWNED BY pessoa.id;


--
-- Name: revisao; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE revisao (
    id bigint NOT NULL,
    conteudo character varying(255),
    revisor_id bigint,
    trabalho_id bigint
);


-- ALTER TABLE revisao OWNER TO postgres;

--
-- Name: revisao_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE revisao_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- ALTER TABLE revisao_id_seq OWNER TO postgres;

--
-- Name: revisao_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE revisao_id_seq OWNED BY revisao.id;


--
-- Name: submissao; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE submissao (
    id bigint NOT NULL,
    data_submissao date,
    tipo_submissao character varying(255),
    trabalho_id bigint
);


-- ALTER TABLE submissao OWNER TO postgres;

--
-- Name: submissao_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE submissao_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- ALTER TABLE submissao_id_seq OWNER TO postgres;

--
-- Name: submissao_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE submissao_id_seq OWNED BY submissao.id;


--
-- Name: trabalho; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE trabalho (
    id bigint NOT NULL,
    titulo character varying(255) NOT NULL,
    evento_id integer,
    trilha_id bigint
);


-- ALTER TABLE trabalho OWNER TO postgres;

--
-- Name: trabalho_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE trabalho_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- ALTER TABLE trabalho_id_seq OWNER TO postgres;

--
-- Name: trabalho_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE trabalho_id_seq OWNED BY trabalho.id;


--
-- Name: trilha; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE trilha (
    id bigint NOT NULL,
    nome character varying(255) NOT NULL,
    evento_id integer
);


-- ALTER TABLE trilha OWNER TO postgres;

--
-- Name: trilha_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE trilha_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


-- ALTER TABLE trilha_id_seq OWNER TO postgres;

--
-- Name: trilha_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE trilha_id_seq OWNED BY trilha.id;


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY evento ALTER COLUMN id SET DEFAULT nextval('evento_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY notificacao ALTER COLUMN id SET DEFAULT nextval('notificacao_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY participacao_evento ALTER COLUMN id SET DEFAULT nextval('participacao_evento_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY participacao_trabalho ALTER COLUMN id SET DEFAULT nextval('participacao_trabalho_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pessoa ALTER COLUMN id SET DEFAULT nextval('pessoa_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY revisao ALTER COLUMN id SET DEFAULT nextval('revisao_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY submissao ALTER COLUMN id SET DEFAULT nextval('submissao_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trabalho ALTER COLUMN id SET DEFAULT nextval('trabalho_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trilha ALTER COLUMN id SET DEFAULT nextval('trilha_id_seq'::regclass);


--
-- Data for Name: evento; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: evento_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('evento_id_seq', 1, false);


--
-- Data for Name: notificacao; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: notificacao_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('notificacao_id_seq', 1, false);


--
-- Data for Name: participacao_evento; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: participacao_evento_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('participacao_evento_id_seq', 1, false);


--
-- Data for Name: participacao_trabalho; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: participacao_trabalho_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('participacao_trabalho_id_seq', 1, false);


--
-- Data for Name: pessoa; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: pessoa_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('pessoa_id_seq', 1, false);


--
-- Data for Name: revisao; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: revisao_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('revisao_id_seq', 1, false);


--
-- Data for Name: submissao; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: submissao_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('submissao_id_seq', 1, false);


--
-- Data for Name: trabalho; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: trabalho_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('trabalho_id_seq', 1, false);


--
-- Data for Name: trilha; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: trilha_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('trilha_id_seq', 1, false);


--
-- Name: evento_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY evento
    ADD CONSTRAINT evento_pkey PRIMARY KEY (id);


--
-- Name: notificacao_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY notificacao
    ADD CONSTRAINT notificacao_pkey PRIMARY KEY (id);


--
-- Name: participacao_evento_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY participacao_evento
    ADD CONSTRAINT participacao_evento_pkey PRIMARY KEY (id);


--
-- Name: participacao_trabalho_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY participacao_trabalho
    ADD CONSTRAINT participacao_trabalho_pkey PRIMARY KEY (id);


--
-- Name: pessoa_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pessoa
    ADD CONSTRAINT pessoa_pkey PRIMARY KEY (id);


--
-- Name: revisao_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY revisao
    ADD CONSTRAINT revisao_pkey PRIMARY KEY (id);


--
-- Name: submissao_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY submissao
    ADD CONSTRAINT submissao_pkey PRIMARY KEY (id);


--
-- Name: trabalho_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trabalho
    ADD CONSTRAINT trabalho_pkey PRIMARY KEY (id);


--
-- Name: trilha_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trilha
    ADD CONSTRAINT trilha_pkey PRIMARY KEY (id);


--
-- Name: fk_706dg7kyw11p30rh5u0ahfukb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY submissao
    ADD CONSTRAINT fk_706dg7kyw11p30rh5u0ahfukb FOREIGN KEY (trabalho_id) REFERENCES trabalho(id);


--
-- Name: fk_9kxiwwysc10fjk6x0c5msark3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trilha
    ADD CONSTRAINT fk_9kxiwwysc10fjk6x0c5msark3 FOREIGN KEY (evento_id) REFERENCES evento(id);


--
-- Name: fk_hqrrhrhy5y2fu3o9hkfcv9x4p; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY participacao_evento
    ADD CONSTRAINT fk_hqrrhrhy5y2fu3o9hkfcv9x4p FOREIGN KEY (pessoa_id) REFERENCES pessoa(id);


--
-- Name: fk_j3ti2uiktiosu9nm0xd7jh8ti; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY revisao
    ADD CONSTRAINT fk_j3ti2uiktiosu9nm0xd7jh8ti FOREIGN KEY (trabalho_id) REFERENCES trabalho(id);


--
-- Name: fk_jyip5h0lxpu6ijvxe9id19vh; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY participacao_trabalho
    ADD CONSTRAINT fk_jyip5h0lxpu6ijvxe9id19vh FOREIGN KEY (pessoa_id) REFERENCES pessoa(id);


--
-- Name: fk_ke4rf2pcx59i5hlhhp1qgmast; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trabalho
    ADD CONSTRAINT fk_ke4rf2pcx59i5hlhhp1qgmast FOREIGN KEY (trilha_id) REFERENCES trilha(id);


--
-- Name: fk_l4y6qow64t5kof7hanou4e3ut; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY participacao_trabalho
    ADD CONSTRAINT fk_l4y6qow64t5kof7hanou4e3ut FOREIGN KEY (trabalho_id) REFERENCES trabalho(id);


--
-- Name: fk_l5h8akrj1ffhy6ks06o0r6rea; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY trabalho
    ADD CONSTRAINT fk_l5h8akrj1ffhy6ks06o0r6rea FOREIGN KEY (evento_id) REFERENCES evento(id);


--
-- Name: fk_prw2j04t1ngiwwlb439gnv5g0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY revisao
    ADD CONSTRAINT fk_prw2j04t1ngiwwlb439gnv5g0 FOREIGN KEY (revisor_id) REFERENCES pessoa(id);


--
-- Name: fk_qevkyl4oucwj187qfuhsjxrfe; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY participacao_evento
    ADD CONSTRAINT fk_qevkyl4oucwj187qfuhsjxrfe FOREIGN KEY (evento_id) REFERENCES evento(id);


--
-- Name: fk_tbt1gwtkjjkse4purep3p2in1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY notificacao
    ADD CONSTRAINT fk_tbt1gwtkjjkse4purep3p2in1 FOREIGN KEY (pessoa_id) REFERENCES pessoa(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--