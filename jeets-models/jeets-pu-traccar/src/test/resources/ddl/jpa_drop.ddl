-- file created at Tue Feb 26 12:51:03 CET 2019

    alter table tc_device_attribute 
       drop constraint FKguwgkku5xouwwi5uw8u3fmxbs;

    alter table tc_device_attribute 
       drop constraint FK2yp7gbcdutco78i4ifwxcgiej;

    alter table tc_device_command 
       drop constraint FKo9g5r02bd18e308uv23kq0gno;

    alter table tc_device_command 
       drop constraint FK1k0tirv08sy0bpcx4xtaky87q;

    alter table tc_device_driver 
       drop constraint FKtaifua5r0jmucxr4hjx2ww9v2;

    alter table tc_device_driver 
       drop constraint FKheoajr1hycpsjuhwp300yvrf6;

    alter table tc_device_geofence 
       drop constraint FKqn8rhjxq5sj0xf8bqfwgrxgih;

    alter table tc_device_geofence 
       drop constraint FKeojtf7c5wmj1twmryb7v72sec;

    alter table tc_device_maintenance 
       drop constraint FKoi90hq57n537x8abkq9b7lk1;

    alter table tc_device_maintenance 
       drop constraint FKi9lxhqmxqjwrggo1u1uqncjdl;

    alter table tc_device_notification 
       drop constraint FKlrjy6hrmph5vjl035gwvakys;

    alter table tc_device_notification 
       drop constraint FKr1btu10mrvqw937j5uixf8rbl;

    alter table tc_devices 
       drop constraint FKc91qyyu54iovy2tyj4w3g5akt;

    alter table tc_devices 
       drop constraint FKb6wl19yp3u9mskkimt0510ls6;

    alter table tc_events 
       drop constraint FK9rosg4vw6auwdmp9w7249yncl;

    alter table tc_events 
       drop constraint FKhp2cb5abhcq6d58a644qmk0y1;

    alter table tc_geofences 
       drop constraint FKnh6ek5t9fwt9gqq39qjnp7a17;

    alter table tc_group_attribute 
       drop constraint FK9y0yqxhrhn8iah5q0d7wpfid5;

    alter table tc_group_attribute 
       drop constraint FK39saym3tmu0kbfuohkc0pmldv;

    alter table tc_group_command 
       drop constraint FKqb028e01cpxdyke9b2ucaqgoq;

    alter table tc_group_command 
       drop constraint FK264p600objn7kkvxt83dun5ya;

    alter table tc_group_driver 
       drop constraint FK40em4jbpcqf2qjcsi2l5s4hu0;

    alter table tc_group_driver 
       drop constraint FKcrkfs1np64f7ne4j3lgcf2j6d;

    alter table tc_group_geofence 
       drop constraint FK4gg8316sin00psv4txpwoj71x;

    alter table tc_group_geofence 
       drop constraint FKb14y33hcpba5eauujck65kldk;

    alter table tc_group_maintenance 
       drop constraint FK9wkphllvf8om9gjber2h3vrf2;

    alter table tc_group_maintenance 
       drop constraint FKpivn4rfh8lb7b19hv1ecf5imu;

    alter table tc_group_notification 
       drop constraint FK59qcv63jfnnlte0yq15q345dp;

    alter table tc_group_notification 
       drop constraint FK951tmv3rd1l0y7si7wv4kiw1r;

    alter table tc_groups 
       drop constraint FKc77s08cm6yyk63043pendovd1;

    alter table tc_notifications 
       drop constraint FK61r7g8x17bi8i5wdlwvs5mcgx;

    alter table tc_positions 
       drop constraint FKsumrnjtumlhopcjdkghvy2qsl;

    alter table tc_user_attribute 
       drop constraint FK5ivie4jvyekwg2t0eu8twijuu;

    alter table tc_user_attribute 
       drop constraint FKi85bg25400q3kuwgugwwpqyr7;

    alter table tc_user_calendar 
       drop constraint FK7t5n5k0f7vs240406gu8u89e3;

    alter table tc_user_calendar 
       drop constraint FKr3ipsjv2h7aryfddsswsbpd8c;

    alter table tc_user_command 
       drop constraint FKbuqp4ub7dbv0ogarmawuux40r;

    alter table tc_user_command 
       drop constraint FK1pwt04oa5ot8iuamwbwb2ddr8;

    alter table tc_user_device 
       drop constraint FK2y4vwwov4d6sh25mqgufypmm0;

    alter table tc_user_device 
       drop constraint FK2629qa83tegiefe06kgann6ag;

    alter table tc_user_driver 
       drop constraint FKoixsoh6ovjonhcrwobgfmssux;

    alter table tc_user_driver 
       drop constraint FK5d95627gmhconifrhcm4jsy59;

    alter table tc_user_geofence 
       drop constraint FK9xq7pnpvq4m31bh04xkse830y;

    alter table tc_user_geofence 
       drop constraint FK724d2ehb5o39unfqd9hbqk9x2;

    alter table tc_user_group 
       drop constraint FKkf7nnsu7m3f7t1xadfu2e3f1j;

    alter table tc_user_group 
       drop constraint FKepfceuu4c19xvuqyrg652m5r9;

    alter table tc_user_maintenance 
       drop constraint FK5nomc7vejsroh8h0ujto2ciat;

    alter table tc_user_maintenance 
       drop constraint FK1pjveimrovfx16jprugcgxaw9;

    alter table tc_user_notification 
       drop constraint FK8sly6c8tcwdo9kt6math7h27q;

    alter table tc_user_notification 
       drop constraint FK9kudifrw37a5o5uq75c4fijbr;

    alter table tc_user_user 
       drop constraint FK90yvbvgy32tporjt785n64q3h;

    alter table tc_user_user 
       drop constraint FKswgidmt0coy70gsayjhdcid4v;

    drop table if exists tc_attributes cascade;

    drop table if exists tc_calendars cascade;

    drop table if exists tc_commands cascade;

    drop table if exists tc_device_attribute cascade;

    drop table if exists tc_device_command cascade;

    drop table if exists tc_device_driver cascade;

    drop table if exists tc_device_geofence cascade;

    drop table if exists tc_device_maintenance cascade;

    drop table if exists tc_device_notification cascade;

    drop table if exists tc_devices cascade;

    drop table if exists tc_drivers cascade;

    drop table if exists tc_events cascade;

    drop table if exists tc_geofences cascade;

    drop table if exists tc_group_attribute cascade;

    drop table if exists tc_group_command cascade;

    drop table if exists tc_group_driver cascade;

    drop table if exists tc_group_geofence cascade;

    drop table if exists tc_group_maintenance cascade;

    drop table if exists tc_group_notification cascade;

    drop table if exists tc_groups cascade;

    drop table if exists tc_maintenances cascade;

    drop table if exists tc_notifications cascade;

    drop table if exists tc_positions cascade;

    drop table if exists tc_servers cascade;

    drop table if exists tc_statistics cascade;

    drop table if exists tc_user_attribute cascade;

    drop table if exists tc_user_calendar cascade;

    drop table if exists tc_user_command cascade;

    drop table if exists tc_user_device cascade;

    drop table if exists tc_user_driver cascade;

    drop table if exists tc_user_geofence cascade;

    drop table if exists tc_user_group cascade;

    drop table if exists tc_user_maintenance cascade;

    drop table if exists tc_user_notification cascade;

    drop table if exists tc_user_user cascade;

    drop table if exists tc_users cascade;

    drop sequence if exists tc_attributes_id_seq;

    drop sequence if exists tc_calendars_id_seq;

    drop sequence if exists tc_commands_id_seq;

    drop sequence if exists tc_devices_id_seq;

    drop sequence if exists tc_drivers_id_seq;

    drop sequence if exists tc_events_id_seq;

    drop sequence if exists tc_geofences_id_seq;

    drop sequence if exists tc_groups_id_seq;

    drop sequence if exists tc_maintenances_id_seq;

    drop sequence if exists tc_notifications_id_seq;

    drop sequence if exists tc_positions_id_seq;

    drop sequence if exists tc_servers_id_seq;

    drop sequence if exists tc_statistics_id_seq;

    drop sequence if exists tc_users_id_seq;
