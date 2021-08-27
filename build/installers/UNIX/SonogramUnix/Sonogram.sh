#/bin/sh

java -showversion -Xms128m -Xmx10240m --add-opens=java.desktop/sun.awt=ALL-UNNAMED -jar Sonogram.jar
