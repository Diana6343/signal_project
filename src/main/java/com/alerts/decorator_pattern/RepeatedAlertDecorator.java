package com.alerts.decorator_pattern;


import java.util.logging.Logger;

public class RepeatedAlertDecorator extends AlertDecorator {
    private static final Logger LOGGER = Logger.getLogger(RepeatedAlertDecorator.class.getName());
    private int repeatCount;

    public RepeatedAlertDecorator(Alert decoratedAlert, int repeatCount) {
        super(decoratedAlert);
        this.repeatCount = repeatCount;
    }

    @Override
    public void trigger() {
        for (int i = 0; i < repeatCount; i++) {
            LOGGER.info("Repeated alert " + (i + 1) + " of " + repeatCount);
            decoratedAlert.trigger();
        }
    }
}

