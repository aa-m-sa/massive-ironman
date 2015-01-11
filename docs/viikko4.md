# Viikko 4 raportti

## Toteutunut

* Kuvantunnistus OpenCV:llä ja peruspelilogiikka - toimii! ainakin jotenkin!
* Suurin haaste: OpenCV:n Java-bindit eivät ole järin kaksiset, mutta paremmat kuitenkin kuin OpenCV:n JavaCV-porttauksessa (ei dookumentaatiota laisinkaan :< ) jota kokeilin aluksi.
* Pelikokemus ei järin kaksinen sillä vaaditaan käyttäjän vahvistus jokaiselle kameran havaitsemalle muutokselle pelilaudalla.
* Demo pidetty
* Tarkemmin: katso loppuraportti

**"miten meni noin niinku omasta mielestä"**

Hyvin:

* robotti osoittautui toteuttamiskelpoiseksi
* kaikki tarvittavat palaset onnistui saattaa toimintakelpoiseksi
* opin uusia juttuja: threading, robotin kaltainen moniosainen systeemi

Parantamisen varaa:

* itse ristinolla 'pelinä' jäi kaipaamaan viilaamista (ei osaa lukea pelaajan siirtoja itsestään, pelikokemus ei siten niin sulava kuin olisin tahtonut)
* samaten ajanpuutteen vuoksi pelitekoäly jäi tekemättä, vaikka päädyin tähän aiheeseen juuri siksi että halusin kokeilla syksyllä Johdatus tekoälyyn kurssilla oli esiteltyjä juttuja (minimax, alpha-beta)
* liikaa aikaa meni teknisten ongelmien kanssa painimiseen
    * Bluetooth
    * USB
    * RojbOS
    * Robotin rakenne (ekalla viikolla kokeilin useita liian monimutkaisia rakenneyrityksiä)
* koodia tuli kirjoiteltua paljon kiireessä ja liiaksi ajattelematta, joten siinä olisi paljon parennettavaa
* testejä ei tullut kirjoiteltua, vaikka ne olisivat erityisen hyödyllisiä jos edelliseen kohtaan puuttuisi ja koodia alkaisi refaktoroida
* lyhyesti: mihin kannattaisi käyttää aikaa ja mihin ei
