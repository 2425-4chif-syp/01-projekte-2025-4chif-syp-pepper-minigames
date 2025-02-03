const {Jimp} = require('jimp');

async function editImage() {
  try {
    const image = await Jimp.read('C:/Users/milad/Downloads/145.jpg');
    const font = await Jimp.loadFont(Jimp.FONT_SANS_32_BLACK);

    await image
      .resize(500, Jimp.RESIZE_NEAREST_NEIGHBOR)
      .quality(80)
      .greyscale()
      .print(font, 10, 10, 'Hello, Jimp!')
      .writeAsync('output-image.jpg');

    console.log('✅ Bild erfolgreich gespeichert als output-image.jpg');
  } catch (error) {
    console.error('❌ Fehler beim Bearbeiten des Bildes:', error);
  }
}

editImage();

