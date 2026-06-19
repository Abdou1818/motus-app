package fr.dauphine.miage.motus.word.config;

import fr.dauphine.miage.motus.word.model.Word;
import fr.dauphine.miage.motus.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final WordRepository wordRepository;

    private static final List<String> FRENCH_WORDS = Arrays.asList(
        // 5 lettres - mots courants
        "ABIME", "ABORD", "ABRIS", "ABSOU", "ACHAT", "ACIDE", "AGATE", "AGILE",
        "AGITE", "AIGLE", "AIMER", "AISSE", "ALLEE", "ALOES", "ALTER", "AMBRE",
        "AMOUR", "ANCRE", "ARBRE", "ARENE", "ARMET", "ASTRE", "ATOME", "AVARE",
        "AVION", "AVIDE", "AZOTE",
        "BALLE", "BANJO", "BARBE", "BARRE", "BELLE", "BICHE", "BIERE", "BILLE",
        "BLANC", "BOEUF", "BOMBE", "BONNE", "BOTTE", "BOULE", "BOURG", "BRAVE",
        "BRISE", "BRUME", "BULLE",
        "CABLE", "CACHE", "CADET", "CALME", "CANNE", "CARPE", "CARTE", "CAUSE",
        "CHAMP", "CHAOS", "CHAUD", "CHIEN", "CHOSE", "CIBLE", "CLAIR", "CLOWN",
        "COBRA", "COEUR", "COLLE", "COMME", "CONTE", "CORPS", "COTON", "COUDE",
        "COUPE", "COURS", "COURT", "CRANE", "CREME", "CRISE", "CROIX", "CRUEL",
        "CUIRE", "CYCLE",
        "DANSE", "DATTE", "DEBUT", "DELTA", "DENSE", "DEPOT", "DESIR", "DETTE",
        "DIGUE", "DIVIN", "DONNE", "DOUCE", "DOYEN", "DROIT",
        "ECLAT", "ECUME", "EFFET", "ELEVE", "ELITE", "ENNUI", "ENFER", "ENTRE",
        "EPAIS", "EPAVE", "EPICE", "EPINE", "ETAGE", "ETAPE", "ETUDE",
        "FABLE", "FACON", "FARCE", "FAUTE", "FAUVE", "FELIN", "FERME", "FERRE",
        "FESSE", "FICHE", "FIERE", "FIGUE", "FILET", "FILLE", "FINAL", "FLEUR",
        "FORTE", "FORET", "FORME", "FOSSE", "FOYER", "FRANC", "FRAIS", "FROID",
        "FRUIT", "FUMEE", "FUSEE",
        "GACHE", "GAFFE", "GAINE", "GALET", "GAMIN", "GARDE", "GAZON", "GENIE",
        "GENRE", "GESTE", "GILET", "GLACE", "GLOBE", "GOLFE", "GOMME", "GORGE",
        "GRACE", "GRAIN", "GRAND", "GRAVE", "GREVE", "GRIVE", "GUIDE",
        "HABIT", "HAINE", "HAUTE", "HERBE", "HEURE", "HOMME", "HOTEL",
        "IDOLE", "IMAGE", "INDEX", "ISSUE",
        "JOUER", "JOLIE", "JOYAU", "JUPON",
        "LABEL", "LAINE", "LAMPE", "LAPIN", "LARGE", "LASER", "LECON", "LEGER",
        "LESTE", "LIBRE", "LIEGE", "LIGNE", "LISTE", "LITRE", "LIVRE", "LOGIS",
        "LOQUE", "LOURD", "LOYAL", "LUEUR", "LUTTE",
        "MAGIE", "MALIN", "MAINS", "MALTE", "MARGE", "MARIN", "MASSE", "MEDIA",
        "MELEE", "MELON", "MERCI", "METAL", "METRE", "MINCE", "MIXTE", "MONDE",
        "MONTE", "MORAL", "MOTUS", "MOULE", "MOYEN", "MULET",
        "NAIVE", "NAPPE", "NOBLE", "NOCES", "NOEUD", "NORME", "NOTER", "NOYAU",
        "NUAGE",
        "OCEAN", "OFFRE", "OLIVE", "ORAGE", "ORDRE", "OUTRE", "OZONE",
        "PAIRE", "PALME", "PASSE", "PAUSE", "PAUME", "PEINE", "PERLE", "PERTE",
        "PETIT", "PHASE", "PIECE", "PIEGE", "PITON", "PIVOT", "PISTE", "PLAGE",
        "PLANE", "PLEIN", "PLUME", "PLUIE", "POINT", "POKER", "POMME", "PORTE",
        "POSER", "POSTE", "POULE", "POUCE", "PREND", "PRIME", "PRISE", "PULPE",
        "PURGE",
        "QUEUE", "QUETE",
        "RADIO", "RAIDE", "RAMPE", "RAYON", "REGLE", "REINE", "REPAS", "REPOS",
        "REVER", "REVUE", "RICHE", "ROCHE", "ROMAN", "ROUGE", "ROUTE", "RUBAN",
        "RUGBY", "RUINE",
        "SABLE", "SAINT", "SALLE", "SAPIN", "SAUCE", "SAVON", "SCENE", "SECHE",
        "SERUM", "SIEGE", "SIGNE", "SOBRE", "SOCLE", "SOLDE", "SOMME", "SONGE",
        "SORTE", "SOUPE", "SOURD", "SOUCI", "STADE", "STAGE", "STORE", "STYLE",
        "SUCRE", "SUEUR", "SUJET", "SUITE", "SUPER",
        "TABLE", "TACHE", "TALON", "TAPIS", "TAROT", "TEMPS", "TENTE", "TENUE",
        "TERME", "TERRE", "TEXTE", "TIGRE", "TISSU", "TITRE", "TORSE", "TOTAL",
        "TOUTE", "TRACE", "TRAIT", "TRAME", "TREVE", "TROIS", "TRONE", "TROUS",
        "TUEUR", "TURBO", "TUYAU",
        "ULTRA", "UNION", "UNITE", "USINE", "USURE", "UTILE",
        "VAGUE", "VALVE", "VEINE", "VENUE", "VERRE", "VIGNE", "VILLE", "VITRE",
        "VOCAL", "VOILE", "VOTRE", "VOUTE",
        "WAGON", "XENON",
        "ZESTE",
        // 6 lettres
        "MIROIR", "JARDIN", "FLEUVE", "PLANTE", "SOLEIL", "CHEMIN", "CHEVAL",
        "BATEAU", "ORANGE", "BANANE", "CITRON", "RAISIN", "AMANDE", "CERISE",
        "FRAISE", "VIOLET", "NUAGES", "ETOILE", "NATURE", "PAPIER", "CRAYON",
        "CAHIER", "GRIFFE", "PLUMES",
        // 7 lettres
        "CHATEAU", "RIVIERE", "SAISONS", "TEMPETE", "FOUDRE"
    );

    @Override
    public void run(String... args) {
        log.info("Checking word dictionary...");
        int added = 0;
        for (String w : FRENCH_WORDS) {
            String upper = w.toUpperCase().trim();
            if (upper.length() >= 5 && upper.length() <= 8
                    && !wordRepository.existsByValue(upper)) {
                Word word = new Word();
                word.setValue(upper);
                word.setLength(upper.length());
                word.setDifficulty(upper.length() <= 5 ? "EASY" : upper.length() <= 6 ? "MEDIUM" : "HARD");
                try {
                    wordRepository.save(word);
                    added++;
                } catch (Exception e) {
                    // skip duplicates
                }
            }
        }
        log.info("Dictionary: {} words total, {} new words added", wordRepository.count(), added);
    }
}
