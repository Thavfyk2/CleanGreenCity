package org.users;

import java.text.DecimalFormat;

public class Sponsor extends Participant {
    private final double donation;
    DecimalFormat df = new DecimalFormat("#.00");

    public Sponsor(String lastname, String firstname, char sexe, int age, String adresse, String mail, String numeroDeTelephone, double donation) {
        super(lastname, firstname, sexe, age, adresse, mail, numeroDeTelephone);
        this.donation = Double.parseDouble(df.format(donation));
    }
}
