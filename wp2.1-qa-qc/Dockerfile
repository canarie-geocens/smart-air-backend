FROM openjdk:10-slim
COPY ./target/sta-qa-qc-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/sta-qa-qc/
COPY ./config.properties /opt/sta-qa-qc/
ENTRYPOINT ["java", "-jar", "/opt/sta-qa-qc/sta-qa-qc-1.0-SNAPSHOT-jar-with-dependencies.jar"]
CMD [""]