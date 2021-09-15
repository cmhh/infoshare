FROM ubuntu:20.04

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update && \
  apt-get --no-install-recommends -y install \
    wget openjdk-11-jre unzip && \
  wget https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_92.0.4515.107-1_amd64.deb -O chrome.deb && \
  wget https://chromedriver.storage.googleapis.com/92.0.4515.107/chromedriver_linux64.zip && \
  apt install -y ./chrome.deb && \
  unzip chromedriver_linux64.zip && \
  mv chromedriver /usr/local/bin/ && \
  rm /chrome* && \
  apt-get clean && \
  rm -rf /var/lib/apt/lists/* 

COPY ./target/scala-2.13/infoshare.jar infoshare.jar

ENTRYPOINT ["java", "-jar", "infoshare.jar"]