# Viikko 2 raportti

## Toteutunut

1. Joulunvieton vuoksi vain muutama päivä tehokasta työskentelyä.

2. Luovuin (ainakin toistaiseksi) yrityksestä rakentaa nätisti kaikenlaisia kuvioita piirtävästä 'käsivarsimaisesta' robotista. Sen sijaan autorobotti + ylös-alas liikkuva kynä -yhdistelmä oli teknisesti helppo toteuttaa. Hieman perustrigonometriaa, ja sain aikaiseksi robotin, joka piirtelee siedettävän näköisiä rukseja paperille kelvollisella tarkkuudella ruudukkoon.

*Lisäys:* video [robotista joka piirtää näkymättömän 'ruudukon' täyteen rukseja](https://drive.google.com/file/d/0B1_61yv8HENsVUpUWXZodVQ4dFE/view?usp=sharing)

3. Aloitin OpenCV/JavaCV:llä leikkimisen + tutoriaalien lukemisen. Rojbosin mukana tullut `Demo.java` kääntyi ja löysi webkameran.

4. Päivitetty suunnitelmaa asianmukaisesti.

5. Koska virtuaali-rojbosin vanha Open/JavaCV aiheutti ongelmia (versiolle ?? vuodelta 2011? vaikeahkoa löytää dokumentaatiota), aloin pystyttämään LeJOS + yms ohjelmointiympäristöä suoraan omalle Mint-buntulle uusilla versioilla kirjastoista: OpenCV:n lisäksi myös -> LeJOS 0.9.1 (fläshätty brickille) ja lopultakin toimiva bluetooth (onneksi, sillä toisaalta USB-nxjupload lakkasi toimimasta LeJOSin USB3.0-bugin takia, kun ei ollut enää USB2.0aa simuloivaa VirtualBoxia).

## Seuraavaksi

Ensi viikolla tarkoitus:

0. Ohjelmointiympäristö vaihdettu VirtuaaliBoxi-tikulta omaan 'buntuun, joten ant- yms java-projektisysteemit tarkistettava (kirjastot eivät enää kansiossa opt/jossain, Eclipse vm.2011:n sijasta voin käyttää jotain Oikeaa Editoria, jne.)
**OK**

1. Järkevää testausta. Trigonometria-osuutta (`Board.java`) voisi ehkä jopa yksikkötestata JUnitilla (miten se onnistuu Lejos + Ant -kombolla, en tiedä).

2. Käskyjen kommunikointi läppäriltä Penbotille. Tätä varten pitänee pyöräyttää jonkinlainen yksinkertainen protokolla ("piirrä X ruudukon koordinaattiin se-ja-se").

3. Ruudukon lukeminen OpenCV:llä. (Ts. peliruudukon löytäminen ja tunnistaminen kuvasta.)
