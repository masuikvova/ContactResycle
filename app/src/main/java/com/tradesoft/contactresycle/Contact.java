package com.tradesoft.contactresycle;


import java.util.ArrayList;

public class Contact {
    private String userName;
    private ArrayList<String> phones = new ArrayList<>();
    private String profilePicture;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<String> getPhones() {
        return phones;
    }

    public void addPhoneNumber(String phoneNumber) {
        phones.add(phoneNumber);
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public boolean equals(Object obj) {
        Contact c = (Contact) obj;
        for (String number : phones)
            for (String objNumber : c.getPhones())
                if (number.contains(objNumber))
                    return true;
        return false;
    }
}
