package com.sequenceiq.cloudbreak.shell.converter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.MethodTarget;

import com.sequenceiq.cloudbreak.client.CloudbreakClient;
import com.sequenceiq.cloudbreak.shell.completion.GcpInstanceType;
import com.sequenceiq.cloudbreak.shell.model.CloudbreakContext;

public class GcpInstanceTypeConverter extends AbstractConverter<GcpInstanceType> {
    @Autowired
    private CloudbreakContext context;

    public GcpInstanceTypeConverter(CloudbreakClient client) {
        super(client);
    }

    @Override
    public boolean supports(Class<?> type, String optionContext) {
        return GcpInstanceType.class.isAssignableFrom(type);
    }

    @Override
    public boolean getAllPossibleValues(List<Completion> completions, Class<?> targetType, String existingData, String optionContext, MethodTarget target) {
        try {
            return getAllPossibleValues(completions, context.getInstanceTypeNamesByPlatform("GCP"));
        } catch (Exception e) {
            return false;
        }
    }
}
