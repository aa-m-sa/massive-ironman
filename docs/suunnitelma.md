# Suunnitelma

**Tavoite:** ristinollaa pelaava robo

Osatavoitteet:

1. Piirtävä robo: Oikein paperin viereen sijoitettu robotti osaa piirtää 'sokeasti' jonkin ennalta määrätyn kuvion (= ruksi) oikeaan paikkaan. Piirtotoiminnallisuutta pyörittävä softa pyörii robotissa, robottia käskytetään bluetoothin yli.
    - [x] robotti joka pystyy piirtämään, rakenneideoita kaksi
        1. kynä kiinni 'käsivarressa', joka pyörii (= x-suunnan liikkuvuus ja hieman y-liikkuvuutta) ja jonka alusta liikkuu edestakaisin (= varsinainen y-suunnan liikkuvuus)
        2. paperille nouseva / laskeva kynä, joka kiinni kaksipyöräisessä autorobotissa **valittu toteutustapa** (ainakin toistaiseksi)
    - [x] ohjelma joka pystyy liikuttamaan piirtovälinettä
    - [ ] piirto-käskyjen ("piirrä ruksi ruutuun 0, 1") antaminen robotille läppäriltä USB:n yli **ei onnistu toistaiseksi**
    - [x] sama bluetoothilla **mutta tämä OK!!**
2. Pelilaudan tilanteen webkameralla (+ OpenCV) tunnistava robo. Kuvantunnistukseen käytetään OpenCV:tä läppärillä.
    - [ ] JavaCV-ympäristön pystytys niin että jonkinlainen kuvaa käsittelevä softa ylipäätään kääntyy
    - [ ] webkamera kuvaa peliruudukon + OpenCV + Java -softa läppärillä joka hahmottaa kuvan perustella pelitilanteen (ruudukko, ruksit, ympyrät)
    - [ ] mahdollisesti hyödynnetään webkameraa piirtimen kalibrointiin?
3. Ristinolla-pelitekoäly kuvantunnistuksen päälle
    - [x] läppärin softa pelaa ristinollaa tyhmästi (osaa tunnistaa tyhjän ruuduun ja piirtää siihen ruksin)
    - [ ] softa pelaa ristinollaa vähemmän tyhmästi (jonkin sortin ristinollatekoäly)

##Haasteet?

1. Piirtävä robo. Teknisiä haasteita (tekniikkalego-aloittelijalle):
    - kynän nosto / laskeminen piirtopinnalle **OK!**
    - kynän liikuttaminen tarpeeksi tarkasti **Luultavasti OK!**
2. Paperin lukeminen webbikameralla.
    - OpenCV:n käyttäminen ylipäätään
    - Kameran sijoittelu, jotta ruudukon muodot eivät vääristy liikaa, tai OpenCV:n 3d-muunnosten käyttäminen

##Fallback?

Mitäs sitten jos haasteet osoittautuvat liian haastellisiksi, ts jos tarpeeksi tarkan & toimivan merkkejä piirtävän robotin kokoaminen tarpeeksi nopeasti (eli jotta koodaukselle jää aikaa, siis ennen viikkoraportti 2 dedistä) ei onnistu?

- [ ] Robotti joka osaa tehdä jotain ulkoisen webbikameran avulla?!
