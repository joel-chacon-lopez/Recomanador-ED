# Documentació de l'activitat 4

## Objectiu

En aquesta activitat s'ha implementat un recomanador d'artistes a partir de dades de `Last.fm`. El programa llegeix dos fitxers TSV: un amb els perfils dels usuaris i un altre amb les escoltes dels artistes. A partir d'aquestes dades, es poden obtenir recomanacions d'artistes per a un usuari concret.

La recomanació es basa en buscar usuaris semblants i proposar artistes que aquests usuaris han escoltat prou vegades, pero que l'usuari objectiu encara no ha escoltat.

## Estructures utilitzades

S'han reutilitzat estructures de dades fetes en activitats anteriors:

- `TADLlista`
- `LlistaDinamica`
- `TADHashMap`
- `HashingIndirecte`
- `Node`
- `NodeHash`

La taula de hash s'utilitza per guardar els usuaris i els artistes, ja que permet consultar ràpidament si un usuari o artista existeix. La llista dinàmica s'utilitza per guardar les escoltes de cada usuari i també per retornar la llista final de recomanacions.

No s'ha utilitzat un graf bipartit, encara que seria una alternativa possible per representar la relació entre usuaris i artistes. En aquest cas s'ha optat per guardar els usuaris en una taula de hash i mantenir dins de cada usuari la seva llista d'escoltes. Aquesta estructura permet accedir ràpidament al perfil de l'usuari i recórrer les seves escoltes sense necessitat de consultar la relació inversa artista-usuaris.

També s'han creat algunes classes pròpies per representar les dades del problema:

- `UsuariLastFm`: guarda la informació del perfil d'un usuari i la seva llista d'escoltes.
- `Artista`: guarda l'identificador i el nom d'un artista.
- `Escolta`: representa una relació entre un usuari i un artista amb el nombre de reproduccions.
- `Recomanador`: carrega les dades i implementa el mètode de recomanació.

## Classe `UsuariLastFm`

La classe `UsuariLastFm` representa un usuari del dataset de Last.fm. Aquesta classe s'ha creat per guardar en un mateix objecte les dades del perfil de l'usuari i les seves escoltes.

Els atributs principals son:

- `id`: identificador únic de l'usuari.
- `sexe`: sexe de l'usuari, si està disponible.
- `edat`: edat de l'usuari. Pot ser `null` si no apareix al fitxer.
- `pais`: país de l'usuari.
- `alta`: data en que l'usuari es va donar d'alta.
- `escoltes`: llista dinàmica amb els artistes que ha escoltat i el nombre de reproduccions.

Funcions principals:

- `afegirEscolta(Escolta escolta)`: afegeix una nova escolta a la llista d'escoltes de l'usuari.
- `haEscoltat(String nomArtista)`: comprova si l'usuari ja ha escoltat un artista concret. Serveix per evitar recomanar artistes repetits a l'usuari objectiu.
- `artistesPreferits(int quantitat)`: retorna els artistes més escoltats per l'usuari. S'utilitza amb valor `2`, ja que el criteri de preferits compara els dos artistes principals.
- `getId()`, `getSexe()`, `getEdat()`, `getPais()`, `getAlta()`: retornen les dades basiques del perfil.
- `getEscoltes()`: retorna la llista d'escoltes de l'usuari.

## Fitxers de dades

El programa pot treballar amb dos conjunts de dades.

Fitxers petits:

```text
Dades/usersha1-profile.tsv
Dades/usersha-artname49271.tsv
```

Fitxers reals:

```text
Dades/lastfm-dataset-360K/usersha1-profile.tsv
Dades/lastfm-dataset-360K/usersha1-artmbid-artname-plays.tsv
```

El fitxer real d'escoltes és molt gran, per això el programa tambe permet carregar nomes les primeres linies indicades.

## Mètode principal de recomanacio

La funció principal és:

```java
TADLlista<String> recomanacio(String userid, int numMinEscoltes, boolean sexe, int rangEdat, boolean pais, boolean preferits)
```

Parametres:

- `userid`: identificador de l'usuari per al qual es volen recomanacions.
- `numMinEscoltes`: nombre mínim d'escoltes per considerar que un artista agrada a un usuari.
- `sexe`: si és `true`, només es tenen en compte usuaris del mateix sexe.
- `rangEdat`: marge d'edat acceptat. Si val `-1`, no es filtra per edat.
- `pais`: si és `true`, només es tenen en compte usuaris del mateix país.
- `preferits`: si és `true`, només es tenen en compte usuaris que comparteixen algun dels dos artistes més escoltats.

Si es demana filtratge per edat pero l'usuari objectiu no té edat disponible, aquesta dimensió no s'utilitza.

Quan s'activen diversos filtres, s'han de complir tots. Per exemple, si es demana mateix país i mateix rang d'edat, només es fan servir usuaris que compleixen les dues condicions.

## Compilació

Des de la carpeta del projecte:

```bash
cd ~/Practica4-ED
```

Compilar:

```bash
javac -d out src/main/java/cat/urv/deim/exceptions/*.java src/main/java/cat/urv/deim/*.java
```

L'opció `-d out` indica que les classes compilades es guardaran dins la carpeta `out`.

## Execució amb els TSV petits

Per defecte, el programa utilitza els fitxers petits:

```bash
java -cp out cat.urv.deim.Main
```

## Execució amb el TSV real complet

Per executar amb el dataset real complet:

```bash
java -Xmx16g -cp out cat.urv.deim.Main real
```

`-Xmx16g` dona més memoria a Java, per poder gestionar totes les dades del fitxer.

## Execució amb el TSV real limitant linies

Per provar dades reals sense carregar tot el fitxer:

```bash
java -Xmx1g -cp out cat.urv.deim.Main real 10000
```

Això carrega tots els perfils reals, però només les primeres `10000` linies del fitxer real d'escoltes. Aquest últim paràmetre es pot canviar en funció de les linies que es vulguin provar.

Altres exemples:

```bash
java -Xmx8g -cp out cat.urv.deim.Main real 50000
java -Xmx8g -cp out cat.urv.deim.Main real 100000
```

## Proves fetes

El `Main` executa 10 recomanacions diferents combinant filtres per edat, pais, sexe i artistes preferits. Entre aquestes hi ha les quatre recomanacions demanades a l'enunciat:

- Recomanació per edat.
- Recomanació per país.
- Recomanació per artistes preferits.
- Recomanació per edat i país.

Tambe he creat una carpeta de tests amb JUnit:

```text
src/test/java/cat/urv/deim/TestRecomanador.java
```

Els tests comproven la càrrega de dades i diferents casos del mètode de recomanació.
