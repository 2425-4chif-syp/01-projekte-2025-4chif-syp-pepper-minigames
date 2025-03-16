// Importiere die Jimp-Bibliothek
const { Jimp } = require('jimp');

// Definiere eine asynchrone Funktion zur Bearbeitung des Bildes
async function editImage() {
  try {
    // Lese das Bild aus der angegebenen Datei ein
    const image = await Jimp.read('C:/Users/milad/Downloads/145.jpg');

    // Lade die Schriftart für das Hinzufügen von Text
    const font = await Jimp.loadFont(Jimp.FONT_SANS_32_BLACK);

    // Bearbeite das Bild:
    await image
      .resize(500, Jimp.RESIZE_NEAREST_NEIGHBOR) // Ändere die Bildgröße auf 500px Breite (Höhe automatisch angepasst)
      .quality(80) // Setze die Bildqualität auf 80%
      .greyscale() // Wandle das Bild in Graustufen um
      .print(font, 10, 10, 'Hello, Jimp!') // Füge Text ("Hello, Jimp!") an Position (10, 10) hinzu
      .writeAsync('output-image.jpg'); // Speichere das bearbeitete Bild als "output-image.jpg"

    // Ausgabe in der Konsole bei Erfolg
    console.log('✅ Bild erfolgreich gespeichert als output-image.jpg');
  } catch (error) {
    // Fehlermeldung, falls etwas schiefgeht
    console.error('❌ Fehler beim Bearbeiten des Bildes:', error);
  }
}

// Rufe die Funktion zur Bildbearbeitung auf
editImage();
