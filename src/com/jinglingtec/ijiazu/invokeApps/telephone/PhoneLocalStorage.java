package com.jinglingtec.ijiazu.invokeApps.telephone;

import com.google.gson.Gson;
import com.jinglingtec.ijiazu.util.FoPreference;

public class PhoneLocalStorage
{
    // load contacts item from the preference
    public static ContactsItem[] loadContactItems()
    {
        try
        {
            final String localContacts = FoPreference.getString(FoPreference.CONTACTS_ITEM);
            if ((null == localContacts) || (localContacts.length() < 1))
            {
                return null;
            }

            Gson gson = new Gson();
            ContactsItem[] contactsItems = gson.fromJson(localContacts, ContactsItem[].class);
            return contactsItems;
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    // save the contacts item to the preference
    public static void saveContactItems(ContactsItem[] contactsItems)
    {
        try
        {
            if ((null == contactsItems) || (contactsItems.length < 1))
            {
                return;
            }

            Gson gson = new Gson();
            final String localContacts = gson.toJson(contactsItems);
            FoPreference.putString(FoPreference.CONTACTS_ITEM, localContacts);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}


