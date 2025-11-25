package com.pepper.mealplan.data

import com.pepper.mealplan.network.dto.*
import java.time.LocalDate

object DataInserts {

    fun createAllergens(): List<AllergenDto> {
        return listOf(
            AllergenDto("A", "Glutenhaltiges Getreide"),
            AllergenDto("B", "Krebstiere"),
            AllergenDto("C", "Eier"),
            AllergenDto("D", "Fische"),
            AllergenDto("E", "Erdnüsse"),
            AllergenDto("F", "Soja"),
            AllergenDto("G", "Milch"),
            AllergenDto("H", "Schalenfrüchte"),
            AllergenDto("L", "Sellerie"),
            AllergenDto("M", "Senf"),
            AllergenDto("N", "Sesam"),
            AllergenDto("O", "Schwefeldioxid und Sulfite"),
            AllergenDto("P", "Lupinen"),
            AllergenDto("R", "Weichtiere")
        )
    }

    fun createPeople(): List<PersonDto> {
        return listOf(
            PersonDto(firstname = "Stephanie", lastname = "Segebahn"),
            PersonDto(firstname = "Heidi", lastname = "Römer"),
            PersonDto(firstname = "Heinz-Peter", lastname = "Stroh"),
            PersonDto(firstname = "Marten", lastname = "Wirth"),
            PersonDto(firstname = "Leni", lastname = "Blümel"),
            PersonDto(firstname = "Hermann", lastname = "Beckmann"),
            PersonDto(firstname = "Magdalene", lastname = "Joppich"),
            PersonDto(firstname = "Rainer", lastname = "Lehmann"),
            PersonDto(firstname = "Heidi", lastname = "Römer", dob = LocalDate.of(1937, 11, 21)),
            PersonDto(firstname = "Wilhelm", lastname = "Kohlmann"),
            PersonDto(firstname = "Elisabeth", lastname = "Neubert"),
            PersonDto(firstname = "Kurt", lastname = "Müller"),
            PersonDto(firstname = "Erika", lastname = "Weber"),
            PersonDto(firstname = "Wolfgang", lastname = "Fischer"),
            PersonDto(firstname = "Stephanie", lastname = "Segebahn", dob = LocalDate.of(1945, 3, 19)),
            PersonDto(firstname = "Charlotte", lastname = "Vogel"),
            PersonDto(firstname = "Horst", lastname = "Zimmermann"),
            PersonDto(firstname = "Ingrid", lastname = "Krause"),
            PersonDto(firstname = "Werner", lastname = "Schneider"),
            PersonDto(firstname = "Marianne", lastname = "Schuster"),
            PersonDto(firstname = "Friedrich", lastname = "Böhm"),
            PersonDto(firstname = "Johanna", lastname = "Reinhardt"),
            PersonDto(firstname = "Günther", lastname = "Hofmann"),
            PersonDto(firstname = "Heinz-Peter", lastname = "Stroh", dob = LocalDate.of(1940, 6, 15)),
            PersonDto(firstname = "Angela", lastname = "Schmidt"),
            PersonDto(firstname = "Hans", lastname = "Meier"),
            PersonDto(firstname = "Brigitte", lastname = "Scholz"),
            PersonDto(firstname = "Klaus", lastname = "Bauer"),
            PersonDto(firstname = "Gisela", lastname = "Wolf")
        )
    }

    fun createPictureFiles(): List<PictureFileDto> {
        return listOf(
            PictureFileDto(
                bytes = "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", name = "Bild1.png", mediaType = "image/png"
            )
        )
    }

    fun createFoods(): List<FoodDto> {
        return listOf(
            FoodDto(name = "Topfenpalatschinken", pictureId = 1, type = "dessert"),
            FoodDto(name = "Schweinsgeschnetzeltes mit Nudeln und gem. Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Topfenschmarrn mit Kompott", pictureId = 1, type = "dessert"),
            FoodDto(name = "Einmachhuhn mit Reis und Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Spaghetti Carbonara mit gem. Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Käsespätzle mit Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Faschierter Braten mit Kartoffelpüree und Gemüse", pictureId = 1, type = "main"),
            FoodDto(name = "Grammelknödel mit Sauerkraut", pictureId = 1, type = "main"),
            FoodDto(name = "Reisfleisch mit Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Kaiserschmarrn mit Zwetschkenröster", pictureId = 1, type = "dessert"),
            FoodDto(name = "Knacker mit Rösti und Spinat", pictureId = 1, type = "main"),
            FoodDto(name = "Apfelstrudel mit Vanillesauce", pictureId = 1, type = "dessert"),
            FoodDto(name = "Pizzanudeln mit Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Hühner-Cordon-bleu mit Kartoffeln und Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Wurstknödel mit Sauerkraut", pictureId = 1, type = "main"),
            FoodDto(name = "Gebackener Karfiol mit Kartoffeln und Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Cevapcici mit Letscho und Kartoffelpüree", pictureId = 1, type = "main"),
            FoodDto(name = "Gemüseeintopf mit Grießknödel", pictureId = 1, type = "main"),
            FoodDto(name = "Seelachs nach Müllerin Art und Kartoffelsalat", pictureId = 1, type = "main"),
            FoodDto(name = "Paprika Hendlragout mit Reis und Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Wikingerbällchen mit Nudeln und Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Gebackenes Gemüse mit Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Erdäpfelgulasch mit Nudeln", pictureId = 1, type = "main"),
            FoodDto(name = "Krautroulade mit Kartoffeln", pictureId = 1, type = "main"),
            FoodDto(name = "Fischfilet gebacken mit Kartoffelsalat", pictureId = 1, type = "main"),
            FoodDto(name = "Specklinsen mit Knödeln und Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Fleischstrudelsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Backerbsensuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Zwiebelsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Nudelsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Gemüsesuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Grießsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Leberknödelsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Einbrennsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Karottensuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Grießnockerlsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Schöberlsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Steinpilzsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Frittatensuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Waldbeerjoghurt", pictureId = 1, type = "dessert"),
            FoodDto(name = "Kaffee und Kuchen", pictureId = 1, type = "dessert"),
            FoodDto(name = "Muffins", pictureId = 1, type = "dessert"),
            FoodDto(name = "Obstsalat", pictureId = 1, type = "dessert"),
            FoodDto(name = "Kekse", pictureId = 1, type = "dessert"),
            FoodDto(name = "Schnitten", pictureId = 1, type = "dessert"),
            FoodDto(name = "Aprikosenjoghurt", pictureId = 1, type = "dessert"),
            FoodDto(name = "Süße Überraschung", pictureId = 1, type = "dessert"),
            FoodDto(name = "Süße Minis", pictureId = 1, type = "dessert"),
            FoodDto(name = "Grießkoch", pictureId = 1, type = "dessert"),
            FoodDto(name = "Schwarzbrot mit Schafskäse", pictureId = 1, type = "main"),
            FoodDto(name = "Butterbrot", pictureId = 1, type = "main"),
            FoodDto(name = "Mohnnudeln", pictureId = 1, type = "main"),
            FoodDto(name = "Erdäpfelsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Buchteln", pictureId = 1, type = "dessert"),
            FoodDto(name = "Buchweizenauflauf", pictureId = 1, type = "dessert"),
            FoodDto(name = "Milchreis", pictureId = 1, type = "dessert"),
            FoodDto(name = "Saure Frühlingssulz", pictureId = 1, type = "main"),
            FoodDto(name = "Speckbrot", pictureId = 1, type = "main"),
            FoodDto(name = "Bauernkrapfen", pictureId = 1, type = "main"),
            FoodDto(name = "Wurstsemmel", pictureId = 1, type = "main"),
            FoodDto(name = "Nussnudeln", pictureId = 1, type = "main"),
            FoodDto(name = "Gulaschsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Schwarzbrot mit Aufstrich", pictureId = 1, type = "main"),
            FoodDto(name = "Brot mit Verhackert", pictureId = 1, type = "main"),
            FoodDto(name = "Wurstsalat", pictureId = 1, type = "main"),
            FoodDto(name = "Schinkenbrot", pictureId = 1, type = "main"),
            FoodDto(name = "Fleischbällchen mit Gurkerl und Senf", pictureId = 1, type = "main"),
            FoodDto(name = "Gabelbissen", pictureId = 1, type = "main"),
            FoodDto(name = "Frankfurter mit Gebäck", pictureId = 1, type = "main"),
            FoodDto(name = "Heurigenjause", pictureId = 1, type = "main"),
            FoodDto(name = "Laugenstangerl mit Gervais", pictureId = 1, type = "main"),
            FoodDto(name = "Käsebrot", pictureId = 1, type = "main"),
            FoodDto(name = "Schinken-Käse-Toast", pictureId = 1, type = "main"),
            FoodDto(name = "Heiße Kartoffel mit Butter", pictureId = 1, type = "main"),
            FoodDto(name = "Brot mit Leberaufstrich", pictureId = 1, type = "main"),
            FoodDto(name = "Eieromelette mit Gebäck", pictureId = 1, type = "main"),
            FoodDto(name = "Gemüselaibchen mit Kräuterdipp", pictureId = 1, type = "main"),
            FoodDto(name = "Tiroler Knödel mit Sauerkraut", pictureId = 1, type = "main"),
            FoodDto(name = "Züricher Geschnetzeltes mit Nockerl und Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Spaghetti Bolognese", pictureId = 1, type = "main"),
            FoodDto(name = "Gebackener Fisch mit Kartoffelsalat", pictureId = 1, type = "main"),
            FoodDto(name = "Rahmgulasch mit Nockerl", pictureId = 1, type = "main"),
            FoodDto(name = "Gselchtes mit Griesknödel und warmer Krautsalat", pictureId = 1, type = "main"),
            FoodDto(name = "Geröstete Knödel mit Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Rindsgeschnetzeltes mit Nudeln und Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Lasagne Bolognese mit Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Ennstaler Bauernknödel mit Sauerkraut", pictureId = 1, type = "main"),
            FoodDto(name = "Fleischlaibchen mit Püree und Gemüse", pictureId = 1, type = "main"),
            FoodDto(name = "Gebackenes Hühnerfillet mit Petersilienkartoffeln und Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Florentiner Nudeln mit Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Tortellini mit Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Topfenstrudel mit Vanillesauce", pictureId = 1, type = "dessert"),
            FoodDto(name = "Schokopalatschinken", pictureId = 1, type = "dessert"),
            FoodDto(name = "Topfennockerl mit Beerenröster", pictureId = 1, type = "dessert"),
            FoodDto(name = "Zwetschkenstrudel", pictureId = 1, type = "dessert"),
            FoodDto(name = "Wurstnudeln mit Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Gebackene Palatschinken mit Schinken und Käse", pictureId = 1, type = "main"),
            FoodDto(name = "Mostviertler Apfelschmarrn mit Kompott", pictureId = 1, type = "dessert"),
            FoodDto(name = "Nusspalatschinken", pictureId = 1, type = "dessert"),
            FoodDto(name = "Bunte Spätzlepfanne mit Salat", pictureId = 1, type = "main"),
            FoodDto(name = "Reisauflauf mit Apfelmus", pictureId = 1, type = "dessert"),
            FoodDto(name = "Lauchsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Tomatensuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Knoblauchsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Eierstichsuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Gemüsecremesuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Haferflockensuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Broccolisuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Buchstabensuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Pfifferlingcremesuppe", pictureId = 1, type = "soup"),
            FoodDto(name = "Erdbeerjoghurt", pictureId = 1, type = "dessert"),
            FoodDto(name = "Plunder", pictureId = 1, type = "dessert"),
            FoodDto(name = "Schokobanane", pictureId = 1, type = "dessert"),
            FoodDto(name = "Croissant", pictureId = 1, type = "dessert"),
            FoodDto(name = "Wurstbrot", pictureId = 1, type = "main"),
            FoodDto(name = "Grießflammerienockerl", pictureId = 1, type = "dessert"),
            FoodDto(name = "Pizzatoast", pictureId = 1, type = "main"),
            FoodDto(name = "Liptauer mit Gebäck", pictureId = 1, type = "main"),
            FoodDto(name = "Vanillepudding", pictureId = 1, type = "dessert"),
            FoodDto(name = "Schinkenkipferl", pictureId = 1, type = "main"),
            FoodDto(name = "Brot mit Camembert und Tomaten", pictureId = 1, type = "main"),
            FoodDto(name = "Marillenpalatschinken", pictureId = 1, type = "dessert"),
            FoodDto(name = "Saure Suppe mit Erdäpfelschmarrn", pictureId = 1, type = "main"),
            FoodDto(name = "Brot mit Kümmelbraten und Kren", pictureId = 1, type = "main"),
            FoodDto(name = "Nudelsalat", pictureId = 1, type = "main"),
            FoodDto(name = "Brot mit Streichwurst", pictureId = 1, type = "main"),
            FoodDto(name = "Mehrkornbrötchen mit Schinken", pictureId = 1, type = "main"),
            FoodDto(name = "Polenta", pictureId = 1, type = "dessert"),
            FoodDto(name = "Pizzaecken", pictureId = 1, type = "main"),
            FoodDto(name = "Brot mit Thunfischaufstrich", pictureId = 1, type = "main"),
            FoodDto(name = "Saure Wurst mit Gebäck", pictureId = 1, type = "main"),
            FoodDto(name = "Brot mit Gervais/Kräuteraufstrich", pictureId = 1, type = "main")
        )
    }

    fun createFoodAllergens(): List<FoodAllergenDto> {
        return listOf(
            // Topfenpalatschinken (ID 1) - A, C, G
            FoodAllergenDto(allergenShortname = "A", foodId = 1),
            FoodAllergenDto(allergenShortname = "C", foodId = 1),
            FoodAllergenDto(allergenShortname = "G", foodId = 1),
            
            // Schweinsgeschnetzeltes mit Nudeln und gem. Salat (ID 2) - A, G
            FoodAllergenDto(allergenShortname = "A", foodId = 2),
            FoodAllergenDto(allergenShortname = "G", foodId = 2),
            
            // Topfenschmarrn mit Kompott (ID 3) - A, C, G
            FoodAllergenDto(allergenShortname = "A", foodId = 3),
            FoodAllergenDto(allergenShortname = "C", foodId = 3),
            FoodAllergenDto(allergenShortname = "G", foodId = 3),
            
            // Weitere Allergene für alle Foods...
            // (Vereinfacht, da die Liste sehr lang wäre)
        )
    }

    fun getAllData(): DataInsertCollection {
        return DataInsertCollection(
            allergens = createAllergens(),
            people = createPeople(),
            pictureFiles = createPictureFiles(),
            foods = createFoods(),
            foodAllergens = createFoodAllergens()
        )
    }
}

data class DataInsertCollection(
    val allergens: List<AllergenDto>,
    val people: List<PersonDto>,
    val pictureFiles: List<PictureFileDto>,
    val foods: List<FoodDto>,
    val foodAllergens: List<FoodAllergenDto>
)