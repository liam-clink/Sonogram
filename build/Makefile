

compile:
	javac -d bin src/de/dfki/sonogram/*.java src/de/dfki/maths/*.java -cp bin
	

run:
	cd bin; java -classpath . -splash:de/dfki/sonogram/Splash.png -showversion -Xms128m -Xmx1536m -Xfuture --add-opens=java.desktop/sun.awt=ALL-UNNAMED de.dfki.sonogram.Sonogram
	

compileAndRun: compile run


jar: compile
	cd bin; jar cfm ../installers/Sonogram.jar META-INF/MANIFEST.MF *
	

runJar:
	cd installers; java --add-opens=java.desktop/sun.awt=ALL-UNNAMED -jar Sonogram.jar


releaseMac:
	cp -f installers/Sonogram.jar installers/MACOS/Sonogram.app/Contents/Resources/Java
	rm -f installers/MACOS/Sonogram.app/Contents/Resources/Java/SonogramConfig.xml
	cd installers/MACOS; zip -r SonogramMacOS.zip Sonogram.app/*
	cp -f installers/MACOS/SonogramMacOS.zip ../bin
	

releaseWindows:
	cp -f installers/Sonogram.jar installers/WINDOWS/Sonogram\ Windows/
	rm -f installers/WINDOWS/Sonogram\ Windows/SonogramConfig.xml
	cd installers/WINDOWS; zip -r SonogramWindows.zip Sonogram\ Windows/*
	cp -f installers/Windows/SonogramWindows.zip ../bin


releaseUnix:
	cp -f installers/Sonogram.jar installers/UNIX/SonogramUnix/
	rm -f installers/UNIX/SonogramUnix/SonogramConfig.xml
	cd installers/UNIX; zip -r SonogramUnix.zip SonogramUnix/*
	cp -f installers/UNIX/SonogramUnix.zip ../bin


all: jar releaseMac releaseWindows releaseUnix 