package org.users;

public class Organizer extends Participant{

    private String role;
    public Organizer(String lastname, String firstname, char sexe, int age, String adresse, String mail, String numeroDeTelephone, String role) {
        super(lastname, firstname, sexe, age, adresse, mail, numeroDeTelephone);
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
