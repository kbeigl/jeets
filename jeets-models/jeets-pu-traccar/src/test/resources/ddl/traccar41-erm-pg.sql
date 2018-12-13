--
-- PostgreSQL database dump
--

-- Dumped from database version 11.0
-- Dumped by pg_dump version 11.0
-- AFTER CREATING TRACCAR 4.1 with liquibase
-- Started on 2018-11-02 18:36:50

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- TOC entry 3187 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 197 (class 1259 OID 16399)
-- Name: databasechangelog; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.databasechangelog (
    id character varying(255) NOT NULL,
    author character varying(255) NOT NULL,
    filename character varying(255) NOT NULL,
    dateexecuted timestamp without time zone NOT NULL,
    orderexecuted integer NOT NULL,
    exectype character varying(10) NOT NULL,
    md5sum character varying(35),
    description character varying(255),
    comments character varying(255),
    tag character varying(255),
    liquibase character varying(20),
    contexts character varying(255),
    labels character varying(255),
    deployment_id character varying(10)
);


ALTER TABLE public.databasechangelog OWNER TO postgres;

--
-- TOC entry 196 (class 1259 OID 16394)
-- Name: databasechangeloglock; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp without time zone,
    lockedby character varying(255)
);


ALTER TABLE public.databasechangeloglock OWNER TO postgres;

--
-- TOC entry 199 (class 1259 OID 16407)
-- Name: tc_attributes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_attributes (
    id integer NOT NULL,
    description character varying(4000) NOT NULL,
    type character varying(128) NOT NULL,
    attribute character varying(128) NOT NULL,
    expression character varying(4000) NOT NULL
);


ALTER TABLE public.tc_attributes OWNER TO postgres;

--
-- TOC entry 198 (class 1259 OID 16405)
-- Name: tc_attributes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_attributes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_attributes_id_seq OWNER TO postgres;

--
-- TOC entry 3188 (class 0 OID 0)
-- Dependencies: 198
-- Name: tc_attributes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_attributes_id_seq OWNED BY public.tc_attributes.id;


--
-- TOC entry 201 (class 1259 OID 16418)
-- Name: tc_calendars; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_calendars (
    id integer NOT NULL,
    name character varying(128) NOT NULL,
    data bytea NOT NULL,
    attributes character varying(4000) NOT NULL
);


ALTER TABLE public.tc_calendars OWNER TO postgres;

--
-- TOC entry 200 (class 1259 OID 16416)
-- Name: tc_calendars_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_calendars_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_calendars_id_seq OWNER TO postgres;

--
-- TOC entry 3189 (class 0 OID 0)
-- Dependencies: 200
-- Name: tc_calendars_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_calendars_id_seq OWNED BY public.tc_calendars.id;


--
-- TOC entry 203 (class 1259 OID 16429)
-- Name: tc_commands; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_commands (
    id integer NOT NULL,
    description character varying(4000) NOT NULL,
    type character varying(128) NOT NULL,
    textchannel boolean DEFAULT false NOT NULL,
    attributes character varying(4000) NOT NULL
);


ALTER TABLE public.tc_commands OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 16427)
-- Name: tc_commands_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_commands_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_commands_id_seq OWNER TO postgres;

--
-- TOC entry 3190 (class 0 OID 0)
-- Dependencies: 202
-- Name: tc_commands_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_commands_id_seq OWNED BY public.tc_commands.id;


--
-- TOC entry 204 (class 1259 OID 16439)
-- Name: tc_device_attribute; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_device_attribute (
    deviceid integer NOT NULL,
    attributeid integer NOT NULL
);


ALTER TABLE public.tc_device_attribute OWNER TO postgres;

--
-- TOC entry 205 (class 1259 OID 16442)
-- Name: tc_device_command; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_device_command (
    deviceid integer NOT NULL,
    commandid integer NOT NULL
);


ALTER TABLE public.tc_device_command OWNER TO postgres;

--
-- TOC entry 206 (class 1259 OID 16445)
-- Name: tc_device_driver; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_device_driver (
    deviceid integer NOT NULL,
    driverid integer NOT NULL
);


ALTER TABLE public.tc_device_driver OWNER TO postgres;

--
-- TOC entry 207 (class 1259 OID 16448)
-- Name: tc_device_geofence; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_device_geofence (
    deviceid integer NOT NULL,
    geofenceid integer NOT NULL
);


ALTER TABLE public.tc_device_geofence OWNER TO postgres;

--
-- TOC entry 208 (class 1259 OID 16451)
-- Name: tc_device_maintenance; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_device_maintenance (
    deviceid integer NOT NULL,
    maintenanceid integer NOT NULL
);


ALTER TABLE public.tc_device_maintenance OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 16454)
-- Name: tc_device_notification; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_device_notification (
    deviceid integer NOT NULL,
    notificationid integer NOT NULL
);


ALTER TABLE public.tc_device_notification OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 16459)
-- Name: tc_devices; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_devices (
    id integer NOT NULL,
    name character varying(128) NOT NULL,
    uniqueid character varying(128) NOT NULL,
    lastupdate timestamp without time zone,
    positionid integer,
    groupid integer,
    attributes character varying(4000),
    phone character varying(128),
    model character varying(128),
    contact character varying(512),
    category character varying(128),
    disabled boolean DEFAULT false
);


ALTER TABLE public.tc_devices OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 16457)
-- Name: tc_devices_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_devices_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_devices_id_seq OWNER TO postgres;

--
-- TOC entry 3191 (class 0 OID 0)
-- Dependencies: 210
-- Name: tc_devices_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_devices_id_seq OWNED BY public.tc_devices.id;


--
-- TOC entry 213 (class 1259 OID 16473)
-- Name: tc_drivers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_drivers (
    id integer NOT NULL,
    name character varying(128) NOT NULL,
    uniqueid character varying(128) NOT NULL,
    attributes character varying(4000) NOT NULL
);


ALTER TABLE public.tc_drivers OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 16471)
-- Name: tc_drivers_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_drivers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_drivers_id_seq OWNER TO postgres;

--
-- TOC entry 3192 (class 0 OID 0)
-- Dependencies: 212
-- Name: tc_drivers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_drivers_id_seq OWNED BY public.tc_drivers.id;


--
-- TOC entry 215 (class 1259 OID 16486)
-- Name: tc_events; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_events (
    id integer NOT NULL,
    type character varying(128) NOT NULL,
    servertime timestamp without time zone NOT NULL,
    deviceid integer,
    positionid integer,
    geofenceid integer,
    attributes character varying(4000),
    maintenanceid integer
);


ALTER TABLE public.tc_events OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 16484)
-- Name: tc_events_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_events_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_events_id_seq OWNER TO postgres;

--
-- TOC entry 3193 (class 0 OID 0)
-- Dependencies: 214
-- Name: tc_events_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_events_id_seq OWNED BY public.tc_events.id;


--
-- TOC entry 217 (class 1259 OID 16497)
-- Name: tc_geofences; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_geofences (
    id integer NOT NULL,
    name character varying(128) NOT NULL,
    description character varying(128),
    area character varying(4096) NOT NULL,
    attributes character varying(4000),
    calendarid integer
);


ALTER TABLE public.tc_geofences OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 16495)
-- Name: tc_geofences_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_geofences_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_geofences_id_seq OWNER TO postgres;

--
-- TOC entry 3194 (class 0 OID 0)
-- Dependencies: 216
-- Name: tc_geofences_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_geofences_id_seq OWNED BY public.tc_geofences.id;


--
-- TOC entry 218 (class 1259 OID 16506)
-- Name: tc_group_attribute; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_group_attribute (
    groupid integer NOT NULL,
    attributeid integer NOT NULL
);


ALTER TABLE public.tc_group_attribute OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16509)
-- Name: tc_group_command; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_group_command (
    groupid integer NOT NULL,
    commandid integer NOT NULL
);


ALTER TABLE public.tc_group_command OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16512)
-- Name: tc_group_driver; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_group_driver (
    groupid integer NOT NULL,
    driverid integer NOT NULL
);


ALTER TABLE public.tc_group_driver OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16515)
-- Name: tc_group_geofence; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_group_geofence (
    groupid integer NOT NULL,
    geofenceid integer NOT NULL
);


ALTER TABLE public.tc_group_geofence OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16518)
-- Name: tc_group_maintenance; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_group_maintenance (
    groupid integer NOT NULL,
    maintenanceid integer NOT NULL
);


ALTER TABLE public.tc_group_maintenance OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16521)
-- Name: tc_group_notification; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_group_notification (
    groupid integer NOT NULL,
    notificationid integer NOT NULL
);


ALTER TABLE public.tc_group_notification OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16526)
-- Name: tc_groups; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_groups (
    id integer NOT NULL,
    name character varying(128) NOT NULL,
    groupid integer,
    attributes character varying(4000)
);


ALTER TABLE public.tc_groups OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16524)
-- Name: tc_groups_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_groups_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_groups_id_seq OWNER TO postgres;

--
-- TOC entry 3195 (class 0 OID 0)
-- Dependencies: 224
-- Name: tc_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_groups_id_seq OWNED BY public.tc_groups.id;


--
-- TOC entry 227 (class 1259 OID 16537)
-- Name: tc_maintenances; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_maintenances (
    id integer NOT NULL,
    name character varying(4000) NOT NULL,
    type character varying(128) NOT NULL,
    start double precision DEFAULT 0 NOT NULL,
    period double precision DEFAULT 0 NOT NULL,
    attributes character varying(4000) NOT NULL
);


ALTER TABLE public.tc_maintenances OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16535)
-- Name: tc_maintenances_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_maintenances_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_maintenances_id_seq OWNER TO postgres;

--
-- TOC entry 3196 (class 0 OID 0)
-- Dependencies: 226
-- Name: tc_maintenances_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_maintenances_id_seq OWNED BY public.tc_maintenances.id;


--
-- TOC entry 229 (class 1259 OID 16550)
-- Name: tc_notifications; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_notifications (
    id integer NOT NULL,
    type character varying(128) NOT NULL,
    attributes character varying(4000),
    always boolean DEFAULT false NOT NULL,
    calendarid integer,
    notificators character varying(128)
);


ALTER TABLE public.tc_notifications OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 16548)
-- Name: tc_notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_notifications_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_notifications_id_seq OWNER TO postgres;

--
-- TOC entry 3197 (class 0 OID 0)
-- Dependencies: 228
-- Name: tc_notifications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_notifications_id_seq OWNED BY public.tc_notifications.id;


--
-- TOC entry 231 (class 1259 OID 16562)
-- Name: tc_positions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_positions (
    id integer NOT NULL,
    protocol character varying(128),
    deviceid integer NOT NULL,
    servertime timestamp without time zone DEFAULT now() NOT NULL,
    devicetime timestamp without time zone NOT NULL,
    fixtime timestamp without time zone NOT NULL,
    valid boolean NOT NULL,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    altitude double precision NOT NULL,
    speed double precision NOT NULL,
    course double precision NOT NULL,
    address character varying(512),
    attributes character varying(4000),
    accuracy double precision DEFAULT 0 NOT NULL,
    network character varying(4000)
);


ALTER TABLE public.tc_positions OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 16560)
-- Name: tc_positions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_positions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_positions_id_seq OWNER TO postgres;

--
-- TOC entry 3198 (class 0 OID 0)
-- Dependencies: 230
-- Name: tc_positions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_positions_id_seq OWNED BY public.tc_positions.id;


--
-- TOC entry 233 (class 1259 OID 16575)
-- Name: tc_servers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_servers (
    id integer NOT NULL,
    registration boolean DEFAULT true NOT NULL,
    latitude double precision DEFAULT 0 NOT NULL,
    longitude double precision DEFAULT 0 NOT NULL,
    zoom integer DEFAULT 0 NOT NULL,
    map character varying(128),
    bingkey character varying(128),
    mapurl character varying(512),
    readonly boolean DEFAULT false NOT NULL,
    twelvehourformat boolean DEFAULT false NOT NULL,
    attributes character varying(4000),
    forcesettings boolean DEFAULT false NOT NULL,
    coordinateformat character varying(128),
    devicereadonly boolean DEFAULT false,
    limitcommands boolean DEFAULT false,
    poilayer character varying(512)
);


ALTER TABLE public.tc_servers OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 16573)
-- Name: tc_servers_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_servers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_servers_id_seq OWNER TO postgres;

--
-- TOC entry 3199 (class 0 OID 0)
-- Dependencies: 232
-- Name: tc_servers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_servers_id_seq OWNED BY public.tc_servers.id;


--
-- TOC entry 235 (class 1259 OID 16595)
-- Name: tc_statistics; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_statistics (
    id integer NOT NULL,
    capturetime timestamp without time zone NOT NULL,
    activeusers integer DEFAULT 0 NOT NULL,
    activedevices integer DEFAULT 0 NOT NULL,
    requests integer DEFAULT 0 NOT NULL,
    messagesreceived integer DEFAULT 0 NOT NULL,
    messagesstored integer DEFAULT 0 NOT NULL,
    attributes character varying(4096) NOT NULL,
    mailsent integer DEFAULT 0 NOT NULL,
    smssent integer DEFAULT 0 NOT NULL,
    geocoderrequests integer DEFAULT 0 NOT NULL,
    geolocationrequests integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.tc_statistics OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 16593)
-- Name: tc_statistics_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_statistics_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_statistics_id_seq OWNER TO postgres;

--
-- TOC entry 3200 (class 0 OID 0)
-- Dependencies: 234
-- Name: tc_statistics_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_statistics_id_seq OWNED BY public.tc_statistics.id;


--
-- TOC entry 236 (class 1259 OID 16613)
-- Name: tc_user_attribute; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_user_attribute (
    userid integer NOT NULL,
    attributeid integer NOT NULL
);


ALTER TABLE public.tc_user_attribute OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 16616)
-- Name: tc_user_calendar; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_user_calendar (
    userid integer NOT NULL,
    calendarid integer NOT NULL
);


ALTER TABLE public.tc_user_calendar OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 16619)
-- Name: tc_user_command; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_user_command (
    userid integer NOT NULL,
    commandid integer NOT NULL
);


ALTER TABLE public.tc_user_command OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 16622)
-- Name: tc_user_device; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_user_device (
    userid integer NOT NULL,
    deviceid integer NOT NULL
);


ALTER TABLE public.tc_user_device OWNER TO postgres;

--
-- TOC entry 240 (class 1259 OID 16625)
-- Name: tc_user_driver; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_user_driver (
    userid integer NOT NULL,
    driverid integer NOT NULL
);


ALTER TABLE public.tc_user_driver OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 16628)
-- Name: tc_user_geofence; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_user_geofence (
    userid integer NOT NULL,
    geofenceid integer NOT NULL
);


ALTER TABLE public.tc_user_geofence OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 16631)
-- Name: tc_user_group; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_user_group (
    userid integer NOT NULL,
    groupid integer NOT NULL
);


ALTER TABLE public.tc_user_group OWNER TO postgres;

--
-- TOC entry 243 (class 1259 OID 16634)
-- Name: tc_user_maintenance; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_user_maintenance (
    userid integer NOT NULL,
    maintenanceid integer NOT NULL
);


ALTER TABLE public.tc_user_maintenance OWNER TO postgres;

--
-- TOC entry 244 (class 1259 OID 16637)
-- Name: tc_user_notification; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_user_notification (
    userid integer NOT NULL,
    notificationid integer NOT NULL
);


ALTER TABLE public.tc_user_notification OWNER TO postgres;

--
-- TOC entry 245 (class 1259 OID 16640)
-- Name: tc_user_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_user_user (
    userid integer NOT NULL,
    manageduserid integer NOT NULL
);


ALTER TABLE public.tc_user_user OWNER TO postgres;

--
-- TOC entry 247 (class 1259 OID 16645)
-- Name: tc_users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tc_users (
    id integer NOT NULL,
    name character varying(128) NOT NULL,
    email character varying(128) NOT NULL,
    hashedpassword character varying(128),
    salt character varying(128),
    readonly boolean DEFAULT false NOT NULL,
    administrator boolean,
    map character varying(128),
    latitude double precision DEFAULT 0 NOT NULL,
    longitude double precision DEFAULT 0 NOT NULL,
    zoom integer DEFAULT 0 NOT NULL,
    twelvehourformat boolean DEFAULT false NOT NULL,
    attributes character varying(4000),
    coordinateformat character varying(128),
    disabled boolean DEFAULT false,
    expirationtime timestamp without time zone,
    devicelimit integer DEFAULT '-1'::integer,
    token character varying(128),
    userlimit integer DEFAULT 0,
    devicereadonly boolean DEFAULT false,
    phone character varying(128),
    limitcommands boolean DEFAULT false,
    login character varying(128),
    poilayer character varying(512)
);


ALTER TABLE public.tc_users OWNER TO postgres;

--
-- TOC entry 246 (class 1259 OID 16643)
-- Name: tc_users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tc_users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tc_users_id_seq OWNER TO postgres;

--
-- TOC entry 3201 (class 0 OID 0)
-- Dependencies: 246
-- Name: tc_users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tc_users_id_seq OWNED BY public.tc_users.id;


--
-- TOC entry 2874 (class 2604 OID 16410)
-- Name: tc_attributes id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_attributes ALTER COLUMN id SET DEFAULT nextval('public.tc_attributes_id_seq'::regclass);


--
-- TOC entry 2875 (class 2604 OID 16421)
-- Name: tc_calendars id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_calendars ALTER COLUMN id SET DEFAULT nextval('public.tc_calendars_id_seq'::regclass);


--
-- TOC entry 2876 (class 2604 OID 16432)
-- Name: tc_commands id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_commands ALTER COLUMN id SET DEFAULT nextval('public.tc_commands_id_seq'::regclass);


--
-- TOC entry 2878 (class 2604 OID 16462)
-- Name: tc_devices id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_devices ALTER COLUMN id SET DEFAULT nextval('public.tc_devices_id_seq'::regclass);


--
-- TOC entry 2880 (class 2604 OID 16476)
-- Name: tc_drivers id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_drivers ALTER COLUMN id SET DEFAULT nextval('public.tc_drivers_id_seq'::regclass);


--
-- TOC entry 2881 (class 2604 OID 16489)
-- Name: tc_events id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_events ALTER COLUMN id SET DEFAULT nextval('public.tc_events_id_seq'::regclass);


--
-- TOC entry 2882 (class 2604 OID 16500)
-- Name: tc_geofences id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_geofences ALTER COLUMN id SET DEFAULT nextval('public.tc_geofences_id_seq'::regclass);


--
-- TOC entry 2883 (class 2604 OID 16529)
-- Name: tc_groups id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_groups ALTER COLUMN id SET DEFAULT nextval('public.tc_groups_id_seq'::regclass);


--
-- TOC entry 2884 (class 2604 OID 16540)
-- Name: tc_maintenances id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_maintenances ALTER COLUMN id SET DEFAULT nextval('public.tc_maintenances_id_seq'::regclass);


--
-- TOC entry 2887 (class 2604 OID 16553)
-- Name: tc_notifications id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_notifications ALTER COLUMN id SET DEFAULT nextval('public.tc_notifications_id_seq'::regclass);


--
-- TOC entry 2889 (class 2604 OID 16565)
-- Name: tc_positions id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_positions ALTER COLUMN id SET DEFAULT nextval('public.tc_positions_id_seq'::regclass);


--
-- TOC entry 2892 (class 2604 OID 16578)
-- Name: tc_servers id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_servers ALTER COLUMN id SET DEFAULT nextval('public.tc_servers_id_seq'::regclass);


--
-- TOC entry 2902 (class 2604 OID 16598)
-- Name: tc_statistics id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_statistics ALTER COLUMN id SET DEFAULT nextval('public.tc_statistics_id_seq'::regclass);


--
-- TOC entry 2912 (class 2604 OID 16648)
-- Name: tc_users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_users ALTER COLUMN id SET DEFAULT nextval('public.tc_users_id_seq'::regclass);


--
-- TOC entry 2924 (class 2606 OID 16398)
-- Name: databasechangeloglock databasechangeloglock_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.databasechangeloglock
    ADD CONSTRAINT databasechangeloglock_pkey PRIMARY KEY (id);


--
-- TOC entry 2926 (class 2606 OID 16415)
-- Name: tc_attributes tc_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_attributes
    ADD CONSTRAINT tc_attributes_pkey PRIMARY KEY (id);


--
-- TOC entry 2928 (class 2606 OID 16426)
-- Name: tc_calendars tc_calendars_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_calendars
    ADD CONSTRAINT tc_calendars_pkey PRIMARY KEY (id);


--
-- TOC entry 2930 (class 2606 OID 16438)
-- Name: tc_commands tc_commands_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_commands
    ADD CONSTRAINT tc_commands_pkey PRIMARY KEY (id);


--
-- TOC entry 2932 (class 2606 OID 16468)
-- Name: tc_devices tc_devices_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_devices
    ADD CONSTRAINT tc_devices_pkey PRIMARY KEY (id);


--
-- TOC entry 2934 (class 2606 OID 16470)
-- Name: tc_devices tc_devices_uniqueid_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_devices
    ADD CONSTRAINT tc_devices_uniqueid_key UNIQUE (uniqueid);


--
-- TOC entry 2936 (class 2606 OID 16481)
-- Name: tc_drivers tc_drivers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_drivers
    ADD CONSTRAINT tc_drivers_pkey PRIMARY KEY (id);


--
-- TOC entry 2938 (class 2606 OID 16483)
-- Name: tc_drivers tc_drivers_uniqueid_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_drivers
    ADD CONSTRAINT tc_drivers_uniqueid_key UNIQUE (uniqueid);


--
-- TOC entry 2940 (class 2606 OID 16494)
-- Name: tc_events tc_events_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_events
    ADD CONSTRAINT tc_events_pkey PRIMARY KEY (id);


--
-- TOC entry 2942 (class 2606 OID 16505)
-- Name: tc_geofences tc_geofences_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_geofences
    ADD CONSTRAINT tc_geofences_pkey PRIMARY KEY (id);


--
-- TOC entry 2944 (class 2606 OID 16534)
-- Name: tc_groups tc_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_groups
    ADD CONSTRAINT tc_groups_pkey PRIMARY KEY (id);


--
-- TOC entry 2946 (class 2606 OID 16547)
-- Name: tc_maintenances tc_maintenances_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_maintenances
    ADD CONSTRAINT tc_maintenances_pkey PRIMARY KEY (id);


--
-- TOC entry 2948 (class 2606 OID 16559)
-- Name: tc_notifications tc_notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_notifications
    ADD CONSTRAINT tc_notifications_pkey PRIMARY KEY (id);


--
-- TOC entry 2950 (class 2606 OID 16572)
-- Name: tc_positions tc_positions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_positions
    ADD CONSTRAINT tc_positions_pkey PRIMARY KEY (id);


--
-- TOC entry 2952 (class 2606 OID 16592)
-- Name: tc_servers tc_servers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_servers
    ADD CONSTRAINT tc_servers_pkey PRIMARY KEY (id);


--
-- TOC entry 2954 (class 2606 OID 16612)
-- Name: tc_statistics tc_statistics_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_statistics
    ADD CONSTRAINT tc_statistics_pkey PRIMARY KEY (id);


--
-- TOC entry 2956 (class 2606 OID 16665)
-- Name: tc_users tc_users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_users
    ADD CONSTRAINT tc_users_email_key UNIQUE (email);


--
-- TOC entry 2958 (class 2606 OID 16663)
-- Name: tc_users tc_users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_users
    ADD CONSTRAINT tc_users_pkey PRIMARY KEY (id);


--
-- TOC entry 2961 (class 2606 OID 16666)
-- Name: tc_device_command fk_device_command_commandid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_device_command
    ADD CONSTRAINT fk_device_command_commandid FOREIGN KEY (commandid) REFERENCES public.tc_commands(id) ON DELETE CASCADE;


--
-- TOC entry 2962 (class 2606 OID 16671)
-- Name: tc_device_command fk_device_command_deviceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_device_command
    ADD CONSTRAINT fk_device_command_deviceid FOREIGN KEY (deviceid) REFERENCES public.tc_devices(id) ON DELETE CASCADE;


--
-- TOC entry 2963 (class 2606 OID 16676)
-- Name: tc_device_driver fk_device_driver_deviceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_device_driver
    ADD CONSTRAINT fk_device_driver_deviceid FOREIGN KEY (deviceid) REFERENCES public.tc_devices(id) ON DELETE CASCADE;


--
-- TOC entry 2964 (class 2606 OID 16681)
-- Name: tc_device_driver fk_device_driver_driverid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_device_driver
    ADD CONSTRAINT fk_device_driver_driverid FOREIGN KEY (driverid) REFERENCES public.tc_drivers(id) ON DELETE CASCADE;


--
-- TOC entry 2965 (class 2606 OID 16686)
-- Name: tc_device_geofence fk_device_geofence_deviceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_device_geofence
    ADD CONSTRAINT fk_device_geofence_deviceid FOREIGN KEY (deviceid) REFERENCES public.tc_devices(id) ON DELETE CASCADE;


--
-- TOC entry 2966 (class 2606 OID 16691)
-- Name: tc_device_geofence fk_device_geofence_geofenceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_device_geofence
    ADD CONSTRAINT fk_device_geofence_geofenceid FOREIGN KEY (geofenceid) REFERENCES public.tc_geofences(id) ON DELETE CASCADE;


--
-- TOC entry 2967 (class 2606 OID 16696)
-- Name: tc_device_maintenance fk_device_maintenance_deviceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_device_maintenance
    ADD CONSTRAINT fk_device_maintenance_deviceid FOREIGN KEY (deviceid) REFERENCES public.tc_devices(id) ON DELETE CASCADE;


--
-- TOC entry 2968 (class 2606 OID 16701)
-- Name: tc_device_maintenance fk_device_maintenance_maintenanceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_device_maintenance
    ADD CONSTRAINT fk_device_maintenance_maintenanceid FOREIGN KEY (maintenanceid) REFERENCES public.tc_maintenances(id) ON DELETE CASCADE;


--
-- TOC entry 2969 (class 2606 OID 16706)
-- Name: tc_device_notification fk_device_notification_deviceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_device_notification
    ADD CONSTRAINT fk_device_notification_deviceid FOREIGN KEY (deviceid) REFERENCES public.tc_devices(id) ON DELETE CASCADE;


--
-- TOC entry 2970 (class 2606 OID 16711)
-- Name: tc_device_notification fk_device_notification_notificationid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_device_notification
    ADD CONSTRAINT fk_device_notification_notificationid FOREIGN KEY (notificationid) REFERENCES public.tc_notifications(id) ON DELETE CASCADE;


--
-- TOC entry 2971 (class 2606 OID 16716)
-- Name: tc_devices fk_devices_groupid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_devices
    ADD CONSTRAINT fk_devices_groupid FOREIGN KEY (groupid) REFERENCES public.tc_groups(id) ON DELETE SET NULL;


--
-- TOC entry 2972 (class 2606 OID 16721)
-- Name: tc_events fk_events_deviceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_events
    ADD CONSTRAINT fk_events_deviceid FOREIGN KEY (deviceid) REFERENCES public.tc_devices(id) ON DELETE CASCADE;


--
-- TOC entry 2973 (class 2606 OID 16726)
-- Name: tc_geofences fk_geofence_calendar_calendarid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_geofences
    ADD CONSTRAINT fk_geofence_calendar_calendarid FOREIGN KEY (calendarid) REFERENCES public.tc_calendars(id) ON DELETE SET NULL;


--
-- TOC entry 2974 (class 2606 OID 16731)
-- Name: tc_group_attribute fk_group_attribute_attributeid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_group_attribute
    ADD CONSTRAINT fk_group_attribute_attributeid FOREIGN KEY (attributeid) REFERENCES public.tc_attributes(id) ON DELETE CASCADE;


--
-- TOC entry 2975 (class 2606 OID 16736)
-- Name: tc_group_attribute fk_group_attribute_groupid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_group_attribute
    ADD CONSTRAINT fk_group_attribute_groupid FOREIGN KEY (groupid) REFERENCES public.tc_groups(id) ON DELETE CASCADE;


--
-- TOC entry 2976 (class 2606 OID 16741)
-- Name: tc_group_command fk_group_command_commandid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_group_command
    ADD CONSTRAINT fk_group_command_commandid FOREIGN KEY (commandid) REFERENCES public.tc_commands(id) ON DELETE CASCADE;


--
-- TOC entry 2977 (class 2606 OID 16746)
-- Name: tc_group_command fk_group_command_groupid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_group_command
    ADD CONSTRAINT fk_group_command_groupid FOREIGN KEY (groupid) REFERENCES public.tc_groups(id) ON DELETE CASCADE;


--
-- TOC entry 2978 (class 2606 OID 16751)
-- Name: tc_group_driver fk_group_driver_driverid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_group_driver
    ADD CONSTRAINT fk_group_driver_driverid FOREIGN KEY (driverid) REFERENCES public.tc_drivers(id) ON DELETE CASCADE;


--
-- TOC entry 2979 (class 2606 OID 16756)
-- Name: tc_group_driver fk_group_driver_groupid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_group_driver
    ADD CONSTRAINT fk_group_driver_groupid FOREIGN KEY (groupid) REFERENCES public.tc_groups(id) ON DELETE CASCADE;


--
-- TOC entry 2980 (class 2606 OID 16761)
-- Name: tc_group_geofence fk_group_geofence_geofenceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_group_geofence
    ADD CONSTRAINT fk_group_geofence_geofenceid FOREIGN KEY (geofenceid) REFERENCES public.tc_geofences(id) ON DELETE CASCADE;


--
-- TOC entry 2981 (class 2606 OID 16766)
-- Name: tc_group_geofence fk_group_geofence_groupid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_group_geofence
    ADD CONSTRAINT fk_group_geofence_groupid FOREIGN KEY (groupid) REFERENCES public.tc_groups(id) ON DELETE CASCADE;


--
-- TOC entry 2982 (class 2606 OID 16771)
-- Name: tc_group_maintenance fk_group_maintenance_groupid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_group_maintenance
    ADD CONSTRAINT fk_group_maintenance_groupid FOREIGN KEY (groupid) REFERENCES public.tc_groups(id) ON DELETE CASCADE;


--
-- TOC entry 2983 (class 2606 OID 16776)
-- Name: tc_group_maintenance fk_group_maintenance_maintenanceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_group_maintenance
    ADD CONSTRAINT fk_group_maintenance_maintenanceid FOREIGN KEY (maintenanceid) REFERENCES public.tc_maintenances(id) ON DELETE CASCADE;


--
-- TOC entry 2984 (class 2606 OID 16781)
-- Name: tc_group_notification fk_group_notification_groupid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_group_notification
    ADD CONSTRAINT fk_group_notification_groupid FOREIGN KEY (groupid) REFERENCES public.tc_groups(id) ON DELETE CASCADE;


--
-- TOC entry 2985 (class 2606 OID 16786)
-- Name: tc_group_notification fk_group_notification_notificationid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_group_notification
    ADD CONSTRAINT fk_group_notification_notificationid FOREIGN KEY (notificationid) REFERENCES public.tc_notifications(id) ON DELETE CASCADE;


--
-- TOC entry 2986 (class 2606 OID 16906)
-- Name: tc_groups fk_groups_groupid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_groups
    ADD CONSTRAINT fk_groups_groupid FOREIGN KEY (groupid) REFERENCES public.tc_groups(id) ON UPDATE RESTRICT ON DELETE SET NULL;


--
-- TOC entry 2987 (class 2606 OID 16791)
-- Name: tc_notifications fk_notification_calendar_calendarid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_notifications
    ADD CONSTRAINT fk_notification_calendar_calendarid FOREIGN KEY (calendarid) REFERENCES public.tc_calendars(id) ON UPDATE RESTRICT ON DELETE SET NULL;


--
-- TOC entry 2988 (class 2606 OID 16796)
-- Name: tc_positions fk_positions_deviceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_positions
    ADD CONSTRAINT fk_positions_deviceid FOREIGN KEY (deviceid) REFERENCES public.tc_devices(id) ON DELETE CASCADE;


--
-- TOC entry 2989 (class 2606 OID 16801)
-- Name: tc_user_attribute fk_user_attribute_attributeid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_attribute
    ADD CONSTRAINT fk_user_attribute_attributeid FOREIGN KEY (attributeid) REFERENCES public.tc_attributes(id) ON DELETE CASCADE;


--
-- TOC entry 2990 (class 2606 OID 16806)
-- Name: tc_user_attribute fk_user_attribute_userid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_attribute
    ADD CONSTRAINT fk_user_attribute_userid FOREIGN KEY (userid) REFERENCES public.tc_users(id) ON DELETE CASCADE;


--
-- TOC entry 2991 (class 2606 OID 16811)
-- Name: tc_user_calendar fk_user_calendar_calendarid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_calendar
    ADD CONSTRAINT fk_user_calendar_calendarid FOREIGN KEY (calendarid) REFERENCES public.tc_calendars(id) ON DELETE CASCADE;


--
-- TOC entry 2992 (class 2606 OID 16816)
-- Name: tc_user_calendar fk_user_calendar_userid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_calendar
    ADD CONSTRAINT fk_user_calendar_userid FOREIGN KEY (userid) REFERENCES public.tc_users(id) ON DELETE CASCADE;


--
-- TOC entry 2993 (class 2606 OID 16821)
-- Name: tc_user_command fk_user_command_commandid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_command
    ADD CONSTRAINT fk_user_command_commandid FOREIGN KEY (commandid) REFERENCES public.tc_commands(id) ON DELETE CASCADE;


--
-- TOC entry 2994 (class 2606 OID 16826)
-- Name: tc_user_command fk_user_command_userid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_command
    ADD CONSTRAINT fk_user_command_userid FOREIGN KEY (userid) REFERENCES public.tc_users(id) ON DELETE CASCADE;


--
-- TOC entry 2959 (class 2606 OID 16831)
-- Name: tc_device_attribute fk_user_device_attribute_attributeid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_device_attribute
    ADD CONSTRAINT fk_user_device_attribute_attributeid FOREIGN KEY (attributeid) REFERENCES public.tc_attributes(id) ON DELETE CASCADE;


--
-- TOC entry 2960 (class 2606 OID 16836)
-- Name: tc_device_attribute fk_user_device_attribute_deviceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_device_attribute
    ADD CONSTRAINT fk_user_device_attribute_deviceid FOREIGN KEY (deviceid) REFERENCES public.tc_devices(id) ON DELETE CASCADE;


--
-- TOC entry 2995 (class 2606 OID 16841)
-- Name: tc_user_device fk_user_device_deviceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_device
    ADD CONSTRAINT fk_user_device_deviceid FOREIGN KEY (deviceid) REFERENCES public.tc_devices(id) ON DELETE CASCADE;


--
-- TOC entry 2996 (class 2606 OID 16846)
-- Name: tc_user_device fk_user_device_userid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_device
    ADD CONSTRAINT fk_user_device_userid FOREIGN KEY (userid) REFERENCES public.tc_users(id) ON DELETE CASCADE;


--
-- TOC entry 2997 (class 2606 OID 16851)
-- Name: tc_user_driver fk_user_driver_driverid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_driver
    ADD CONSTRAINT fk_user_driver_driverid FOREIGN KEY (driverid) REFERENCES public.tc_drivers(id) ON DELETE CASCADE;


--
-- TOC entry 2998 (class 2606 OID 16856)
-- Name: tc_user_driver fk_user_driver_userid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_driver
    ADD CONSTRAINT fk_user_driver_userid FOREIGN KEY (userid) REFERENCES public.tc_users(id) ON DELETE CASCADE;


--
-- TOC entry 2999 (class 2606 OID 16861)
-- Name: tc_user_geofence fk_user_geofence_geofenceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_geofence
    ADD CONSTRAINT fk_user_geofence_geofenceid FOREIGN KEY (geofenceid) REFERENCES public.tc_geofences(id) ON DELETE CASCADE;


--
-- TOC entry 3000 (class 2606 OID 16866)
-- Name: tc_user_geofence fk_user_geofence_userid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_geofence
    ADD CONSTRAINT fk_user_geofence_userid FOREIGN KEY (userid) REFERENCES public.tc_users(id) ON DELETE CASCADE;


--
-- TOC entry 3001 (class 2606 OID 16871)
-- Name: tc_user_group fk_user_group_groupid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_group
    ADD CONSTRAINT fk_user_group_groupid FOREIGN KEY (groupid) REFERENCES public.tc_groups(id) ON DELETE CASCADE;


--
-- TOC entry 3002 (class 2606 OID 16876)
-- Name: tc_user_group fk_user_group_userid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_group
    ADD CONSTRAINT fk_user_group_userid FOREIGN KEY (userid) REFERENCES public.tc_users(id) ON DELETE CASCADE;


--
-- TOC entry 3003 (class 2606 OID 16881)
-- Name: tc_user_maintenance fk_user_maintenance_maintenanceid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_maintenance
    ADD CONSTRAINT fk_user_maintenance_maintenanceid FOREIGN KEY (maintenanceid) REFERENCES public.tc_maintenances(id) ON DELETE CASCADE;


--
-- TOC entry 3004 (class 2606 OID 16886)
-- Name: tc_user_maintenance fk_user_maintenance_userid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_maintenance
    ADD CONSTRAINT fk_user_maintenance_userid FOREIGN KEY (userid) REFERENCES public.tc_users(id) ON DELETE CASCADE;


--
-- TOC entry 3005 (class 2606 OID 16891)
-- Name: tc_user_notification fk_user_notification_notificationid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_notification
    ADD CONSTRAINT fk_user_notification_notificationid FOREIGN KEY (notificationid) REFERENCES public.tc_notifications(id) ON DELETE CASCADE;


--
-- TOC entry 3006 (class 2606 OID 16896)
-- Name: tc_user_notification fk_user_notification_userid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_notification
    ADD CONSTRAINT fk_user_notification_userid FOREIGN KEY (userid) REFERENCES public.tc_users(id) ON DELETE CASCADE;


--
-- TOC entry 3008 (class 2606 OID 16911)
-- Name: tc_user_user fk_user_user_manageduserid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_user
    ADD CONSTRAINT fk_user_user_manageduserid FOREIGN KEY (manageduserid) REFERENCES public.tc_users(id) ON DELETE CASCADE;


--
-- TOC entry 3007 (class 2606 OID 16901)
-- Name: tc_user_user fk_user_user_userid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tc_user_user
    ADD CONSTRAINT fk_user_user_userid FOREIGN KEY (userid) REFERENCES public.tc_users(id) ON DELETE CASCADE;


-- Completed on 2018-11-02 18:36:53

--
-- PostgreSQL database dump complete
--

