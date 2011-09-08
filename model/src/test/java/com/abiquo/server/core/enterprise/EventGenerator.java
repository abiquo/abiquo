package com.abiquo.server.core.enterprise;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class EventGenerator extends DefaultEntityGenerator<Event>
{

    public EventGenerator(final SeedGenerator seed)
    {
        super(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final Event obj1, final Event obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Event.COMPONENT_PROPERTY,
            Event.ACTION_PERFORMED_PROPERTY, Event.PERFORMED_BY_PROPERTY,
            Event.STORAGE_POOL_PROPERTY, Event.STRACKTRACE_PROPERTY, Event.TIMESTAMP_PROPERTY,
            Event.VIRTUAL_APP_PROPERTY, Event.DATACENTER_PROPERTY,
            Event.VIRTUAL_DATACENTER_PROPERTY, Event.ENTERPRISE_PROPERTY,
            Event.STORAGE_SYSTEM_PROPERTY, Event.NETWORK_PROPERTY, Event.PHYSICAL_MACHINE_PROPERTY,
            Event.RACK_PROPERTY, Event.VIRTUAL_MACHINE_PROPERTY, Event.VOLUME_PROPERTY,
            Event.SUBNET_PROPERTY, Event.SEVERITY_PROPERTY, Event.USER_PROPERTY);
    }

    @Override
    public Event createUniqueInstance()
    {
        String action =
            newString(nextSeed(), Event.ACTION_PERFORMED_LENGTH_MIN,
                Event.ACTION_PERFORMED_LENGTH_MAX);

        Event event = new Event(action, action, action, action, 1, action, 1, 1);

        return event;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Event entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }

}
