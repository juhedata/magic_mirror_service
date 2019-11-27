package com.juhe.demo.ao;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

/**
 * @author luxianzhu
 */
public class AdminAOGroupSequenceProvider implements DefaultGroupSequenceProvider<AdminAO> {

    @Override
    public List<Class<?>> getValidationGroups(AdminAO adminAO) {
        List<Class<?>> defaultGroupSequence = new ArrayList<>();
        defaultGroupSequence.add(AdminAO.class);
        if (adminAO != null && adminAO.getId() == null) {
            defaultGroupSequence.add(AdminAO.PasswordCheck.class);
        }
        return defaultGroupSequence;
    }
}
