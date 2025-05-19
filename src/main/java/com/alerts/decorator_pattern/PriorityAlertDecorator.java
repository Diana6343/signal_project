package com.alerts.decorator_pattern;


import java.util.logging.Logger;

public class PriorityAlertDecorator extends AlertDecorator {
    private static final Logger LOGGER = Logger.getLogger(PriorityAlertDecorator.class.getName());
    private String priority;

    public PriorityAlertDecorator(Alert decoratedAlert, String priority) {
        super(decoratedAlert);
        this.priority = priority;
    }

    @Override
    public void trigger() {
        LOGGER.info("Priority: " + priority);
        decoratedAlert.trigger();
    }
}