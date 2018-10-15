FROM mysql:5.6

ENV LANG C.UTF-8
ENV MYSQL_ROOT_PASSWORD passwd
ENV TZ Asia/Tokyo

COPY work/ /home/work

WORKDIR /home/work
RUN cp -i custom.cnf /etc/mysql/conf.d

EXPOSE 3306
