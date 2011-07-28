package com.abiquo.model.enumerator;

import java.util.Comparator;

import com.abiquo.model.rest.RESTLink;

public enum LinkOrder implements Comparator<RESTLink>
{
    BY_REL
    {
        @Override
        public int compare(final RESTLink link0, final RESTLink link1)
        {
            if (link0.getRel() == null || link1.getRel() == null)
            {
                return 0;
            }

            return String.CASE_INSENSITIVE_ORDER.compare(link0.getRel(), link1.getRel());
        }
    },

    BY_TITLE
    {
        @Override
        public int compare(final RESTLink link0, final RESTLink link1)
        {
            if (link0.getTitle() == null || link1.getTitle() == null)
            {
                return 0;
            }

            return String.CASE_INSENSITIVE_ORDER.compare(link0.getTitle(), link1.getTitle());
        }
    }
}
