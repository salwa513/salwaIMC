package com.example.calculimc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var editTextPoids: EditText
    private lateinit var editTextTaille: EditText

    private lateinit var buttonCalculer: Button
    private lateinit var buttonEffacer: Button

    private lateinit var textViewImc: TextView
    private lateinit var textViewCategorie: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Récupération des composants
        editTextPoids = findViewById(R.id.editTextPoids)
        editTextTaille = findViewById(R.id.editTextTaille)

        buttonCalculer = findViewById(R.id.buttonCalculer)
        buttonEffacer = findViewById(R.id.buttonEffacer)

        textViewImc = findViewById(R.id.textViewImc)
        textViewCategorie = findViewById(R.id.textViewCategorie)

        // Bouton Calculer
        buttonCalculer.setOnClickListener {
            calculerImc()
        }

        // Bouton Effacer
        buttonEffacer.setOnClickListener {
            effacer()
        }

        // Conserver le résultat après rotation
        savedInstanceState?.let {

            textViewImc.text = it.getString(KEY_IMC, "")
            textViewCategorie.text = it.getString(KEY_CATEGORIE, "")

            val couleur = it.getInt(KEY_COULEUR, 0)

            if (couleur != 0) {
                textViewImc.setTextColor(couleur)
                textViewCategorie.setTextColor(couleur)
            }
        }
    }

    private fun calculerImc() {

        // Récupérer les valeurs saisies
        val poidsTexte = editTextPoids.text
            .toString()
            .trim()
            .replace(',', '.')

        val tailleTexte = editTextTaille.text
            .toString()
            .trim()
            .replace(',', '.')

        // Supprimer les anciennes erreurs
        editTextPoids.error = null
        editTextTaille.error = null

        // Vérifier et convertir les valeurs
        val poids = lireValeur(editTextPoids, poidsTexte)
        val taille = lireValeur(editTextTaille, tailleTexte)

        // Si une valeur est incorrecte
        if (poids == null || taille == null) {

            effacerResultat()

            Toast.makeText(
                this,
                R.string.toast_erreur,
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // IMPORTANT :
        // La taille est saisie en centimètres.
        // On la convertit en mètres.
        val tailleMetres = taille / 100

        // Calcul de l'IMC
        val imc = Math.round(
            poids / (tailleMetres * tailleMetres) * 100
        ) / 100.0

        // Variables pour la catégorie et la couleur
        val categorie: Int
        val couleurId: Int

        // Déterminer la catégorie
        if (imc < 18.5) {

            categorie = R.string.cat_insuffisance
            couleurId = R.color.imc_orange

        } else if (imc < 25) {

            categorie = R.string.cat_normale
            couleurId = R.color.imc_vert

        } else if (imc < 30) {

            categorie = R.string.cat_surpoids
            couleurId = R.color.imc_orange

        } else if (imc < 35) {

            categorie = R.string.cat_obesite_moderee
            couleurId = R.color.imc_rouge

        } else if (imc < 40) {

            categorie = R.string.cat_obesite_severe
            couleurId = R.color.imc_rouge

        } else {

            categorie = R.string.cat_obesite_morbide
            couleurId = R.color.imc_rouge_fonce
        }

        // Récupérer la couleur
        val couleur = ContextCompat.getColor(
            this,
            couleurId
        )

        // Afficher l'IMC
        textViewImc.text = getString(
            R.string.resultat_imc,
            imc
        )

        // Afficher la catégorie
        textViewCategorie.text = getString(
            categorie
        )

        // Appliquer la couleur
        textViewImc.setTextColor(couleur)
        textViewCategorie.setTextColor(couleur)
    }

    // Vérifier une valeur
    private fun lireValeur(
        champ: EditText,
        texte: String
    ): Double? {

        // Champ vide
        if (texte.isEmpty()) {

            champ.error = getString(
                R.string.erreur_champ_vide
            )

            return null
        }

        // Conversion en Double
        val valeur = texte.toDoubleOrNull()

        // Valeur invalide
        if (valeur == null) {

            champ.error = getString(
                R.string.erreur_valeur_invalide
            )

            return null
        }

        // Valeur négative ou zéro
        if (valeur <= 0) {

            champ.error = getString(
                R.string.erreur_valeur_positive
            )

            return null
        }

        return valeur
    }

    // Effacer le résultat
    private fun effacerResultat() {

        textViewImc.text = ""
        textViewCategorie.text = ""
    }

    // Bouton Effacer
    private fun effacer() {

        editTextPoids.text.clear()
        editTextTaille.text.clear()

        editTextPoids.error = null
        editTextTaille.error = null

        effacerResultat()

        editTextPoids.requestFocus()
    }

    // Sauvegarder le résultat lors de la rotation
    override fun onSaveInstanceState(
        outState: Bundle
    ) {

        super.onSaveInstanceState(outState)

        outState.putString(
            KEY_IMC,
            textViewImc.text.toString()
        )

        outState.putString(
            KEY_CATEGORIE,
            textViewCategorie.text.toString()
        )

        outState.putInt(
            KEY_COULEUR,
            textViewImc.currentTextColor
        )
    }

    companion object {

        private const val KEY_IMC = "imc"

        private const val KEY_CATEGORIE = "categorie"

        private const val KEY_COULEUR = "couleur"
    }
}