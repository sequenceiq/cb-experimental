package com.sequenceiq.cloudbreak.websocket;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.domain.Cluster;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.repository.ClusterRepository;
import com.sequenceiq.cloudbreak.repository.StackRepository;
import com.sequenceiq.cloudbreak.service.notification.Notification;
import com.sequenceiq.cloudbreak.service.notification.NotificationSender;

@Component
public class UptimeNotifier {
    private static final String UPTIME_NOTIFICATION = "UPTIME_NOTIFICATION";

    @Inject
    private ClusterRepository clusterRepository;

    @Inject
    private StackRepository stackRepository;

    @Inject
    private NotificationSender notificationSender;


    @Scheduled(fixedDelay = 60000)
    public void sendUptime() {
        List<Cluster> clusters = (List<Cluster>) clusterRepository.findAll();
        long now = new Date().getTime();
        for (Cluster cluster : clusters) {
            Stack stack = stackRepository.findStackForCluster(cluster.getId());
            if (stack != null && !stack.isDeleteCompleted()) {
                Long uptime = cluster.getUpSince() == null || !cluster.isAvailable() ? 0L : now - cluster.getUpSince();
                Notification notification = createUptimeNotification(stack, uptime);
                notificationSender.send(notification);
            }
        }
    }

    private Notification createUptimeNotification(Stack stack, Long uptime) {
        Notification notification = new Notification();
        notification.setOwner(stack.getOwner());
        notification.setAccount(stack.getAccount());
        notification.setStackId(stack.getId());
        notification.setEventType(UPTIME_NOTIFICATION);
        notification.setEventMessage(String.valueOf(uptime));
        if (stack.getCredential() == null) {
            notification.setCloud("null");
        } else {
            notification.setCloud(stack.getCredential().cloudPlatform().toString());
        }
        if (stack.getCluster() == null || stack.getCluster().getBlueprint() == null) {
            notification.setBlueprintId(null);
            notification.setBlueprintName("null");
        } else {
            notification.setBlueprintId(stack.getCluster().getBlueprint().getId());
            notification.setBlueprintName(stack.getCluster().getBlueprint().getBlueprintName());
        }
        return notification;
    }
}
