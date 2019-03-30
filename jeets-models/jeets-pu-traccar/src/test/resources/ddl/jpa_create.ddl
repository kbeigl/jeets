-- file created at Tue Feb 26 12:51:03 CET 2019
create sequence tc_attributes_id_seq start 1 increment 50;
create sequence tc_calendars_id_seq start 1 increment 50;
create sequence tc_commands_id_seq start 1 increment 50;
create sequence tc_devices_id_seq start 1 increment 1;
create sequence tc_drivers_id_seq start 1 increment 50;
create sequence tc_events_id_seq start 1 increment 1;
create sequence tc_geofences_id_seq start 1 increment 50;
create sequence tc_groups_id_seq start 1 increment 50;
create sequence tc_maintenances_id_seq start 1 increment 50;
create sequence tc_notifications_id_seq start 1 increment 50;
create sequence tc_positions_id_seq start 1 increment 1;
create sequence tc_servers_id_seq start 1 increment 50;
create sequence tc_statistics_id_seq start 1 increment 50;
create sequence tc_users_id_seq start 1 increment 50;

    create table tc_attributes (
       id int4 not null,
        attribute varchar(255),
        description varchar(255),
        expression varchar(255),
        type varchar(255),
        primary key (id)
    );

    create table tc_calendars (
       id int4 not null,
        attributes varchar(255),
        data bytea,
        name varchar(255),
        primary key (id)
    );

    create table tc_commands (
       id int4 not null,
        attributes varchar(255),
        description varchar(255),
        textchannel boolean,
        type varchar(255),
        primary key (id)
    );

    create table tc_device_attribute (
       deviceid int4 not null,
        attributeid int4 not null,
        primary key (deviceid, attributeid)
    );

    create table tc_device_command (
       deviceid int4 not null,
        commandid int4 not null
    );

    create table tc_device_driver (
       driverid int4 not null,
        deviceid int4 not null
    );

    create table tc_device_geofence (
       geofenceid int4 not null,
        deviceid int4 not null
    );

    create table tc_device_maintenance (
       maintenanceid int4 not null,
        deviceid int4 not null
    );

    create table tc_device_notification (
       notificationid int4 not null,
        deviceid int4 not null
    );

    create table tc_devices (
       id int4 not null,
        attributeString varchar(255),
        category varchar(255),
        contact varchar(255),
        disabled boolean,
        lastupdate timestamp,
        model varchar(255),
        name varchar(255),
        phone varchar(255),
        uniqueid varchar(255),
        groupid int4,
        positionid int4,
        primary key (id)
    );

    create table tc_drivers (
       id int4 not null,
        attributes varchar(255),
        name varchar(255),
        uniqueid varchar(255),
        primary key (id)
    );

    create table tc_events (
       id int4 not null,
        attributes varchar(255),
        geofence bytea,
        maintenanceid int4,
        servertime timestamp,
        type varchar(255),
        deviceid int4,
        positionid int4,
        primary key (id)
    );

    create table tc_geofences (
       id int4 not null,
        area varchar(255),
        attributes varchar(255),
        description varchar(255),
        name varchar(255),
        calendarid int4,
        primary key (id)
    );

    create table tc_group_attribute (
       groupid int4 not null,
        attributeid int4 not null
    );

    create table tc_group_command (
       groupid int4 not null,
        commandid int4 not null
    );

    create table tc_group_driver (
       groupid int4 not null,
        driverid int4 not null
    );

    create table tc_group_geofence (
       groupid int4 not null,
        geofenceid int4 not null
    );

    create table tc_group_maintenance (
       maintenanceid int4 not null,
        groupid int4 not null
    );

    create table tc_group_notification (
       notificationid int4 not null,
        groupid int4 not null
    );

    create table tc_groups (
       id int4 not null,
        attributeString varchar(255),
        name varchar(255),
        groupid int4,
        primary key (id)
    );

    create table tc_maintenances (
       id int4 not null,
        attributes varchar(255),
        name varchar(255),
        period float8 not null,
        start float8 not null,
        type varchar(255),
        primary key (id)
    );

    create table tc_notifications (
       id int4 not null,
        always boolean,
        attributes varchar(255),
        notificators varchar(255),
        type varchar(255),
        calendarid int4,
        primary key (id)
    );

    create table tc_positions (
       id int4 not null,
        accuracy float8 not null,
        address varchar(255),
        altitude float8 not null,
        attributes varchar(255),
        course float8 not null,
        devicetime timestamp,
        fixtime timestamp,
        latitude float8 not null,
        longitude float8 not null,
        network varchar(255),
        protocol varchar(255),
        servertime timestamp,
        speed float8 not null,
        valid boolean,
        deviceid int4,
        primary key (id)
    );

    create table tc_servers (
       id int4 not null,
        attributes varchar(255),
        bingkey varchar(255),
        coordinateformat varchar(255),
        devicereadonly boolean,
        forcesettings boolean,
        latitude float8 not null,
        limitcommands boolean,
        longitude float8 not null,
        map varchar(255),
        mapurl varchar(255),
        poilayer varchar(255),
        readonly boolean,
        registration boolean,
        twelvehourformat boolean,
        zoom int4,
        primary key (id)
    );

    create table tc_statistics (
       id int4 not null,
        activedevices int4,
        activeusers int4,
        attributes varchar(255),
        capturetime timestamp,
        geocoderrequests int4,
        geolocationrequests int4,
        mailsent int4,
        messagesreceived int4,
        messagesstored int4,
        requests int4,
        smssent int4,
        primary key (id)
    );

    create table tc_user_attribute (
       userid int4 not null,
        attributeid int4 not null
    );

    create table tc_user_calendar (
       userid int4 not null,
        calendarid int4 not null
    );

    create table tc_user_command (
       userid int4 not null,
        commandid int4 not null
    );

    create table tc_user_device (
       userid int4 not null,
        deviceid int4 not null
    );

    create table tc_user_driver (
       userid int4 not null,
        driverid int4 not null
    );

    create table tc_user_geofence (
       userid int4 not null,
        geofenceid int4 not null
    );

    create table tc_user_group (
       userid int4 not null,
        groupid int4 not null
    );

    create table tc_user_maintenance (
       userid int4 not null,
        maintenanceid int4 not null
    );

    create table tc_user_notification (
       userid int4 not null,
        notificationid int4 not null
    );

    create table tc_user_user (
       userid int4 not null,
        manageduserid int4 not null
    );

    create table tc_users (
       id int4 not null,
        administrator boolean,
        attributeString varchar(255),
        coordinateformat varchar(255),
        devicelimit int4,
        devicereadonly boolean,
        disabled boolean,
        email varchar(255),
        expirationtime timestamp,
        hashedpassword varchar(255),
        latitude float8 not null,
        limitcommands boolean,
        login varchar(255),
        longitude float8 not null,
        map varchar(255),
        name varchar(255),
        phone varchar(255),
        poilayer varchar(255),
        readonly boolean,
        salt varchar(255),
        token varchar(255),
        twelvehourformat boolean,
        userlimit int4,
        zoom int4,
        primary key (id)
    );

    alter table tc_devices 
       add constraint UKocbnlmquh6k10krarwn1y1nn9 unique (uniqueid);

    alter table tc_device_attribute 
       add constraint FKguwgkku5xouwwi5uw8u3fmxbs 
       foreign key (attributeid) 
       references tc_attributes;

    alter table tc_device_attribute 
       add constraint FK2yp7gbcdutco78i4ifwxcgiej 
       foreign key (deviceid) 
       references tc_devices;

    alter table tc_device_command 
       add constraint FKo9g5r02bd18e308uv23kq0gno 
       foreign key (commandid) 
       references tc_commands;

    alter table tc_device_command 
       add constraint FK1k0tirv08sy0bpcx4xtaky87q 
       foreign key (deviceid) 
       references tc_devices;

    alter table tc_device_driver 
       add constraint FKtaifua5r0jmucxr4hjx2ww9v2 
       foreign key (deviceid) 
       references tc_devices;

    alter table tc_device_driver 
       add constraint FKheoajr1hycpsjuhwp300yvrf6 
       foreign key (driverid) 
       references tc_drivers;

    alter table tc_device_geofence 
       add constraint FKqn8rhjxq5sj0xf8bqfwgrxgih 
       foreign key (deviceid) 
       references tc_devices;

    alter table tc_device_geofence 
       add constraint FKeojtf7c5wmj1twmryb7v72sec 
       foreign key (geofenceid) 
       references tc_geofences;

    alter table tc_device_maintenance 
       add constraint FKoi90hq57n537x8abkq9b7lk1 
       foreign key (deviceid) 
       references tc_devices;

    alter table tc_device_maintenance 
       add constraint FKi9lxhqmxqjwrggo1u1uqncjdl 
       foreign key (maintenanceid) 
       references tc_maintenances;

    alter table tc_device_notification 
       add constraint FKlrjy6hrmph5vjl035gwvakys 
       foreign key (deviceid) 
       references tc_devices;

    alter table tc_device_notification 
       add constraint FKr1btu10mrvqw937j5uixf8rbl 
       foreign key (notificationid) 
       references tc_notifications;

    alter table tc_devices 
       add constraint FKc91qyyu54iovy2tyj4w3g5akt 
       foreign key (groupid) 
       references tc_groups;

    alter table tc_devices 
       add constraint FKb6wl19yp3u9mskkimt0510ls6 
       foreign key (positionid) 
       references tc_positions;

    alter table tc_events 
       add constraint FK9rosg4vw6auwdmp9w7249yncl 
       foreign key (deviceid) 
       references tc_devices;

    alter table tc_events 
       add constraint FKhp2cb5abhcq6d58a644qmk0y1 
       foreign key (positionid) 
       references tc_positions;

    alter table tc_geofences 
       add constraint FKnh6ek5t9fwt9gqq39qjnp7a17 
       foreign key (calendarid) 
       references tc_calendars;

    alter table tc_group_attribute 
       add constraint FK9y0yqxhrhn8iah5q0d7wpfid5 
       foreign key (attributeid) 
       references tc_attributes;

    alter table tc_group_attribute 
       add constraint FK39saym3tmu0kbfuohkc0pmldv 
       foreign key (groupid) 
       references tc_groups;

    alter table tc_group_command 
       add constraint FKqb028e01cpxdyke9b2ucaqgoq 
       foreign key (commandid) 
       references tc_commands;

    alter table tc_group_command 
       add constraint FK264p600objn7kkvxt83dun5ya 
       foreign key (groupid) 
       references tc_groups;

    alter table tc_group_driver 
       add constraint FK40em4jbpcqf2qjcsi2l5s4hu0 
       foreign key (driverid) 
       references tc_drivers;

    alter table tc_group_driver 
       add constraint FKcrkfs1np64f7ne4j3lgcf2j6d 
       foreign key (groupid) 
       references tc_groups;

    alter table tc_group_geofence 
       add constraint FK4gg8316sin00psv4txpwoj71x 
       foreign key (geofenceid) 
       references tc_geofences;

    alter table tc_group_geofence 
       add constraint FKb14y33hcpba5eauujck65kldk 
       foreign key (groupid) 
       references tc_groups;

    alter table tc_group_maintenance 
       add constraint FK9wkphllvf8om9gjber2h3vrf2 
       foreign key (groupid) 
       references tc_groups;

    alter table tc_group_maintenance 
       add constraint FKpivn4rfh8lb7b19hv1ecf5imu 
       foreign key (maintenanceid) 
       references tc_maintenances;

    alter table tc_group_notification 
       add constraint FK59qcv63jfnnlte0yq15q345dp 
       foreign key (groupid) 
       references tc_groups;

    alter table tc_group_notification 
       add constraint FK951tmv3rd1l0y7si7wv4kiw1r 
       foreign key (notificationid) 
       references tc_notifications;

    alter table tc_groups 
       add constraint FKc77s08cm6yyk63043pendovd1 
       foreign key (groupid) 
       references tc_groups;

    alter table tc_notifications 
       add constraint FK61r7g8x17bi8i5wdlwvs5mcgx 
       foreign key (calendarid) 
       references tc_calendars;

    alter table tc_positions 
       add constraint FKsumrnjtumlhopcjdkghvy2qsl 
       foreign key (deviceid) 
       references tc_devices;

    alter table tc_user_attribute 
       add constraint FK5ivie4jvyekwg2t0eu8twijuu 
       foreign key (attributeid) 
       references tc_attributes;

    alter table tc_user_attribute 
       add constraint FKi85bg25400q3kuwgugwwpqyr7 
       foreign key (userid) 
       references tc_users;

    alter table tc_user_calendar 
       add constraint FK7t5n5k0f7vs240406gu8u89e3 
       foreign key (calendarid) 
       references tc_calendars;

    alter table tc_user_calendar 
       add constraint FKr3ipsjv2h7aryfddsswsbpd8c 
       foreign key (userid) 
       references tc_users;

    alter table tc_user_command 
       add constraint FKbuqp4ub7dbv0ogarmawuux40r 
       foreign key (commandid) 
       references tc_commands;

    alter table tc_user_command 
       add constraint FK1pwt04oa5ot8iuamwbwb2ddr8 
       foreign key (userid) 
       references tc_users;

    alter table tc_user_device 
       add constraint FK2y4vwwov4d6sh25mqgufypmm0 
       foreign key (deviceid) 
       references tc_devices;

    alter table tc_user_device 
       add constraint FK2629qa83tegiefe06kgann6ag 
       foreign key (userid) 
       references tc_users;

    alter table tc_user_driver 
       add constraint FKoixsoh6ovjonhcrwobgfmssux 
       foreign key (driverid) 
       references tc_drivers;

    alter table tc_user_driver 
       add constraint FK5d95627gmhconifrhcm4jsy59 
       foreign key (userid) 
       references tc_users;

    alter table tc_user_geofence 
       add constraint FK9xq7pnpvq4m31bh04xkse830y 
       foreign key (geofenceid) 
       references tc_geofences;

    alter table tc_user_geofence 
       add constraint FK724d2ehb5o39unfqd9hbqk9x2 
       foreign key (userid) 
       references tc_users;

    alter table tc_user_group 
       add constraint FKkf7nnsu7m3f7t1xadfu2e3f1j 
       foreign key (groupid) 
       references tc_groups;

    alter table tc_user_group 
       add constraint FKepfceuu4c19xvuqyrg652m5r9 
       foreign key (userid) 
       references tc_users;

    alter table tc_user_maintenance 
       add constraint FK5nomc7vejsroh8h0ujto2ciat 
       foreign key (maintenanceid) 
       references tc_maintenances;

    alter table tc_user_maintenance 
       add constraint FK1pjveimrovfx16jprugcgxaw9 
       foreign key (userid) 
       references tc_users;

    alter table tc_user_notification 
       add constraint FK8sly6c8tcwdo9kt6math7h27q 
       foreign key (notificationid) 
       references tc_notifications;

    alter table tc_user_notification 
       add constraint FK9kudifrw37a5o5uq75c4fijbr 
       foreign key (userid) 
       references tc_users;

    alter table tc_user_user 
       add constraint FK90yvbvgy32tporjt785n64q3h 
       foreign key (manageduserid) 
       references tc_users;

    alter table tc_user_user 
       add constraint FKswgidmt0coy70gsayjhdcid4v 
       foreign key (userid) 
       references tc_users;
