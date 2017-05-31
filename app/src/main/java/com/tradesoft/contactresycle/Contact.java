package com.tradesoft.contactresycle;


public class Contact {
    private String userName;
    private String phoneNumber;
    private String profilePicture;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else
            return phoneNumber.equals(((Contact) obj).getPhoneNumber());
    }
}
