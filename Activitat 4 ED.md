# Activitat 4: Implementació d'un recomanador d'artistes

Aquesta darrera activitat és el projecte final de l’assignatura. Haureu d’utilitzar la majoria de les estructures de dades desenvolupades en les activitats anteriors. No disposeu de cap codi base ni d’un conjunt de proves; per tant, haureu de construir el projecte des de zero i lliurar-lo a l’Activitat 4.

En aquesta darrera activitat heu d'implementar un recomanador d'artistes per als usuaris. En aquesta activitat us donem unes directrius generals del funcionament, però hi haurà decisions de disseny que podreu prendre vosaltres. No existeix un banc de proves, ja que no hi ha una solució perfecta: us pot donar diferents resultats segons les decisions que prengueu.

## 1. Context de l'activitat

Per fer el recomanador utilitzareu el conjunt de dades de last.fm disponible a http://ocelma.net/MusicRecommendationDataset/lastfm-360K.html.

Aquí trobareu dos fitxers: usersha1-artmbid-artname-plays.tsv i usersha1-profile.tsv. A la pàgina anterior trobareu el format de cadascun dels fitxers. En el primer fitxer hi ha un id d'usuari, un id d'artista, el nom de l'artista i el nombre de vegades que l'usuari ha escoltat una cançó de l'artista. En el segon fitxer hi ha l'id d'usuari, el sexe, l'edat, el país i quan es va donar d'alta. Noteu que hi ha dades que no estan disponibles, com per exemple l'edat d'un usuari.

## 2. Operacions que ha de suportar la implementació

L'objectiu de la pràctica és dissenyar un recomanador aprofitant els TADs que heu construït a les activitats anteriors. En podeu afegir d'altres si us calen.

Un sistema recomanador analitza dades dels usuaris i dels ítems a recomanar per suggerir allò que probablement agradarà a un usuari.

Per exemple, donat un usuari que té un registre dels músics que escolta, el recomanador busca altres usuaris semblants i utilitza les preferències d'aquests com a recomanacions per al primer. O també pot analitzar les característiques dels artistes (gènere musical, etc.) i recomanar música similar a la que l'usuari escolta. Fins i tot es poden combinar les dues opcions anteriors per millorar la precisió.

No obstant això, els sistemes recomanadors són complexos i, per tant, en aquest cas farem una simplificació.

Les funcionalitats que ha de tenir el nostre recomanador són les següents:

* Fer recomanacions d'artistes per usuaris semblants.

Donat un usuari, cal retornar tots els artistes que hagin agradat a usuaris semblants sense incloure artistes que ja hagi escoltat. Podem suposar que a un usuari li agrada un determinat artista si l'escolta més d'un cert nombre de vegades. Considerem que dos usuaris són semblants si tenen el mateix rang d'edat (per exemple, són entre 5 anys més joves i 5 anys més grans) i/o són del mateix país i/o del mateix sexe i/o tenen els mateixos artistes preferits. Podem suposar que tenen els mateixos artistes preferits si comparteixen almenys un dels dos artistes que més han escoltat, tot i que podeu modificar aquest criteri si així ho considereu.

Per tant, es poden demanar recomanacions per usuaris del mateix país, del mateix rang d'edat, del mateix sexe, per artistes preferits o per qualsevol combinació de les anteriors. En la sortida no hi pot haver artistes repetits.

Per accedir-hi de forma conjunta tindrem una funció `recomanacio` amb diferents paràmetres que ens permetrà ajustar quines dimensions volem considerar en la recomanació.

## recomanacio

**Signatura:** `TADLlista<String> recomanacio(String userid, int numMinEscoltes, boolean sexe, int rangEdat, boolean pais, boolean preferits)`

**Descripció:** Retorna una llista dels artistes recomanats. Si `sexe`, `pais` o `preferits` són `false`, no es consideren aquestes dimensions a l'hora de fer la recomanació. Si `rangEdat = -1`, no es considera l'edat de l'usuari.

Per exemple:

* `recomanacio(user0001, 100, false, 5, true, false)` retorna els artistes que els usuaris del mateix país que `user0001` i que estan en el rang d'edat `[age-5, age+5]` han escoltat 100 o més vegades.
* `recomanacio(user0001, 100, false, -1, false, true)` retorna els artistes que els usuaris que tenen els mateixos artistes preferits que `user0001` han escoltat 100 o més vegades.

## 3. Criteris d’avaluació

* Que es facin servir de forma adequada les estructures dissenyades en les activitats prèvies de la classe.
* Que el codi sigui capaç de llegir els fitxers d'entrada i carregar les dades a les vostres estructures de dades.
* Que s'hagi implementat correctament el mètode de recomanació d'artistes.
* Us demanem que ens presenteu dins del vostre projecte els resultats obtinguts per almenys 10 recomanacions diferents combinant recomanacions per usuaris del mateix país, del mateix rang d'edat, del mateix sexe i per artistes preferits, tot i que es valorarà que proveu recomanacions diverses. N'hi ha quatre que són imprescindibles: (1) una recomanació per edat, (2) una per país, (3) una per artistes preferits i (4) una per edat i país.
* Si es demana recomanació per edat i l'usuari objectiu no té l'edat disponible, la recomanació no podrà utilitzar aquesta dimensió.