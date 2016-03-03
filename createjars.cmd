CALL mvn package -Pwindows32
CALL mvn package -Pwindows64

CALL mvn package -Plinux32
CALL mvn package -Plinux64

CALL mvn package -Pmacosx32
CALL mvn package -Pmacosx64

PAUSE