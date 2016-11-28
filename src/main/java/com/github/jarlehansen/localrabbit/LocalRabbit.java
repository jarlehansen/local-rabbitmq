package com.github.jarlehansen.localrabbit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class LocalRabbit {

    private final String IMAGE_RABBITMQ = "rabbitmq:3-management";

    private DockerClient docker;
    private String containerId;

    public void start() {
        if (docker == null) {
            log.info("Starting local rabbitmq");

            try {
                docker = DefaultDockerClient.fromEnv().build();
                docker.pull(IMAGE_RABBITMQ);

                Map<String, List<PortBinding>> portBindings = ImmutableMap.of(
                        "5672/tcp", Lists.newArrayList(PortBinding.of("", "5672")),
                        "15672/tcp", Lists.newArrayList(PortBinding.of("", "15672"))
                );

                HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();
                ContainerConfig containerConfig = ContainerConfig.builder()
                        .image(IMAGE_RABBITMQ)
                        .hostConfig(hostConfig)
                        .build();
                ContainerCreation container = docker.createContainer(containerConfig);

                containerId = container.id();
                docker.startContainer(containerId);

                log.info("Rabbitmq docker container started");
                Thread.sleep(5000);
            } catch (InterruptedException | DockerCertificateException | DockerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() {
        try {
            ContainerInfo containerInfo = docker.inspectContainer(containerId);
            if (containerInfo != null && containerInfo.state().running()) {
                docker.stopContainer(containerId, 5);
                docker.removeContainer(containerId);
                docker.close();
            }
        } catch (InterruptedException | DockerException e) {
            throw new RuntimeException(e);
        }
    }

}
