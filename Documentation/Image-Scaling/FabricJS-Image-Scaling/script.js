const canvas = new fabric.Canvas("canvas");
let imgObj = null; // Speichert das Bildobjekt

// 📌 Bild über Dateiauswahl hochladen
document.getElementById("fileInput").addEventListener("change", function (event) {
    const file = event.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = function (e) {
        fabric.Image.fromURL(e.target.result, function (img) {
            if (imgObj) canvas.remove(imgObj); // Falls schon ein Bild existiert, entfernen

            imgObj = img;
            imgObj.scaleToWidth(300); // Standardgröße setzen
            imgObj.set({
                left: 150,
                top: 100,
                selectable: true, // Objekt kann mit der Maus angeklickt werden
                hasControls: true, // Skalieren & Drehen ermöglichen
                lockScalingFlip: true // Kein Umkehren der Skalierung
            });

            canvas.add(imgObj);
            canvas.setActiveObject(imgObj);
            canvas.renderAll();
        });
    };
    reader.readAsDataURL(file);
});

// 📌 Skalierung vergrößern
document.getElementById("scaleUp").addEventListener("click", function () {
    if (imgObj) {
        imgObj.scale(imgObj.scaleX * 1.2); // 20% vergrößern
        canvas.renderAll();
    }
});

// 📌 Skalierung verkleinern
document.getElementById("scaleDown").addEventListener("click", function () {
    if (imgObj) {
        imgObj.scale(imgObj.scaleX * 0.8); // 20% verkleinern
        canvas.renderAll();
    }
});

// 📌 Interaktion per Maus aktivieren
canvas.on("object:modified", function (event) {
    console.log("Bild geändert:", event.target);
});
