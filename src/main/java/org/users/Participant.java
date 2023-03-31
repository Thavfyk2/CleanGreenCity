package org.users;

import java.util.Objects;
public class Participant {

    private String lastname;
    private String firstname;
    private char sexe;
    private int age;
    private String adresse;
    private String mail;
    private String numeroDeTelephone;

    public Participant(String lastname, String firstname, char sexe, int age, String adresse, String mail, String numeroDeTelephone) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.sexe = sexe;
        this.age = age;
        this.adresse = adresse;
        this.mail = mail;
        this.numeroDeTelephone = numeroDeTelephone;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public char getSexe() {
        return sexe;
    }

    public void setSexe(char sexe) {
        this.sexe = sexe;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNumeroDeTelephone() {
        return numeroDeTelephone;
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Participant)) return false;
        if (!super.equals(object)) return false;
        Participant participant = (Participant) object;
        return sexe == participant.sexe && age == participant.age && lastname.equals(participant.lastname)
                && firstname.equals(participant.firstname) && java.util.Objects.equals(adresse, participant.adresse)
                && java.util.Objects.equals(mail, participant.mail)
                && java.util.Objects.equals(numeroDeTelephone, participant.numeroDeTelephone);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), lastname, firstname, sexe, age, adresse, mail, numeroDeTelephone);
    }

    public void setNumeroDeTelephone(String numeroDeTelephone) {
        this.numeroDeTelephone = numeroDeTelephone;
    }
}
