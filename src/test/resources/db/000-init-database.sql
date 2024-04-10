

create table jnu_sites
(
    sitename varchar(300)         not null
        constraint jnu_sites_pkey
            primary key,
    enabled  boolean default true not null
);

create table jnu_projects
(
    id         varchar(60)                not null
        constraint jnu_projects_pkey
            primary key,
    regdate    timestamp(0) default now() not null,
    usercreate varchar(100)               not null,
    site       varchar(300)               not null
        constraint jnu_projects_fk
            references jnu_sites
            on update cascade on delete cascade
);

create table jnu_components
(
    id            varchar(60)                                    not null
        constraint jnu_components_pkey
            primary key,
    description   varchar(300),
    project       varchar(60)
        constraint jnu_components_fk1
            references jnu_projects
            on update cascade on delete cascade,
    css           text,
    jshead        text,
    jsbody        text,
    regdate       timestamp(0) default now(),
    regupdate     timestamp(0),
    usercreate    varchar(100),
    userupdate    varchar(100),
    title         varchar(300),
    showtitle     boolean      default true,
    componenttype varchar(10)  default 'FORM'::character varying not null,
    jdbc          varchar(60),
    jsonobject    text,
    usecache      boolean      default false                     not null
);

create table jnu_events
(
    id        serial
        constraint jnu_events_pkey
            primary key,
    name      varchar(60)       not null,
    component varchar(60)
        constraint jnu_events_fk
            references jnu_components
            on update cascade on delete cascade,
    project   varchar(60)       not null
        constraint jnu_events_fk1
            references jnu_projects
            on update cascade on delete cascade,
    seq       integer default 0 not null,
    conf      text,
    label     varchar(60),
    enabled      boolean      default true                     not null
);

create table jnu_eventactions
(
    id           serial
        constraint jnu_eventactions_pkey
            primary key,
    seq          integer   default 0     not null,
    regdate      timestamp default now() not null,
    regupdate    timestamp,
    usercreate   varchar(100),
    userupdate   varchar(100),
    event        integer
        constraint jnu_eventactions_fk
            references jnu_events
            on delete cascade,
    actionclass  varchar(200)            not null,
    actiondata   text                    not null,
    eventaction  integer
        constraint jnu_eventactions_fk1
            references jnu_eventactions
            on delete cascade,
    jdbc         varchar(60),
    enabled      boolean   default true  not null,
    description  text,
    actionscript varchar(30)
);

create table jnu_componentparts
(
    id           serial
        constraint jnu_componentparts_pkey
            primary key,
    component    varchar(60)             not null
        constraint jnu_componentparts_fk
            references jnu_components
            on update cascade on delete cascade,
    varname      varchar(60)             not null,
    label        varchar(60),
    showlabel    boolean   default true,
    datatype     varchar(60)             not null,
    action       varchar(60),
    canbenull    boolean   default true,
    valbyquery   varchar(80),
    dependon     varchar(60),
    seq          integer                 not null,
    valbyrequest varchar(80),
    valbydefault varchar(80),
    size         integer,
    placeholder  varchar(100),
    json         text,
    regdate      timestamp default now() not null,
    regupdate    timestamp,
    usercreate   varchar(100),
    userupdate   varchar(100),
    tabgroup     varchar(20),
    "column"     varchar(20)
);

create table mae_albums
(
    id          varchar(300)         primary key,
    album       varchar(300)            not null,
    usr         integer                 not null,
    description text,
    regdate     timestamp default now() not null,
    visitors    integer   default 0     not null,
    iscover     boolean   default false not null,
    isprofile   boolean   default false not null,
    seq         integer,
    site        varchar(300),
    title       varchar(300),
    imgtype     varchar(5),
    filename    varchar(300)
);

create table jnu_configuration
(
    id            integer   primary key,
    servername    varchar(200),
    sysadminemail varchar(80),
    smtpserver    varchar(200),
    smtpport      integer    default 25,
    smtptls       boolean    default false,
    smtpuser      varchar(80),
    smtppassword  varchar(60),
    debug         boolean    default false,
    filesdir      text,
    imgquality    varchar(4),
    filesize      integer,
    defaultlang   varchar(4) default 'en'::character varying,
    regupdate     timestamp  default now() not null,
    userupdate    varchar(100),
    jnuversion    double precision,
    lastupdate    timestamp(0)
);

create table jnu_siteconf
(
    site          varchar(300) primary key,
    configuration text,
    prefs         text,
    passwordkeyspolicy text
);

create table mae_usernotifications
(
    id         serial,
    objecttype varchar(10)           not null,
    objectid   varchar(300)          not null,
    regdate    date    default now() not null,
    ownerid    integer               not null,
    followerid integer               not null,
    readed     boolean default false not null
);

CREATE SEQUENCE MAE_DRIVE_GROUPID_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE MAE_DRIVE_ID_SEQ START WITH 1 INCREMENT BY 1;

create table mae_drive
(
    id       serial primary key,
    filename varchar(400)               not null,
    groupid  integer,
    usr      integer,
    regdate  timestamp(0) default now() not null,
    parentid integer,
    isfile   boolean      default true  not null,
    site     varchar(300),
    readonly boolean      default false not null
);

create table jnu_eventaccess
(
    eventid integer      not null,
    site    varchar(300) not null,
    role    varchar(60)  not null,
    primary key (eventid, site, role)
);

create table jnu_users
(
    id             serial primary key,
    email          varchar(100)                                    not null,
    password       varchar(100),
    keystr         varchar(200),
    keydate        timestamp    default now(),
    lang           varchar(3)   default 'en'::character varying    not null,
    role           varchar(60)  default 'GUEST'::character varying not null,
    enabled        boolean      default true                       not null,
    name           varchar(60)                                     not null,
    alias          varchar(60) unique,
    regdate        timestamp(0) default now()                      not null,
    regupdate      timestamp(0),
    lastlogin      timestamp(0),
    usercreate     varchar(100),
    userupdate     varchar(100),
    externaluid    varchar(300),
    isconfigured   boolean      default false                      not null,
    photo          varchar(300),
    site           varchar(300),
    birthdate      date,
    city           integer,
    sex            varchar(4),
    additionaldata text
);

create table mae_msg
(
    id        serial primary key,
    usrfrom   integer,
    usrto     integer,
    content   text                    not null,
    regdate   timestamp default now() not null,
    ismsg     boolean   default true,
    isread    boolean   default false not null,
    parentid  integer,
    ispublic  boolean   default true  not null,
    hidefrom  boolean   default false not null,
    hideto    boolean   default false not null,
    postdate  timestamp,
    site      varchar(300)            not null,
    ownerid   integer                 not null,
    title     varchar(200),
    debugcode text
);

create table mae_driveusers
(
    userid integer not null,
    drive  integer not null,
    primary key (userid, drive)
);

create table mae_driveroles
(
    role  varchar(60) not null,
    drive integer     not null,
    primary key (role, drive)
);

create table mae_drivedownstat
(
    id      serial primary key,
    fileid  integer                 not null,
    regdate timestamp default now() not null,
    ip      varchar(80)             not null,
    usr     integer
);

create table jnu_cities
(
    id         serial primary key,
    name       varchar(200)          not null,
    department integer               not null,
    latitude   real,
    longitude  real,
    enabled    boolean default false not null
);

create table jnu_departments
(
    id      serial primary key,
    name    varchar(200) not null,
    country integer      not null
);

create table jnu_countries
(
    id      serial primary key,
    name    varchar(200) not null unique,
    img     varchar(100),
    code    varchar(2),
    enabled boolean default true not null
);

create table jnu_keys
(
    keystr    varchar(200)                    not null
        primary key,
    userid    integer,
    created   timestamp default now()         not null,
    lastused  timestamp,
    keyexpire integer   default '-1'::integer not null,
    site      varchar(300),
    keyrefresh varchar(200)
);

create table jnu_roles
(
    role varchar(60) not null
        primary key,
    site varchar(300)
);

create table jnu_componentaccess
(
    componentid varchar(60)  not null,
    site        varchar(300) not null,
    role        varchar(60)  not null,
    primary key (componentid, site, role)
);
create table jnu_imgcache(id varchar(300) not null primary key, creationdate timestamp default now() not null);
