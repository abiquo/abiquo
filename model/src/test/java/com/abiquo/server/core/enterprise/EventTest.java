  package com.abiquo.server.core.enterprise;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class EventTest extends DefaultEntityTestBase<Event>
  {

      @Override
      protected InstanceTester<Event> createEntityInstanceGenerator()
      {
          return new EventGenerator(getSeed());
      }
  }
