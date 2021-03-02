
FROM ubuntu:16.04

# Update apt
RUN apt update

# Install requirements
RUN apt -y install lsb wget curl ca-certificates gnupg

# Add PostgreSQL's repository. It contains the most recent stable release
#  of PostgreSQL.
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ `lsb_release -cs`-pgdg main" | tee  /etc/apt/sources.list.d/pgdg.list

RUN wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add -

# Install ``python-software-properties``, ``software-properties-common`` and PostgreSQL 12
#  There are some warnings (in red) that show up during the build. You can hide
#  them by prefixing each apt-get statement with DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y python-software-properties software-properties-common postgresql-12 postgresql-client-12 postgresql-12-cron

# Run the rest of the commands as the ``postgres`` user created by the ``postgres-12`` package when it was ``apt-get installed``
USER postgres

# Create a schema `acs` owned by the ``postgres`` role.
# Note: here we use ``&&\`` to run commands one after the other - the ``\``
#       allows the RUN command to span multiple lines.
RUN /etc/init.d/postgresql start &&\
    psql --command "ALTER USER postgres WITH ENCRYPTED PASSWORD 'XXX';" &&\
    psql --command "CREATE SCHEMA acs;"

# Adjust PostgreSQL configuration so that remote connections to the
# database are possible.
# RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/12/main/pg_hba.conf

# And add ``listen_addresses`` to ``/etc/postgresql/12/main/postgresql.conf``
RUN echo "listen_addresses='*'" >> /etc/postgresql/12/main/postgresql.conf

# Pg_cron config
RUN echo "shared_preload_libraries = 'pg_cron'" >> /etc/postgresql/12/main/postgresql.conf
RUN echo "cron.database_name = 'postgres'" >> /etc/postgresql/12/main/postgresql.conf
RUN echo "local all  postgres    trust" > /etc/postgresql/12/main/pg_hba.conf
RUN echo "host all  all    127.0.0.1/32  trust" >> /etc/postgresql/12/main/pg_hba.conf
RUN echo "host all  all    ::1/128       md5" >> /etc/postgresql/12/main/pg_hba.conf
RUN echo "local   replication     all                                     peer" >> /etc/postgresql/12/main/pg_hba.conf
RUN echo "host    replication     all             127.0.0.1/32            md5" >> /etc/postgresql/12/main/pg_hba.conf
RUN echo "host    replication     all             ::1/128                 md5" >> /etc/postgresql/12/main/pg_hba.conf
RUN echo "host postgres postgres localhost trust" >> /etc/postgresql/12/main/pg_hba.conf
RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/12/main/pg_hba.conf
RUN echo "local   all             all                                     trust" >> /etc/postgresql/12/main/pg_hba.conf
RUN service postgresql restart
RUN /etc/init.d/postgresql start &&\
    psql --command "CREATE EXTENSION pg_cron;"&&\
    psql --command "UPDATE cron.job SET nodename = 'localhost';"&&\
# Create function
    psql --command "CREATE OR REPLACE FUNCTION update_events() returns void as \$$ DECLARE events integer[]; e integer; BEGIN SET SCHEMA 'acs'; update \"event\" set duration = duration - 60 where id >= 0; events := Array(select \"id\" from \"event\" where duration <= 0); foreach e in ARRAY events loop delete from event_doors where event_id = e; delete from \"event\" where id = e; end loop; END; \$$ LANGUAGE plpgsql;"&&\
# Assign schedule function to cron
    psql --command "SELECT cron.schedule('*/1 * * * *', 'select update_events();');"

# Expose poort 5432
EXPOSE 5432

# Add VOLUMEs to allow backup of config, logs and databases
VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

# Set the default command to run when starting the container
CMD ["/usr/lib/postgresql/12/bin/postgres", "-D", "/var/lib/postgresql/12/main", "-c", "config_file=/etc/postgresql/12/main/postgresql.conf"]