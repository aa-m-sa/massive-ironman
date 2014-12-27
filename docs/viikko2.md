# Viikko 2 raportti

## Toteutunut

1. Joulunvieton vuoksi vain muutama päivä tehokasta työskentelyä.

2. Luovuin (ainakin toistaiseksi) yrityksestä rakentaa nätisti kaikenlaisia kuvioita piirtävästä 'käsivarsimaisesta' robotista. Sen sijaan autorobotti + ylös-alas liikkuva kynä -yhdistelmä oli teknisesti helppo toteuttaa. Hieman perustrigonometriaa, ja sain aikaiseksi robotin, joka piirtelee siedettävän näköisiä rukseja paperille kelvollisella tarkkuudella ruudukkoon.

3. Aloitin OpenCV/JavaCV:llä leikkimisen + tutoriaalien lukemisen. Rojbosin mukana tullut `Demo.java` kääntyi ja löysi webkameran.

4. Päivitetty suunnitelmaa asianmukaisesti.

## Seuraavaksi

Ensi viikolla tarkoitus:

1. Järkevää testausta. Trigonometria-osuutta (`Board.java`) voisi ehkä jopa yksikkötestata JUnitilla (miten se onnistuu Lejos + Ant -kombolla, en tiedä).

2. Käskyjen kommunikointi läppäriltä Penbotille. Tätä varten pitänee pyöräyttää jonkinlainen yksinkertainen protokolla ("piirrä X ruudukon koordinaattiin se-ja-se").

3. Ruudukon lukeminen OpenCV:llä. (Ts. peliruudukon löytäminen ja tunnistaminen kuvasta.)
