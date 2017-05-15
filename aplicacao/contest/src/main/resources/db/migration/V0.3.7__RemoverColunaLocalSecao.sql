ALTER TABLE secao DROP COLUMN local;

CREATE SEQUENCE secao_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: secao_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE secao_id_seq OWNED BY secao.id;


--
-- Name: secao; Type: TABLE; Schema: public; Owner: -
--


ALTER TABLE ONLY secao ALTER COLUMN id SET DEFAULT nextval('secao_id_seq'::regclass);

SELECT pg_catalog.setval('secao_id_seq', 1, false);

CREATE SEQUENCE hibernate_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE hibernate_sequence OWNER TO postgres;