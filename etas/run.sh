bin/etas \
    -Dplay.http.secret.key='MY_SECRET_APPLICATION_KEY' \
    -Dhttp.port=82 \
    -J-Xms128M \
    -J-Xmx512m \
    -J-server \
    -Dconfig.file=./conf/application.conf
