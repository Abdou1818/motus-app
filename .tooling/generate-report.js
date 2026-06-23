const fs = require('fs');
const path = require('path');
const {
  Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
  ImageRun, AlignmentType, LevelFormat, TableOfContents, HeadingLevel,
  BorderStyle, WidthType, ShadingType, PageNumber, PageBreak, Header, Footer
} = require('docx');

const DIA = path.join(__dirname, '..', 'docs', 'diagrams');
const img = (f) => fs.readFileSync(path.join(DIA, f));

const INDIGO = "6366F1", VIOLET = "8B5CF6", INK = "1B1F33", MUTE = "5D6480";
const LIGHT = "EEF1FB", BORDER = "E6E9F5";

// ---- helpers ----------------------------------------------------------
function h1(t){return new Paragraph({heading:HeadingLevel.HEADING_1,children:[new TextRun(t)]});}
function h2(t){return new Paragraph({heading:HeadingLevel.HEADING_2,children:[new TextRun(t)]});}
function h3(t){return new Paragraph({heading:HeadingLevel.HEADING_3,children:[new TextRun(t)]});}
function p(text,opts={}){return new Paragraph({spacing:{after:120,line:276},children:[new TextRun({text,...opts})]});}
function bullet(text){return new Paragraph({numbering:{reference:"b",level:0},spacing:{after:60},children:runs(text)});}
function num(text){return new Paragraph({numbering:{reference:"n",level:0},spacing:{after:60},children:runs(text)});}
// bold-on-** parser for convenience
function runs(text){
  const parts = text.split(/(\*\*[^*]+\*\*)/g).filter(Boolean);
  return parts.map(s => s.startsWith('**') && s.endsWith('**')
    ? new TextRun({text:s.slice(2,-2),bold:true})
    : new TextRun(s));
}
function para(text){return new Paragraph({spacing:{after:120,line:276},children:runs(text)});}

function figure(file,w,caption){
  const ratio = {'01-use-case.png':1.406,'02-architecture.png':1.278,'03-class.png':1.455,
    '04-sequence.png':1.289,'05-mcd.png':1.567,'06-ui-mockup.png':1.267}[file];
  const h = Math.round(w/ratio);
  return [
    new Paragraph({alignment:AlignmentType.CENTER,spacing:{before:120,after:60},
      children:[new ImageRun({type:"png",data:img(file),transformation:{width:w,height:h},
        altText:{title:caption,description:caption,name:file}})]}),
    new Paragraph({alignment:AlignmentType.CENTER,spacing:{after:200},
      children:[new TextRun({text:caption,italics:true,size:18,color:MUTE})]})
  ];
}

// table builder
const bd = {style:BorderStyle.SINGLE,size:1,color:"CCCCCC"};
const borders = {top:bd,bottom:bd,left:bd,right:bd};
function cell(text,{w,head=false,bold=false,fill}={}){
  return new TableCell({borders,width:{size:w,type:WidthType.DXA},
    shading: fill?{fill,type:ShadingType.CLEAR}:(head?{fill:INDIGO,type:ShadingType.CLEAR}:undefined),
    margins:{top:60,bottom:60,left:120,right:120},
    children:[new Paragraph({children:[new TextRun({text,bold:head||bold,
      color:head?"FFFFFF":INK,size:19})]})]});
}
function table(headers,rows,widths){
  const total = widths.reduce((a,b)=>a+b,0);
  const headRow = new TableRow({tableHeader:true,children:headers.map((t,i)=>cell(t,{w:widths[i],head:true}))});
  const bodyRows = rows.map((r,ri)=>new TableRow({children:r.map((t,i)=>
    cell(t,{w:widths[i],fill: ri%2? "F7F8FC": undefined}))}));
  return new Table({width:{size:total,type:WidthType.DXA},columnWidths:widths,rows:[headRow,...bodyRows]});
}
function spacer(){return new Paragraph({spacing:{after:120},children:[new TextRun("")]});}

// ---- document ---------------------------------------------------------
const children = [];

// TITLE PAGE
children.push(
  new Paragraph({spacing:{before:2600,after:0},alignment:AlignmentType.CENTER,
    children:[new TextRun({text:"MOTUS",bold:true,size:96,color:INDIGO})]}),
  new Paragraph({alignment:AlignmentType.CENTER,spacing:{after:80},
    children:[new TextRun({text:"Le jeu de mots",italics:true,size:30,color:VIOLET})]}),
  new Paragraph({alignment:AlignmentType.CENTER,spacing:{before:240,after:60},
    children:[new TextRun({text:"Conception et réalisation d'une application",size:30,color:INK})]}),
  new Paragraph({alignment:AlignmentType.CENTER,spacing:{after:600},
    children:[new TextRun({text:"en architecture microservices",size:30,color:INK})]}),
  new Paragraph({alignment:AlignmentType.CENTER,spacing:{after:40},
    children:[new TextRun({text:"Dossier de projet",bold:true,size:26,color:MUTE})]}),
  new Paragraph({alignment:AlignmentType.CENTER,spacing:{after:1000},
    children:[new TextRun({text:"Master MIAGE M2 — Université Paris-Dauphine",size:24,color:MUTE})]}),
  new Paragraph({alignment:AlignmentType.CENTER,spacing:{after:40},
    children:[new TextRun({text:"Auteur : « Votre Nom »",size:24,color:INK})]}),
  new Paragraph({alignment:AlignmentType.CENTER,spacing:{after:40},
    children:[new TextRun({text:"Unité d'enseignement : Architectures Microservices",size:22,color:MUTE})]}),
  new Paragraph({alignment:AlignmentType.CENTER,spacing:{after:40},
    children:[new TextRun({text:"Année universitaire 2025 – 2026",size:22,color:MUTE})]}),
  new Paragraph({alignment:AlignmentType.CENTER,spacing:{before:600},
    children:[new TextRun({text:"Dépôt : github.com/Abdou1818/motus-app",size:20,color:INDIGO})]}),
  new Paragraph({children:[new PageBreak()]})
);

// TOC
children.push(
  new Paragraph({heading:HeadingLevel.HEADING_1,children:[new TextRun("Sommaire")]}),
  new TableOfContents("Sommaire",{hyperlink:true,headingStyleRange:"1-3"}),
  new Paragraph({children:[new PageBreak()]})
);

// 1. INTRODUCTION
children.push(h1("1. Introduction et contexte"));
children.push(para("Ce dossier présente la conception et la réalisation de **Motus**, une application web reproduisant le célèbre jeu télévisé français de devinettes de mots (variante francophone de Wordle). Le joueur doit retrouver un mot mystère d'une longueur donnée en un maximum de six essais ; après chaque proposition, chaque lettre est colorée selon qu'elle est **bien placée**, **mal placée** ou **absente** du mot recherché."));
children.push(para("L'objectif pédagogique du projet n'est pas seulement de produire un jeu fonctionnel, mais de mettre en œuvre une **architecture microservices** complète et réaliste : découpage du domaine en services autonomes, communication inter-services via API REST, point d'entrée unique (API Gateway), persistance isolée par service, conteneurisation Docker et orchestration Kubernetes."));
children.push(para("Le périmètre fonctionnel couvre les **quatre responsabilités** imposées par le sujet : la gestion des joueurs, la gestion des parties, le suivi des scores et l'administration du jeu. Chacune est portée par un microservice dédié, comme détaillé dans la suite du document."));

// 2. CAHIER DES CHARGES
children.push(h1("2. Cahier des charges"));
children.push(para("Le sujet impose la couverture de quatre grandes fonctionnalités. Le tableau ci-dessous les met en correspondance avec le microservice qui les implémente."));
children.push(table(
  ["Fonctionnalité imposée","Description","Service responsable"],
  [
    ["Gérer les joueurs","Inscription, consultation et mise à jour des profils joueurs (pseudo, e-mail).","player-service"],
    ["Gérer les parties","Démarrer une partie, soumettre des propositions, appliquer les règles du Motus.","game-service"],
    ["Suivre les scores","Enregistrer les résultats, calculer le classement et les statistiques.","score-service"],
    ["Administrer le jeu","Superviser joueurs et parties, consulter les statistiques globales (accès protégé).","admin-service"],
  ],
  [3000,4360,2000]
));
children.push(spacer());
children.push(h2("2.1 Exigences fonctionnelles"));
children.push(bullet("Un joueur peut **créer un profil** (pseudo et e-mail uniques) puis se reconnecter via son pseudo."));
children.push(bullet("Un joueur peut **démarrer une partie** en choisissant la longueur du mot (5, 6 ou 7 lettres) ou en la laissant aléatoire."));
children.push(bullet("À chaque essai, le mot proposé doit exister dans le **dictionnaire** et commencer par la **première lettre révélée** (règle classique du Motus)."));
children.push(bullet("Le joueur dispose de **six essais** ; la partie se termine en victoire si le mot est trouvé, en défaite sinon."));
children.push(bullet("Le joueur peut consulter son **historique** de parties et le **classement** général."));
children.push(bullet("L'administrateur, après authentification par clé secrète, peut **lister les joueurs**, **rechercher les parties** (par date, statut, joueur) et voir les **statistiques globales**."));
children.push(h2("2.2 Exigences non fonctionnelles"));
children.push(bullet("**Modularité** : chaque service est déployable et testable indépendamment."));
children.push(bullet("**Isolation des données** : une base de données par service (pattern *Database per Service*)."));
children.push(bullet("**Portabilité** : l'ensemble se lance par une seule commande via Docker Compose."));
children.push(bullet("**Scalabilité** : manifestes Kubernetes fournis pour un déploiement orchestré."));

// 3. ARCHITECTURE
children.push(new Paragraph({children:[new PageBreak()]}));
children.push(h1("3. Architecture générale"));
children.push(para("L'application suit une architecture microservices à couplage faible. Le navigateur ne communique jamais directement avec les services métier : toutes les requêtes transitent par l'**API Gateway**, qui assure le routage et la gestion centralisée du CORS. Chaque service métier possède sa propre base de données PostgreSQL et expose une API REST."));
children.push(...figure("02-architecture.png",600,"Figure 1 — Architecture microservices et déploiement Docker"));
children.push(h2("3.1 Rôle de chaque composant"));
children.push(table(
  ["Composant","Port","Responsabilité"],
  [
    ["API Gateway","8090 → 8080","Point d'entrée unique, routage par préfixe d'URL, CORS."],
    ["player-service","8081","Cycle de vie des joueurs et statistiques individuelles."],
    ["game-service","8082","Moteur du jeu : parties, essais, algorithme Motus."],
    ["word-service","8083","Dictionnaire français, tirage aléatoire et validation."],
    ["score-service","8084","Enregistrement des résultats, classement, statistiques."],
    ["admin-service","8085","Agrégation pour l'administration (sans base propre)."],
    ["frontend","3000","Application monopage (SPA) servie par Nginx."],
    ["PostgreSQL","5432","Quatre bases isolées : db_players, db_games, db_words, db_scores."],
  ],
  [2600,1800,4960]
));
children.push(spacer());
children.push(h2("3.2 Communication inter-services"));
children.push(para("Les services collaborent par appels REST synchrones via RestTemplate. Les dépendances principales sont :"));
children.push(bullet("**game-service → word-service** : obtention d'un mot aléatoire au démarrage et validation d'une proposition."));
children.push(bullet("**game-service → score-service** : enregistrement du résultat à la fin d'une partie (gagnée ou perdue)."));
children.push(bullet("**player-service → score-service** : récupération des statistiques d'un joueur."));
children.push(bullet("**admin-service → player / game / score** : agrégation des données pour les écrans d'administration."));
children.push(para("Le couplage reste faible : un service ne connaît que l'URL REST de ses voisins (injectée par variable d'environnement), jamais leur base de données."));

// 4. MODELE DE DONNEES
children.push(new Paragraph({children:[new PageBreak()]}));
children.push(h1("4. Modèle de données"));
children.push(para("Conformément au pattern *Database per Service*, chaque microservice persiste ses propres entités dans une base dédiée. Il n'existe **aucune clé étrangère physique entre bases** : les références inter-services (par exemple `playerId` dans une partie) sont de simples identifiants, résolus applicativement par appel REST. Seule la relation `games` 1—∞ `attempts`, interne à la base `db_games`, est une véritable clé étrangère."));
children.push(...figure("05-mcd.png",600,"Figure 2 — Modèle physique de données (une base par service)"));
children.push(h2("4.1 Diagramme de classes du domaine"));
children.push(para("Les entités JPA reflètent directement le modèle relationnel. La couleur identifie le service propriétaire de chaque classe."));
children.push(...figure("03-class.png",615,"Figure 3 — Diagramme de classes des entités du domaine"));

// 5. SERVICES & API
children.push(new Paragraph({children:[new PageBreak()]}));
children.push(h1("5. Description des services et de l'API REST"));
children.push(para("Toutes les routes sont exposées au travers de l'API Gateway sous le préfixe `/api`. Les principaux points d'accès sont récapitulés ci-dessous."));
children.push(h2("5.1 player-service — /api/players"));
children.push(table(["Méthode","Endpoint","Description"],[
  ["POST","/register","Créer un joueur"],
  ["GET","/","Lister les joueurs"],
  ["GET","/{id}","Consulter un joueur"],
  ["PUT","/{id}","Modifier un joueur"],
  ["DELETE","/{id}","Supprimer un joueur"],
  ["GET","/{id}/stats","Statistiques d'un joueur"],
],[1800,3200,4360]));
children.push(spacer());
children.push(h2("5.2 game-service — /api/games"));
children.push(table(["Méthode","Endpoint","Description"],[
  ["POST","/","Démarrer une partie"],
  ["GET","/{id}","État d'une partie"],
  ["GET","/player/{playerId}","Parties d'un joueur"],
  ["GET","/all","Toutes les parties (filtres date/statut)"],
  ["POST","/{id}/attempts","Soumettre un mot"],
  ["GET","/{id}/attempts","Historique des tentatives"],
],[1800,3200,4360]));
children.push(spacer());
children.push(h2("5.3 word-service — /api/words"));
children.push(table(["Méthode","Endpoint","Description"],[
  ["GET","/random?length=","Mot aléatoire"],
  ["POST","/validate","Valider l'existence d'un mot"],
  ["GET / POST / DELETE","/ , /{id}","Gestion du dictionnaire"],
],[2400,2600,4360]));
children.push(spacer());
children.push(h2("5.4 score-service — /api/scores"));
children.push(table(["Méthode","Endpoint","Description"],[
  ["POST","/","Enregistrer un résultat"],
  ["GET","/player/{playerId}","Résultats d'un joueur"],
  ["GET","/player/{playerId}/stats","Statistiques agrégées"],
  ["GET","/leaderboard","Classement (top 10)"],
],[1800,3200,4360]));
children.push(spacer());
children.push(h2("5.5 admin-service — /api/admin"));
children.push(para("Toutes les routes exigent l'en-tête `X-Admin-Key: admin-secret`."));
children.push(table(["Méthode","Endpoint","Description"],[
  ["GET","/games","Parties (filtres date, statut, joueur)"],
  ["GET","/players","Liste des joueurs"],
  ["GET","/stats","Statistiques globales"],
],[1800,3200,4360]));

// 6. USE CASES
children.push(new Paragraph({children:[new PageBreak()]}));
children.push(h1("6. Cas d'utilisation"));
children.push(para("Deux acteurs interagissent avec le système : le **Joueur** et l'**Administrateur**. Le diagramme suivant synthétise leurs cas d'utilisation, qui recouvrent les quatre fonctions imposées par le sujet."));
children.push(...figure("01-use-case.png",560,"Figure 4 — Diagramme de cas d'utilisation"));

// 7. SEQUENCE
children.push(h1("7. Scénario dynamique : déroulement d'une partie"));
children.push(para("Le diagramme de séquence ci-dessous illustre les deux interactions centrales du jeu : le **démarrage d'une partie** (tirage d'un mot via word-service) et la **soumission d'une proposition** (validation, évaluation, et enregistrement du score en fin de partie)."));
children.push(...figure("04-sequence.png",605,"Figure 5 — Diagramme de séquence : démarrer une partie et proposer un mot"));

// 8. ALGORITHME
children.push(new Paragraph({children:[new PageBreak()]}));
children.push(h1("8. L'algorithme du Motus"));
children.push(para("Le cœur du jeu réside dans la coloration des lettres, implémentée par la classe `MotusAlgorithm`. L'algorithme procède en **deux passes** afin de gérer correctement les lettres dupliquées :"));
children.push(num("**Passe 1 — lettres bien placées (CORRECT).** On parcourt la proposition ; toute lettre identique à celle du mot secret à la même position est marquée *bien placée* (vert) et la position correspondante du mot secret est consommée."));
children.push(num("**Passe 2 — lettres mal placées (MISPLACED).** Pour chaque lettre non encore validée, on cherche une occurrence **non consommée** dans le mot secret ; si elle existe, la lettre est *mal placée* (jaune) et l'occurrence est consommée."));
children.push(num("**Lettres absentes (ABSENT).** Toute lettre restante n'apparaissant pas (ou plus) dans le mot secret est marquée *absente* (gris)."));
children.push(para("Ce mécanisme de **consommation des positions** garantit qu'une lettre ne soit signalée *mal placée* qu'autant de fois qu'elle apparaît réellement dans le mot secret, déduction faite des correspondances exactes — point délicat souvent mal traité dans les implémentations naïves."));
children.push(para("Une règle métier supplémentaire est appliquée côté serveur : toute proposition doit **commencer par la première lettre révélée**, sous peine d'un refus (HTTP 400). Cette contrainte est également vérifiée côté interface pour un retour immédiat au joueur."));

// 9. UI
children.push(h1("9. Interface utilisateur"));
children.push(para("L'interface est une application monopage (SPA) écrite en HTML, CSS et JavaScript *vanilla*, sans framework ni dépendance externe, servie par Nginx. Elle a fait l'objet d'une refonte graphique adoptant un thème **clair, moderne et dynamique** : fond en dégradés animés, couleur signature indigo–violet–rose, cartes arrondies et nombreuses micro-animations (animation 3D de retournement des cases, confettis de victoire, clavier virtuel réactif)."));
children.push(...figure("06-ui-mockup.png",545,"Figure 6 — Écran de jeu (thème clair)"));
children.push(para("L'interface comporte trois onglets : **Jouer** (inscription, grille de jeu, clavier virtuel, historique), **Classement** (tableau des meilleurs joueurs avec taux de victoire) et **Admin** (statistiques globales, liste des joueurs, recherche de parties filtrée)."));

// 10. SECURITE
children.push(new Paragraph({children:[new PageBreak()]}));
children.push(h1("10. Sécurité"));
children.push(bullet("**Protection de l'administration** : les routes `/api/admin/**` exigent l'en-tête `X-Admin-Key`. Toute requête sans clé valide reçoit une réponse HTTP 401."));
children.push(bullet("**Gestion centralisée du CORS** : configurée une seule fois au niveau de l'API Gateway, évitant la duplication dans chaque service."));
children.push(bullet("**Validation des entrées** : longueur et première lettre des propositions contrôlées côté serveur, indépendamment du client."));
children.push(bullet("**Unicité** : contraintes d'unicité sur le pseudo et l'e-mail des joueurs, ainsi que sur la valeur des mots du dictionnaire."));
children.push(para("Les pistes d'amélioration (authentification JWT, secrets externalisés, HTTPS de bout en bout) sont évoquées en conclusion."));

// 11. TESTS
children.push(h1("11. Stratégie de tests"));
children.push(para("Le service le plus critique — game-service — fait l'objet de tests automatisés :"));
children.push(bullet("**MotusAlgorithmTest** : tests unitaires de l'algorithme de coloration, incluant les cas de lettres dupliquées (CORRECT / MISPLACED / ABSENT)."));
children.push(bullet("**GameServiceIntegrationTest** : tests d'intégration s'appuyant sur une base **H2** en mémoire et des **mocks Mockito** pour les services distants (word, score)."));
children.push(para("Les tests se lancent par `mvn test` dans le module concerné. Cette approche valide la logique métier sans dépendre de l'infrastructure complète."));

// 12. DEPLOIEMENT
children.push(h1("12. Déploiement"));
children.push(h2("12.1 Docker Compose"));
children.push(para("L'ensemble de la plateforme démarre par une seule commande : `docker-compose up --build`. Le fichier `docker-compose.yml` orchestre huit conteneurs et gère leur ordre de démarrage grâce aux **health checks** et aux dépendances conditionnelles (`depends_on: condition: service_healthy`). Les services ne démarrent qu'une fois PostgreSQL — puis leurs dépendances — réellement disponibles."));
children.push(h2("12.2 Kubernetes"));
children.push(para("Le dossier `k8s/` fournit un manifeste de *Deployment* et de *Service* par composant. Le déploiement s'effectue par `kubectl apply -f k8s/`, l'API étant exposée via un *NodePort* (port 30080). Cette cible illustre la capacité de l'architecture à passer à l'échelle dans un environnement orchestré."));

// 13. DIFFICULTES
children.push(new Paragraph({children:[new PageBreak()]}));
children.push(h1("13. Difficultés rencontrées et limites"));
children.push(bullet("**Gestion des lettres dupliquées** dans l'algorithme Motus : la solution naïve sur-colorie les doublons ; l'algorithme en deux passes avec consommation des positions a été nécessaire."));
children.push(bullet("**Cohérence des données inter-services** : en l'absence de clés étrangères entre bases, la cohérence repose sur la discipline applicative (propagation des identifiants et du pseudo)."));
children.push(bullet("**Orchestration du démarrage** : sans *health checks*, le game-service tentait d'appeler le word-service avant que celui-ci ne soit prêt ; les sondes de santé ont résolu ces conditions de course."));
children.push(bullet("**Conflit de port de la passerelle** : exposition externe sur le port 8090 (mappé sur 8080 interne) pour éviter une collision sur le poste de développement."));
children.push(para("Limites actuelles : authentification simplifiée (pas de JWT), pas de découverte de services dynamique (URLs statiques), et couverture de tests concentrée sur le game-service."));

// 14. CONCLUSION
children.push(h1("14. Conclusion et perspectives"));
children.push(para("Le projet atteint l'ensemble des objectifs fixés : les quatre fonctionnalités imposées sont opérationnelles et portées par des microservices autonomes, communicant par API REST derrière une passerelle unique, chacun disposant de sa propre base de données. La plateforme est intégralement conteneurisée et déployable aussi bien via Docker Compose que sur Kubernetes."));
children.push(para("Plusieurs évolutions permettraient de gagner en robustesse : introduction d'une **authentification JWT** et d'une gestion de rôles, mise en place d'un **annuaire de services** (service discovery) et d'un **circuit breaker** pour la résilience, externalisation de la configuration, et extension de la **couverture de tests** à l'ensemble des services. Ces pistes inscrivent naturellement le projet dans une démarche d'industrialisation."));

// ---- assemble ---------------------------------------------------------
const doc = new Document({
  creator:"MIAGE M2 — Paris-Dauphine",
  title:"Rapport — Motus Microservices",
  styles:{
    default:{document:{run:{font:"Arial",size:22,color:INK}}},
    paragraphStyles:[
      {id:"Heading1",name:"Heading 1",basedOn:"Normal",next:"Normal",quickFormat:true,
        run:{size:32,bold:true,font:"Arial",color:INDIGO},
        paragraph:{spacing:{before:320,after:160},outlineLevel:0}},
      {id:"Heading2",name:"Heading 2",basedOn:"Normal",next:"Normal",quickFormat:true,
        run:{size:26,bold:true,font:"Arial",color:VIOLET},
        paragraph:{spacing:{before:220,after:120},outlineLevel:1}},
      {id:"Heading3",name:"Heading 3",basedOn:"Normal",next:"Normal",quickFormat:true,
        run:{size:23,bold:true,font:"Arial",color:INK},
        paragraph:{spacing:{before:160,after:100},outlineLevel:2}},
    ]
  },
  numbering:{config:[
    {reference:"b",levels:[{level:0,format:LevelFormat.BULLET,text:"•",alignment:AlignmentType.LEFT,
      style:{paragraph:{indent:{left:560,hanging:280}}}}]},
    {reference:"n",levels:[{level:0,format:LevelFormat.DECIMAL,text:"%1.",alignment:AlignmentType.LEFT,
      style:{paragraph:{indent:{left:560,hanging:280}}}}]},
  ]},
  sections:[{
    properties:{page:{size:{width:12240,height:15840},margin:{top:1440,right:1440,bottom:1440,left:1440}}},
    footers:{default:new Footer({children:[new Paragraph({alignment:AlignmentType.CENTER,
      children:[new TextRun({text:"Motus — Architecture Microservices  ·  ",size:16,color:MUTE}),
        new TextRun({text:"",size:16,color:MUTE,children:[PageNumber.CURRENT]})]})]})},
    children
  }]
});

Packer.toBuffer(doc).then(buf=>{
  const out = path.join(__dirname,'..','docs','Rapport-Motus-Microservices.docx');
  fs.writeFileSync(out,buf);
  console.log("written",out,Math.round(buf.length/1024)+"KB");
});
