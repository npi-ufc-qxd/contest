ALTER TABLE secao DROP COLUMN local;
--
-- Name: secao_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

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