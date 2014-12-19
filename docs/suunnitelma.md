# Suunnitelma

**Tavoite:** ristinollaa pelaava robo

Osatavoitteet:

1. Piirtävä robo: Oikein paperin viereen sijoitettu robotti osaa piirtää 'sokeasti' jonkin ennalta määrätyn kuvion (= ruksi) oikeaan paikkaan. Piirtotoiminnallisuutta pyörittävä softa pyörii robotissa, robottia käskytetään bluetoothin yli.
    - [ ] robotti joka pystyy piirtämään, rakenneideoita kaksi
        1. kynä kiinni 'käsivarressa', joka pyörii (= x-suunnan liikkuvuus ja hieman y-liikkuvuutta) ja jonka alusta liikkuu edestakaisin (= varsinainen y-suunnan liikkuvuus)
        2. kynä keskellä kaksipyöräistä autorobottia
    - [ ] ohjelma joka pystyy liikuttamaan piirtovälinettä
    - [ ] piirto-ohjeiden antaminen robotille läppäriltä bluetoothin yli
2. Pelilaudan tilanteen webkameralla (+ OpenCV) tunnistava robo. Kuvantunnistukseen käytetään OpenCV:tä läppärillä, .
    - [ ] webkamera kuvaa peliruudukon + OpenCV + Java -softa läppärillä joka hahmottaa kuvan perustella pelitilanteen (ruudukko, ruksit, ympyrät)
    - [ ] mahdollisesti hyödynnetään webkameraa piirtimen kalibrointiin?
3. Ristinolla-pelitekoäly kuvantunnistuksen päälle
    - [ ] läppärin softa pelaa ristinollaa tyhmästi (osaa tunnistaa tyhjän ruuduun ja piirtää siihen ruksin)
    - [ ] softa pelaa ristinollaa vähemmän tyhmästi (jonkin sortin ristinollatekoäly)

##Haasteet?

1. Piirtävä robo. Teknisiä haasteita (tekniikkalego-aloittelijalle):
    - kynän nosto / laskeminen piirtopinnalle
    - kynän liikuttaminen: molemmissa rakenneideoissa haasteensa
2. Paperin lukeminen webbikameralla.
    - OpenCV:n käyttäminen
    - Kameran sijoittelu, jotta ruudukon muodot eivät vääristy liikaa (vai selviääkö OpenCV tästä)?

##Fallback?

Mitäs sitten jos haasteet osoittautuvat liian haastellisiksi, ts jos tarpeeksi tarkan & toimivan merkkejä piirtävän robotin kokoaminen tarpeeksi nopeasti (eli jotta koodaukselle jää aikaa, siis ennen viikkoraportti 2 dedistä) ei onnistu?

- [ ] Robotti joka osaa tehdä jotain ulkoisen webbikameran avulla?!
