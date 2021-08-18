#/bin/sh

java -showversion -Xms128m -Xmx1536m --add-opens=java.desktop/sun.awt=ALL-UNNAMED -jar Sonogram.jar
