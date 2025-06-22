package com.example.pepperdiebspiel.screens

import com.example.pepperdiebspiel.R

data class GameItemInfo(
    val imageRes: Int,
    val description: String,
    val soundRes: Int
)

val allGameInfos = listOf(
    GameItemInfo(R.drawable.alarm, "Alarm – Sirene", R.raw.alarm),
    GameItemInfo(R.drawable.zombie, "Zombie – Stöhnt", R.raw.zombie),
    GameItemInfo(R.drawable.skeleton, "Skelett – Klappert", R.raw.skeleton),
    GameItemInfo(R.drawable.church, "Kirche – Glockenläuten", R.raw.church_bells),
    GameItemInfo(R.drawable.cow, "Kuh – Muh", R.raw.cow),
    GameItemInfo(R.drawable.monster, "Monster – Knurrt", R.raw.monster),
    GameItemInfo(R.drawable.owl, "Eule – Schreit nachts", R.raw.owl),
    GameItemInfo(R.drawable.sheep, "Schaf – Blökt", R.raw.sheep_bleat),
    GameItemInfo(R.drawable.vampir, "Vampir – Fauch", R.raw.vampire),
    GameItemInfo(R.drawable.water, "Wasser – Plätschert", R.raw.water_sound),
    GameItemInfo(R.drawable.witch, "Hexe – Lacht böse", R.raw.witch_laugh),
    GameItemInfo(R.drawable.wolf, "Wolf – Heult", R.raw.wolf_howl),
    GameItemInfo(R.drawable.bird, "Vogel – Zwitschert", R.raw.bird_chirp),

    )
