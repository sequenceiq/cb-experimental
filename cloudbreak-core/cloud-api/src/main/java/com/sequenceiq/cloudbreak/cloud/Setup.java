package com.sequenceiq.cloudbreak.cloud;

import com.sequenceiq.cloudbreak.cloud.context.AuthenticatedContext;
import com.sequenceiq.cloudbreak.cloud.model.CloudStack;
import com.sequenceiq.cloudbreak.cloud.model.Image;
import com.sequenceiq.cloudbreak.cloud.notification.PersistenceNotifier;
import com.sequenceiq.cloudbreak.common.type.ImageStatusResult;

/**
 * Collection of basic methods to prepare the Cloud provider to launch a given stack.
 */
public interface Setup {

    /**
     * Creates the VM if it is not available. Some platform does not allow to start a VM from a central image but it forces the user to copy the image to
     * its own storage.
     * <p/>
     * To check whether the image copy is finished use {@link #checkImageStatus(AuthenticatedContext, Image)}
     *
     * @param authenticatedContext the context which already contains the authenticated client
     * @param image                the image to be copied
     */
    void prepareImage(AuthenticatedContext authenticatedContext, Image image);

    /**
     * Invoked by Cloudbreak to check the whether the image copy is finished
     *
     * @param authenticatedContext the context which already contains the authenticated client
     * @param image                the image to be copied
     * @return state of the image
     */
    ImageStatusResult checkImageStatus(AuthenticatedContext authenticatedContext, Image image);

    /**
     * Implementation of this method shall contain basic checks, e.g. checking that the the flavours defined in {@link CloudStack} available or the
     * platform or checking whether the defined subnet is in the same region where the stack intended to be launched
     *
     * @param authenticatedContext the context which already contains the authenticated client
     * @param stack                stack the definition of infrastucture that needs to be launched
     * @param persistenceNotifier  if a resource has been created during this prerequisit check then the Cloud provider can persist them to Cloudbreak's
     */
    void prerequisites(AuthenticatedContext authenticatedContext, CloudStack stack, PersistenceNotifier persistenceNotifier);

}
