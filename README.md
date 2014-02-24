Digipost for Android
=======

Digipost for Android er en app for Android som gir tilgang til brukerens sikre digitale postkasse i Digipost. Bruker må opprette en konto på https://www.digipost.no/.

App-en kan lastes ned fra Google Play, https://play.google.com/store/apps/details?id=no.digipost.android.

Kildekoden er her tilgjengelig som fri programvare under lisensen Apache License, Version 2.0, som beskrevet i lisensfilen.

Viktig:
------
For å kunne bruke koden og få tilgang til Digipost sitt api vil du trenge client id og client secret. Hvordan du får tak i det kan du lese mer om på https://www.digipost.no/plattform/privat/ 
Når du har fått client id og client secret er du nødt til å opprette en fil kalt Secret.java,legge den i mappen src/no/digipost/android/authentication og gi den følgende innhold:

    package no.digipost.android.authentication;
    public class Secret {
      public static final String CLIENT_ID = "";
      public static final String CLIENT_SECRET = "";
      public static final String REDIRECT_URI = "http://localhost:1979";
      public static String ACCESS_TOKEN = "";
    }
