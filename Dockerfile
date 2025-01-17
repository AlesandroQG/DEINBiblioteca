FROM ubuntu:20.04
RUN apt-get update -y
RUN apt-get upgrade -y
RUN apt install openjdk-21-jdk -y
RUN apt install git -y
RUN apt install maven -y
RUN git clone https://github.com/AlesandroQG/DEINBiblioteca
WORKDIR /DEINBiblioteca
RUN mvn package
CMD java -jar target/DEINBiblioteca-1.0-SNAPSHOT.jar