package com.simplecash.object;

import javax.persistence.*;
import java.util.Set;

/**
 * Contact information for other entities such as Supplier and Customer.
 * A contact is composed of zero or more ContactInfo and zero or more Address instances, ie,
 * one contact can have 1 email and 2 phones, another 3 emails 2 addresses and 1 phone.
 */
@Entity
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinTable(
            name = "Contact_ContactInfo",
            joinColumns = { @JoinColumn(name = "id_contact") },
            inverseJoinColumns = { @JoinColumn(name = "id_contactInfo") })
    private Set<ContactInfo> contactInfos;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinTable(
            name = "Contact_Address",
            joinColumns = { @JoinColumn(name = "id_contact") },
            inverseJoinColumns = { @JoinColumn(name = "id_address") })
    private Set<Address> addresses;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ContactInfo> getContactInfos() {
        return contactInfos;
    }

    public void setContactInfos(Set<ContactInfo> contactInfos) {
        this.contactInfos = contactInfos;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }
}
